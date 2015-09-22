/**
 * Copyright (c) 2015 Persinity Inc.
 */
package com.persinity.haka.impl.actor.executor;

import akka.actor.ActorSystem;

import com.persinity.haka.impl.actor.HakaNode;

/**
 * Implements embedded HakaExecutor..
 * 
 * @author Ivan Dachev
 */
public class EmbeddedHakaExecutorImpl extends HakaExecutorImpl {
	public EmbeddedHakaExecutorImpl(final HakaNode hakaNode) {
		final String hakaAddress = "";

		final ActorSystem system = hakaNode.getActorSystem();

		try {
			init(system, hakaAddress);
		} catch (final RuntimeException e) {
			try {
				shutdown();
			} catch(RuntimeException ignored) {
			}
			throw e;
		}
	}
}
