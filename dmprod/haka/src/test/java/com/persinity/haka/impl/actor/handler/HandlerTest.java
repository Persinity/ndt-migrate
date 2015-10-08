/**
 * Copyright (c) 2015 Persinity Inc.
 */
package com.persinity.haka.impl.actor.handler;

import akka.actor.ActorPath;
import akka.actor.ActorRef;
import akka.actor.Address;
import akka.actor.RootActorPath;
import com.persinity.haka.impl.actor.*;
import com.persinity.haka.impl.actor.pool.WorkerPool;
import org.easymock.EasyMockSupport;
import org.junit.Before;
import scala.concurrent.duration.FiniteDuration;

import java.util.concurrent.TimeUnit;

import static org.easymock.EasyMock.expect;

/**
 * @author Ivan Dachev
 */
public class HandlerTest extends EasyMockSupport {
	@Before
	public void setUp() throws Exception {
		ContextLoggingAdapter log = createNiceMock(ContextLoggingAdapter.class);
		workerMock = createMock(Worker.class);
		expect(workerMock.getLog()).andReturn(log).anyTimes();

		Address address = new Address("akka", "haka");
		path = new RootActorPath(address, "path");

		sender = new TestActorRefMock(path);

		self = new TestActorRefMock(path);

		workerState = new WorkerState();
		expect(workerMock.getState()).andReturn(workerState).anyTimes();

		settingsMock = createMock(HakaSettings.class);

		FiniteDuration delayOneSecond = FiniteDuration.apply(1, TimeUnit.SECONDS);
		expect(settingsMock.getMsgResendDelay()).andReturn(delayOneSecond).anyTimes();
		expect(settingsMock.getStatusUpdateTimeout()).andReturn(delayOneSecond).anyTimes();

		expect(workerMock.getSettings()).andReturn(settingsMock).anyTimes();

		expect(workerMock.getSelf()).andReturn(self).anyTimes();
	}

	Worker workerMock;
	HakaSettings settingsMock;
	ActorRef sender;
	ActorRef self;
	ActorPath path;
	WorkerState workerState;
}