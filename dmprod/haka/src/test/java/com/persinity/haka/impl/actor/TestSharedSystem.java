/**
 * Copyright (c) 2015 Persinity Inc.
 */
package com.persinity.haka.impl.actor;

import akka.actor.ActorSystem;

/**
 * Used to share system between JobProducers and tests.
 *
 * @author Ivan Dachev
 */
public class TestSharedSystem {
	public static ActorSystem system;
}
