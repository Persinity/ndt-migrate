/**
 * Copyright (c) 2015 Persinity Inc.
 */
package com.persinity.common.metrics;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import org.junit.Test;

import com.codahale.metrics.Counter;
import com.codahale.metrics.Meter;

/**
 * @author Ivan Dachev
 */
public class MetricsTest {

//    @Test
//    public void testReset() throws Exception {
//        final String counterName = "test" + System.currentTimeMillis();
//        final Counter counter = Metrics.getMetrics().getMetricRegistry().counter(counterName);
//        assertThat(Metrics.getMetrics().getCount(counterName), is(0L));
//
//        counter.inc(1);
//        assertThat(Metrics.getMetrics().getCount(counterName), is(1L));
//
//        Metrics.getMetrics().reset();
//
//        assertThat(Metrics.getMetrics().getCount(counterName), is(0L));
//
//        counter.inc(10);
//        assertThat(Metrics.getMetrics().getCount(counterName), is(10L));
//    }

    @Test
    public void testGetCount_Counter() throws Exception {
        final String counterName = "test1";
        final Counter counter = Metrics.getMetrics().getMetricRegistry().counter(counterName);
        assertThat(Metrics.getMetrics().getCount(counterName), is(0L));

        counter.inc(1);
        assertThat(Metrics.getMetrics().getCount(counterName), is(1L));

        counter.inc(10);
        assertThat(Metrics.getMetrics().getCount(counterName), is(11L));
    }

    @Test
    public void testGetCount_Meter() throws Exception {
        final String meterName = "test2";
        final Meter meter = Metrics.getMetrics().getMetricRegistry().meter(meterName);
        assertThat((long) Metrics.getMetrics().getOneMinuteRate(meterName), is(0L));
    }
}