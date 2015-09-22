/**
 * Copyright (c) 2015 Persinity Inc.
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
