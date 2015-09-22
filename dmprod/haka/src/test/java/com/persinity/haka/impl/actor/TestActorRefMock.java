/**
 * Copyright (c) 2015 Persinity Inc.
 */
package com.persinity.haka.impl.actor;

import akka.actor.ActorPath;
import akka.actor.ActorRef;

/**
 * @author Ivan Dachev
 */
@SuppressWarnings({ "serial", "deprecation" })
public class TestActorRefMock extends ActorRef {
	public TestActorRefMock(ActorPath path) {
		this.path = path;
	}

	@Override
	public ActorPath path() {
		return path;
	}

	@Override
	public boolean isTerminated() {
		return false;
	}

	private final ActorPath path;
}
