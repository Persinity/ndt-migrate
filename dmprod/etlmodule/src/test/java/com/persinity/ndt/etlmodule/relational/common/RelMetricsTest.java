/**
 * Copyright (c) 2015 Persinity Inc.
 */
package com.persinity.ndt.etlmodule.relational.common;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import com.persinity.common.metrics.MetricCounterFunc;
import com.persinity.common.metrics.MetricMeterFunc;

/**
 * @author Ivan Dachev
 */
public class RelMetricsTest {

    /**
     * Test method for {@link RelMetrics#getMigrateRows()}
     *
     * @throws Exception
     */
    @Test
    public void testGetMigrateRows() throws Exception {
        MetricCounterFunc testee = new MetricCounterFunc(RelMetrics.COUNTER_MIGRATE_ROWS);
        testee.apply(10);
        assertTrue(RelMetrics.getMigrateRows() >= 10);
    }

    /**
     * Test method for {@link RelMetrics#getTransformRows()}
     *
     * @throws Exception
     */
    @Test
    public void testGetTransformRows() throws Exception {
        MetricCounterFunc testee = new MetricCounterFunc(RelMetrics.COUNTER_TRANSFORM_ROWS);
        testee.apply(10);
        assertTrue(RelMetrics.getTransformRows() >= 10);
    }

    @Test
    public void testGetTps() throws Exception {
        MetricMeterFunc testee = new MetricMeterFunc(RelMetrics.METER_TRANSFORM_TPS);
        testee.apply(1);
        assertEquals(0, RelMetrics.getTransformTps());
    }
}