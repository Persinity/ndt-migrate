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

package com.persinity.common.db.metainfo;

import static com.persinity.common.StringUtils.format;
import static com.persinity.common.StringUtils.formatObj;
import static com.persinity.common.invariant.Invariant.notEmpty;
import static com.persinity.common.invariant.Invariant.notNull;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.log4j.Logger;

import com.google.common.base.Function;
import com.persinity.common.db.metainfo.constraint.FK;
import com.persinity.common.db.metainfo.constraint.PK;
import com.persinity.common.logging.Log4jLogger;

/**
 * {@link Schema} that caches all meta-info on first request for faster access.
 *
 * @author dyordanov
 */
public class BufferedSchema implements Schema {

    public BufferedSchema(final Schema schema) {
        notNull(schema);

        this.schema = schema;

        table2ColsMap = new ConcurrentHashMap<>();
        table2PkMap = new ConcurrentHashMap<>();
        table2FksMap = new ConcurrentHashMap<>();
        pkName2TableMap = new ConcurrentHashMap<>();

        getColsF = new Function<String, Set<Col>>() {
            @Override
            public Set<Col> apply(final String input) {
                return schema.getTableCols(input);
            }
        };
        getFksF = new Function<String, Set<FK>>() {
            @Override
            public Set<FK> apply(final String input) {
                return schema.getTableFks(input);
            }
        };
        getPkF = new Function<String, PK>() {
            @Override
            public PK apply(final String input) {
                PK result = schema.getTablePk(input);
                if (result == null) {
                    result = DUMMY_PK;
                }
                return result;
            }
        };
        getTbF = new Function<String, String>() {
            @Override
            public String apply(final String input) {
                return schema.getTableName(input);
            }
        };
    }

    @Override
    public Set<Col> getTableCols(final String tableName) {
        return cacheCheckSetNGet(table2ColsMap, tableName, getColsF);
    }

    @Override
    public Set<FK> getTableFks(final String tableName) {
        return cacheCheckSetNGet(table2FksMap, tableName, getFksF);
    }

    @Override
    public Set<String> getTableNames() {
        if (tables == null || tables.size() == 0) {
            tables = schema.getTableNames();
        }
        return tables;
    }

    @Override
    public PK getTablePk(final String tableName) {
        PK result = cacheCheckSetNGet(table2PkMap, tableName, getPkF);
        if (result != DUMMY_PK)
            return result;
        else
            return null;
    }

    @Override
    public String getTableName(final String pkName) {
        return cacheCheckSetNGet(pkName2TableMap, pkName, getTbF);
    }

    @Override
    public String getUserName() {
        if (userName == null) {
            userName = schema.getUserName();
        }
        return userName;
    }

    @Override
    public String toString() {
        if (toString == null) {
            toString = format("{}({})", formatObj(this), schema);
        }
        return toString;
    }

    static <T> T cacheCheckSetNGet(final Map<String, T> cache, String key, final Function<String, T> getF) {
        notEmpty(key);

        key = key.toUpperCase();
        T val = cache.get(key);
        if (val == null) {
            if ((val = getF.apply(key)) != null) {
                cache.put(key, val);
            }
        }

        return val;
    }

    public static final boolean SKIP_WARMUP_FKS = false;
    public static final boolean WARMUP_FKS = true;
    private static final PK DUMMY_PK = new PK("pk_ndt_dummy", new HashSet<>(Arrays.asList(new Col("col_ndt_dummy"))));

    private final Schema schema;
    private final Map<String, Set<Col>> table2ColsMap;
    private final Map<String, PK> table2PkMap;
    private final Map<String, Set<FK>> table2FksMap;
    private final Map<String, String> pkName2TableMap;
    private final Function<String, Set<Col>> getColsF;
    private final Function<String, Set<FK>> getFksF;
    private final Function<String, PK> getPkF;
    private final Function<String, String> getTbF;

    private String userName;
    private Set<String> tables;
    private String toString;

    private static final Log4jLogger log = new Log4jLogger(Logger.getLogger(BufferedSchema.class));
}
