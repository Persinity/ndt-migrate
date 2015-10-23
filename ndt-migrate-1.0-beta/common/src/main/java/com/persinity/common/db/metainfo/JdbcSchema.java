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

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;

import com.persinity.common.Resource;
import com.persinity.common.Resource.Accessor;
import com.persinity.common.db.metainfo.constraint.FK;
import com.persinity.common.db.metainfo.constraint.PK;
import com.persinity.common.db.metainfo.constraint.Unique;
import com.persinity.common.logging.Log4jLogger;

/**
 * {@link Schema} that reflects via JDBC meta info capabilities.
 *
 * @author dyordanov
 */
public class JdbcSchema implements Schema {

    public JdbcSchema(final Connection conn) {
        notNull(conn);

        this.conn = conn;
        try {
            catalog = conn.getCatalog();
            userName = conn.getMetaData().getUserName().toUpperCase();
            userNameLower = userName.toLowerCase();
            connToString = format("{}@{}", userNameLower, Integer.toHexString(conn.hashCode()));
        } catch (final SQLException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * @param tableName
     * @return Sorted set of columns for the given table or empty set if none found
     */
    public Set<Col> getTableCols(final String tableName) {
        notEmpty(tableName);

        final String tableNameLower = tableName.toLowerCase();
        final Set<Col> cols = new LinkedHashSet<>();

        try {
            final ResultSet rs = conn.getMetaData().getColumns(catalog, userName, tableName.toUpperCase(), "%");
            while (rs.next()) {
                final Col col = new Col(rs);
                cols.add(col);
            }
        } catch (final SQLException e) {
            throw new RuntimeException(format("Failed to get columns for {}", tableNameLower), e);
        }

        final Set<Col> resCols = Collections.unmodifiableSet(cols);
        log.debug("[{}] Retrieved cols for {} : {}", connToString, tableNameLower, resCols);
        return resCols;
    }

    /**
     * @param tableName
     * @return
     */
    public Set<FK> getTableFks(final String tableName) {
        notEmpty(tableName);

        final String tableNameUpper = tableName.toUpperCase();

        final ResultSet resultSet = resource.access(new Accessor<Connection, ResultSet>(conn, null) {
            @Override
            public ResultSet access(final Connection resource) throws Exception {
                return resource.getMetaData().getImportedKeys(catalog, userName, tableNameUpper);
            }
        });

        final Set<FK> fks = resource.accessAndAutoClose(
                new Resource.Accessor<ResultSet, Set<FK>>(resultSet, format("getTableFks({})", tableName)) {

                    @Override
                    public Set<FK> access(final ResultSet resource) throws Exception {

                        final Set<FK> result = new HashSet<>();
                        Map<String, Col> srcColsMap = Col.toColsMap(getTableCols(tableName));

                        int seq = Integer.MIN_VALUE;
                        Set<Col> dstColumns = new HashSet<>(), srcColumns = new HashSet<>();
                        String dstTable = null, srcTable = null;
                        String name = null;
                        Map<String, Col> dstColsMap = null;

                        while (resource.next()) {
                            if (seq >= resource.getInt(SEQ_INDEX)) {
                                addFk(result, dstColumns, srcColumns, dstTable, srcTable, name);
                                dstColsMap = null;
                                dstColumns = new HashSet<>();
                                srcColumns = new HashSet<>();
                            }
                            seq = resource.getInt(SEQ_INDEX);

                            name = resource.getString(FK_NAME_INDEX);
                            if (name == null) {
                                // TODO use sql strategy to get constraint name from the catalog.
                            }
                            name = name.toLowerCase();
                            dstTable = resource.getString(PK_TABLE_INDEX).toLowerCase();
                            if (dstColsMap == null) {
                                dstColsMap = Col.toColsMap(getTableCols(dstTable));
                            }
                            final String dstColName = resource.getString(PK_COLUMN_INDEX).toLowerCase();
                            dstColumns.add(dstColsMap.get(dstColName));

                            srcTable = resource.getString(FK_TABLE_INDEX).toLowerCase();
                            assert srcTable.equals(tableName);
                            final String srcColName = resource.getString(FK_COLUMN_INDEX).toLowerCase();
                            srcColumns.add(srcColsMap.get(srcColName));

                        }
                        if (!dstColumns.isEmpty()) {
                            addFk(result, dstColumns, srcColumns, dstTable, srcTable, name);
                        }
                        return result;
                    }
                });

        final Set<FK> resFks = Collections.unmodifiableSet(fks);
        log.debug("[{}] Retrieved FK for {} : {}", connToString, tableName, resFks);
        return resFks;
    }

    /**
     * @return The set of tables from the schema of the current connection or empty set if none found.
     */
    public Set<String> getTableNames() {
        final Set<String> tableNames = new HashSet<>();

        try {
            ResultSet rs = conn.getMetaData().getTables(catalog, userName, "%", new String[] { "TABLE" });
            while (rs.next()) {
                final String tableName = rs.getString(3).toLowerCase();
                tableNames.add(tableName);
            }
        } catch (final SQLException e) {
            throw new RuntimeException(e);
        }

        return tableNames;
    }

    /**
     * @param tableName
     * @return The PK constraint or null if none found
     */
    public PK getTablePk(final String tableName) {
        notEmpty(tableName);

        final String tableNameUpper = tableName.toUpperCase();

        final Set<Col> tableCols = getTableCols(tableName);
        final Map<String, Col> tableColsMap = new HashMap<>();
        for (final Col tableCol : tableCols) {
            tableColsMap.put(tableCol.getName(), tableCol);
        }

        final ResultSet rs = resource.access(new Accessor<Connection, ResultSet>(conn, null) {
            @Override
            public ResultSet access(final Connection resource) throws Exception {
                return resource.getMetaData().getPrimaryKeys(catalog, userName, tableNameUpper);
            }
        });
        final PK pk = resource.accessAndAutoClose(new Accessor<ResultSet, PK>(rs, format("getTablePk({})", tableName)) {
            @Override
            public PK access(final ResultSet resource) throws Exception {
                String constraintName = null;
                final Set<Col> pkCols = new LinkedHashSet<>();
                while (resource.next()) {
                    constraintName = resource.getString(PK_NAME_INDEX);
                    if (constraintName == null) {
                        // TODO use sql strategy to get constraint name from the catalog.
                    }
                    constraintName = constraintName.toLowerCase();

                    final String colName = resource.getString(4).toLowerCase();
                    final Col col = tableColsMap.get(colName);
                    notNull(col);
                    pkCols.add(col);
                }
                PK pk = null;
                if (pkCols.size() > 0) {
                    pk = new PK(constraintName, tableName.toLowerCase(), pkCols);
                }
                return pk;
            }
        });

        log.debug("[{}] Retrieved PK for {} : {}", connToString, tableName, pk);
        return pk;
    }

    @Override
    public String getTableName(final String pkName) {
        notEmpty(pkName);

        for (String table : getTableNames()) {
            final PK pk = getTablePk(table);
            if (pk != null && pkName.equals(pk.getName())) {
                return pk.getTable();
            }
        }

        return null;
    }

    @Override
    public String toString() {
        if (toString == null) {
            toString = format("{}('{}')", formatObj(this), connToString);
        }
        return toString;
    }

    public String getUserName() {
        return userNameLower;
    }

    private void addFk(final Set<FK> result, final Set<Col> dstColumns, final Set<Col> srcColumns,
            final String dstTable, final String srcTable, final String name) {
        final Unique dstConstraint = new Unique(dstTable, dstColumns);
        final FK fk = new FK(name, srcTable, srcColumns, dstConstraint);
        result.add(fk);
    }

    private static final int PK_NAME_INDEX = 6;
    private static final int FK_NAME_INDEX = 12;
    private static final int SEQ_INDEX = 9;
    private static final int FK_COLUMN_INDEX = 8;
    private static final int FK_TABLE_INDEX = 7;

    private static final int PK_COLUMN_INDEX = 4;
    private static final int PK_TABLE_INDEX = 3;

    private final String catalog;

    private final Connection conn;
    private final String userName;
    private final String userNameLower;
    private final String connToString;

    private String toString;

    private static final Resource resource = new Resource();
    private static final Log4jLogger log = new Log4jLogger(Logger.getLogger(Schema.class));
}
