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
