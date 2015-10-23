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
package com.persinity.haka.impl.actor.pool;

import static com.persinity.haka.impl.actor.TestWorkerUtil.shutdownAndWaitTermination;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.assertThat;

import java.util.concurrent.TimeUnit;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Props;
import akka.actor.UntypedActor;
import akka.testkit.TestActorRef;
import com.typesafe.config.ConfigFactory;
import scala.concurrent.duration.FiniteDuration;

/**
 * @author Ivan Dachev
 */
public abstract class WorkerPoolTest {
    public abstract WorkerPool getPool(ActorSystem system);

    static class TestWorker extends UntypedActor {
        @Override
        public void onReceive(Object message) throws Exception {
            lastMsg = message;
        }

        Object lastMsg;
    }

    @Before
    public void setUp() {
        system = ActorSystem.create("haka", ConfigFactory.load("test-application-random-port.conf"));
    }

    @After
    public void tearDown() throws InterruptedException {
        shutdownAndWaitTermination(system);
        system = null;
    }

    @Test
    public void test() throws InterruptedException {
        WorkerPool pool = getPool(system);

        TestActorRef<TestWorker> ref = TestActorRef
                .create(system, Props.create(TestWorker.class), "test" + System.currentTimeMillis());
        pool.add(ref);
        Thread.sleep(TEST_ACTOR_UNBLOCK_WORKER_MS);

        TestWorker worker = ref.underlyingActor();
        assertThat(worker.lastMsg, nullValue());

        pool.tell("test1", ActorRef.noSender());
        Thread.sleep(TEST_ACTOR_UNBLOCK_WORKER_MS);
        assertThat((String) worker.lastMsg, is("test1"));

        worker.lastMsg = null;
        pool.tell("test2", ActorRef.noSender(), true);
        Thread.sleep(TEST_ACTOR_UNBLOCK_WORKER_MS);
        assertThat((String) worker.lastMsg, is("test2"));

        worker.lastMsg = null;
        pool.schedule("test3", ActorRef.noSender(), FiniteDuration.apply(1, TimeUnit.MILLISECONDS));
        Thread.sleep(TEST_ACTOR_UNBLOCK_WORKER_MS);
        assertThat((String) worker.lastMsg, is("test3"));

        worker.lastMsg = null;
        pool.schedule("test4", ActorRef.noSender(), true, FiniteDuration.apply(1, TimeUnit.MILLISECONDS));
        Thread.sleep(TEST_ACTOR_UNBLOCK_WORKER_MS);
        assertThat((String) worker.lastMsg, is("test4"));

        assertThat(pool.toString(), notNullValue());

        pool.destroy();
        Thread.sleep(TEST_ACTOR_UNBLOCK_WORKER_MS);

        worker.lastMsg = null;
        pool.tell("test5", ActorRef.noSender());
        Thread.sleep(TEST_ACTOR_UNBLOCK_WORKER_MS);
        assertThat(worker.lastMsg, nullValue());

        TestActorRef<TestWorker> ref2 = TestActorRef
                .create(system, Props.create(TestWorker.class), "test" + System.currentTimeMillis());
        pool.add(ref2);

        TestWorker worker2 = ref.underlyingActor();
        assertThat(worker2.lastMsg, nullValue());

        pool.tell("test6", ActorRef.noSender());
        Thread.sleep(TEST_ACTOR_UNBLOCK_WORKER_MS);
        assertThat(worker2.lastMsg, nullValue());
    }

    static final long TEST_ACTOR_UNBLOCK_WORKER_MS = 250;

    private ActorSystem system;
}