/**
 * Copyright (c) 2015 Persinity Inc.
 */
package com.persinity.ndt.dbagent.relational.oracle;

import java.util.Properties;

import com.persinity.common.Config;

/**
 * @author Ivan Dachev
 */
public class DbAgentConfig {
    public DbAgentConfig(final Properties props, final String propsSource) {
        config = new Config(props, propsSource);
    }

    /**
     * @return CLOG trigger template
     */
    public String getClogTriggerTemplate() {
        return config.getString(CDC_CLOG_TRIGGER_TEMPLATE_KEY);
    }

    /**
     * @return TRLOG trigger template
     */
    public String getTrlogTriggerTemplate() {
        return config.getString(CDC_TRLOG_TRIGGER_TEMPLATE_KEY);
    }

    public static final String DEFAULT_CONFIG_FILE_NAME = "dbagent.properties";
    private static final String CDC_CLOG_TRIGGER_TEMPLATE_KEY = "cdc.clog_trigger_template";
    private static final String CDC_TRLOG_TRIGGER_TEMPLATE_KEY = "cdc.trlog_trigger_template";
    private final Config config;
}
