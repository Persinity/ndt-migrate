/**
 * Copyright (c) 2015 Persinity Inc.
 */
package com.persinity.haka.impl.actor.executor;

import com.persinity.haka.HakaExecutor;
import com.persinity.haka.HakaExecutorFactory;
import com.persinity.haka.impl.actor.HakaNode;

/**
 * Factory for creating HakaExecutor.
 * 
 * @author Ivan Dachev
 */
public class HakaExecutorFactoryImpl implements HakaExecutorFactory {
	/**
	 * @param config
	 *            new config to be used by remote HakaExecutor impl
	 */
	public void setRemoteHakaConfig(final String config) {
		RemoteHakaExecutorImpl.setConfig(config);
	}

	@Override
	public HakaExecutor newRemoteInstance(final String hakaHost, final int hakaPort) {
		return new RemoteHakaExecutorImpl(hakaHost, hakaPort);
	}

	@Override
	public HakaExecutor newEmbeddedInstance(final HakaNode hakaNode) {
		return new EmbeddedHakaExecutorImpl(hakaNode);
	}
}
