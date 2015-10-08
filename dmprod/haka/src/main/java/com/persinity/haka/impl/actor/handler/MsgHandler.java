/**
 * Copyright (c) 2015 Persinity Inc.
 */
package com.persinity.haka.impl.actor.handler;

import akka.actor.ActorRef;
import com.persinity.haka.impl.actor.message.Msg;

/**
 * Interface for message handlers.
 *
 * @author Ivan Dachev
 */
public interface MsgHandler<T extends Msg> {
	void handleMsg(T msg, ActorRef sender);

	Class<T> getMsgClass();
}
