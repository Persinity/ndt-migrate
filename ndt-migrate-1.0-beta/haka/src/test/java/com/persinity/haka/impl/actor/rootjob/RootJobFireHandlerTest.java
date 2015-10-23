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

import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.expectLastCall;
import static org.easymock.EasyMock.newCapture;
import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;

import org.easymock.Capture;
import org.easymock.EasyMock;
import org.junit.Test;

import akka.actor.ActorRef;
import com.persinity.common.Id;
import com.persinity.haka.impl.actor.JobState;
import com.persinity.haka.impl.actor.execjob.ExecJobState;
import com.persinity.haka.impl.actor.message.Msg;
import com.persinity.haka.impl.actor.message.NewMsg;

/**
 * @author Ivan Dachev
 */
public class RootJobFireHandlerTest extends RootJobHandlerTest {

    @Test
    public void testHandleFire() throws Exception {
        expect(rootJobSettings.getJobClass()).andReturn(TestRootJob.class.getName());

        Capture<String> capturedPath = newCapture();
        Capture<Msg> capturedMsg = newCapture();
        Capture<ActorRef> capturedSrcRef = newCapture();
        workerMock.sendMsg(EasyMock.capture(capturedPath), EasyMock.capture(capturedMsg),
                EasyMock.capture(capturedSrcRef));
        expectLastCall();

        workerMock.doSnapshot();
        expectLastCall();

        replayAll();

        RootJobFireHandler handler = new RootJobFireHandler(workerMock);

        handler.handleFire();

        verifyAll();

        assertThat(capturedPath.getValue(), is("/user/supervisor"));
        assertEquals(((NewMsg) capturedMsg.getValue()).getJob().getJobProducerClass(), TestRootJobProducer.class);
        assertThat(capturedSrcRef.getValue(), is(self));
    }

    @Test
    public void testHandleFire_AlreadyProcessingRootJob() throws Exception {
        TestRootJob job = new TestRootJob();

        ExecJobState jobState = new ExecJobState(job, Id.nextValue(), sender);
        jobState.setStatus(JobState.Status.PROCESSING);
        workerState.addJobState(jobState);

        replayAll();

        RootJobFireHandler handler = new RootJobFireHandler(workerMock);

        handler.handleFire();

        verifyAll();
    }

    @Test
    public void testHandleFire_InvalidRootJob() throws Exception {
        expect(rootJobSettings.getJobClass()).andReturn("Invalid" + System.currentTimeMillis());

        replayAll();

        RootJobFireHandler handler = new RootJobFireHandler(workerMock);

        try {
            handler.handleFire();
            fail("Should throw ReflectiveOperationException");
        } catch (RuntimeException e) {
            assertThat(e.getCause(), instanceOf(ReflectiveOperationException.class));
        }

        verifyAll();
    }
}