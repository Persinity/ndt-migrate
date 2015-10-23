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

import java.util.Collections;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.PoisonPill;
import akka.event.Logging;
import akka.event.LoggingAdapter;
import akka.routing.ActorRefRoutee;
import akka.routing.AddRoutee;
import akka.routing.RoundRobinGroup;
import com.persinity.common.logging.LogUtil;
import scala.concurrent.duration.FiniteDuration;

/**
 * Implements simple local pool backed by router.
 *
 * @author Ivan Dachev
 */
public class WorkerPoolLocal implements WorkerPool {
    public WorkerPoolLocal(ActorSystem system, String poolName) {
        this.system = system;
        this.poolName = poolName;

        log = Logging.getLogger(system, this);

        router = system.actorOf(new RoundRobinGroup(Collections.<String>emptyList()).props(), poolName);
    }

    @Override
    public void tell(Object msg, ActorRef sender) {
        tell(msg, sender, false);
    }

    @Override
    public void tell(Object msg, ActorRef sender, boolean localAffinity) {
        ActorRef _router = router;
        if (_router == null) {
            return;
        }
        _router.tell(msg, sender);
    }

    @Override
    public void add(ActorRef worker) {
        ActorRef _router = router;
        if (_router == null) {
            return;
        }
        log.debug(String.format("Add to router: %s worker: %s", _router, worker));
        _router.tell(new AddRoutee(new ActorRefRoutee(worker)), null);
    }

    @Override
    public void destroy() {
        ActorRef _router = router;
        router = null;
        if (_router != null) {
            log.debug(String.format("Stop router: %s", _router));
            _router.tell(PoisonPill.getInstance(), ActorRef.noSender());
        }
    }

    @Override
    public void schedule(Object msg, ActorRef sender, FiniteDuration delay) {
        schedule(msg, sender, false, delay);
    }

    @Override
    public void schedule(Object msg, ActorRef sender, boolean localAffinity, FiniteDuration delay) {
        class PoolDelayedSender implements Runnable {
            final Object msg;
            final ActorRef sender;
            final boolean localAffinity;

            PoolDelayedSender(Object msg, ActorRef sender, boolean localAffinity) {
                this.msg = msg;
                this.sender = sender;
                this.localAffinity = localAffinity;
            }

            @Override
            public void run() {
                WorkerPoolLocal.this.tell(msg, sender, localAffinity);
            }
        }

        system.scheduler().scheduleOnce(delay, new PoolDelayedSender(msg, sender, localAffinity), system.dispatcher());
    }

    @Override
    public String toString() {
        return String.format("%s[%s]", LogUtil.formatPackageName(super.toString()), poolName);
    }

    protected ActorSystem getSystem() {
        return system;
    }

    protected String getPoolName() {
        return poolName;
    }

    protected ActorRef getRouter() {
        return router;
    }

    private final ActorSystem system;
    private final String poolName;
    private final LoggingAdapter log;
    private ActorRef router;
}
