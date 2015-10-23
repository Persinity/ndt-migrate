/*
 * Copyright 2015 Persinity Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
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
