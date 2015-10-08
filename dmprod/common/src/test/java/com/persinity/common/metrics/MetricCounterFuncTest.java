/**
 * Copyright (c) 2015 Persinity Inc.
 */
package com.persinity.common.metrics;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import org.junit.Test;

/**
 * @author Ivan Dachev
 */
public class MetricCounterFuncTest {

    @Test
    public void testApply() throws Exception {
        final String counterName = "test" + System.currentTimeMillis();
        MetricCounterFunc testee = new MetricCounterFunc(counterName);
        assertThat(Metrics.getMetrics().getCount(counterName), is(0L));

        int res = testee.apply(1);
        assertThat(res, is(1));
        assertThat(Metrics.getMetrics().getCount(counterName), is(1L));

        res = testee.apply(10);
        assertThat(res, is(10));
        assertThat(Metrics.getMetrics().getCount(counterName), is(11L));
    }
}