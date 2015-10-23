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
package com.persinity.haka.impl.actor.handler;

import static org.easymock.EasyMock.expect;

import java.util.concurrent.TimeUnit;

import org.easymock.EasyMockSupport;
import org.junit.Before;

import akka.actor.ActorPath;
import akka.actor.ActorRef;
import akka.actor.Address;
import akka.actor.RootActorPath;
import com.persinity.haka.impl.actor.ContextLoggingAdapter;
import com.persinity.haka.impl.actor.HakaSettings;
import com.persinity.haka.impl.actor.TestActorRefMock;
import com.persinity.haka.impl.actor.Worker;
import com.persinity.haka.impl.actor.WorkerState;
import scala.concurrent.duration.FiniteDuration;

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