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
package com.persinity.ndt.datamutator.hibernate;

import java.util.Properties;

import org.hibernate.cfg.Configuration;

import com.persinity.common.Config;
import com.persinity.common.db.DbConfig;

/**
 * @author Ivan Dachev
 */
public class HibernateConfig {
    public HibernateConfig(final Properties props, final String propsSource) {
        config = new Config(props, propsSource);
        dbConfig = new DbConfig(props, propsSource, "");
    }

    /**
     * @return {@link Configuration}
     */
    public Configuration getHibernateConfiguration() {
        Configuration configuration = new Configuration().configure();

        configuration.setProperty("hibernate.connection.url", dbConfig.getDbUrl());
        configuration.setProperty("hibernate.connection.username", dbConfig.getDbUser());
        configuration.setProperty("hibernate.connection.password", dbConfig.getDbPass());
        configuration.setProperty("hibernate.dialect",
                config.getStringDefault(HIBERNATE_DIALECT_KEY, DEFAULT_HIBERNATE_DIALECT));
        configuration.setProperty("hibernate.connection.driver_class",
                config.getStringDefault(HIBERNATE_DRIVER_CLASS_KEY, DEFAULT_HIBERNATE_DRIVER_CLASS));

        if (config.getBooleanDefault(HIBERNATE_DEBUG_KEY, false)) {
            configuration.setProperty("show_sql", "true");
            configuration.setProperty("format_sql", "true");
        }

        configuration.setNamingStrategy(new NamingStrategy(TABLE_PREFIX));

        return configuration;
    }

    /**
     * @return {@link DbConfig} for connections details
     */
    public DbConfig getDbConfig() {
        return dbConfig;
    }

    static final String HIBERNATE_DIALECT_KEY = "hibernate.dialect";
    static final String HIBERNATE_DRIVER_CLASS_KEY = "hibernate.driver.class";
    static final String HIBERNATE_DEBUG_KEY = "hibernate.debug";

    static final String DEFAULT_HIBERNATE_DIALECT = "org.hibernate.dialect.Oracle10gDialect";
    static final String DEFAULT_HIBERNATE_DRIVER_CLASS = "oracle.jdbc.OracleDriver";

    private static final String TABLE_PREFIX = "DM_";

    private final Config config;
    private final DbConfig dbConfig;
}
