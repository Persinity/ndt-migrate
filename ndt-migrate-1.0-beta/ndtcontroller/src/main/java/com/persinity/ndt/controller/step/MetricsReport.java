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
package com.persinity.ndt.controller.step;

import static com.persinity.common.StringUtils.format;
import static com.persinity.common.ThreadUtil.waitForCondition;

import java.util.Map;

import com.google.common.base.Function;
import com.persinity.ndt.controller.NdtController;
import com.persinity.ndt.controller.script.BaseStep;
import com.persinity.ndt.controller.script.Step;
import com.persinity.ndt.etlmodule.relational.common.RelMetrics;

/**
 * @author Ivan Dachev
 */
public class MetricsReport extends BaseStep {
    /**
     * @param prev
     *         Step or {@code null} if the first step
     * @param delaySecs
     *         from previous step
     * @param ctx
     */
    public MetricsReport(final Step prev, final int delaySecs, final Map<Object, Object> ctx) {
        super(prev, delaySecs, ctx);

        final NdtController ndtController = getController();

        metricsReportingIntervalSeconds = ndtController.getConfig().getEtlMetricsReportingIntervalSeconds();
    }

    @Override
    protected void work() {
        Thread.currentThread().setName(getClass().getSimpleName());

        while (waitForNextInterval()) {
            final long migrateRows = RelMetrics.getMigrateRows();
            final long transformRows = RelMetrics.getTransformRows();
            final long tps = RelMetrics.getTransformTps();

            if (lastReportedMigrateRows != migrateRows || lastReportedTransformRows != transformRows) {
                getController().getView().setNdtStatusMessage(
                        format("Migrated rows to Staging: {}, to Destination: {}; Speed: {} tps", migrateRows,
                                transformRows, tps));
                lastReportedMigrateRows = migrateRows;
                lastReportedTransformRows = transformRows;
            }
        }
    }

    private boolean waitForNextInterval() {
        final Function<Void, Boolean> condition = new Function<Void, Boolean>() {
            @Override
            public Boolean apply(final Void aVoid) {
                return isStopRequested();
            }
        };
        return !waitForCondition(condition, metricsReportingIntervalSeconds * 1000L);
    }

    private final int metricsReportingIntervalSeconds;

    private long lastReportedMigrateRows = 0;
    private long lastReportedTransformRows = 0;
}
