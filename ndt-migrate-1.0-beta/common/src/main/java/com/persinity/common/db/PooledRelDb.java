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

package com.persinity.common.db;

import static com.persinity.common.invariant.Invariant.notNull;

import java.sql.Connection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.persinity.common.db.metainfo.Schema;

/**
 * {@link RelDb} wrapper that deregisters the wrapped db from the given pool.
 *
 * @author dyordanov
 */
public class PooledRelDb implements RelDb {
    public PooledRelDb(final RelDbPool pool, final RelDb relDb) {
        notNull(pool);
        notNull(relDb);

        this.pool = pool;
        this.relDb = relDb;
    }

    @Override
    public Schema metaInfo() {
        return relDb.metaInfo();
    }

    @Override
    public int executeDmdl(final String sql) {
        return relDb.executeDmdl(sql);
    }

    @Override
    public void executeSp(final String signature, final Object... params) {
        relDb.executeSp(signature, params);
    }

    @Override
    public Long executeNumericSf(final String signature, final Object... params) {
        return relDb.executeNumericSf(signature, params);
    }

    @Override
    public void close() {
        pool.remove(relDb);
    }

    @Override
    public boolean isDbOutputEnabled() {
        return relDb.isDbOutputEnabled();
    }

    @Override
    public void commit() {
        relDb.commit();
    }

    @Override
    public void rollback() {
        relDb.rollback();
    }

    @Override
    public Iterator<Object[]> query(final String sql) {
        return relDb.query(sql);
    }

    @Override
    public void executeScript(final String scriptName) {
        relDb.executeScript(scriptName);
    }

    @Override
    public Iterator<Map<String, Object>> executePreparedQuery(final String sql, final List<?> params) {
        return relDb.executePreparedQuery(sql, params);
    }

    @Override
    public Iterator<Map<String, Object>> executeQuery(final String sql) {
        return relDb.executeQuery(sql);
    }

    @Override
    public int executePreparedDml(final String sql, final List<?> params) {
        return relDb.executePreparedDml(sql, params);
    }

    @Override
    public int getInt(final String sql) {
        return relDb.getInt(sql);
    }

    @Override
    public long getLong(final String sql) {
        return relDb.getLong(sql);
    }

    @Override
    public String getUserName() {
        return relDb.getUserName();
    }

    @Override
    public SqlStrategy getSqlStrategy() {
        return relDb.getSqlStrategy();
    }

    @Override
    public String toString() {
        return relDb.toString();
    }

    @Override
    public int hashCode() {
        return relDb.hashCode();
    }

    @Override
    public boolean equals(final Object obj) {
        return relDb.equals(obj);
    }

    public Connection getConnection() {
        return ((SimpleRelDb) relDb).getConnection();
    }

    private final RelDbPool pool;
    private final RelDb relDb;
}
