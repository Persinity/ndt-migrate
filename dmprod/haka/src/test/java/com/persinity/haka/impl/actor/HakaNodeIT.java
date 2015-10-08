/**
 * Copyright (c) 2015 Persinity Inc.
 */
package com.persinity.haka.impl.actor;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import java.util.concurrent.TimeUnit;

import org.junit.Test;

import akka.actor.ActorRef;
import akka.actor.ActorSelection;
import akka.actor.ActorSystem;
import akka.pattern.Patterns;
import akka.util.Timeout;
import scala.concurrent.Await;
import scala.concurrent.Future;
import scala.concurrent.duration.FiniteDuration;

/**
 * @author Ivan Dachev
 */
public class HakaNodeIT {

    @Test
    public void testMain() throws Exception {
        HakaNode.main("-i", "testNodeId", "--config", "test-haka-embedded-node.conf");

        HakaNode hakaNode = HakaNode.getHaka();

        testPingPong(hakaNode);

        hakaNode.shutdown();
    }

    @Test
    public void testConstructor() throws Exception {
        HakaNode hakaNode = new HakaNode("embedded", "test-haka-embedded-node.conf");

        testPingPong(hakaNode);

        hakaNode.shutdown();
    }

    @SuppressWarnings("unchecked")
    private void testPingPong(HakaNode hakaNode) throws Exception {
        ActorSystem system = hakaNode.getActorSystem();

        ActorRef ref = resolvePoolSupervisor(system);

        Future future = Patterns.ask(ref, WorkerBase.PingPong.PING, Timeout.durationToTimeout(TIMEOUT));

        WorkerBase.PingPong res = Await.<WorkerBase.PingPong>result(future, TIMEOUT);

        assertThat(res, is(WorkerBase.PingPong.PONG));
    }

    private ActorRef resolvePoolSupervisor(ActorSystem system) throws Exception {
        final String workersSupervisorName = WorkersSupervisor.class.getSimpleName();
        final String path = String.format("/user/%s-*", workersSupervisorName);

        final ActorSelection execJobWorker = system.actorSelection(path);

        final scala.concurrent.Future<ActorRef> actorRefFuture = execJobWorker.resolveOne(TIMEOUT);

        final ActorRef ref = Await.result(actorRefFuture, TIMEOUT);

        return ref;
    }

    static final FiniteDuration TIMEOUT = FiniteDuration.apply(500, TimeUnit.MILLISECONDS);
}