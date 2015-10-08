/**
 * Copyright (c) 2015 Persinity Inc.
 */
package com.persinity.haka.impl.actor.rootjob;

import com.persinity.common.Id;
import com.persinity.haka.impl.actor.JobState;
import org.easymock.Capture;
import org.easymock.EasyMock;
import org.junit.Test;

import static org.easymock.EasyMock.expectLastCall;
import static org.easymock.EasyMock.newCapture;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.*;

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
