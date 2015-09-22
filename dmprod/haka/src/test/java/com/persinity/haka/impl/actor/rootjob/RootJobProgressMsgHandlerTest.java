/**
 * Copyright (c) 2015 Persinity Inc.
 */
package com.persinity.haka.impl.actor.rootjob;

import akka.actor.ActorRef;
import com.persinity.common.Id;
import com.persinity.haka.impl.actor.JobState;
import com.persinity.haka.impl.actor.execjob.ExecJobState;
import com.persinity.haka.impl.actor.message.ProgressMsg;
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
public class RootJobProgressMsgHandlerTest extends RootJobHandlerTest {

	@Test
	public void testHandleMsg() throws Exception {
		TestRootJob job = new TestRootJob();

		ExecJobState jobState = new ExecJobState(job, Id.nextValue(), sender);
		jobState.setStatus(JobState.Status.NEW);
		workerState.addJobState(jobState);

		replayAll();

		RootJobProgressMsgHandler handler = new RootJobProgressMsgHandler(workerMock);

		assertEquals(handler.getMsgClass(), ProgressMsg.class);

		ProgressMsg msg = new ProgressMsg(job.getId(), jobState.getSessionId());

		handler.handleMsg(msg, sender);

		verifyAll();

		assertThat(jobState.getStatus(), is(JobState.Status.PROCESSING));
	}

	@Test
	public void testHandleMsg_JobStateDone() throws Exception {
		TestRootJob job = new TestRootJob();

		JobState jobState = new JobState(job, Id.nextValue(), path);
		jobState.setStatus(JobState.Status.DONE);
		workerState.addJobState(jobState);

		Capture<ProgressMsg> capturedProgressMsg = newCapture();
		Capture<ActorRef> capturedSender = newCapture();
		workerMock.sendMsgProgressIgnored(EasyMock.capture(capturedProgressMsg), EasyMock.capture(capturedSender));
		expectLastCall();

		replayAll();

		RootJobProgressMsgHandler handler = new RootJobProgressMsgHandler(workerMock);

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
		TestRootJob job = new TestRootJob();

		Capture<ProgressMsg> capturedProgressMsg = newCapture();
		Capture<ActorRef> capturedSender = newCapture();
		workerMock.sendMsgProgressIgnored(EasyMock.capture(capturedProgressMsg), EasyMock.capture(capturedSender));
		expectLastCall();

		replayAll();

		RootJobProgressMsgHandler handler = new RootJobProgressMsgHandler(workerMock);

		ProgressMsg msg = new ProgressMsg(job.getId(), Id.nextValue());

		handler.handleMsg(msg, sender);

		verifyAll();

		ProgressMsg progressMsg = capturedProgressMsg.getValue();
		assertThat(progressMsg.getJobId(), is(job.getId()));

		assertThat(capturedSender.getValue(), is(sender));
	}
}