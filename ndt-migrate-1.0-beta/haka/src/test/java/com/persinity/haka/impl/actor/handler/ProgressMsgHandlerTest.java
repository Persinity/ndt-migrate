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
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;

import java.util.Collections;

import org.easymock.Capture;
import org.easymock.EasyMock;
import org.junit.Test;

import akka.actor.ActorRef;
import com.persinity.common.Id;
import com.persinity.haka.Job;
import com.persinity.haka.impl.actor.JobState;
import com.persinity.haka.impl.actor.TestJob;
import com.persinity.haka.impl.actor.TestJobProducerNoChildren;
import com.persinity.haka.impl.actor.message.ProgressMsg;

/**
 * @author Ivan Dachev
 */
public class ProgressMsgHandlerTest extends HandlerTest {

    @Test
    public void testHandleMsg() throws Exception {
        TestJob job = new TestJob(TestJobProducerNoChildren.class);

        JobState jobState = new JobState(job, Id.nextValue(), path);
        jobState.setStatus(JobState.Status.PROCESSING);
        workerState.addJobState(jobState);

        TestJob childJob = new TestJob(job.getId(), TestJobProducerNoChildren.class);
        jobState.appendChildren(Collections.singleton((Job) childJob));
        JobState childJobState = jobState.getChildren().get(childJob.getId().id);
        childJobState.setSessionId(Id.nextValue());

        ProgressMsg msg = new ProgressMsg(childJob.getId(), childJobState.getSessionId());

        replayAll();

        ProgressMsgHandler handler = new ProgressMsgHandler(workerMock);

        assertEquals(handler.getMsgClass(), ProgressMsg.class);

        handler.handleMsg(msg, sender);

        verifyAll();
    }

    @Test
    public void testHandleMsg_ChildJobState_Done() throws Exception {
        TestJob job = new TestJob(TestJobProducerNoChildren.class);

        JobState jobState = new JobState(job, Id.nextValue(), path);
        jobState.setStatus(JobState.Status.PROCESSING);
        workerState.addJobState(jobState);

        TestJob childJob = new TestJob(job.getId(), TestJobProducerNoChildren.class);
        jobState.appendChildren(Collections.singleton((Job) childJob));

        jobState.getChildren().get(childJob.getId().id).setStatus(JobState.Status.DONE);

        ProgressMsg msg = new ProgressMsg(childJob.getId(), Id.nextValue());

        Capture<ProgressMsg> capturedProgressMsg = newCapture();
        Capture<ActorRef> capturedSender = newCapture();
        workerMock.sendMsgProgressIgnored(EasyMock.capture(capturedProgressMsg), EasyMock.capture(capturedSender));
        expectLastCall();

        replayAll();

        ProgressMsgHandler handler = new ProgressMsgHandler(workerMock);

        assertEquals(handler.getMsgClass(), ProgressMsg.class);

        handler.handleMsg(msg, sender);

        verifyAll();

        assertThat(capturedProgressMsg.getValue().getJobId(), is(childJob.getId()));
        assertThat(capturedSender.getValue(), is(sender));
    }

    @Test
    public void testHandleMsg_ChildJobState_MismatchNewMsgId() throws Exception {
        TestJob job = new TestJob(TestJobProducerNoChildren.class);

        JobState jobState = new JobState(job, Id.nextValue(), path);
        jobState.setStatus(JobState.Status.PROCESSING);
        workerState.addJobState(jobState);

        TestJob childJob = new TestJob(job.getId(), TestJobProducerNoChildren.class);
        jobState.appendChildren(Collections.singleton((Job) childJob));
        JobState childJobState = jobState.getChildren().get(childJob.getId().id);
        childJobState.setStatus(JobState.Status.PROCESSING);
        childJobState.setSessionId(Id.nextValue());

        ProgressMsg msg = new ProgressMsg(childJob.getId(), Id.nextValue());

        Capture<ProgressMsg> capturedProgressMsg = newCapture();
        Capture<ActorRef> capturedSender = newCapture();
        workerMock.sendMsgProgressIgnored(EasyMock.capture(capturedProgressMsg), EasyMock.capture(capturedSender));
        expectLastCall();

        replayAll();

        ProgressMsgHandler handler = new ProgressMsgHandler(workerMock);

        assertEquals(handler.getMsgClass(), ProgressMsg.class);

        handler.handleMsg(msg, sender);

        verifyAll();

        assertThat(capturedProgressMsg.getValue().getJobId(), is(childJob.getId()));
        assertThat(capturedSender.getValue(), is(sender));
    }

    @Test
    public void testHandleMsg_NoChildJobState() throws Exception {
        TestJob job = new TestJob(TestJobProducerNoChildren.class);

        JobState jobState = new JobState(job, Id.nextValue(), path);
        jobState.setStatus(JobState.Status.PROCESSING);
        workerState.addJobState(jobState);

        TestJob childJob = new TestJob(job.getId(), TestJobProducerNoChildren.class);

        ProgressMsg msg = new ProgressMsg(childJob.getId(), Id.nextValue());

        Capture<ProgressMsg> capturedProgressMsg = newCapture();
        Capture<ActorRef> capturedSender = newCapture();
        workerMock.sendMsgProgressIgnored(EasyMock.capture(capturedProgressMsg), EasyMock.capture(capturedSender));
        expectLastCall();

        replayAll();

        ProgressMsgHandler handler = new ProgressMsgHandler(workerMock);

        assertEquals(handler.getMsgClass(), ProgressMsg.class);

        handler.handleMsg(msg, sender);

        verifyAll();

        assertThat(capturedProgressMsg.getValue().getJobId(), is(childJob.getId()));
        assertThat(capturedSender.getValue(), is(sender));
    }

    @Test
    public void testHandleMsg_NoParentJobState() throws Exception {
        TestJob childJob = new TestJob(TestJobProducerNoChildren.class);

        ProgressMsg msg = new ProgressMsg(childJob.getId(), Id.nextValue());

        Capture<ProgressMsg> capturedProgressMsg = newCapture();
        Capture<ActorRef> capturedSender = newCapture();
        workerMock.sendMsgProgressIgnored(EasyMock.capture(capturedProgressMsg), EasyMock.capture(capturedSender));
        expectLastCall();

        replayAll();

        ProgressMsgHandler handler = new ProgressMsgHandler(workerMock);

        assertEquals(handler.getMsgClass(), ProgressMsg.class);

        handler.handleMsg(msg, sender);

        verifyAll();

        assertThat(capturedProgressMsg.getValue().getJobId(), is(childJob.getId()));
        assertThat(capturedSender.getValue(), is(sender));
    }
}