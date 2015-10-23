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
package com.persinity.ndt.controller;

import static com.persinity.common.ThreadUtil.waitForCondition;
import static org.junit.Assert.fail;

import java.util.List;

import com.google.common.base.Function;

/**
 * Executes SQL batches in a thread.
 *
 * @author Ivan Dachev
 */
class TestMutateInParallel extends Thread {

    TestMutateInParallel(final TestNdtUtil testNdtUtil, final List<List<String>> sqlBatchesParallel) {
        this.testNdtUtil = testNdtUtil;
        this.sqlBatchesParallel = sqlBatchesParallel;
    }

    @Override
    public void run() {
        setName(this.getClass().getSimpleName() + '|' + testNdtUtil.getAppBridge().src());
        if (sqlBatchesParallel.size() > 0) {
            testNdtUtil.mutateSrcData(sqlBatchesParallel, 750);
        }
        done = true;
    }

    public void waitDone() {
        final Thread thread = this;
        final Function<Void, Boolean> condition = new Function<Void, Boolean>() {
            @Override
            public Boolean apply(final Void aVoid) {
                return done || !thread.isAlive();
            }
        };

        long timeWaitSeconds = sqlBatchesParallel.size() * WAIT_DONE_PER_BATCH_SECONDS;
        if (timeWaitSeconds == 0) {
            timeWaitSeconds = WAIT_DONE_PER_BATCH_SECONDS;
        }

        if (!waitForCondition(condition, timeWaitSeconds * 1000L)) {
            fail("Timeout waiting for mutate data");
        }
    }

    private static final long WAIT_DONE_PER_BATCH_SECONDS = 120;

    private boolean done = false;

    private final TestNdtUtil testNdtUtil;
    private final List<List<String>> sqlBatchesParallel;
}
