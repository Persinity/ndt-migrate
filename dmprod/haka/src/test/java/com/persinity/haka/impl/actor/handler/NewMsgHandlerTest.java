/**
 * Copyright (c) 2015 Persinity Inc.
 */
package com.persinity.haka.impl.actor.handler;

import akka.actor.ActorRef;
import com.persinity.common.Id;
import com.persinity.haka.JobIdentity;
import com.persinity.haka.impl.actor.JobState;
import com.persinity.haka.impl.actor.TestJob;
import com.persinity.haka.impl.actor.TestJobProducerNoChildren;
import com.persinity.haka.impl.actor.message.NewMsg;
import org.easymock.Capture;
import org.easymock.EasyMock;
import org.junit.Test;
import scala.concurrent.duration.FiniteDuration;

import java.util.concurrent.TimeUnit;

import static org.easymock.EasyMock.expectLastCall;
import static org.easymock.EasyMock.newCapture;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;

/**
 * @author Ivan Dachev
 */
public class NewMsgHandlerTest extends HandlerTest {

	@Test
	public void testHandleMsg() throws Exception {
		TestJob job = new TestJob(TestJobProducerNoChildren.class);

		EasyMock.expect(settingsMock.getMaxJobsPerWorker()).andReturn(1).anyTimes();

		NewMsg msg = new NewMsg(job);

		Capture<JobIdentity> capturedJobStateId = newCapture();
		workerMock.sendProcessJobStateMsg(EasyMock.capture(capturedJobStateId));
		expectLastCall();

		replayAll();

		// to test the construct for making helper by worker only
		@SuppressWarnings("UnusedAssignment")
		NewMsgHandler handler = new NewMsgHandler(workerMock);

		handler = new NewMsgHandler(workerMock);

		assertEquals(handler.getMsgClass(), NewMsg.class);

		handler.handleMsg(msg, sender);

		verifyAll();

		JobIdentity jobStateId = capturedJobStateId.getValue();
		assertThat(jobStateId, is(job.getId()));
	}

	@Test
	public void testHandleMsg_MaxWorkersHit_ResendNewMsg_to_Pool() throws Exception {
		TestJob job = new TestJob(TestJobProducerNoChildren.class);

		EasyMock.expect(settingsMock.getMaxJobsPerWorker()).andReturn(1).anyTimes();

		TestJob jobExisting = new TestJob(TestJobProducerNoChildren.class);
		JobState jobState = new JobState(jobExisting, jobExisting.getId().id, null);
		workerState.addJobState(jobState);

		NewMsg msg = new NewMsg(job);

		Capture<NewMsg> capturedNewMsg = newCapture();
		Capture<ActorRef> capturedSender = newCapture();
		workerMock.resendMsgNew(EasyMock.capture(capturedNewMsg), EasyMock.capture(capturedSender));
		expectLastCall();

		replayAll();

		NewMsgHandler handler = new NewMsgHandler(workerMock);

		handler.handleMsg(msg, sender);

		verifyAll();

		NewMsg msgSend = capturedNewMsg.getValue();
		assertThat(msg, is(msgSend));
	}

	@Test
	public void testHandleMsg_AlreadyProcessingSameJob() throws Exception {
		TestJob job = new TestJob(TestJobProducerNoChildren.class);

		JobState jobState = new JobState(job, Id.nextValue(), path);
		workerState.addJobState(jobState);

		EasyMock.expect(settingsMock.getMaxJobsPerWorker()).andReturn(2).anyTimes();

		NewMsg msg = new NewMsg(job);

		replayAll();

		NewMsgHandler handler = new NewMsgHandler(workerMock);

		handler.handleMsg(msg, sender);

		verifyAll();
	}

}