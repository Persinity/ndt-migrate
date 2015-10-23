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

import com.persinity.common.Id;
import com.persinity.haka.impl.actor.JobState;
import com.persinity.haka.impl.actor.TestJob;
import com.persinity.haka.impl.actor.TestJobProducerNoChildren;
import com.persinity.haka.impl.actor.message.ProcessedAckMsg;

/**
 * @author Ivan Dachev
 */
public class ProcessedAckMsgHandlerTest extends HandlerTest {

    @Test
    public void testHandleMsg() throws Exception {
        TestJob job = new TestJob(TestJobProducerNoChildren.class);

        JobState jobState = new JobState(job, Id.nextValue(), path);
        jobState.setStatus(JobState.Status.DONE);
        workerState.addJobState(jobState);

        ProcessedAckMsg msg = new ProcessedAckMsg(job.getId(), jobState.getSessionId());

        Capture<JobState> capturedJobState = newCapture();
        workerMock.cleanupJobState(EasyMock.capture(capturedJobState));
        expectLastCall();

        replayAll();

        ProcessedAckMsgHandler handler = new ProcessedAckMsgHandler(workerMock);

        assertEquals(handler.getMsgClass(), ProcessedAckMsg.class);

        handler.handleMsg(msg, sender);

        verifyAll();

        jobState = capturedJobState.getValue();

        assertThat(jobState.getJob().getId(), is(job.getId()));
        assertEquals(jobState.getJob().getJobProducerClass(), job.getJobProducerClass());
    }

    @Test
    public void testHandleMsg_JobState_Processing() throws Exception {
        TestJob job = new TestJob(TestJobProducerNoChildren.class);

        JobState jobState = new JobState(job, Id.nextValue(), path);
        jobState.setStatus(JobState.Status.PROCESSING);
        workerState.addJobState(jobState);

        ProcessedAckMsg msg = new ProcessedAckMsg(job.getId(), jobState.getSessionId());

        replayAll();

        ProcessedAckMsgHandler handler = new ProcessedAckMsgHandler(workerMock);

        handler.handleMsg(msg, sender);

        verifyAll();
    }

    @Test
    public void testHandleMsg_JobState_New() throws Exception {
        TestJob job = new TestJob(TestJobProducerNoChildren.class);

        JobState jobState = new JobState(job, Id.nextValue(), path);
        jobState.setStatus(JobState.Status.NEW);
        workerState.addJobState(jobState);

        ProcessedAckMsg msg = new ProcessedAckMsg(job.getId(), jobState.getSessionId());

        replayAll();

        ProcessedAckMsgHandler handler = new ProcessedAckMsgHandler(workerMock);

        handler.handleMsg(msg, sender);

        verifyAll();
    }

    @Test
    public void testHandleMsg_NoJobState() throws Exception {
        TestJob job = new TestJob(TestJobProducerNoChildren.class);

        ProcessedAckMsg msg = new ProcessedAckMsg(job.getId(), Id.nextValue());

        replayAll();

        ProcessedAckMsgHandler handler = new ProcessedAckMsgHandler(workerMock);

        handler.handleMsg(msg, sender);

        verifyAll();
    }

    @Test
    public void testHandleMsg_JobState_MismatchNewMsgOriginId() throws Exception {
        TestJob job = new TestJob(TestJobProducerNoChildren.class);

        JobState jobState = new JobState(job, Id.nextValue(), path);
        jobState.setStatus(JobState.Status.DONE);
        workerState.addJobState(jobState);

        ProcessedAckMsg msg = new ProcessedAckMsg(job.getId(), Id.nextValue());

        replayAll();

        ProcessedAckMsgHandler handler = new ProcessedAckMsgHandler(workerMock);

        handler.handleMsg(msg, sender);

        verifyAll();
    }
}