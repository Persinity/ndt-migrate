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
package com.persinity.ndt.etlmodule.relational.common;

import com.persinity.common.metrics.Metrics;

/**
 * @author Ivan Dachev
 */
public class RelMetrics {
    public static String COUNTER_MIGRATE_ROWS = "com.persinity.ndt.etlmodule.relational.migrate.rows";
    public static String COUNTER_TRANSFORM_ROWS = "com.persinity.ndt.etlmodule.relational.transform.rows";
    public static String METER_TRANSFORM_TPS = "com.persinity.ndt.etlmodule.relational.transform.tps";

    /**
     * @return migrated rows
     */
    public static long getMigrateRows() {
        return Metrics.getMetrics().getCount(COUNTER_MIGRATE_ROWS);
    }

    /**
     * @return merged rows
     */
    public static long getTransformRows() {
        return Metrics.getMetrics().getCount(COUNTER_TRANSFORM_ROWS);
    }

    /**
     * @return transform TPS
     */
    public static long getTransformTps() {
        return (long) Metrics.getMetrics().getOneMinuteRate(METER_TRANSFORM_TPS);
    }

}
