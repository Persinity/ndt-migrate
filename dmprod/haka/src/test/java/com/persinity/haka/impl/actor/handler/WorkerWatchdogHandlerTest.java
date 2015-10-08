/**
 * Copyright (c) 2015 Persinity Inc.
 */
package com.persinity.haka.impl.actor.handler;

import static org.easymock.EasyMock.expectLastCall;
import static org.easymock.EasyMock.newCapture;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;

import java.util.Collections;

import org.easymock.Capture;
import org.easymock.EasyMock;
import org.junit.Test;

import com.persinity.common.Id;
import com.persinity.haka.Job;
import com.persinity.haka.JobIdentity;
import com.persinity.haka.impl.actor.JobState;
import com.persinity.haka.impl.actor.TestJob;
import com.persinity.haka.impl.actor.TestJobProducerNoChildren;
import com.persinity.haka.impl.actor.TestJobProducerOneChild;

/**
 * @author Ivan Dachev
 */
public class WorkerWatchdogHandlerTest extends HandlerTest {

    @Test
    public void testHandleWatchdog_NoJobStates() throws Exception {
        replayAll();

        final WorkerWatchdogHandler handler = new WorkerWatchdogHandler(workerMock);

        handler.handleWatchdog();

        verifyAll();
    }

    @Test
    public void testHandleWatchdog_JobStateNew() throws Exception {
        TestJob job = new TestJob(TestJobProducerOneChild.class);

        JobState jobState = new JobState(job, Id.nextValue(), path);
        jobState.setStatus(JobState.Status.NEW);

        workerState.addJobState(jobState);

        replayAll();

        WorkerWatchdogHandler handler = new WorkerWatchdogHandler(workerMock);

        handler.handleWatchdog();

        verifyAll();
    }

    @Test
    public void testHandleWatchdog_JobStateProcessing_ChildrenDone() throws Exception {
        TestJob job = new TestJob(TestJobProducerOneChild.class);

        JobState jobState = new JobState(job, Id.nextValue(), path);
        jobState.setStatus(JobState.Status.PROCESSING);

        workerState.addJobState(jobState);

        Capture<JobIdentity> capturedJobStateId = newCapture();
        workerMock.sendProcessJobStateMsg(EasyMock.capture(capturedJobStateId));
        expectLastCall();

        replayAll();

        WorkerWatchdogHandler handler = new WorkerWatchdogHandler(workerMock);

        handler.handleWatchdog();

        verifyAll();

        JobIdentity jobStateId = capturedJobStateId.getValue();
        assertThat(jobStateId, is(job.getId()));
    }

    @Test
    public void testHandleWatchdog_ChildrenInNewAndProcessingTimeout() throws Exception {
        TestJob job = new TestJob(TestJobProducerOneChild.class);

        JobState jobState = new JobState(job, Id.nextValue(), path);
        jobState.setStatus(JobState.Status.PROCESSING);

        TestJob childJobNew = new TestJob(job.getId(), TestJobProducerNoChildren.class);
        jobState.appendChildren(Collections.singleton((Job) childJobNew));

        TestJob childJobProcessing = new TestJob(job.getId(), TestJobProducerNoChildren.class);
        jobState.appendChildren(Collections.singleton((Job) childJobProcessing));
        jobState.getChildren().get(childJobProcessing.getId().id).setStatus(JobState.Status.PROCESSING);

        workerState.addJobState(jobState);

        Capture<JobState> capturedJobState = newCapture();
        workerMock.sendMsgProgress(EasyMock.capture(capturedJobState));
        expectLastCall();

        Capture<JobState> capturedJobStateChild1 = newCapture();
        workerMock.sendNewChildJobMsg(EasyMock.capture(capturedJobStateChild1));
        expectLastCall();

        Capture<JobState> capturedJobStateChild2 = newCapture();
        workerMock.sendNewChildJobMsg(EasyMock.capture(capturedJobStateChild2));
        expectLastCall();

        replayAll();

        WorkerWatchdogHandler handler = new WorkerWatchdogHandler(workerMock);

        // sleep to timeout child job status update
        Thread.sleep(1050);

        handler.handleWatchdog();

        verifyAll();

        // verify call process for parent
        jobState = capturedJobState.getValue();
        assertThat(jobState.getJob().getId(), is(job.getId()));
        assertEquals(jobState.getJob().getJobProducerClass(), job.getJobProducerClass());
        assertThat(jobState.getStatus(), is(JobState.Status.PROCESSING));

        // verify resend new children msgs
        JobState jobStateNew = null;
        JobState jobStateProcessing = null;

        jobState = capturedJobStateChild1.getValue();
        if (jobState.getJob().getId().equals(childJobNew.getId())) {
            jobStateNew = jobState;
        } else {
            jobStateProcessing = jobState;
        }

        jobState = capturedJobStateChild2.getValue();
        if (jobState.getJob().getId().equals(childJobNew.getId())) {
            jobStateNew = jobState;
        } else {
            jobStateProcessing = jobState;
        }

        assertThat(jobStateNew, notNullValue());
        assertThat(jobStateProcessing, notNullValue());

        assertThat(jobStateProcessing.getJob().getId(), is(childJobProcessing.getId()));
        assertEquals(jobStateProcessing.getJob().getJobProducerClass(), childJobProcessing.getJobProducerClass());
        assertThat(jobStateProcessing.getStatus(), is(JobState.Status.NEW));

        assertThat(jobStateNew.getJob().getId(), is(childJobNew.getId()));
        assertEquals(jobStateNew.getJob().getJobProducerClass(), childJobNew.getJobProducerClass());
        assertThat(jobStateNew.getStatus(), is(JobState.Status.NEW));
    }

    @Test
    public void testHandleWatchdog_JobStateDone() throws Exception {
        TestJob job = new TestJob(TestJobProducerOneChild.class);

        JobState jobState = new JobState(job, Id.nextValue(), path);
        jobState.setStatus(JobState.Status.DONE);

        workerState.addJobState(jobState);

        Capture<JobState> capturedJobState = newCapture();
        Capture<Boolean> capturedNeedAck = newCapture();
        workerMock.sendMsgProcessed(EasyMock.capture(capturedJobState), EasyMock.captureBoolean(capturedNeedAck));
        expectLastCall();

        replayAll();

        WorkerWatchdogHandler handler = new WorkerWatchdogHandler(workerMock);

        // sleep to timeout job status update
        Thread.sleep(1050);

        handler.handleWatchdog();

        verifyAll();

        // verify call process for parent
        jobState = capturedJobState.getValue();
        assertThat(jobState.getJob().getId(), is(job.getId()));
        assertEquals(jobState.getJob().getJobProducerClass(), job.getJobProducerClass());
        assertThat(jobState.getStatus(), is(JobState.Status.DONE));

        assertThat(capturedNeedAck.getValue(), is(true));
    }

    @Test
    public void testHandleWatchdog_JobStateDone_NoTimeout() throws Exception {
        TestJob job = new TestJob(TestJobProducerOneChild.class);

        JobState jobState = new JobState(job, Id.nextValue(), path);
        jobState.setStatus(JobState.Status.DONE);

        workerState.addJobState(jobState);

        replayAll();

        WorkerWatchdogHandler handler = new WorkerWatchdogHandler(workerMock);

        handler.handleWatchdog();

        verifyAll();
    }
}
