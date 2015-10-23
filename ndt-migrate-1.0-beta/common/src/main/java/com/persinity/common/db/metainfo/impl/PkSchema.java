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

package com.persinity.common.db.metainfo.impl;

import static com.persinity.common.db.metainfo.Col.toColsMap;
import static com.persinity.common.invariant.Invariant.notEmpty;
import static com.persinity.common.invariant.Invariant.notNull;

import java.util.Arrays;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;

import com.persinity.common.collection.DirectedEdge;
import com.persinity.common.db.RelDb;
import com.persinity.common.db.SqlStrategy;
import com.persinity.common.db.metainfo.BufferedSchema;
import com.persinity.common.db.metainfo.Col;
import com.persinity.common.db.metainfo.Schema;
import com.persinity.common.db.metainfo.constraint.FK;
import com.persinity.common.db.metainfo.constraint.PK;
import com.persinity.common.logging.Log4jLogger;
import com.persinity.ndt.transform.ParamQryFunc;

/**
 * {@link Schema} that knows how to retrieve PK meta-info only. Non PK retrievers throw {@code UnsupportedOperationException}
 *
 * @author dyordanov
 */
public class PkSchema implements Schema {

    public PkSchema(final RelDb db, final BufferedSchema tableSchema, final ParamQryFunc tabConsF,
            final ParamQryFunc tabNameForPkNameF) {
        notNull(db);
        notNull(tableSchema);
        notNull(tabConsF);
        notNull(tabNameForPkNameF);

        this.db = db;
        this.tabConsF = tabConsF;
        this.tabNameForPkNameF = tabNameForPkNameF;
        this.tableSchema = tableSchema;
    }

    @Override
    public Set<Col> getTableCols(final String tableName) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Set<FK> getTableFks(final String tableName) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Set<String> getTableNames() {
        throw new UnsupportedOperationException();
    }

    @Override
    public PK getTablePk(final String tableName) {
        notEmpty(tableName);

        final String tableNameLower = tableName.toLowerCase();
        final Iterator<Map<String, Object>> it = tabConsF.apply(toArgs(db, tableName.toUpperCase(), CONS_TYPE_PK));
        if (!it.hasNext()) {
            log.debug("[{}] Retrieved no PK for {}", db, tableName);
            return null;
        }

        final Map<String, Col> tableColsMap = toColsMap(tableSchema.getTableCols(tableName));

        String pkName = null;
        final Set<Col> pkCols = new LinkedHashSet<>();
        while (it.hasNext()) {
            final Map<String, Object> row = it.next();
            pkName = getConstraintName(row);
            final String colName = getConstraintColName(row);
            final Col col = tableColsMap.get(colName);
            notNull(col);
            pkCols.add(col);
        }

        final PK result = new PK(pkName, tableNameLower, pkCols);
        log.debug("[{}] Retrieved PK for {} : {}", db, tableNameLower, result);
        return result;
    }

    @Override
    public String getTableName(final String pkName) {
        notEmpty(pkName);

        final Iterator<Map<String, Object>> it = tabNameForPkNameF.apply(toArgs(db, pkName.toUpperCase()));
        if (!it.hasNext()) {
            log.debug("[{}] Retrieved no table for PK name: {}", db, pkName);
            return null;
        }

        final String tableName = it.next().values().iterator().next().toString().toLowerCase();
        log.debug("[{}] Retrieved for PK name: {} table: {}", db, pkName, tableName);
        return tableName;
    }

    @Override
    public String getUserName() {
        throw new UnsupportedOperationException();
    }

    static DirectedEdge<RelDb, List<?>> toArgs(final RelDb db, final String... args) {
        return new DirectedEdge<RelDb, List<?>>(db, Arrays.asList(args));
    }

    static String getConstraintColName(final Map<String, Object> row) {
        return ((String) row.get(SqlStrategy.COL_COLUMN_NAME)).toLowerCase();
    }

    static String getConstraintName(final Map<String, Object> row) {
        final String pkName;
        pkName = ((String) row.get(SqlStrategy.COL_CONSTRAINT_NAME)).toLowerCase();
        return pkName;
    }

    static final String CONS_TYPE_PK = "P";

    private final RelDb db;
    private final ParamQryFunc tabConsF;
    private final ParamQryFunc tabNameForPkNameF;
    private final BufferedSchema tableSchema;

    private static final Log4jLogger log = new Log4jLogger(Logger.getLogger(PkSchema.class));
}
