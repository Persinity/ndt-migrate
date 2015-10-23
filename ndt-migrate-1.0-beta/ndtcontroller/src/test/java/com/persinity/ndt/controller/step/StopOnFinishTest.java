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
package com.persinity.ndt.controller.step;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import com.persinity.ndt.controller.script.Step;

/**
 * @author Ivan Dachev
 */
public class StopOnFinishTest extends NdtStepBaseTest {

    @Test
    public void testWork() throws Exception {
        final boolean[] sigStop = new boolean[1];
        sigStop[0] = false;
        final Step stepToStop = new Step() {
            @Override
            public boolean isFinished() {
                return false;
            }

            @Override
            public void sigStop() {
                sigStop[0] = true;
            }

            @Override
            public boolean isStopRequested() {
                return false;
            }

            @Override
            public boolean isFailed() {
                return false;
            }

            @Override
            public void waitToFinish(final long timeoutMs) {

            }

            @Override
            public RuntimeException getFailedException() {
                return null;
            }

            @Override
            public void run() {

            }
        };
        final StopOnFinish testee = new StopOnFinish(null, Step.NO_DELAY, stepToStop, ctx);

        replayAll();

        assertFalse(sigStop[0]);
        testee.work();
        assertTrue(sigStop[0]);

        verifyAll();

    }
}