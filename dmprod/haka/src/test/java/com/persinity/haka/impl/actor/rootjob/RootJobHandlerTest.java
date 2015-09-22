/**
 * Copyright (c) 2015 Persinity Inc.
 */
package com.persinity.haka.impl.actor.rootjob;

import akka.actor.ActorPath;
import akka.actor.ActorRef;
import akka.actor.Address;
import akka.actor.RootActorPath;
import akka.event.LoggingAdapter;
import com.persinity.haka.impl.actor.ContextLoggingAdapter;
import com.persinity.haka.impl.actor.HakaSettings;
import com.persinity.haka.impl.actor.TestActorRefMock;
import com.persinity.haka.impl.actor.WorkerState;
import org.easymock.EasyMockSupport;
import org.junit.Before;
import scala.concurrent.duration.FiniteDuration;

import java.util.concurrent.TimeUnit;

import static org.easymock.EasyMock.*;

/**
 * @author Ivan Dachev
 */
public class RootJobHandlerTest extends EasyMockSupport {
	@Before
	public void setUp() throws Exception {
		ContextLoggingAdapter log = createNiceMock(ContextLoggingAdapter.class);
		workerMock = createMock(RootJobWorker.class);
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

		rootJobSettings = createMock(RootJobSettings.class);
		expect(rootJobSettings.getJobTimeout()).andReturn(delayOneSecond).anyTimes();

		expect(workerMock.getRootJobSettings()).andReturn(rootJobSettings).anyTimes();

		expect(workerMock.getSelf()).andReturn(self).anyTimes();

		expect(workerMock.getPoolSupervisorName()).andStubReturn("supervisor");
	}

	RootJobWorker workerMock;
	HakaSettings settingsMock;
	RootJobSettings rootJobSettings;
	ActorRef sender;
	ActorRef self;
	ActorPath path;
	WorkerState workerState;
}