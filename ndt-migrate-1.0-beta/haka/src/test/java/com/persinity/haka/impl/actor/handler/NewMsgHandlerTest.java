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

import org.easymock.Capture;
import org.easymock.EasyMock;
import org.junit.Test;

import akka.actor.ActorRef;
import com.persinity.common.Id;
import com.persinity.haka.JobIdentity;
import com.persinity.haka.impl.actor.JobState;
import com.persinity.haka.impl.actor.TestJob;
import com.persinity.haka.impl.actor.TestJobProducerNoChildren;
import com.persinity.haka.impl.actor.message.NewMsg;

/**
 * @author Ivan Dachev
 */
public class NewMsgHandlerTest extends HandlerTest {

    @Test
    public void testHandleMsg() throws Exception {
        TestJob job = new TestJob(TestJobProducerNoChildren.class);

        EasyMock.expect(settingsMock.getMaxJobsPerWorker()).andReturn(1).anyTimes();

        NewMsg msg = new NewMsg(job);

        Capture<JobIdentity> capturedJobStateId = newCapture();
        workerMock.sendProcessJobStateMsg(EasyMock.capture(capturedJobStateId));
        expectLastCall();

        replayAll();

        // to test the construct for making helper by worker only
        @SuppressWarnings("UnusedAssignment")
        NewMsgHandler handler = new NewMsgHandler(workerMock);

        handler = new NewMsgHandler(workerMock);

        assertEquals(handler.getMsgClass(), NewMsg.class);

        handler.handleMsg(msg, sender);

        verifyAll();

        JobIdentity jobStateId = capturedJobStateId.getValue();
        assertThat(jobStateId, is(job.getId()));
    }

    @Test
    public void testHandleMsg_MaxWorkersHit_ResendNewMsg_to_Pool() throws Exception {
        TestJob job = new TestJob(TestJobProducerNoChildren.class);

        EasyMock.expect(settingsMock.getMaxJobsPerWorker()).andReturn(1).anyTimes();

        TestJob jobExisting = new TestJob(TestJobProducerNoChildren.class);
        JobState jobState = new JobState(jobExisting, jobExisting.getId().id, null);
        workerState.addJobState(jobState);

        NewMsg msg = new NewMsg(job);

        Capture<NewMsg> capturedNewMsg = newCapture();
        Capture<ActorRef> capturedSender = newCapture();
        workerMock.resendMsgNew(EasyMock.capture(capturedNewMsg), EasyMock.capture(capturedSender));
        expectLastCall();

        replayAll();

        NewMsgHandler handler = new NewMsgHandler(workerMock);

        handler.handleMsg(msg, sender);

        verifyAll();

        NewMsg msgSend = capturedNewMsg.getValue();
        assertThat(msg, is(msgSend));
    }

    @Test
    public void testHandleMsg_AlreadyProcessingSameJob() throws Exception {
        TestJob job = new TestJob(TestJobProducerNoChildren.class);

        JobState jobState = new JobState(job, Id.nextValue(), path);
        workerState.addJobState(jobState);

        EasyMock.expect(settingsMock.getMaxJobsPerWorker()).andReturn(2).anyTimes();

        NewMsg msg = new NewMsg(job);

        replayAll();

        NewMsgHandler handler = new NewMsgHandler(workerMock);

        handler.handleMsg(msg, sender);

        verifyAll();
    }

}