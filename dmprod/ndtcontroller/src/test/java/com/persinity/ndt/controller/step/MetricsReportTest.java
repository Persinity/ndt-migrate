/**
 * Copyright (c) 2015 Persinity Inc.
 */
package com.persinity.ndt.controller.step;

import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.expectLastCall;

import org.junit.Test;

import com.persinity.common.ThreadUtil;
import com.persinity.common.metrics.MetricCounterFunc;
import com.persinity.common.metrics.MetricMeterFunc;
import com.persinity.common.metrics.Metrics;
import com.persinity.ndt.controller.script.Step;
import com.persinity.ndt.etlmodule.relational.common.RelMetrics;

/**
 * @author Ivan Dachev
 */
public class MetricsReportTest extends NdtStepBaseTest {

    @Test
    public void testWork() throws Exception {
        expect(ndtControllerConfig.getEtlMetricsReportingIntervalSeconds()).andStubReturn(1);

        ndtControllerView.setNdtStatusMessage("Migrated rows to Staging: 1, to Destination: 5; Speed: 0 tps");
        expectLastCall();

        replayAll();

        final MetricsReport testee = new MetricsReport(null, Step.NO_DELAY, ctx);

        Metrics.getMetrics().reset();

        final Thread th = new Thread() {
            @Override
            public void run() {
                ThreadUtil.sleep(1100);

                new MetricCounterFunc(RelMetrics.COUNTER_MIGRATE_ROWS).apply(1);
                new MetricCounterFunc(RelMetrics.COUNTER_TRANSFORM_ROWS).apply(5);
                new MetricMeterFunc(RelMetrics.METER_TRANSFORM_TPS).apply(1);

                ThreadUtil.sleep(1400);

                testee.sigStop();
            }
        };

        th.start();
        testee.work();

        verifyAll();
    }
}