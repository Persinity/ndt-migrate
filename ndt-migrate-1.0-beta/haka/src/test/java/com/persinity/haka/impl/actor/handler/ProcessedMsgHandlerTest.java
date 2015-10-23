/*
 * Copyright 2015 Persinity Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.persinity.haka.impl.actor.handler;

import static org.easymock.EasyMock.expectLastCall;
import static org.easymock.EasyMock.newCapture;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;

import java.util.Collections;

import org.easymock.Capture;
import org.easymock.EasyMock;
import org.junit.Test;

import akka.actor.ActorRef;
import com.persinity.common.Id;
import com.persinity.haka.Job;
import com.persinity.haka.JobIdentity;
import com.persinity.haka.impl.actor.JobState;
import com.persinity.haka.impl.actor.TestChildJob;
import com.persinity.haka.impl.actor.TestChildJobProducer;
import com.persinity.haka.impl.actor.TestJob;
import com.persinity.haka.impl.actor.TestJobProducerNoChildren;
import com.persinity.haka.impl.actor.TestJobProducerOneChild;
import com.persinity.haka.impl.actor.TestJobProducerThrowOnProcessed;
import com.persinity.haka.impl.actor.message.ProcessedMsg;

/**
 * @author Ivan Dachev
 */
public class ProcessedMsgHandlerTest extends HandlerTest {

    @Test
    public void testHandleMsg() throws Exception {
        TestJob job = new TestJob(TestJobProducerOneChild.class);

        JobState jobState = new JobState(job, Id.nextValue(), path);
        jobState.setStatus(JobState.Status.PROCESSING);
        workerState.addJobState(jobState);

        TestChildJob childJob = new TestChildJob(job, TestJobProducerNoChildren.class);
        jobState.appendChildren(Collections.singleton((Job) childJob));

        childJob.state = TestChildJobProducer.STATE_CHILD_DONE;
        JobState childJobState = jobState.getChildren().get(childJob.getId().id);
        childJobState.setSessionId(Id.nextValue());

        job.state = TestJobProducerOneChild.STATE_ONE_CHILD_CREATED;

        Capture<ProcessedMsg> capturedProcessedMsgFromAck = newCapture();
        Capture<ActorRef> capturedSender = newCapture();
        workerMock.sendMsgProcessedAck(EasyMock.capture(capturedProcessedMsgFromAck), EasyMock.capture(capturedSender));
        expectLastCall();

        Capture<JobIdentity> capturedJobStateId = newCapture();
        workerMock.sendProcessJobStateMsg(EasyMock.capture(capturedJobStateId));
        expectLastCall();

        workerMock.doSnapshot();
        expectLastCall().times(1);

        replayAll();

        // to test constructor with worker only
        @SuppressWarnings("UnusedAssignment")
        ProcessedMsgHandler handler = new ProcessedMsgHandler(workerMock);

        handler = new ProcessedMsgHandler(workerMock);

        assertEquals(handler.getMsgClass(), ProcessedMsg.class);

        ProcessedMsg msg = new ProcessedMsg(childJob, childJobState.getSessionId(), true);

        handler.handleMsg(msg, sender);

        verifyAll();

        // verify processed msg ack for child
        ProcessedMsg processedMsg = capturedProcessedMsgFromAck.getValue();
        assertThat(processedMsg.getJob().getId(), is(childJob.getId()));
        assertEquals(processedMsg.getJob().getJobProducerClass(), childJob.getJobProducerClass());
        assertThat(processedMsg.needAck(), is(true));

        assertThat(capturedSender.getValue(), is(sender));

        // verify call process for parent
        JobIdentity jobStateId = capturedJobStateId.getValue();
        assertThat(jobStateId, is(job.getId()));

        jobState = jobState.getChildren().get(childJob.getId().id);
        assertThat(jobState, nullValue());
    }

    @Test
    public void testHandleMsg_NoJobStateForParent() throws Exception {
        TestJob childJob = new TestJob(TestJobProducerNoChildren.class);

        ProcessedMsg msg = new ProcessedMsg(childJob, Id.nextValue(), true);

        Capture<ProcessedMsg> capturedProcessedMsgFromAck = newCapture();
        Capture<ActorRef> capturedSender = newCapture();
        workerMock.sendMsgProcessedAck(EasyMock.capture(capturedProcessedMsgFromAck), EasyMock.capture(capturedSender));
        expectLastCall();

        replayAll();

        ProcessedMsgHandler handler = new ProcessedMsgHandler(workerMock);

        handler.handleMsg(msg, sender);

        verifyAll();

        // verify processed msg ack for child
        ProcessedMsg processedMsg = capturedProcessedMsgFromAck.getValue();
        assertThat(processedMsg.getJob().getId(), is(childJob.getId()));
        assertEquals(processedMsg.getJob().getJobProducerClass(), childJob.getJobProducerClass());
        assertThat(processedMsg.needAck(), is(true));
    }

