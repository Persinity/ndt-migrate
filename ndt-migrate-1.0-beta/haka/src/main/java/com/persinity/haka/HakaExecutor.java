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
package com.persinity.haka;

import java.util.concurrent.Future;

/**
 * Used to execute a job and return its result trough a Future.
 *
 * @author Ivan Dachev
 */
public interface HakaExecutor {
    /**
     * @param job
     *         Job to execute
     * @param timeoutMs
     *         Timeout in ms to wait for Job execution
     * @return Future for Job result
     */
    <T extends Job> Future<T> executeJob(T job, long timeoutMs);

    /**
     * Should be called when no longer used.
     */
    void shutdown();
}
