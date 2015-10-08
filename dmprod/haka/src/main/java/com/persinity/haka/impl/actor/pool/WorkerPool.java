/**
 * Copyright (c) 2015 Persinity Inc.
 */
package com.persinity.haka.impl.actor.pool;

import akka.actor.ActorRef;
import scala.concurrent.duration.FiniteDuration;

/**
 * Abstracting the pool of workers.
 *
 * @author Ivan Dachev
 */
public interface WorkerPool {
	/**
	 * Used to send a message to the pool.
	 *
	 * @param msg
	 * @param sender
	 */
	void tell(Object msg, ActorRef sender);

	/**
	 * Used to send a message to the pool.
	 *
	 * @param msg
	 * @param sender
	 * @param localAffinity
	 */
	void tell(Object msg, ActorRef sender, boolean localAffinity);

	/**
	 * Add new worker to pool.
	 *
	 * @param worker
	 */
	void add(ActorRef worker);

	/**
	 * Should destroy itself.
	 */
	void destroy();

	/**
	 * Schedule tell of message after given delay.
	 *
	 * @param msg
	 * @param sender
	 * @param delay
	 */
	void schedule(Object msg, ActorRef sender, FiniteDuration delay);

	/**
	 * Schedule tell of message after given delay.
	 *
	 * @param msg
	 * @param sender
	 * @param localAffinity
	 * @param delay
	 */
	void schedule(Object msg, ActorRef sender, boolean localAffinity, FiniteDuration delay);
}
