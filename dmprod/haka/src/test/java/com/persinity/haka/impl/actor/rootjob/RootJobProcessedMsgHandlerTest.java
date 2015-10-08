/**
 * Copyright (c) 2015 Persinity Inc.
 */
package com.persinity.haka.impl.actor.rootjob;

import akka.actor.ActorRef;
import com.persinity.common.Id;
import com.persinity.haka.impl.actor.JobState;
import com.persinity.haka.impl.actor.execjob.ExecJobState;
import com.persinity.haka.impl.actor.message.ProcessedMsg;
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
public class RootJobProcessedMsgHandlerTest extends RootJobHandlerTest {

	@Test
	public void testHandleMsg() throws Exception {
		TestRootJob job = new TestRootJob();

		ExecJobState jobState = new ExecJobState(job, Id.nextValue(), sender);
		jobState.setStatus(JobState.Status.PROCESSING);
		workerState.addJobState(jobState);

		Capture<ProcessedMsg> capturedProcessedMsgFromAck = newCapture();
		Capture<ActorRef> capturedSender = newCapture();
		workerMock.sendMsgProcessedAck(EasyMock.capture(capturedProcessedMsgFromAck), EasyMock.capture(capturedSender));
		expectLastCall();

		workerMock.doSnapshot();
		expectLastCall();

		replayAll();

		RootJobProcessedMsgHandler handler = new RootJobProcessedMsgHandler(workerMock);

		assertEquals(handler.getMsgClass(), ProcessedMsg.class);

		ProcessedMsg msg = new ProcessedMsg(job, jobState.getSessionId(), true);

		handler.handleMsg(msg, sender);

		verifyAll();

		// verify processed msg ack for job
		ProcessedMsg processedMsg = capturedProcessedMsgFromAck.getValue();
		assertThat(processedMsg.getJob().getId(), is(job.getId()));
		assertEquals(processedMsg.getJob().getJobProducerClass(), job.getJobProducerClass());
		assertThat(processedMsg.needAck(), is(true));

		assertThat(capturedSender.getValue(), is(sender));

		assertThat(jobState.getStatus(), is(JobState.Status.DONE));

		assertThat(workerState.getJobStates().size(), is(0));
	}

	@Test
	public void testHandleMsg_NoJobState() throws Exception {
		TestRootJob job = new TestRootJob();

		Capture<ProcessedMsg> capturedProcessedMsgFromAck = newCapture();
		Capture<ActorRef> capturedSender = newCapture();
		workerMock.sendMsgProcessedAck(EasyMock.capture(capturedProcessedMsgFromAck), EasyMock.capture(capturedSender));
		expectLastCall();

		replayAll();

		RootJobProcessedMsgHandler handler = new RootJobProcessedMsgHandler(workerMock);

		ProcessedMsg msg = new ProcessedMsg(job, Id.nextValue(), true);

		handler.handleMsg(msg, sender);

		verifyAll();

		// verify processed msg ack for job
		ProcessedMsg processedMsg = capturedProcessedMsgFromAck.getValue();
		assertThat(processedMsg.getJob().getId(), is(job.getId()));
		assertEquals(processedMsg.getJob().getJobProducerClass(), job.getJobProducerClass());
		assertThat(processedMsg.needAck(), is(true));

		assertThat(capturedSender.getValue(), is(sender));

		assertThat(workerState.getJobStates().size(), is(0));
	}

	@Test
	public void testHandleMsg_JobStateDone() throws Exception {
		TestRootJob job = new TestRootJob();

		ExecJobState jobState = new ExecJobState(job, Id.nextValue(), sender);
		jobState.setStatus(JobState.Status.DONE);
		workerState.addJobState(jobState);

		Capture<ProcessedMsg> capturedProcessedMsgFromAck = newCapture();
		Capture<ActorRef> capturedSender = newCapture();
		workerMock.sendMsgProcessedAck(EasyMock.capture(capturedProcessedMsgFromAck), EasyMock.capture(capturedSender));
		expectLastCall();

		replayAll();

		RootJobProcessedMsgHandler handler = new RootJobProcessedMsgHandler(workerMock);

		assertEquals(handler.getMsgClass(), ProcessedMsg.class);

		ProcessedMsg msg = new ProcessedMsg(job, jobState.getSessionId(), true);

		handler.handleMsg(msg, sender);

		verifyAll();

		// verify processed msg ack for job
		ProcessedMsg processedMsg = capturedProcessedMsgFromAck.getValue();
		assertThat(processedMsg.getJob().getId(), is(job.getId()));
		assertEquals(processedMsg.getJob().getJobProducerClass(), job.getJobProducerClass());
		assertThat(processedMsg.needAck(), is(true));

		assertThat(capturedSender.getValue(), is(sender));

		assertThat(jobState.getStatus(), is(JobState.Status.DONE));

		assertThat(workerState.getJobStates().size(), is(1));
	}
}