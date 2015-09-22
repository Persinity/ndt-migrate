/**
 * Copyright (c) 2015 Persinity Inc.
 */
package com.persinity.haka.impl.actor.pool;

import akka.actor.ActorSystem;

/**
 * @author Ivan Dachev
 */
public class WorkerPoolClusterTest extends WorkerPoolTest {
	@Override
	public WorkerPool getPool(ActorSystem system) {
		return new WorkerPoolCluster(system, "poolLocal");
	}
}