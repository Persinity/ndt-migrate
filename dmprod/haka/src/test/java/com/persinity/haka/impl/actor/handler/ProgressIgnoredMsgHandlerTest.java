/**
 * Copyright (c) 2015 Persinity Inc.
 */
package com.persinity.haka.impl.actor.handler;

import com.persinity.common.Id;
import com.persinity.haka.impl.actor.JobState;
import com.persinity.haka.impl.actor.TestJob;
import com.persinity.haka.impl.actor.TestJobProducerNoChildren;
import com.persinity.haka.impl.actor.message.ProgressIgnoredMsg;
import org.easymock.Capture;
import org.easymock.EasyMock;
import org.junit.Test;

import static org.easymock.EasyMock.expectLastCall;
import static org.easymock.EasyMock.newCapture;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;

/**
 * @author Ivan Dachev
 */
public class ProgressIgnoredMsgHandlerTest extends HandlerTest {

	@Test
	public void testHandleMsg() throws Exception {
		TestJob job = new TestJob(TestJobProducerNoChildren.class);

		JobState jobState = new JobState(job, Id.nextValue(), path);
		jobState.setStatus(JobState.Status.PROCESSING);
		workerState.addJobState(jobState);

		ProgressIgnoredMsg msg = new ProgressIgnoredMsg(job.getId(), jobState.getSessionId());

		Capture<JobState> capturedJobState = newCapture();
		workerMock.cleanupJobState(EasyMock.capture(capturedJobState));
		expectLastCall();

		replayAll();

		ProgressIgnoredMsgHandler handler = new ProgressIgnoredMsgHandler(workerMock);

		assertEquals(handler.getMsgClass(), ProgressIgnoredMsg.class);

		handler.handleMsg(msg, sender);

		verifyAll();

		jobState = capturedJobState.getValue();
		assertThat(jobState.getJob().getId(), is(job.getId()));
		assertEquals(jobState.getJob().getJobProducerClass(), job.getJobProducerClass());
		assertThat(jobState.getStatus(), is(JobState.Status.PROCESSING));
	}

	@Test
	public void testHandleMsg_NoJobState() throws Exception {
		TestJob job = new TestJob(TestJobProducerNoChildren.class);

		ProgressIgnoredMsg msg = new ProgressIgnoredMsg(job.getId(), Id.nextValue());

		replayAll();

		ProgressIgnoredMsgHandler handler = new ProgressIgnoredMsgHandler(workerMock);

		assertEquals(handler.getMsgClass(), ProgressIgnoredMsg.class);

		handler.handleMsg(msg, sender);

		verifyAll();
	}

	@Test
	public void testHandleMsg_MismatchNewMsgIdOrigin() throws Exception {
		TestJob job = new TestJob(TestJobProducerNoChildren.class);

		JobState jobState = new JobState(job, Id.nextValue(), path);
		jobState.setStatus(JobState.Status.PROCESSING);
		workerState.addJobState(jobState);

		ProgressIgnoredMsg msg = new ProgressIgnoredMsg(job.getId(), Id.nextValue());

		replayAll();

		ProgressIgnoredMsgHandler handler = new ProgressIgnoredMsgHandler(workerMock);

		assertEquals(handler.getMsgClass(), ProgressIgnoredMsg.class);

		handler.handleMsg(msg, sender);

		verifyAll();
	}
}