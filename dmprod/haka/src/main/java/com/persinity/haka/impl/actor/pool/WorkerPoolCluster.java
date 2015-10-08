/**
 * Copyright (c) 2015 Persinity Inc.
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
