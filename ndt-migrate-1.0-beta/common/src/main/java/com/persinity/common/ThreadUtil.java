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
package com.persinity.common;

import static com.persinity.common.invariant.Invariant.assertArg;

import com.google.common.base.Function;

/**
 * @author Ivan Dachev
 */
public class ThreadUtil {
    /**
     * Method that sleeps current Thread that wraps InterruptedException in RuntimeException
     *
     * @param timeMs
     *         interval to wait
     */
    public static void sleep(final long timeMs) {
        assertArg(timeMs > 0);
        try {
            Thread.sleep(timeMs);
        } catch (final InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Method that sleeps current Thread that wraps InterruptedException in RuntimeException
     *
     * @param timeSeconds
     *         interval to wait
     */
    public static void sleepSeconds(final int timeSeconds) {
        assertArg(timeSeconds > 0);
        try {
            Thread.sleep(timeSeconds * 1000);
        } catch (final InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * @param condition
     *         to wait for, should return true when met
     * @param timeoutMs
     *         timeout to wait for
     * @return true if condition was met or false on timeout
     */
    @SuppressWarnings("ConstantConditions")
    public static boolean waitForCondition(final Function<Void, Boolean> condition, final long timeoutMs) {
        long sleepTime = 0;
        while ((sleepTime < timeoutMs) && !condition.apply(null)) {
            sleep(CHECK_CONDITION_INTERVAL_MS);
            sleepTime += CHECK_CONDITION_INTERVAL_MS;
        }

        return condition.apply(null);
    }

    private static final long CHECK_CONDITION_INTERVAL_MS = 500;
}
