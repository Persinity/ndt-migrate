/**
 * Copyright (c) 2015 Persinity Inc.
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
