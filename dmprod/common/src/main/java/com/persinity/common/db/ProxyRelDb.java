/**
 * Copyright (c) 2015 Persinity Inc.
 */

package com.persinity.common.db;

import static com.persinity.common.db.HikariConnectionSource.HIKARI_DEFAULT_CONFIG_FILE;
import static com.persinity.common.db.RelDbUtil.newSqlStrategyFor;
import static com.persinity.common.db.metainfo.ProxySchema.newSchema;
import static com.persinity.common.invariant.Invariant.notNull;

import java.sql.Connection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.google.common.base.Function;
import com.persinity.common.collection.ThreadBasedPool;
import com.persinity.common.db.metainfo.Schema;

/**
 * {@link RelDb} implementation that proxies requests to pool of {@link SimpleRelDb} instances.
 * The latter are maintained per thread and released upon commit/rollback.
 *
 * @author dyordanov
 */
public class ProxyRelDb implements RelDb {

    /**
     * @param dbConfig
     *         creates from db config
     */
    public ProxyRelDb(final DbConfig dbConfig) {
        this(dbConfig, new HikariConnectionSource(dbConfig, HIKARI_DEFAULT_CONFIG_FILE));
    }

    public ProxyRelDb(final DbConfig dbConfig, final ConnectionSource connSource) {
        notNull(connSource);
        notNull(dbConfig);

        this.dbConfig = dbConfig;
        this.connSource = connSource;
        systemRelDb = new SimpleRelDb(dbConfig);
        sqlStrategy = newSqlStrategyFor(dbConfig.getSqlStrategy());
        metaInfo = newSchema(systemRelDb, sqlStrategy, dbConfig.getSkipTables());

        final Function<Long, RelDb> newRelDbF = new Function<Long, RelDb>() {
            @Override
            public RelDb apply(final Long input) {
                return new SimpleRelDb(connSource.getConnection(), sqlStrategy, metaInfo, dbConfig.getDbEnableOutput(),
                        dbConfig.getCacheSql(), dbConfig.getSkipTables());
            }
        };
        final Function<RelDb, RelDb> closeRelDbF = new Function<RelDb, RelDb>() {
            @Override
            public RelDb apply(final RelDb input) {
                input.close();
                return input;
            }
        };
        dbPool = new ThreadBasedPool<>(newRelDbF, closeRelDbF, POOL_GC_PERIOD_MS); // TODO pull from DbConfig
        final Thread dbPoolGcThread = new Thread(dbPool);
        dbPoolGcThread.setDaemon(true);
        dbPoolGcThread.start();
    }

    public Connection getConnection() {
        return ((SimpleRelDb) systemRelDb).getConnection();
    }

    @Override
    public Schema metaInfo() {
        return metaInfo;
    }

    @Override
    public int executeDmdl(final String sql) {
        return dbPool.get().executeDmdl(sql);
    }

    @Override
    public void executeSp(final String signature, final Object... params) {
        dbPool.get().executeSp(signature, params);
    }

    @Override
    public Long executeNumericSf(final String signature, final Object... params) {
        return dbPool.get().executeNumericSf(signature, params);
    }

    @Override
    public void close() {
        dbPool.close();
        connSource.close();
        systemRelDb.close();
    }

    @Override
    public boolean isDbOutputEnabled() {
        return dbConfig.getDbEnableOutput();
    }

    @Override
    public void commit() {
        final RelDb db = dbPool.get();
        db.commit();
        dbPool.remove(db);
    }

    @Override
    public void rollback() {
        final RelDb db = dbPool.get();
        db.rollback();
        dbPool.remove(db);
    }

    @Override
    public Iterator<Object[]> query(final String sql) {
        return dbPool.get().query(sql);
    }

    @Override
    public void executeScript(final String scriptName) {
        dbPool.get().executeScript(scriptName);
    }

    @Override
    public Iterator<Map<String, Object>> executePreparedQuery(final String sql, final List<?> params) {
        return dbPool.get().executePreparedQuery(sql, params);
    }

    @Override
    public Iterator<Map<String, Object>> executeQuery(final String sql) {
        return dbPool.get().executeQuery(sql);
    }

    @Override
    public int executePreparedDml(final String sql, final List<?> params) {
        return dbPool.get().executePreparedDml(sql, params);
    }

    @Override
    public int getInt(final String sql) {
        return dbPool.get().getInt(sql);
    }

    @Override
    public long getLong(final String sql) {
        return dbPool.get().getLong(sql);
    }

    @Override
    public String getUserName() {
        return metaInfo.getUserName();
    }

    @Override
    public SqlStrategy getSqlStrategy() {
        return sqlStrategy;
    }

    private static final long POOL_GC_PERIOD_MS = 5000;

    private final ConnectionSource connSource;
    private final RelDb systemRelDb;
    private final Schema metaInfo;
    private final ThreadBasedPool<RelDb> dbPool;
    private final DbConfig dbConfig;
    private final SqlStrategy sqlStrategy;
}
