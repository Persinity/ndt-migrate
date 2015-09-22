/**
 * Copyright (c) 2015 Persinity Inc.
 */
package com.persinity.haka.impl.actor.handler;

import akka.actor.ActorRef;
import com.persinity.common.Id;
import com.persinity.haka.Job;
import com.persinity.haka.JobIdentity;
import com.persinity.haka.impl.actor.*;
import com.persinity.haka.impl.actor.message.ProcessedMsg;
import org.easymock.Capture;
import org.easymock.EasyMock;
import org.junit.Test;

import java.util.Collections;

import static org.easymock.EasyMock.expectLastCall;
import static org.easymock.EasyMock.newCapture;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.*;

/**
 * @author Ivan Dachev
 */
public class ProcessedMsgHandlerTest extends HandlerTest {

	@Test
	public void testHandleMsg() throws Exception {
		TestJob job = new TestJob(TestJobProducerOneChild.class);

		JobState jobState = new JobState(job, Id.nextValue(), path);
		jobState.setStatus(JobState.Status.PROCESSING);
		workerState.addJobState(jobState);

		TestChildJob childJob = new TestChildJob(job, TestJobProducerNoChildren.class);
		jobState.appendChildren(Collections.singleton((Job) childJob));

		childJob.state = TestChildJobProducer.STATE_CHILD_DONE;
		JobState childJobState = jobState.getChildren().get(childJob.getId().id);
		childJobState.setSessionId(Id.nextValue());

		job.state = TestJobProducerOneChild.STATE_ONE_CHILD_CREATED;

		Capture<ProcessedMsg> capturedProcessedMsgFromAck = newCapture();
		Capture<ActorRef> capturedSender = newCapture();
		workerMock.sendMsgProcessedAck(EasyMock.capture(capturedProcessedMsgFromAck), EasyMock.capture(capturedSender));
		expectLastCall();

		Capture<JobIdentity> capturedJobStateId = newCapture();
		workerMock.sendProcessJobStateMsg(EasyMock.capture(capturedJobStateId));
		expectLastCall();

		workerMock.doSnapshot();
		expectLastCall().times(1);

		replayAll();

		// to test constructor with worker only
		@SuppressWarnings("UnusedAssignment")
		ProcessedMsgHandler handler = new ProcessedMsgHandler(workerMock);

		handler = new ProcessedMsgHandler(workerMock);

		assertEquals(handler.getMsgClass(), ProcessedMsg.class);

		ProcessedMsg msg = new ProcessedMsg(childJob, childJobState.getSessionId(), true);

		handler.handleMsg(msg, sender);

		verifyAll();

		// verify processed msg ack for child
		ProcessedMsg processedMsg = capturedProcessedMsgFromAck.getValue();
		assertThat(processedMsg.getJob().getId(), is(childJob.getId()));
		assertEquals(processedMsg.getJob().getJobProducerClass(), childJob.getJobProducerClass());
		assertThat(processedMsg.needAck(), is(true));

		assertThat(capturedSender.getValue(), is(sender));

		// verify call process for parent
		JobIdentity jobStateId = capturedJobStateId.getValue();
		assertThat(jobStateId, is(job.getId()));

		jobState = jobState.getChildren().get(childJob.getId().id);
		assertThat(jobState, nullValue());
	}

	@Test
	public void testHandleMsg_NoJobStateForParent() throws Exception {
		TestJob childJob = new TestJob(TestJobProducerNoChildren.class);

		ProcessedMsg msg = new ProcessedMsg(childJob, Id.nextValue(), true);

		Capture<ProcessedMsg> capturedProcessedMsgFromAck = newCapture();
		Capture<ActorRef> capturedSender = newCapture();
		workerMock.sendMsgProcessedAck(EasyMock.capture(capturedProcessedMsgFromAck), EasyMock.capture(capturedSender));
		expectLastCall();

		replayAll();

		ProcessedMsgHandler handler = new ProcessedMsgHandler(workerMock);

		handler.handleMsg(msg, sender);

		verifyAll();

		// verify processed msg ack for child
		ProcessedMsg processedMsg = capturedProcessedMsgFromAck.getValue();
		assertThat(processedMsg.getJob().getId(), is(childJob.getId()));
		assertEquals(processedMsg.getJob().getJobProducerClass(), childJob.getJobProducerClass());
		assertThat(processedMsg.needAck(), is(true));
	}

