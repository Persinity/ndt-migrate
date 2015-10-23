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

import static com.persinity.common.StringUtils.format;
import static com.persinity.common.StringUtils.formatObj;
import static com.persinity.common.db.RelDbUtil.newSqlStrategyFor;
import static com.persinity.common.invariant.Invariant.notNull;

import java.util.Set;

import com.google.common.base.Function;
import com.persinity.common.collection.ManualPool;
import com.persinity.common.collection.Pool;
import com.persinity.common.db.metainfo.ProxySchema;
import com.persinity.common.db.metainfo.Schema;

/**
 * @author dyordanov
 */
public class RelDbPool implements Pool<RelDb> {

    public RelDbPool(final ConnectionSource connSource, final DbConfig dbConfig) {
        notNull(dbConfig);
        notNull(connSource);
        this.connSource = connSource;
        this.dbConfig = dbConfig;

        sqlStrategy = newSqlStrategyFor(dbConfig.getSqlStrategy());

        schemaRelDb = new SimpleRelDb(dbConfig);
        metaInfo = ProxySchema.newSchema(schemaRelDb, sqlStrategy, dbConfig.getSkipTables());

        final Function<RelDb, RelDb> closeRelDbF = new Function<RelDb, RelDb>() {
            @Override
            public RelDb apply(final RelDb input) {
                input.close();
                return input;
            }
        };
        final Function<Void, RelDb> newRelDbF = new Function<Void, RelDb>() {
            @Override
            public RelDb apply(final Void input) {
                final RelDb relDb = new SimpleRelDb(connSource.getConnection(), sqlStrategy, metaInfo,
                        dbConfig.getDbEnableOutput(), dbConfig.getCacheSql(), dbConfig.getSkipTables());
                return relDb;
            }
        };
        relDbPool = new ManualPool<>(newRelDbF, closeRelDbF);
    }

    @Override
    public RelDb get() {
        final RelDb relDb = relDbPool.get();
        final RelDb result = new PooledRelDb(getPool(), relDb);
        return result;
    }

    @Override
    public void remove(final RelDb value) {
        if (value instanceof PooledRelDb) {
            throw new UnsupportedOperationException("To remove this pooled entry, call its close() method.");
        }
        relDbPool.remove(value);
    }

    @Override
    public Set<RelDb> entries() {
        return relDbPool.entries();
    }

    @Override
    public void close() throws RuntimeException {
        relDbPool.close();
        schemaRelDb.close();
        connSource.close();
    }

    @Override
    public String toString() {
        if (toString == null) {
            toString = format("{}({})", formatObj(this), metaInfo);
        }
        return toString;
    }

    /**
     * @return {@link Schema} describing all connections returned by this pool
     */
    public Schema metaInfo() {
        return metaInfo;
    }

    private RelDbPool getPool() {
        return this;
    }

    private final Pool<RelDb> relDbPool;
    private final DbConfig dbConfig;
    private final ConnectionSource connSource;
    private final SqlStrategy sqlStrategy;
    private final RelDb schemaRelDb;
    private final Schema metaInfo;
    private String toString;
}
