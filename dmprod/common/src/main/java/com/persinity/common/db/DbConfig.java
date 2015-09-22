/**
 * Copyright (c) 2015 Persinity Inc.
 */
package com.persinity.common.db;

import static com.persinity.common.StringUtils.format;
import static com.persinity.common.StringUtils.formatObj;
import static com.persinity.common.invariant.Invariant.notNull;

import java.util.Arrays;
import java.util.List;
import java.util.Properties;

import com.persinity.common.Config;
import com.persinity.common.collection.CollectionUtils;

/**
 * DB related configuration.
 *
 * @author Ivan Dachev
 */
public class DbConfig {

    public DbConfig(final Properties props, final String propsSource, final String keysPrefix) {
        notNull(keysPrefix);
        config = new Config(props, propsSource);
        this.keysPrefix = keysPrefix;
    }

    /**
     * @return DB URL
     */
    public String getDbUrl() {
        return config.getString(keysPrefix + DB_URL_KEY);
    }

    /**
     * @return DB user
     */
    public String getDbUser() {
        return config.getString(keysPrefix + DB_USER_KEY);
    }

    /**
     * @return DB password
     */
    public String getDbPass() {
        return config.getString(keysPrefix + DB_PASS_KEY);
    }

    /**
     * @return DB enable output
     */
    public boolean getDbEnableOutput() {
        return config.getBooleanDefault(keysPrefix + DB_ENABLE_OUTPUT_KEY, false);
    }

    @Override
    public String toString() {
        if (toString == null) {
            toString = format("{}@({}, {})", formatObj(this), config.getPropsSource(), config.dumpProperties(
                    Arrays.asList(keysPrefix + DB_URL_KEY, keysPrefix + DB_USER_KEY, keysPrefix + DB_ENABLE_OUTPUT_KEY,
                            keysPrefix + DB_TABLES_SKIPLIST_KEY)));
        }
        return toString;
    }

    /**
     * @return List of tables to ignore as non-existing or empty list if no such.
     */
    public List<String> getSkipTables() {
        final String skipTablesStr = config.getStringDefault(keysPrefix + DB_TABLES_SKIPLIST_KEY, "");
        return CollectionUtils.explode(skipTablesStr);
    }

    /**
     * @return Cache prepared statements. Performance improvement where drivers do not have such feature (e.g. Postgres)
     */
    public boolean getCacheSql() {
        return config.getBooleanDefault(keysPrefix + DB_CACHE_SQL_KEY, false);
    }

    /**
     * @return The fully qualified class name of the {@link SqlStrategy}.
     */
    public String getSqlStrategy() {
        return config.getStringDefault(keysPrefix + DB_SQL_STRATEGY_KEY, DEFAULT_SQL_STRATEGY);
    }

    public static final String NO_KEY_PREFIX = "";
    private static final String DEFAULT_SQL_STRATEGY = "com.persinity.common.db.OracleSqlStrategy";

    private final String keysPrefix;
    private final Config config;

    private String toString;

    final static String DB_URL_KEY = "db.url";
    final static String DB_USER_KEY = "db.user";
    final static String DB_PASS_KEY = "db.pass";
    final static String DB_ENABLE_OUTPUT_KEY = "db.enableOutput";
    final static String DB_TABLES_SKIPLIST_KEY = "db.tables.skiplist";
    final static String DB_CACHE_SQL_KEY = "db.sql.cache";
    static final String DB_SQL_STRATEGY_KEY = "db.sql.strategy";

    private SqlStrategy sqlStrategy;
}
