/**
 * Copyright (c) 2015 Persinity Inc.
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