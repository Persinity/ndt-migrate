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
import static org.junit.Assert.assertThat;

import org.easymock.Capture;
import org.easymock.EasyMock;
import org.junit.Test;

import com.google.common.collect.Sets;
import com.persinity.common.Id;
import com.persinity.haka.Job;
import com.persinity.haka.JobIdentity;
import com.persinity.haka.impl.actor.JobState;
import com.persinity.haka.impl.actor.TestChildJobProducer;
import com.persinity.haka.impl.actor.TestIdleJob;
import com.persinity.haka.impl.actor.TestJob;
import com.persinity.haka.impl.actor.TestJobProducerOneChild;

/**
 * @author Ivan Dachev
 */
public class WorkerIdleJobHandlerTest extends HandlerTest {

    @Test
    public void testHandleWatchdog_NoJobStates() throws Exception {
        replayAll();

        final WorkerIdleJobHandler handler = new WorkerIdleJobHandler(workerMock);

        handler.handleIdleJob();

        verifyAll();
    }

    @Test
    public void testHandleWatchdog_JobStateIdleJob_AllStates() throws Exception {
        final TestJob jobParent = new TestJob(TestJobProducerOneChild.class);
        final TestIdleJob jobN = new TestIdleJob(jobParent, TestChildJobProducer.class);
        final TestIdleJob jobP = new TestIdleJob(jobParent, TestChildJobProducer.class);
        final TestIdleJob jobD = new TestIdleJob(jobParent, TestChildJobProducer.class);

        final JobState jobStateParent = new JobState(jobParent, Id.nextValue(), path);

        workerState.addJobState(jobStateParent);
        jobStateParent.appendChildren(Sets.<Job>newHashSet(jobN, jobP, jobD));

        Capture<JobIdentity> capturedJobStateId1 = newCapture();
        workerMock.sendProcessJobStateMsg(EasyMock.capture(capturedJobStateId1));
        expectLastCall();

        Capture<JobIdentity> capturedJobStateId2 = newCapture();
        workerMock.sendProcessJobStateMsg(EasyMock.capture(capturedJobStateId2));
        expectLastCall();

        Capture<JobIdentity> capturedJobStateId3 = newCapture();
        workerMock.sendProcessJobStateMsg(EasyMock.capture(capturedJobStateId3));
        expectLastCall();

        replayAll();

        final WorkerIdleJobHandler handler = new WorkerIdleJobHandler(workerMock);

        handler.handleIdleJob();

        verifyAll();

        JobIdentity jobStateId = capturedJobStateId1.getValue();
        assertThat(jobStateId, is(jobParent.getId()));
    }

    @Test
    public void testHandleWatchdog_JobStateNoIdleJob() throws Exception {
        TestJob job = new TestJob(TestJobProducerOneChild.class);

        JobState jobState = new JobState(job, Id.nextValue(), path);

        workerState.addJobState(jobState);

        replayAll();

        final WorkerIdleJobHandler handler = new WorkerIdleJobHandler(workerMock);

        handler.handleIdleJob();

        verifyAll();
    }
}
