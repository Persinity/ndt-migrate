/**
 * Copyright (c) 2015 Persinity Inc.
 */

package com.persinity.common.db;

import static com.persinity.common.invariant.Invariant.notNull;

import java.sql.Connection;
import java.sql.SQLException;

import org.apache.log4j.Logger;

import com.persinity.common.Config;
import com.persinity.common.StringUtils;
import com.persinity.common.logging.Log4jLogger;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

/**
 * {@link ConnectionSource} backed by Hikari Connection Pool (CP).<BR>
 * Hikari CP <a href="http://brettwooldridge.github.io/HikariCP/">claims</a> significantly better performance
 * when compared to Tomcat, DBCP, et al CP players.
 *
 * @author dyordanov
 */
public class HikariConnectionSource implements ConnectionSource {

    public HikariConnectionSource(final DbConfig dbConfig) {
        this(dbConfig, null);
    }

    /**
     * @param dbConfig
     * @param hikariConfigFile
     *         optional
     */
    public HikariConnectionSource(final DbConfig dbConfig, final String hikariConfigFile) {
        notNull(dbConfig);
        if (hikariConfigFile != null) {
            hikariConfig = new HikariConfig(Config.loadPropsFrom(hikariConfigFile));
        } else {
            hikariConfig = new HikariConfig();
        }
        hikariConfig.setJdbcUrl(dbConfig.getDbUrl());
        hikariConfig.setUsername(dbConfig.getDbUser());
        hikariConfig.setPassword(dbConfig.getDbPass());
        hikariConfig.setAutoCommit(false);
        hikariConfig.setMinimumIdle(2); // TODO useful to go to a config for initial connections
        log.debug("Starting {}", this);

        hikariDS = new HikariDataSource(hikariConfig);
    }

    @Override
    public Connection getConnection() {
        try {
            return hikariDS.getConnection();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void close() {
        log.debug("Closing {}", this);
        hikariDS.close();
    }

    @Override
    public String toString() {
        if (toString == null) {
            toString = StringUtils.format("{}({})", getClass().getSimpleName(), hikariConfig.getUsername());
        }
        return toString;
    }

    public static final String HIKARI_DEFAULT_CONFIG_FILE = "cp-hikari.properties";

    private static final Log4jLogger log = new Log4jLogger(Logger.getLogger(HikariConnectionSource.class));
    private final HikariDataSource hikariDS;
    private final HikariConfig hikariConfig;
    private String toString;
}