    @Test
    public void testHandleMsg_NoJobStateForChild() throws Exception {
        TestJob job = new TestJob(TestJobProducerNoChildren.class);

        JobState jobState = new JobState(job, Id.nextValue(), path);
        jobState.setStatus(JobState.Status.PROCESSING);
        workerState.addJobState(jobState);

        TestJob childJob = new TestJob(job.getId(), TestJobProducerNoChildren.class);

        ProcessedMsg msg = new ProcessedMsg(childJob, Id.nextValue(), true);

        Capture<ProcessedMsg> capturedProcessedMsgFromAck = newCapture();
        Capture<ActorRef> capturedSender = newCapture();
        workerMock.sendMsgProcessedAck(EasyMock.capture(capturedProcessedMsgFromAck), EasyMock.capture(capturedSender));
        expectLastCall();

        replayAll();

        ProcessedMsgHandler handler = new ProcessedMsgHandler(workerMock);

        handler.handleMsg(msg, sender);

        verifyAll();

        // verify processed msg ack for child
        ProcessedMsg processedMsg = capturedProcessedMsgFromAck.getValue();
        assertThat(processedMsg.getJob().getId(), is(childJob.getId()));
        assertEquals(processedMsg.getJob().getJobProducerClass(), childJob.getJobProducerClass());
        assertThat(processedMsg.needAck(), is(true));
    }

    @Test
    public void testHandleMsg_JobStateForChildIsDone() throws Exception {
        TestJob job = new TestJob(TestJobProducerNoChildren.class);

        JobState jobState = new JobState(job, Id.nextValue(), path);
        jobState.setStatus(JobState.Status.PROCESSING);
        workerState.addJobState(jobState);

        TestJob childJob = new TestJob(job.getId(), TestJobProducerNoChildren.class);
        jobState.appendChildren(Collections.singleton((Job) childJob));

        jobState.getChildren().get(childJob.getId().id).setStatus(JobState.Status.DONE);

        ProcessedMsg msg = new ProcessedMsg(childJob, Id.nextValue(), true);

        Capture<ProcessedMsg> capturedProcessedMsgFromAck = newCapture();
        Capture<ActorRef> capturedSender = newCapture();
        workerMock.sendMsgProcessedAck(EasyMock.capture(capturedProcessedMsgFromAck), EasyMock.capture(capturedSender));
        expectLastCall();

        replayAll();

        ProcessedMsgHandler handler = new ProcessedMsgHandler(workerMock);

        handler.handleMsg(msg, sender);

        verifyAll();

        // verify processed msg ack for child
        ProcessedMsg processedMsg = capturedProcessedMsgFromAck.getValue();
        assertThat(processedMsg.getJob().getId(), is(childJob.getId()));
        assertEquals(processedMsg.getJob().getJobProducerClass(), childJob.getJobProducerClass());
        assertThat(processedMsg.needAck(), is(true));
    }

    @Test
    public void testHandleMsg_JobStateForChildMismatchNewMsgIdOrigin() throws Exception {
        TestJob job = new TestJob(TestJobProducerNoChildren.class);

        JobState jobState = new JobState(job, Id.nextValue(), path);
        jobState.setStatus(JobState.Status.PROCESSING);
        workerState.addJobState(jobState);

        TestJob childJob = new TestJob(job.getId(), TestJobProducerNoChildren.class);
        jobState.appendChildren(Collections.singleton((Job) childJob));
        JobState childJobState = jobState.getChildren().get(childJob.getId().id);
        childJobState.setSessionId(Id.nextValue());

        jobState.getChildren().get(childJob.getId().id).setStatus(JobState.Status.PROCESSING);

        ProcessedMsg msg = new ProcessedMsg(childJob, Id.nextValue(), true);

        Capture<ProcessedMsg> capturedProcessedMsgFromAck = newCapture();
        Capture<ActorRef> capturedSender = newCapture();
        workerMock.sendMsgProcessedAck(EasyMock.capture(capturedProcessedMsgFromAck), EasyMock.capture(capturedSender));
        expectLastCall();

        replayAll();

        ProcessedMsgHandler handler = new ProcessedMsgHandler(workerMock);

        handler.handleMsg(msg, sender);

        verifyAll();

        // verify processed msg ack for child
        ProcessedMsg processedMsg = capturedProcessedMsgFromAck.getValue();
        assertThat(processedMsg.getJob().getId(), is(childJob.getId()));
        assertEquals(processedMsg.getJob().getJobProducerClass(), childJob.getJobProducerClass());
        assertThat(processedMsg.needAck(), is(true));
    }

    @Test
    public void testHandleMsg_JobProducerThrowOnProcessed() throws Exception {
        TestJob job = new TestJob(TestJobProducerThrowOnProcessed.class);

        JobState jobState = new JobState(job, Id.nextValue(), path);
        jobState.setStatus(JobState.Status.PROCESSING);
        workerState.addJobState(jobState);

        TestJob childJob = new TestJob(job.getId(), TestJobProducerThrowOnProcessed.class);
        jobState.appendChildren(Collections.singleton((Job) childJob));

        JobState childJobState = jobState.getChildren().get(childJob.getId().id);
        childJobState.setStatus(JobState.Status.PROCESSING);
        childJobState.setSessionId(Id.nextValue());

        ProcessedMsg msg = new ProcessedMsg(childJob, childJobState.getSessionId(), true);

        replayAll();

        ProcessedMsgHandler handler = new ProcessedMsgHandler(workerMock);

        try {
            handler.handleMsg(msg, sender);
            fail("Expected to throw RuntimeException");
        } catch (RuntimeException e) {
            // expected
        }

        verifyAll();
    }
}