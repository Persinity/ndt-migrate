/**
 * Copyright (c) 2015 Persinity Inc.
 */
package com.persinity.haka.impl.actor.handler;

import static org.easymock.EasyMock.expectLastCall;
import static org.easymock.EasyMock.newCapture;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.Collection;
import java.util.Collections;

import org.easymock.Capture;
import org.easymock.EasyMock;
import org.junit.Test;

import com.persinity.common.Id;
import com.persinity.haka.IdleJob;
import com.persinity.haka.Job;
import com.persinity.haka.impl.actor.JobState;
import com.persinity.haka.impl.actor.TestIdleJobProducer;
import com.persinity.haka.impl.actor.TestJob;
import com.persinity.haka.impl.actor.TestJobProducerNoChildren;
import com.persinity.haka.impl.actor.TestJobProducerNullChildrenInvalid;
import com.persinity.haka.impl.actor.TestJobProducerOneChild;
import com.persinity.haka.impl.actor.TestJobProducerThrowOnProcess;

/**
 * @author Ivan Dachev
 */
public class ProcessJobStateHandlerTest extends HandlerTest {

    @Test
    public void testProcessJob_NewJobStateNoChildren() throws Exception {
        TestJob job = new TestJob(TestJobProducerNoChildren.class);

        JobState jobState = new JobState(job, Id.nextValue(), path);
        jobState.setStatus(JobState.Status.NEW);

        workerState.addJobState(jobState);

        Capture<JobState> capturedJobStateProcessed = newCapture();
        Capture<Boolean> needAck = newCapture();
        workerMock.sendMsgProcessed(EasyMock.capture(capturedJobStateProcessed), EasyMock.captureBoolean(needAck));
        expectLastCall();

        Capture<JobState> capturedJobStateCleanup = newCapture();
        workerMock.cleanupJobState(EasyMock.capture(capturedJobStateCleanup));
        expectLastCall();

        replayAll();

        ProcessJobStateHandler handler = new ProcessJobStateHandler(workerMock);

        handler.processJobState(jobState.getJob().getId());

        verifyAll();

        jobState = capturedJobStateProcessed.getValue();
        assertThat(jobState.getJob().getId(), is(job.getId()));
        assertEquals(jobState.getJob().getJobProducerClass(), job.getJobProducerClass());
        assertThat(jobState.getStatus(), is(JobState.Status.DONE));

        assertThat(needAck.getValue(), is(false));

        jobState = capturedJobStateCleanup.getValue();
        assertThat(jobState.getJob().getId(), is(job.getId()));
        assertEquals(jobState.getJob().getJobProducerClass(), job.getJobProducerClass());
        assertThat(jobState.getStatus(), is(JobState.Status.DONE));

        assertThat(job.state, is(TestJob.STATE_DONE));
    }

    @Test
    public void testProcessJob_ProcessingJobStateNoChildren() throws Exception {
        TestJob job = new TestJob(TestJobProducerNoChildren.class);

        JobState jobState = new JobState(job, Id.nextValue(), path);
        jobState.setStatus(JobState.Status.PROCESSING);

        workerState.addJobState(jobState);

        Capture<JobState> capturedJobStateProcessed = newCapture();
        Capture<Boolean> needAck = newCapture();
        workerMock.sendMsgProcessed(EasyMock.capture(capturedJobStateProcessed), EasyMock.captureBoolean(needAck));
        expectLastCall();

        replayAll();

        ProcessJobStateHandler handler = new ProcessJobStateHandler(workerMock);

        handler.processJobState(jobState.getJob().getId());

        verifyAll();

        jobState = capturedJobStateProcessed.getValue();
        assertThat(jobState.getJob().getId(), is(job.getId()));
        assertEquals(jobState.getJob().getJobProducerClass(), job.getJobProducerClass());
        assertThat(jobState.getStatus(), is(JobState.Status.DONE));

        assertThat(needAck.getValue(), is(true));

        assertThat(job.state, is(TestJob.STATE_DONE));
    }

    @Test
    public void testProcessJob_JobStateReturnChildren() throws Exception {
        TestJob job = new TestJob(TestJobProducerOneChild.class);

        JobState jobState = new JobState(job, Id.nextValue(), path);
        jobState.setStatus(JobState.Status.PROCESSING);

        workerState.addJobState(jobState);

        workerMock.doSnapshot();
        expectLastCall().times(1);

        Capture<JobState> capturedJobState = newCapture();
        workerMock.sendNewChildrenJobMsg(EasyMock.capture(capturedJobState));
        expectLastCall();

        assertThat(jobState.getChildren().size(), is(0));

        replayAll();

        ProcessJobStateHandler handler = new ProcessJobStateHandler(workerMock);

        handler.processJobState(jobState.getJob().getId());

        verifyAll();

        assertThat(jobState.getChildren().size(), is(1));

        assertThat(job.state, is(TestJobProducerOneChild.STATE_ONE_CHILD_CREATED));

        JobState jobState1 = capturedJobState.getValue();
        assertThat(jobState1.getJob().getId().parentId, is(job.getId().parentId));
    }

    @Test
    public void testProcessJob_JobStateDone() throws Exception {
        TestJob job = new TestJob(TestJobProducerOneChild.class);

        JobState jobState = new JobState(job, Id.nextValue(), path);
        jobState.setStatus(JobState.Status.DONE);

        workerState.addJobState(jobState);

        replayAll();

        ProcessJobStateHandler handler = new ProcessJobStateHandler(workerMock);

        handler.processJobState(jobState.getJob().getId());

        verifyAll();
    }

