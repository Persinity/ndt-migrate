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

import java.util.Set;

import org.apache.log4j.Logger;

import com.google.common.base.Stopwatch;
import com.persinity.common.collection.DirectedEdge;
import com.persinity.common.collection.Pool;
import com.persinity.common.db.metainfo.Schema;
import com.persinity.common.logging.Log4jLogger;

/**
 * @author dyordanov
 */
public class RelDbUtil {

    /**
     * @param schema
     *         to warm up the cache
     */
    public static void warmUpCache(final Schema schema, final boolean warmUpFks) {
        log.debug("Warming up {} of type {}", schema, schema.getClass().getSimpleName());
        final Stopwatch st = Stopwatch.createStarted();
        schema.getUserName();
        final Set<String> tableNames = schema.getTableNames();
        log.info("Warming up tables: {} {} {}...", tableNames.size(), warmUpFks ? "with FKs" : "without FKs", schema);
        for (String tableName : tableNames) {
            schema.getTableCols(tableName);
            schema.getTablePk(tableName);
            if (warmUpFks) {
                schema.getTableFks(tableName);
            }
        }
        st.stop();
        log.info("Warm up done for {}", st);
    }

    /**
     * @param sqlStrategyClassName
     * @return {@link SqlStrategy} instance
     */
    public static SqlStrategy newSqlStrategyFor(final String sqlStrategyClassName) {
        SqlStrategy sqlStrategy;
        try {
            final Class<? extends SqlStrategy> sqlStrategyClass = (Class<? extends SqlStrategy>) Class
                    .forName(sqlStrategyClassName);
            sqlStrategy = sqlStrategyClass.newInstance();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return sqlStrategy;
    }

    /**
     * @param relDbPoolBridge
     * @param relDbBridge
     *         to remove from the pool bridge.
     */
    public static void closeBridge(final DirectedEdge<Pool<RelDb>, Pool<RelDb>> relDbPoolBridge,
            final DirectedEdge<RelDb, RelDb> relDbBridge) {
        relDbBridge.src().close();
        relDbBridge.dst().close();
    }

    /**
     * @param relDbPoolBridge
     * @return db bridge retrieved from the pool bridge.
     */
    public static <S extends Closeable, D extends Closeable> DirectedEdge<S, D> getBridge(
            final DirectedEdge<Pool<S>, Pool<D>> relDbPoolBridge) {
        return new DirectedEdge<>(relDbPoolBridge.src().get(), relDbPoolBridge.dst().get());
    }

    private static final Log4jLogger log = new Log4jLogger(Logger.getLogger(RelDbUtil.class));
}
