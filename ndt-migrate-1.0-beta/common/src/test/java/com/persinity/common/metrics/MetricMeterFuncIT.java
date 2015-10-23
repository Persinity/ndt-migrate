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

import org.apache.log4j.Logger;
import org.junit.Test;

import com.persinity.common.logging.Log4jLogger;

/**
 * @author Doichin Yordanov
 */
public class MetricMeterFuncIT {

    @Test
    public void testApply() throws Exception {
        final String meterName = "test" + System.currentTimeMillis();
        MetricMeterFunc testee = new MetricMeterFunc(meterName);
        assertThat((long) Metrics.getMetrics().getOneMinuteRate(meterName), is(0L));

        int res = testee.apply(1);
        for (int i = 0; i < 60; i++) {
            Thread.currentThread().sleep(1000);
            log.debug("Setting frequency of 1 for {}", testee);
            testee.apply(1);
        }
        testee.apply(1);
        assertThat(res, is(1));
        assertThat((long) Metrics.getMetrics().getOneMinuteRate(meterName), is(1L));
    }

    private static final Log4jLogger log = new Log4jLogger(Logger.getLogger(MetricMeterFuncIT.class));
}