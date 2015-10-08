/**
 * Copyright (c) 2015 Persinity Inc.
 */
package com.persinity.haka;

import com.persinity.haka.impl.actor.HakaNode;

/**
 * Factory for creating HakaExecutor.
 *
 * @author Ivan Dachev
 */
public interface HakaExecutorFactory {
	/**
	 * Creates a new HakaExecutor for remote haka node.
	 *
	 * @param hakaHost Haka host to connect to
	 * @param hakaPort Haka port to connect to
	 * @return HakaExecutor for desired Job
	 */
	HakaExecutor newRemoteInstance(String hakaHost, int hakaPort);

	/**
	 * Creates a new HakaExecutor for embedded haka node.
	 *
	 * @param hakaNode HakaNode to use as embedded
	 * @return HakaExecutor for desired Job
	 */
	HakaExecutor newEmbeddedInstance(HakaNode hakaNode);
}
