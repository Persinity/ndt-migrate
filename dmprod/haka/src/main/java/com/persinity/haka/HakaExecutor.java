/**
 * Copyright (c) 2015 Persinity Inc.
 */
package com.persinity.haka;

import java.util.concurrent.Future;

/**
 * Used to execute a job and return its result trough a Future.
 *
 * @author Ivan Dachev
 */
public interface HakaExecutor {
	/**
	 * @param job       Job to execute
	 * @param timeoutMs Timeout in ms to wait for Job execution
	 * @return Future for Job result
	 */
	<T extends Job>
	Future<T> executeJob(T job, long timeoutMs);

	/**
	 * Should be called when no longer used.
	 */
	void shutdown();
}
