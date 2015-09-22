/**
 * Copyright (c) 2015 Persinity Inc.
 */
package com.persinity.haka;

import com.persinity.haka.impl.actor.executor.HakaExecutorFactoryImpl;

/**
 * @author Ivan Dachev
 */
public class HakaExecutorFactoryProvider {

	public static HakaExecutorFactory getFactory() {
		return hakaExecutorFactory;
	}

//	TODO initialize it with dependency injection framework like spring.
//	@Autowired(required = true)
//	private HakaExecutorFactoryProvider(HakaExecutorFactory _hakaExecutorFactory) {
//		hakaExecutorFactory = _hakaExecutorFactory;
//	}

	private static HakaExecutorFactory hakaExecutorFactory = new HakaExecutorFactoryImpl();
}
