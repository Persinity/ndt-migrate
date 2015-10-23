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