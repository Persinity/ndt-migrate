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

import com.persinity.common.invariant.Invariant;

/**
 * Usage: new WaitForCondition(5000, "Timeout to wait for ...") {
 *
 * @author Ivan Dachev
 * @Override public boolean condition() { return ...custom checks...; } }.waitOrTimeout();
 */
public abstract class WaitForCondition {

    public WaitForCondition(final long timeoutMs) {
        this(timeoutMs, DEFAULT_TIMEOUT_MSG);
    }

    public WaitForCondition(final long timeoutMs, final String timeoutMsg) {
        this(timeoutMs, timeoutMsg, DEFAULT_TICK_MS);
    }

    /**
     * @param timeoutMs
     *         to wait for {@link #condition()} call to return true
     * @param timeoutMsg
     *         msg to be included in the RuntimeException that is thrown from {@link #waitOrTimeout()}
     * @param tickMs
     *         the tick to be used on which {@link #condition()} will be called
     */
    public WaitForCondition(final long timeoutMs, final String timeoutMsg, final long tickMs) {
        Invariant.assertArg(tickMs > 0, "Expect positive tick interval: {} ms", tickMs);
        Invariant.assertArg(timeoutMs >= tickMs, "The timeout interval: {} ms should be greater then tick one: {} ms",
                timeoutMs, tickMs);

        this.timeoutMs = timeoutMs;
        this.timeoutMsg = timeoutMsg;
        this.tickMs = tickMs;
    }

    /**
     * Call this in order to start wait for condition.
     */
    public void waitOrTimeout() {
        final long deadline = System.currentTimeMillis() + timeoutMs;
        while (System.currentTimeMillis() < deadline) {
            if (condition()) {
                return;
            }
            try {
                Thread.sleep(tickMs);
            } catch (final InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
        throw new RuntimeException(timeoutMsg);
    }

    /**
     * The check will be called multiple times during enforce and should not block.
     *
     * @return true if check is OK to terminate wait
     */
    public abstract boolean condition();

    private final static long DEFAULT_TICK_MS = 500;
    private final static String DEFAULT_TIMEOUT_MSG = "Timeout";

    private final long timeoutMs;
    private final String timeoutMsg;
    private final long tickMs;
}
