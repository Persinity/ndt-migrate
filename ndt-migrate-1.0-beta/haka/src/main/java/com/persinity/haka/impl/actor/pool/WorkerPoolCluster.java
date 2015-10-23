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

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.contrib.pattern.DistributedPubSubExtension;
import akka.contrib.pattern.DistributedPubSubMediator;

/**
 * Implements cluster pool backed by DistributedPubSubMediator
 *
 * @author Ivan Dachev
 */
public class WorkerPoolCluster extends WorkerPoolLocal {

    public WorkerPoolCluster(ActorSystem system, String poolName) {
        super(system, poolName);

        ActorRef mediator = DistributedPubSubExtension.get(system).mediator();

        mediator.tell(new DistributedPubSubMediator.Put(getRouter()), null);
    }

    @Override
    public void tell(Object msg, ActorRef sender, boolean localAffinity) {
        if (localAffinity) {
            super.tell(msg, sender, true);
        } else {
            ActorRef mediator = DistributedPubSubExtension.get(getSystem()).mediator();

            mediator.tell(new DistributedPubSubMediator.Send("/user/" + getPoolName(), msg), sender);
        }
    }

    @Override
    public void destroy() {
        ActorRef mediator = DistributedPubSubExtension.get(getSystem()).mediator();

        mediator.tell(new DistributedPubSubMediator.Remove("/user/" + getPoolName()), null);

        super.destroy();
    }
}
