/**
 * Copyright (c) 2015 Persinity Inc.
 */
package com.persinity.haka.impl.actor.handler;

/**
 * Watchdog handler interface.
 *
 * @author Ivan Dachev
 */
public interface WatchdogHandler {
	void handleWatchdog() throws Exception;
}
