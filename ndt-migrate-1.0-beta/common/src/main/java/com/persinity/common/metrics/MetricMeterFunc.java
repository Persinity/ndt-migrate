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

import static com.persinity.common.StringUtils.format;
import static com.persinity.common.StringUtils.formatObj;
import static com.persinity.common.invariant.Invariant.notEmpty;

import com.codahale.metrics.Meter;
import com.google.common.base.Function;

/**
 * @author dyordanov
 */
public class MetricMeterFunc implements Function<Integer, Integer> {

    public MetricMeterFunc(String meterName) {
        notEmpty(meterName);

        this.meterName = meterName;
    }

    @Override
    public Integer apply(final Integer input) {
        final Meter meter = Metrics.getMetrics().getMetricRegistry().meter(meterName);
        meter.mark(input);
        return input;
    }

    @Override
    public String toString() {
        if (toString == null) {
            toString = format("{}({})", formatObj(this), meterName);
        }
        return toString;
    }

    private final String meterName;
    private String toString;
}
