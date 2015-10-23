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
package com.persinity.ndt.controller;

import static com.persinity.common.StringUtils.format;
import static com.persinity.common.StringUtils.formatObj;

import java.util.Properties;

import com.persinity.common.Config;

/**
 * DB related configuration.
 *
 * @author Ivan Dachev
 */
public class NdtControllerConfig {

    public NdtControllerConfig(final Properties props, final String propsSource) {
        config = new Config(props, propsSource);
    }

    /**
     * @return DB config name
     */
    public String getDbConfigName() {
        return config.getString(DB_CONFIG_NAME_KEY);
    }

    /**
     * @return view classname
     */
    public String getViewClassname() {
        return config.getString(VIEW_CLASSNAME_KEY);
    }

    /**
     * @return ETL migration window size
     */
    public int getMigrateWindowSize() {
        return config.getPositiveInt(ETL_WINDOWS_SIZE_KEY);
    }

    /**
     * @return ETL transform window size
     */
    public int getTransformWindowSize() {
        return config.getPositiveInt(ETL_WINDOWS_SIZE_KEY) * MIGRATE_TO_TRANSF_WIN_SIZE_COEF;
    }

    /**
     * @return ETL instruction size
     */
    public int getEtlInstructionSize() {
        return config.getPositiveInt(ETL_INSTRUCTION_SIZE_KEY);
    }

    /**
     * @return ETL metrics reporting interval in seconds
     */
    public int getEtlWindowCheckIntervalSeconds() {
        return config.getPositiveInt(ETL_WINDOW_CHECK_INTERVAL_SECONDS_KEY);
    }

    /**
     * @return ETL metrics reporting interval in seconds
     */
    public int getEtlMetricsReportingIntervalSeconds() {
        return config.getPositiveInt(ETL_METRICS_REPORTING_INTERVAL_SECONDS_KEY);
    }

    /**
     * @return true if need to add pause before uninstall step for debug purpose only
     */
    public boolean isDebugModeOn() {
        return config.getBoolean(DEBUG_MODE);
    }

    /**
     * @return DB agent CLOG GC interval in seconds
     */
    public int getDbAgentClogGcIntervalSeconds() {
        return config.getPositiveInt(DBAGENT_CLOG_GC_INTERVAL_SECONDS_KEY);
    }

    /**
     * @return HAKA enable
     */
    public boolean getHakaEnable() {
        return config.getBoolean(HAKA_ENABLE_KEY);
    }

    /**
     * @return true if records are coalesced while migrated from source to destination staging areas.
     */
    public boolean getMigrateCoalesce() {
        return config.getBooleanDefault(ETL_MIGRATE_COALESCE_KEY, true);
    }

    /**
     * @return HAKA timeout in seconds
     */
    public int getHakaTimeoutSeconds() {
        return config.getPositiveInt(HAKA_TIMEOUT_SECONDS_KEY);
    }

    /**
     * @return haka name
     */
    public String getHakaName() {
        return config.getString(HAKA_NAME_KEY);
    }

    /**
     * @return haka config name
     */
    public String getHakaConfigName() {
        return config.getString(HAKA_CONFIG_NAME_KEY);
    }

    @Override
    public String toString() {
        if (toString == null) {
            toString = format("{}@({}, {})", formatObj(this), config.getPropsSource(), config.dumpProperties());
        }
        return toString;
    }

    public static final String DEFAULT_CONFIG_FILE_NAME = "ndt-controller.properties";

    static final String DB_CONFIG_NAME_KEY = "db.config.name";
    static final String VIEW_CLASSNAME_KEY = "view.classname";
    static final String ETL_WINDOWS_SIZE_KEY = "etl.window.size";
    static final String ETL_MIGRATE_COALESCE_KEY = "etl.migrate.coalesce";
    static final String ETL_INSTRUCTION_SIZE_KEY = "etl.instruction.size";
    static final String ETL_WINDOW_CHECK_INTERVAL_SECONDS_KEY = "etl.window.check.interval.seconds";
    static final String ETL_METRICS_REPORTING_INTERVAL_SECONDS_KEY = "etl.metrics.reporting.interval.seconds";
    static final String DEBUG_MODE = "debug.mode";
    static final String DBAGENT_CLOG_GC_INTERVAL_SECONDS_KEY = "dbagent.clog.gc.interval.seconds";
    static final String HAKA_ENABLE_KEY = "haka.enable";
    static final String HAKA_TIMEOUT_SECONDS_KEY = "haka.timeout.seconds";
    static final String HAKA_NAME_KEY = "haka.name";
    static final String HAKA_CONFIG_NAME_KEY = "haka.config.name";
    static final int MIGRATE_TO_TRANSF_WIN_SIZE_COEF = 2;

    private final Config config;
    private String toString;
}
