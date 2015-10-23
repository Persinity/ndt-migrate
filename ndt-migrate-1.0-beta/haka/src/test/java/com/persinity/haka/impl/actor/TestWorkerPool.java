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

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.PoisonPill;
import akka.testkit.JavaTestKit;
import com.persinity.haka.impl.actor.pool.WorkerPool;
import scala.concurrent.duration.FiniteDuration;

/**
 * @author Ivan Dachev
 */
public class TestWorkerPool implements WorkerPool {
    TestWorkerPool(ActorSystem system) {
        this.system = system;
        testKit = new JavaTestKit(system);
    }

    @Override
    public void tell(Object msg, ActorRef sender) {
        tell(msg, sender, true);
    }

    @Override
    public void tell(Object msg, ActorRef sender, boolean localAffinity) {
        testKit.getRef().tell(msg, sender);
    }

    @Override
    public void add(ActorRef worker) {
    }

    @Override
    public void destroy() {
        testKit.getRef().tell(PoisonPill.getInstance(), ActorRef.noSender());
    }

    @Override
    public void schedule(Object msg, ActorRef sender, FiniteDuration delay) {
        schedule(msg, sender, true, delay);
    }

    @Override
    public void schedule(Object msg, ActorRef sender, boolean localAffinity, FiniteDuration delay) {
        class PoolDelayedSender implements Runnable {
            private final Object msg;
            private final ActorRef sender;

            PoolDelayedSender(Object msg, ActorRef sender) {
                this.msg = msg;
                this.sender = sender;
            }

            @Override
            public void run() {
                TestWorkerPool.this.tell(msg, sender);
            }
        }

        system.scheduler().scheduleOnce(delay, new PoolDelayedSender(msg, sender), system.dispatcher());
    }

    final ActorSystem system;
    final JavaTestKit testKit;
}
