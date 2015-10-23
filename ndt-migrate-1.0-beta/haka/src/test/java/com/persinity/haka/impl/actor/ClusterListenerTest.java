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
package com.persinity.haka.impl.actor;

import static com.persinity.haka.impl.actor.TestWorkerUtil.shutdownAndWaitTermination;

import org.easymock.EasyMock;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.PoisonPill;
import akka.actor.Props;
import akka.cluster.ClusterEvent;
import akka.testkit.TestActorRef;
import com.typesafe.config.ConfigFactory;

/**
 * @author Ivan Dachev
 */
public class ClusterListenerTest {

    @Before
    public void setUp() {
        system = ActorSystem.create("haka", ConfigFactory.load("test-application-random-port.conf"));
    }

    @After
    public void tearDown() {
        shutdownAndWaitTermination(system);
        system = null;
    }

    @Test
    public void testOnReceive() throws Exception {
        final Props props = Props.create(ClusterListener.class);
        TestActorRef<ClusterListener> ref = TestActorRef.create(system, props, "clusterListener");

        ref.tell(EasyMock.createNiceMock(ClusterEvent.CurrentClusterState.class), ActorRef.noSender());
        ref.tell(EasyMock.createNiceMock(ClusterEvent.MemberUp.class), ActorRef.noSender());
        ref.tell(EasyMock.createNiceMock(ClusterEvent.UnreachableMember.class), ActorRef.noSender());
        ref.tell(EasyMock.createNiceMock(ClusterEvent.MemberRemoved.class), ActorRef.noSender());
        ref.tell(EasyMock.createNiceMock(ClusterEvent.MemberEvent.class), ActorRef.noSender());
        ref.tell("", ActorRef.noSender());

        ref.tell(PoisonPill.getInstance(), ActorRef.noSender());
    }

    private ActorSystem system;
}