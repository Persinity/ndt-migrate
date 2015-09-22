/**
 * Copyright (c) 2015 Persinity Inc.
 */
package com.persinity.haka.impl.actor.handler;

import akka.actor.ActorRef;
import com.persinity.common.Id;
import com.persinity.haka.Job;
import com.persinity.haka.impl.actor.JobState;
import com.persinity.haka.impl.actor.TestJob;
import com.persinity.haka.impl.actor.TestJobProducerNoChildren;
import com.persinity.haka.impl.actor.message.ProgressMsg;
import org.easymock.Capture;
import org.easymock.EasyMock;
import org.junit.Test;

import java.util.Collections;

import static org.easymock.EasyMock.expectLastCall;
import static org.easymock.EasyMock.newCapture;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;

/**
 * @author Ivan Dachev
 */
public class ProgressMsgHandlerTest extends HandlerTest {

	@Test
	public void testHandleMsg() throws Exception {
		TestJob job = new TestJob(TestJobProducerNoChildren.class);

		JobState jobState = new JobState(job, Id.nextValue(), path);
		jobState.setStatus(JobState.Status.PROCESSING);
		workerState.addJobState(jobState);

		TestJob childJob = new TestJob(job.getId(), TestJobProducerNoChildren.class);
		jobState.appendChildren(Collections.singleton((Job) childJob));
		JobState childJobState = jobState.getChildren().get(childJob.getId().id);
		childJobState.setSessionId(Id.nextValue());

		ProgressMsg msg = new ProgressMsg(childJob.getId(), childJobState.getSessionId());

		replayAll();

		ProgressMsgHandler handler = new ProgressMsgHandler(workerMock);

		assertEquals(handler.getMsgClass(), ProgressMsg.class);

		handler.handleMsg(msg, sender);

		verifyAll();
	}

	@Test
	public void testHandleMsg_ChildJobState_Done() throws Exception {
		TestJob job = new TestJob(TestJobProducerNoChildren.class);

		JobState jobState = new JobState(job, Id.nextValue(), path);
		jobState.setStatus(JobState.Status.PROCESSING);
		workerState.addJobState(jobState);

		TestJob childJob = new TestJob(job.getId(), TestJobProducerNoChildren.class);
		jobState.appendChildren(Collections.singleton((Job) childJob));

		jobState.getChildren().get(childJob.getId().id).setStatus(JobState.Status.DONE);

		ProgressMsg msg = new ProgressMsg(childJob.getId(), Id.nextValue());

		Capture<ProgressMsg> capturedProgressMsg = newCapture();
		Capture<ActorRef> capturedSender = newCapture();
		workerMock.sendMsgProgressIgnored(EasyMock.capture(capturedProgressMsg), EasyMock.capture(capturedSender));
		expectLastCall();

		replayAll();

		ProgressMsgHandler handler = new ProgressMsgHandler(workerMock);

		assertEquals(handler.getMsgClass(), ProgressMsg.class);

		handler.handleMsg(msg, sender);

		verifyAll();

		assertThat(capturedProgressMsg.getValue().getJobId(), is(childJob.getId()));
		assertThat(capturedSender.getValue(), is(sender));
	}

	@Test
	public void testHandleMsg_ChildJobState_MismatchNewMsgId() throws Exception {
		TestJob job = new TestJob(TestJobProducerNoChildren.class);

		JobState jobState = new JobState(job, Id.nextValue(), path);
		jobState.setStatus(JobState.Status.PROCESSING);
		workerState.addJobState(jobState);

		TestJob childJob = new TestJob(job.getId(), TestJobProducerNoChildren.class);
		jobState.appendChildren(Collections.singleton((Job) childJob));
		JobState childJobState = jobState.getChildren().get(childJob.getId().id);
		childJobState.setStatus(JobState.Status.PROCESSING);
		childJobState.setSessionId(Id.nextValue());

		ProgressMsg msg = new ProgressMsg(childJob.getId(), Id.nextValue());

		Capture<ProgressMsg> capturedProgressMsg = newCapture();
		Capture<ActorRef> capturedSender = newCapture();
		workerMock.sendMsgProgressIgnored(EasyMock.capture(capturedProgressMsg), EasyMock.capture(capturedSender));
		expectLastCall();

		replayAll();

		ProgressMsgHandler handler = new ProgressMsgHandler(workerMock);

		assertEquals(handler.getMsgClass(), ProgressMsg.class);

		handler.handleMsg(msg, sender);

		verifyAll();

		assertThat(capturedProgressMsg.getValue().getJobId(), is(childJob.getId()));
		assertThat(capturedSender.getValue(), is(sender));
	}

	@Test
	public void testHandleMsg_NoChildJobState() throws Exception {
		TestJob job = new TestJob(TestJobProducerNoChildren.class);

		JobState jobState = new JobState(job, Id.nextValue(), path);
		jobState.setStatus(JobState.Status.PROCESSING);
		workerState.addJobState(jobState);

		TestJob childJob = new TestJob(job.getId(), TestJobProducerNoChildren.class);

		ProgressMsg msg = new ProgressMsg(childJob.getId(), Id.nextValue());

		Capture<ProgressMsg> capturedProgressMsg = newCapture();
		Capture<ActorRef> capturedSender = newCapture();
		workerMock.sendMsgProgressIgnored(EasyMock.capture(capturedProgressMsg), EasyMock.capture(capturedSender));
		expectLastCall();

		replayAll();

		ProgressMsgHandler handler = new ProgressMsgHandler(workerMock);

		assertEquals(handler.getMsgClass(), ProgressMsg.class);

		handler.handleMsg(msg, sender);

		verifyAll();

		assertThat(capturedProgressMsg.getValue().getJobId(), is(childJob.getId()));
		assertThat(capturedSender.getValue(), is(sender));
	}

	@Test
	public void testHandleMsg_NoParentJobState() throws Exception {
		TestJob childJob = new TestJob(TestJobProducerNoChildren.class);

		ProgressMsg msg = new ProgressMsg(childJob.getId(), Id.nextValue());

		Capture<ProgressMsg> capturedProgressMsg = newCapture();
		Capture<ActorRef> capturedSender = newCapture();
		workerMock.sendMsgProgressIgnored(EasyMock.capture(capturedProgressMsg), EasyMock.capture(capturedSender));
		expectLastCall();

		replayAll();

		ProgressMsgHandler handler = new ProgressMsgHandler(workerMock);

		assertEquals(handler.getMsgClass(), ProgressMsg.class);

		handler.handleMsg(msg, sender);

		verifyAll();

		assertThat(capturedProgressMsg.getValue().getJobId(), is(childJob.getId()));
		assertThat(capturedSender.getValue(), is(sender));
	}
}