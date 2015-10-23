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
package com.persinity.haka.impl.actor.rootjob;

import static org.easymock.EasyMock.expectLastCall;
import static org.easymock.EasyMock.newCapture;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;

import org.easymock.Capture;
import org.easymock.EasyMock;
import org.junit.Test;

import akka.actor.ActorRef;
import com.persinity.common.Id;
import com.persinity.haka.impl.actor.JobState;
import com.persinity.haka.impl.actor.execjob.ExecJobState;
import com.persinity.haka.impl.actor.message.ProcessedMsg;

/**
 * @author Ivan Dachev
 */
public class RootJobProcessedMsgHandlerTest extends RootJobHandlerTest {

    @Test
    public void testHandleMsg() throws Exception {
        TestRootJob job = new TestRootJob();

        ExecJobState jobState = new ExecJobState(job, Id.nextValue(), sender);
        jobState.setStatus(JobState.Status.PROCESSING);
        workerState.addJobState(jobState);

        Capture<ProcessedMsg> capturedProcessedMsgFromAck = newCapture();
        Capture<ActorRef> capturedSender = newCapture();
        workerMock.sendMsgProcessedAck(EasyMock.capture(capturedProcessedMsgFromAck), EasyMock.capture(capturedSender));
        expectLastCall();

        workerMock.doSnapshot();
        expectLastCall();

        replayAll();

        RootJobProcessedMsgHandler handler = new RootJobProcessedMsgHandler(workerMock);

        assertEquals(handler.getMsgClass(), ProcessedMsg.class);

        ProcessedMsg msg = new ProcessedMsg(job, jobState.getSessionId(), true);

        handler.handleMsg(msg, sender);

        verifyAll();

        // verify processed msg ack for job
        ProcessedMsg processedMsg = capturedProcessedMsgFromAck.getValue();
        assertThat(processedMsg.getJob().getId(), is(job.getId()));
        assertEquals(processedMsg.getJob().getJobProducerClass(), job.getJobProducerClass());
        assertThat(processedMsg.needAck(), is(true));

        assertThat(capturedSender.getValue(), is(sender));

        assertThat(jobState.getStatus(), is(JobState.Status.DONE));

        assertThat(workerState.getJobStates().size(), is(0));
    }

    @Test
    public void testHandleMsg_NoJobState() throws Exception {
        TestRootJob job = new TestRootJob();

        Capture<ProcessedMsg> capturedProcessedMsgFromAck = newCapture();
        Capture<ActorRef> capturedSender = newCapture();
        workerMock.sendMsgProcessedAck(EasyMock.capture(capturedProcessedMsgFromAck), EasyMock.capture(capturedSender));
        expectLastCall();

        replayAll();

        RootJobProcessedMsgHandler handler = new RootJobProcessedMsgHandler(workerMock);

        ProcessedMsg msg = new ProcessedMsg(job, Id.nextValue(), true);

        handler.handleMsg(msg, sender);

        verifyAll();

        // verify processed msg ack for job
        ProcessedMsg processedMsg = capturedProcessedMsgFromAck.getValue();
        assertThat(processedMsg.getJob().getId(), is(job.getId()));
        assertEquals(processedMsg.getJob().getJobProducerClass(), job.getJobProducerClass());
        assertThat(processedMsg.needAck(), is(true));

        assertThat(capturedSender.getValue(), is(sender));

        assertThat(workerState.getJobStates().size(), is(0));
    }

    @Test
    public void testHandleMsg_JobStateDone() throws Exception {
        TestRootJob job = new TestRootJob();

        ExecJobState jobState = new ExecJobState(job, Id.nextValue(), sender);
        jobState.setStatus(JobState.Status.DONE);
        workerState.addJobState(jobState);

        Capture<ProcessedMsg> capturedProcessedMsgFromAck = newCapture();
        Capture<ActorRef> capturedSender = newCapture();
        workerMock.sendMsgProcessedAck(EasyMock.capture(capturedProcessedMsgFromAck), EasyMock.capture(capturedSender));
        expectLastCall();

        replayAll();

        RootJobProcessedMsgHandler handler = new RootJobProcessedMsgHandler(workerMock);

        assertEquals(handler.getMsgClass(), ProcessedMsg.class);

        ProcessedMsg msg = new ProcessedMsg(job, jobState.getSessionId(), true);

        handler.handleMsg(msg, sender);

        verifyAll();

        // verify processed msg ack for job
        ProcessedMsg processedMsg = capturedProcessedMsgFromAck.getValue();
        assertThat(processedMsg.getJob().getId(), is(job.getId()));
        assertEquals(processedMsg.getJob().getJobProducerClass(), job.getJobProducerClass());
        assertThat(processedMsg.needAck(), is(true));

        assertThat(capturedSender.getValue(), is(sender));

        assertThat(jobState.getStatus(), is(JobState.Status.DONE));

        assertThat(workerState.getJobStates().size(), is(1));
    }
}