	@Test
	public void testHandleMsg_NoJobStateForChild() throws Exception {
		TestJob job = new TestJob(TestJobProducerNoChildren.class);

		JobState jobState = new JobState(job, Id.nextValue(), path);
		jobState.setStatus(JobState.Status.PROCESSING);
		workerState.addJobState(jobState);

		TestJob childJob = new TestJob(job.getId(), TestJobProducerNoChildren.class);

		ProcessedMsg msg = new ProcessedMsg(childJob, Id.nextValue(), true);

		Capture<ProcessedMsg> capturedProcessedMsgFromAck = newCapture();
		Capture<ActorRef> capturedSender = newCapture();
		workerMock.sendMsgProcessedAck(EasyMock.capture(capturedProcessedMsgFromAck), EasyMock.capture(capturedSender));
		expectLastCall();

		replayAll();

		ProcessedMsgHandler handler = new ProcessedMsgHandler(workerMock);

		handler.handleMsg(msg, sender);

		verifyAll();

		// verify processed msg ack for child
		ProcessedMsg processedMsg = capturedProcessedMsgFromAck.getValue();
		assertThat(processedMsg.getJob().getId(), is(childJob.getId()));
		assertEquals(processedMsg.getJob().getJobProducerClass(), childJob.getJobProducerClass());
		assertThat(processedMsg.needAck(), is(true));
	}

	@Test
	public void testHandleMsg_JobStateForChildIsDone() throws Exception {
		TestJob job = new TestJob(TestJobProducerNoChildren.class);

		JobState jobState = new JobState(job, Id.nextValue(), path);
		jobState.setStatus(JobState.Status.PROCESSING);
		workerState.addJobState(jobState);

		TestJob childJob = new TestJob(job.getId(), TestJobProducerNoChildren.class);
		jobState.appendChildren(Collections.singleton((Job) childJob));

		jobState.getChildren().get(childJob.getId().id).setStatus(JobState.Status.DONE);

		ProcessedMsg msg = new ProcessedMsg(childJob, Id.nextValue(), true);

		Capture<ProcessedMsg> capturedProcessedMsgFromAck = newCapture();
		Capture<ActorRef> capturedSender = newCapture();
		workerMock.sendMsgProcessedAck(EasyMock.capture(capturedProcessedMsgFromAck), EasyMock.capture(capturedSender));
		expectLastCall();

		replayAll();

		ProcessedMsgHandler handler = new ProcessedMsgHandler(workerMock);

		handler.handleMsg(msg, sender);

		verifyAll();

		// verify processed msg ack for child
		ProcessedMsg processedMsg = capturedProcessedMsgFromAck.getValue();
		assertThat(processedMsg.getJob().getId(), is(childJob.getId()));
		assertEquals(processedMsg.getJob().getJobProducerClass(), childJob.getJobProducerClass());
		assertThat(processedMsg.needAck(), is(true));
	}

	@Test
	public void testHandleMsg_JobStateForChildMismatchNewMsgIdOrigin() throws Exception {
		TestJob job = new TestJob(TestJobProducerNoChildren.class);

		JobState jobState = new JobState(job, Id.nextValue(), path);
		jobState.setStatus(JobState.Status.PROCESSING);
		workerState.addJobState(jobState);

		TestJob childJob = new TestJob(job.getId(), TestJobProducerNoChildren.class);
		jobState.appendChildren(Collections.singleton((Job) childJob));
		JobState childJobState = jobState.getChildren().get(childJob.getId().id);
		childJobState.setSessionId(Id.nextValue());

		jobState.getChildren().get(childJob.getId().id).setStatus(JobState.Status.PROCESSING);

		ProcessedMsg msg = new ProcessedMsg(childJob, Id.nextValue(), true);

		Capture<ProcessedMsg> capturedProcessedMsgFromAck = newCapture();
		Capture<ActorRef> capturedSender = newCapture();
		workerMock.sendMsgProcessedAck(EasyMock.capture(capturedProcessedMsgFromAck), EasyMock.capture(capturedSender));
		expectLastCall();

		replayAll();

		ProcessedMsgHandler handler = new ProcessedMsgHandler(workerMock);

		handler.handleMsg(msg, sender);

		verifyAll();

		// verify processed msg ack for child
		ProcessedMsg processedMsg = capturedProcessedMsgFromAck.getValue();
		assertThat(processedMsg.getJob().getId(), is(childJob.getId()));
		assertEquals(processedMsg.getJob().getJobProducerClass(), childJob.getJobProducerClass());
		assertThat(processedMsg.needAck(), is(true));
	}

	@Test
	public void testHandleMsg_JobProducerThrowOnProcessed() throws Exception {
		TestJob job = new TestJob(TestJobProducerThrowOnProcessed.class);

		JobState jobState = new JobState(job, Id.nextValue(), path);
		jobState.setStatus(JobState.Status.PROCESSING);
		workerState.addJobState(jobState);

		TestJob childJob = new TestJob(job.getId(), TestJobProducerThrowOnProcessed.class);
		jobState.appendChildren(Collections.singleton((Job) childJob));

		JobState childJobState = jobState.getChildren().get(childJob.getId().id);
		childJobState.setStatus(JobState.Status.PROCESSING);
		childJobState.setSessionId(Id.nextValue());

		ProcessedMsg msg = new ProcessedMsg(childJob, childJobState.getSessionId(), true);

		replayAll();

		ProcessedMsgHandler handler = new ProcessedMsgHandler(workerMock);

		try {
			handler.handleMsg(msg, sender);
			fail("Expected to throw RuntimeException");
		} catch (RuntimeException e) {
			// expected
		}

		verifyAll();
	}
}