    @Test
    public void testProcessJob_JobStateChildrenNotDone() throws Exception {
        TestJob job = new TestJob(TestJobProducerOneChild.class);

        JobState jobState = new JobState(job, Id.nextValue(), path);
        jobState.setStatus(JobState.Status.PROCESSING);

        workerState.addJobState(jobState);

        TestJob childJob = new TestJob(job.getId(), TestJobProducerNoChildren.class);
        jobState.appendChildren(Collections.singleton((Job) childJob));

        replayAll();

        ProcessJobStateHandler handler = new ProcessJobStateHandler(workerMock);

        handler.processJobState(jobState.getJob().getId());

        verifyAll();

        assertThat(jobState.getChildren().size(), is(1));

        assertThat(job.state, is(TestJob.STATE_INIT));
    }

    @Test
    public void testProcessJob_JobStateReturnChildrenNull() throws Exception {
        TestJob job = new TestJob(TestJobProducerNullChildrenInvalid.class);

        JobState jobState = new JobState(job, Id.nextValue(), path);
        jobState.setStatus(JobState.Status.PROCESSING);

        workerState.addJobState(jobState);

        replayAll();

        ProcessJobStateHandler handler = new ProcessJobStateHandler(workerMock);

        try {
            handler.processJobState(jobState.getJob().getId());
            fail("Expect to throw IllegalStateException");
        } catch (IllegalStateException e) {
            // expected
        }

        verifyAll();

        assertThat(jobState.getChildren().size(), is(0));

        assertThat(job.state, is(TestJob.STATE_INIT));
    }

    @Test
    public void testProcessJob_JobProducerThrowOnProcess() throws Exception {
        TestJob job = new TestJob(TestJobProducerThrowOnProcess.class);

        JobState jobState = new JobState(job, Id.nextValue(), path);
        jobState.setStatus(JobState.Status.PROCESSING);

        workerState.addJobState(jobState);

        replayAll();

        ProcessJobStateHandler handler = new ProcessJobStateHandler(workerMock);

        try {
            handler.processJobState(jobState.getJob().getId());
            fail("Expect to throw RuntimeException");
        } catch (RuntimeException e) {
            // expected
        }

        verifyAll();

        assertThat(jobState.getChildren().size(), is(0));

        assertThat(job.state, is(TestJob.STATE_INIT));
    }

    @Test
    public void testProcessJob_NoSuchJobState() throws Exception {
        TestJob job = new TestJob(TestJobProducerOneChild.class);

        JobState jobState = new JobState(job, Id.nextValue(), path);

        replayAll();

        ProcessJobStateHandler handler = new ProcessJobStateHandler(workerMock);

        handler.processJobState(jobState.getJob().getId());

        verifyAll();
    }

    @Test
    public void testProcessJob_JobStateIdleChild() throws Exception {
        TestJob job = new TestJob(TestIdleJobProducer.class);

        JobState jobState = new JobState(job, Id.nextValue(), path);
        jobState.setStatus(JobState.Status.PROCESSING);

        workerState.addJobState(jobState);

        workerMock.doSnapshot();
        expectLastCall().times(4);

        Capture<JobState> capturedJobState1 = newCapture();
        workerMock.sendNewChildrenJobMsg(EasyMock.capture(capturedJobState1));
        expectLastCall();

        Capture<JobState> capturedJobState2 = newCapture();
        workerMock.sendNewChildrenJobMsg(EasyMock.capture(capturedJobState2));
        expectLastCall();

        Capture<JobState> capturedJobState3 = newCapture();
        workerMock.sendNewChildrenJobMsg(EasyMock.capture(capturedJobState3));
        expectLastCall();

        Capture<JobState> capturedJobState4 = newCapture();
        workerMock.sendNewChildrenJobMsg(EasyMock.capture(capturedJobState4));
        expectLastCall();

        assertThat(jobState.getChildren().size(), is(0));

        replayAll();

        ProcessJobStateHandler handler = new ProcessJobStateHandler(workerMock);

        handler.processJobState(jobState.getJob().getId());
        assertThat(jobState.getChildren().size(), is(1));
        // mark first none idle child as done
        jobState.getChildren().values().iterator().next().setStatus(JobState.Status.DONE);

        handler.processJobState(jobState.getJob().getId());
        assertThat(jobState.getChildren().size(), is(2));
        assertTrue(hasIdleJob(jobState.getChildren().values()));

        handler.processJobState(jobState.getJob().getId());
        assertThat(jobState.getChildren().size(), is(2));
        assertTrue(hasIdleJob(jobState.getChildren().values()));

        handler.processJobState(jobState.getJob().getId());
        assertThat(jobState.getChildren().size(), is(2));
        assertFalse(hasIdleJob(jobState.getChildren().values()));

        verifyAll();

        assertThat(jobState.getChildren().size(), is(2));

        assertThat(job.state, is(TestIdleJobProducer.STATE_CHILD_2_CREATED));

        JobState jobState1 = capturedJobState1.getValue();
        assertThat(jobState1.getJob().getId().parentId, is(job.getId().parentId));
        JobState jobState2 = capturedJobState2.getValue();
        assertThat(jobState2.getJob().getId().parentId, is(job.getId().parentId));
        JobState jobState3 = capturedJobState3.getValue();
        assertThat(jobState3.getJob().getId().parentId, is(job.getId().parentId));
        JobState jobState4 = capturedJobState4.getValue();
        assertThat(jobState4.getJob().getId().parentId, is(job.getId().parentId));
    }

    private boolean hasIdleJob(final Collection<JobState> jobStates) {
        for (JobState jobState : jobStates) {
            if (jobState.getJob() instanceof IdleJob) {
                return true;
            }
        }
        return false;
    }
}