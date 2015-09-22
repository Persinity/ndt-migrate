/*
 * Copyright (c) 2015 Persinity Inc.
 */

package com.persinity.ndt.datamutator.reldb;

import java.util.Properties;

import com.persinity.common.Config;
import com.persinity.common.db.DbConfig;

/**
 * @author Ivo Yanakiev
 */
public class RelDbConfig {

    public RelDbConfig(final Properties props, final String propsSource) {
        config = new Config(props, propsSource);
        dbConfig = new DbConfig(props, propsSource, "");
    }

    /**
     * @return {@link DbConfig} for connections details
     */
    public DbConfig getDbConfig() {
        return dbConfig;
    }

    /**
     * @return Schema init script's name
     */
    public String getSchemaInitSql() {
        return config.getString(SCHEMA_INIT_SQL);
    }

    /**
     * @return Schema drop script's name
     */
    public String getSchemaDropSql() {
        return config.getString(SCHEMA_DROP_SQL);
    }

    static final String SCHEMA_INIT_SQL = "schema.init.sql";
    static final String SCHEMA_DROP_SQL = "schema.drop.sql";

    private final Config config;
    private final DbConfig dbConfig;
}
