/**
 * Copyright (c) 2015 Persinity Inc.
 */
package com.persinity.ndt.controller.script;

/**
 * Script step
 *
 * @author Doichin Yordanov
 */
public interface Step extends Runnable {

    int NO_DELAY = 0;

    /**
     * @return {@code true} if the workload is finished
     */
    boolean isFinished();

    /**
     * Sends stop signal to the Step. Particular step implementations might honer or disregard the signal
     */
    void sigStop();

    /**
     * @return {@code true} if stop has been requested.
     */
    boolean isStopRequested();

    /**
     * @return {@code true} if step raised exception on execution.
     */
    boolean isFailed();

    /**
     * Should block until step is done or timeout elapsed.
     */
    void waitToFinish(long timeoutMs);

    /**
     * @return the raised exception on execution.
     */
    RuntimeException getFailedException();
}
