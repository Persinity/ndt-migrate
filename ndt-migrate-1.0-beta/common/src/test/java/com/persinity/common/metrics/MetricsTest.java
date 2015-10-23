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