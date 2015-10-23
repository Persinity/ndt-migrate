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

import java.sql.Connection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.persinity.common.db.metainfo.Schema;

/**
 * @author dyordanov
 */
public interface RelDb extends Closeable {
    /**
     * DB output buffered in DB is collected and logged. Use for debugging and avoid in production due to performance
     * overhead.
     */
    boolean OUTPUT_ENABLED = true;
    /**
     * DB output buffered in DB is not collected. Use for production for better peformance.
     */
    boolean OUTPUT_DISABLED = false;

    /**
     * May boost performance for DB drivers that do not support prepared statement caching.
     */
    boolean CACHE_SQL = true;
    /**
     * Preserves memory resources possibly at the price of worse SQL performance
     */
    boolean DONT_CACHE_SQL = false;

    Schema metaInfo();

    /**
     * @param sql
     *         DML or DDL SQL string
     */
    int executeDmdl(String sql);

    /**
     * Executes SP that does not have OUTPUT params.
     *
     * @param signature
     *         In the form sp_name(?, ?)
     * @param params
     *         actual values for the formal parameters
     */
    void executeSp(String signature, Object... params);

    /**
     * Execute store function that returns numeric parameter
     *
     * @param signature
     *         in the form sf_name(?, ?)
     * @param params
     *         actual values for the formal parameters
     * @return the value returned by the sf.
     */
    Long executeNumericSf(String signature, Object... params);

    /**
     * Releases underlying resources, and closes the wrapped {@link Connection}
     */
    void close();

    /**
     * @return {@code true} if DB output is collected for the wrapped connection
     */
    boolean isDbOutputEnabled();

    /**
     * Issues DB commit
     */
    void commit();

    /**
     * Issues DB rollback
     */
    void rollback();

    /**
     * @param sql
     *         SQL query string
     * @return {@link Iterator} over result set of the executed query or over empty set if no result is returned.
     */
    Iterator<Object[]> query(String sql);

    /**
     * Executes SQL script, where lines are statements are delimited with "/" (put on separate line)
     *
     * @param scriptName
     *         The name of the script file. The file should be located within the system resources path, e.g.
     *         main/resources
     */
    void executeScript(String scriptName);

    /**
     * @param sql
     * @param params
     */
    Iterator<Map<String, Object>> executePreparedQuery(String sql, List<?> params);

    /**
     * @param sql
     */
    Iterator<Map<String, Object>> executeQuery(String sql);

    /**
     * @param sql
     * @param params
     * @return the number of affected rows
     */
    int executePreparedDml(String sql, List<?> params);

    /**
     * @param sql
     *         Query that returns single int, such as count query.
     * @return
     */
    int getInt(String sql);

    /**
     * @param sql
     *         Query that returns single long, such as count query.
     * @return
     */
    long getLong(String sql);

    String getUserName();

    SqlStrategy getSqlStrategy();

    /**
     * DB privileges
     */
    enum Privs {
        EXECUTE,
        INSERT,
        UPDATE,
        DELETE,
        SELECT
    }
}
