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
import static org.junit.Assert.fail;

import org.easymock.Capture;
import org.easymock.EasyMock;
import org.junit.Test;

import com.persinity.common.Id;
import com.persinity.haka.impl.actor.JobState;

/**
 * @author Ivan Dachev
 */
public class RootJobWatchdogHandlerTest extends RootJobHandlerTest {

    @Test
    public void testHandleWatchdog_NoJobStates() throws Exception {
        replayAll();

        RootJobWatchdogHandler handler = new RootJobWatchdogHandler(workerMock);

        handler.handleWatchdog();

        verifyAll();
    }

    @Test
    public void testHandleWatchdog_JobStateNew() throws Exception {
        TestRootJob job = new TestRootJob();

        JobState jobState = new JobState(job, Id.nextValue(), path);
        jobState.setStatus(JobState.Status.NEW);

        workerState.addJobState(jobState);

        replayAll();

        RootJobWatchdogHandler handler = new RootJobWatchdogHandler(workerMock);

        try {
            handler.handleWatchdog();
            fail("Should throw IllegalStateException");
        } catch (IllegalStateException e) {
            // expected
        }

        verifyAll();
    }

    @Test
    public void testHandleWatchdog_JobStateProcessing() throws Exception {
        TestRootJob job = new TestRootJob();

        JobState jobState = new JobState(job, Id.nextValue(), path);
        jobState.setStatus(JobState.Status.PROCESSING);

        workerState.addJobState(jobState);

        Capture<JobState> capturedJobState = newCapture();
        workerMock.cleanupJobState(EasyMock.capture(capturedJobState));

        workerMock.resendRootJob();
        expectLastCall();

        replayAll();

        RootJobWatchdogHandler handler = new RootJobWatchdogHandler(workerMock);

        // sleep to timeout job status update
        Thread.sleep(1050);

        handler.handleWatchdog();

        verifyAll();

        // verify call process for parent
        jobState = capturedJobState.getValue();
        assertThat(jobState.getJob().getId(), is(job.getId()));
        assertEquals(jobState.getJob().getJobProducerClass(), job.getJobProducerClass());
        assertThat(jobState.getStatus(), is(JobState.Status.PROCESSING));
    }

    @Test
    public void testHandleWatchdog_JobStateDone() throws Exception {
        TestRootJob job = new TestRootJob();

        JobState jobState = new JobState(job, Id.nextValue(), path);
        jobState.setStatus(JobState.Status.DONE);

        workerState.addJobState(jobState);

        Capture<JobState> capturedJobState = newCapture();
        workerMock.cleanupJobState(EasyMock.capture(capturedJobState));

        replayAll();

        RootJobWatchdogHandler handler = new RootJobWatchdogHandler(workerMock);

        // sleep to timeout job status update
        Thread.sleep(1050);

        handler.handleWatchdog();

        verifyAll();

        // verify call process for parent
        jobState = capturedJobState.getValue();
        assertThat(jobState.getJob().getId(), is(job.getId()));
        assertEquals(jobState.getJob().getJobProducerClass(), job.getJobProducerClass());
        assertThat(jobState.getStatus(), is(JobState.Status.DONE));
    }

    @Test
    public void testHandleWatchdog_JobStateDone_NoTimeout() throws Exception {
        TestRootJob job = new TestRootJob();

        JobState jobState = new JobState(job, Id.nextValue(), path);
        jobState.setStatus(JobState.Status.DONE);

        workerState.addJobState(jobState);

        replayAll();

        RootJobWatchdogHandler handler = new RootJobWatchdogHandler(workerMock);

        handler.handleWatchdog();

        verifyAll();
    }
}
