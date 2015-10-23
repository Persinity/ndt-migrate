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
import static com.persinity.common.invariant.Invariant.notNull;

import java.sql.Connection;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import com.persinity.common.db.PooledRelDb;
import com.persinity.common.db.ProxyRelDb;
import com.persinity.common.db.RelDb;
import com.persinity.common.db.SimpleRelDb;
import com.persinity.common.db.SqlStrategy;
import com.persinity.common.db.metainfo.constraint.FK;
import com.persinity.common.db.metainfo.constraint.PK;
import com.persinity.common.db.metainfo.impl.FkSchema;
import com.persinity.common.db.metainfo.impl.PkSchema;
import com.persinity.ndt.transform.ParamQryFunc;

/**
 * Proxy {@link Schema} that operates with {@link ProxySchema} delegate for the efficient retrieval of FK meta-info
 * and delegates the rest of the calls to the supplied {@link JdbcSchema} delegate.
 *
 * @author dyordanov
 */
public class ProxySchema implements Schema {

    /**
     * @param db
     *         to use for getting user name meta-info
     * @param tableSchema
     *         to use for getting table and column meta-info
     * @param pkSchema
     *         to use for getting PK meta-info
     * @param fkSchema
     *         to use for getting FK meta-info
     */
    ProxySchema(final RelDb db, final BufferedSchema tableSchema, final BufferedSchema pkSchema,
            final BufferedSchema fkSchema) {
        notNull(db);
        notNull(tableSchema);
        notNull(pkSchema);
        notNull(fkSchema);

        this.db = db;
        this.tableSchema = tableSchema;
        this.pkSchema = pkSchema;
        this.fkSchema = fkSchema;
    }

    @Override
    public Set<Col> getTableCols(final String tableName) {
        return tableSchema.getTableCols(tableName);
    }

    @Override
    public Set<FK> getTableFks(final String tableName) {
        return fkSchema.getTableFks(tableName);
    }

    @Override
    public Set<String> getTableNames() {
        return tableSchema.getTableNames();
    }

    @Override
    public PK getTablePk(final String tableName) {
        return pkSchema.getTablePk(tableName);
    }

    @Override
    public String getTableName(final String pkName) {
        return pkSchema.getTableName(pkName);
    }

    @Override
    public String getUserName() {
        if (userName == null) {
            userName = db.getUserName();
        }
        return userName;
    }

    @Override
    public String toString() {
        if (toString == null) {
            toString = format("{}({})", formatObj(this), db);
        }
        return toString;
    }

    /**
     * @param db
     * @return Schema ready to be used by NDT logic.
     */
    public static Schema newSchema(final RelDb db, final SqlStrategy sqlStrategy, final Collection<String> skipTables) {
        notNull(db);
        notNull(sqlStrategy);
        notNull(skipTables);

        final Connection conn;
        if (db instanceof SimpleRelDb) {
            conn = ((SimpleRelDb) db).getConnection();
        } else if (db instanceof PooledRelDb) {
            conn = ((PooledRelDb) db).getConnection();
        } else if (db instanceof ProxyRelDb) {
            conn = ((ProxyRelDb) db).getConnection();
        } else {
            throw new IllegalArgumentException(format("{} is not supported!", db.getClass().getSimpleName()));
        }
        final BufferedSchema tableSchema = new BufferedSchema(new JdbcSchema(conn));
        final ParamQryFunc tabConsF = new ParamQryFunc(CONSTRAINT_COLS, sqlStrategy.tableConstraintsInfo());
        final ParamQryFunc tabNameForPkNameF = new ParamQryFunc(PK_TABLE_COLS, sqlStrategy.tableForPkInfo());
        final BufferedSchema pkSchema = new BufferedSchema(new PkSchema(db, tableSchema, tabConsF, tabNameForPkNameF));
        final BufferedSchema fkSchema = new BufferedSchema(new FkSchema(db, tableSchema, pkSchema, tabConsF));
        Schema result = new ProxySchema(db, tableSchema, pkSchema, fkSchema);
        if (!skipTables.isEmpty()) {
            result = new SkipTablesSchema(skipTables, result);
        }
        return result;
    }

    /**
     * @param db
     * @return Schema ready to be used by NDT logic.
     */
    public static Schema newSchema(final RelDb db, final SqlStrategy sqlStrategy) {
        return newSchema(db, sqlStrategy, Collections.<String>emptyList());
    }

    private static final List<Col> CONSTRAINT_COLS = Arrays
            .asList(new Col(SqlStrategy.COL_CONSTRAINT_NAME), new Col(SqlStrategy.COL_TABLE_NAME),
                    new Col(SqlStrategy.COL_COLUMN_NAME), new Col(SqlStrategy.COL_REF_CONSTRAINT_NAME));
    private static final List<Col> PK_TABLE_COLS = Collections.singletonList(new Col(SqlStrategy.COL_TABLE_NAME));

    private final RelDb db;
    private final Schema pkSchema;
    private final Schema tableSchema;
    private final Schema fkSchema;
    private String toString;
    private String userName;
}
