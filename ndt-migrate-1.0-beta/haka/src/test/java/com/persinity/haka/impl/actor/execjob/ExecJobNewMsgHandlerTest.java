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

import java.util.HashMap;

import org.easymock.Capture;
import org.easymock.EasyMock;
import org.junit.Test;

import com.persinity.common.Id;
import com.persinity.haka.impl.actor.JobState;
import com.persinity.haka.impl.actor.TestJob;
import com.persinity.haka.impl.actor.TestJobProducerNoChildren;
import com.persinity.haka.impl.actor.message.NewMsg;

/**
 * @author Ivan Dachev
 */
public class ExecJobNewMsgHandlerTest extends ExecJobHandlerTest {

    @Test
    public void testHandleMsg() throws Exception {
        TestJob job = new TestJob(TestJobProducerNoChildren.class);

        NewMsg msg = new NewMsg(job);

        Capture<NewMsg> capturedNewMsg = newCapture();
        workerMock.sendMsgToPool(EasyMock.capture(capturedNewMsg));
        expectLastCall();

        replayAll();

        ExecJobNewMsgHandler handler = new ExecJobNewMsgHandler(workerMock);

        assertEquals(handler.getMsgClass(), NewMsg.class);

        handler.handleMsg(msg, sender);

        verifyAll();

        NewMsg capturedMsg = capturedNewMsg.getValue();

        assertThat(capturedMsg.getJob().getId(), is(job.getId()));
        assertEquals(capturedMsg.getJob().getJobProducerClass(), job.getJobProducerClass());

        HashMap<Id, JobState> states = workerMock.getState().getJobStates();
        assertThat(states.size(), is(1));

        JobState jobState = states.get(job.getId().id);
        assertThat(jobState.getJob().getId(), is(job.getId()));
        assertThat(jobState.getStatus(), is(JobState.Status.PROCESSING));
    }
}