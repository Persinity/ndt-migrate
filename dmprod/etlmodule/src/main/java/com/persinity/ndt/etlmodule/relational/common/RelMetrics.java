/**
 * Copyright (c) 2015 Persinity Inc.
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
