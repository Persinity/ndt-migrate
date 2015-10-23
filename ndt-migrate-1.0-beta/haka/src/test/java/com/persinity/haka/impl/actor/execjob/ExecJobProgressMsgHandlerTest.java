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
package com.persinity.haka.impl.actor.execjob;

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
import com.persinity.haka.impl.actor.TestJob;
import com.persinity.haka.impl.actor.TestJobProducerNoChildren;
import com.persinity.haka.impl.actor.message.ProgressMsg;

/**
 * @author Ivan Dachev
 */
public class ExecJobProgressMsgHandlerTest extends ExecJobHandlerTest {

    @Test
    public void testHandleMsg() throws Exception {
        TestJob job = new TestJob(TestJobProducerNoChildren.class);

        ExecJobState jobState = new ExecJobState(job, Id.nextValue(), sender);
        jobState.setStatus(JobState.Status.NEW);
        workerState.addJobState(jobState);

        replayAll();

        ExecJobProgressMsgHandler handler = new ExecJobProgressMsgHandler(workerMock);

        assertEquals(handler.getMsgClass(), ProgressMsg.class);

        ProgressMsg msg = new ProgressMsg(job.getId(), jobState.getSessionId());

        handler.handleMsg(msg, sender);

        verifyAll();

        assertThat(jobState.getStatus(), is(JobState.Status.PROCESSING));
    }

    @Test
    public void testHandleMsg_JobStateDone() throws Exception {
        TestJob job = new TestJob(TestJobProducerNoChildren.class);

        ExecJobState jobState = new ExecJobState(job, Id.nextValue(), sender);
        jobState.setStatus(JobState.Status.DONE);
        workerState.addJobState(jobState);

        Capture<ProgressMsg> capturedProgressMsg = newCapture();
        Capture<ActorRef> capturedSender = newCapture();
        workerMock.sendMsgProgressIgnored(EasyMock.capture(capturedProgressMsg), EasyMock.capture(capturedSender));
        expectLastCall();

        replayAll();

        ExecJobProgressMsgHandler handler = new ExecJobProgressMsgHandler(workerMock);

        ProgressMsg msg = new ProgressMsg(job.getId(), jobState.getSessionId());

        handler.handleMsg(msg, sender);

        verifyAll();

        assertThat(jobState.getStatus(), is(JobState.Status.DONE));

        ProgressMsg progressMsg = capturedProgressMsg.getValue();
        assertThat(progressMsg.getJobId(), is(job.getId()));

        assertThat(capturedSender.getValue(), is(sender));
    }

    @Test
    public void testHandleMsg_NoJobState() throws Exception {
        TestJob job = new TestJob(TestJobProducerNoChildren.class);

        Capture<ProgressMsg> capturedProgressMsg = newCapture();
        Capture<ActorRef> capturedSender = newCapture();
        workerMock.sendMsgProgressIgnored(EasyMock.capture(capturedProgressMsg), EasyMock.capture(capturedSender));
        expectLastCall();

        replayAll();

        ExecJobProgressMsgHandler handler = new ExecJobProgressMsgHandler(workerMock);

        ProgressMsg msg = new ProgressMsg(job.getId(), Id.nextValue());

        handler.handleMsg(msg, sender);

        verifyAll();

        ProgressMsg progressMsg = capturedProgressMsg.getValue();
        assertThat(progressMsg.getJobId(), is(job.getId()));

        assertThat(capturedSender.getValue(), is(sender));
    }
}