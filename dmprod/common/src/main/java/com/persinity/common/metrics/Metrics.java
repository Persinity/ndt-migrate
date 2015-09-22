/**
 * Copyright (c) 2015 Persinity Inc.
 */
package com.persinity.common.metrics;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.SortedMap;
import java.util.concurrent.TimeUnit;

import org.apache.log4j.Logger;

import com.codahale.metrics.ConsoleReporter;
import com.codahale.metrics.Counter;
import com.codahale.metrics.Meter;
import com.codahale.metrics.MetricRegistry;
import com.persinity.common.logging.Log4jLogger;

/**
 * @author Ivan Dachev
 */
public class Metrics {
    /**
     * @return Metrics singleton
     */
    public static Metrics getMetrics() {
        return metrics;
    }

    /**
     * Dump to the log current metrics report.
     */
    public void report() {
        printStream.flush();
        outputStream.reset();
        reporter.report();
        printStream.flush();
        log.info('\n' + outputStream.toString());
    }

    /**
     * Reset current metrics.
     * <p/>
     * TODO implement reset for all kind of metrics in MetricRegistry
     */
    public void reset() {
        final SortedMap<String, Counter> counters = metricRegistry.getCounters();
        for (Counter counter : counters.values()) {
            counter.dec(counter.getCount());
        }

        final SortedMap<String, Meter> meters = metricRegistry.getMeters();
        for (String meterName : meters.keySet()) {
            metricRegistry.remove(meterName);
            metricRegistry.register(meterName, new Meter());
        }
    }

    /**
     * @param counterName
     *         to return count for
     * @return count
     */
    public long getCount(final String counterName) {
        return metricRegistry.counter(counterName).getCount();
    }

    /**
     * @param meterName
     *         to return count for
     * @return count
     */
    public long getMeterCount(final String meterName) {
        return metricRegistry.meter(meterName).getCount();
    }

    /**
     * @param meterName
     *         to return rate for
     * @return rate
     */
    public double getOneMinuteRate(final String meterName) {
        return metricRegistry.meter(meterName).getOneMinuteRate();
    }

    /**
     * @return MetricRegistry
     */
    MetricRegistry getMetricRegistry() {
        return metricRegistry;
    }

    private Metrics() {
        metricRegistry = new MetricRegistry();
        outputStream = new ByteArrayOutputStream();
        printStream = new PrintStream(outputStream);
        reporter = ConsoleReporter.forRegistry(metricRegistry).outputTo(printStream).convertRatesTo(TimeUnit.SECONDS)
                .convertDurationsTo(TimeUnit.MILLISECONDS).build();
    }

    private final MetricRegistry metricRegistry;
    private final ConsoleReporter reporter;
    private final PrintStream printStream;
    private final ByteArrayOutputStream outputStream;

    private static final Metrics metrics = new Metrics();
    private static final Log4jLogger log = new Log4jLogger(Logger.getLogger(Metrics.class));
}
