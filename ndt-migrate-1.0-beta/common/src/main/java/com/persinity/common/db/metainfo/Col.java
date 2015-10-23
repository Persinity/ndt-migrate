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

import static com.persinity.common.invariant.Invariant.notEmpty;

import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Provides meta information for a table column.
 *
 * @author Doichin Yordanov
 */
public class Col implements SqlPredicate {

    /**
     * @param cols
     *         to make Map for
     * @return the Map
     */
    public static Map<String, Col> toColsMap(final Set<Col> cols) {
        Map<String, Col> colsMap = new HashMap<>();
        for (Col col : cols) {
            colsMap.put(col.getName(), col);
        }
        return colsMap;
    }

    private static final Set<Integer> DECIMAL_JDBC_TYPES = new HashSet<Integer>(
            Arrays.asList(new Integer[] { Types.DECIMAL, Types.NUMERIC }));
    private static final Set<Integer> SIZEABLE_JDBC_TYPES = new HashSet<Integer>(Arrays.asList(
            new Integer[] { Types.ARRAY, Types.BIGINT, Types.CHAR, Types.INTEGER, Types.LONGNVARCHAR,
                    Types.LONGVARBINARY, Types.LONGNVARCHAR, Types.NCHAR, Types.NVARCHAR, Types.SMALLINT, Types.TINYINT,
                    Types.VARCHAR, Types.VARBINARY, Types.DECIMAL, Types.DOUBLE, Types.FLOAT, Types.NUMERIC,
                    Types.REAL }));

    static final int IDX_COL_NAME = 4;
    static final int IDX_COL_JDBCTYPE = 5;
    static final int IDX_COL_TYPE = 6;
    static final int IDX_COL_SIZE = 7;
    static final int IDX_COL_DECSIZE = 9;
    static final int IDX_NULLABLE = 11;

    private final String name;
    private final String type;
    private final boolean nullAllowed;

    /**
     * @param name
     *         Column name
     * @param type
     *         String representation of the column type, e.g. "VARCHAR", "NUMBER(1,0)" etc.
     */
    public Col(final String name, final String type, final boolean nullAllowed) {
        notEmpty(name);
        notEmpty(type);

        this.name = name;
        this.type = type;
        this.nullAllowed = nullAllowed;
    }

    public Col(final String name) {
        this.name = name;
        this.type = "";
        this.nullAllowed = false;
    }

    /**
     * @param rs
     *         Column description: A row from resultset of
     *         {@link DatabaseMetaData#getColumns(String, String, String, String)} call.
     */
    public Col(final ResultSet rs) {
        try {
            name = rs.getString(IDX_COL_NAME).toLowerCase();

            nullAllowed = rs.getInt(IDX_NULLABLE) == DatabaseMetaData.columnNullable;

            type = calcTypeSql(rs).toUpperCase();

        } catch (final Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * @return The column name
     */
    public String getName() {
        return name;
    }

    /**
     * @return The SQL type of the column, e.g. VARCHAR(20)
     */
    public String getType() {
        return type;
    }

    public boolean isNullAllowed() {
        return nullAllowed;
    }

    @Override
    public int hashCode() {
        return name.hashCode();
    }

    /**
     * @return true if the given column has the same name as this one.
     */
    @Override
    public boolean equals(final Object obj) {
        if (!(obj instanceof Col)) {
            return false;
        }

        final Col that = (Col) obj;
        if (this == that) {
            return true;
        }

        return name.equals(that.name);
    }

    /**
     * @return The name of the column
     */
    @Override
    public String toString() {
        return name;
    }

    /**
     * @param rs
     *         Resultset returned from {@code DatabaseMetaData#getColumns(String, String, String, String)}
     * @return The SQL representation of the type, e.g. VARCHAR2(20), NUMBER(1,0), etc
     * @throws SQLException
     */
    public static String calcTypeSql(final ResultSet rs) throws SQLException {
        String type = rs.getString(IDX_COL_TYPE);
        final int jdbcType = rs.getInt(IDX_COL_JDBCTYPE);
        Integer size = null;
        Integer decimalSize = null;
        if (SIZEABLE_JDBC_TYPES.contains(jdbcType)) {
            size = rs.getInt(IDX_COL_SIZE);
            if (size.equals(0)) {
                size = null;
            }
            if (size != null && DECIMAL_JDBC_TYPES.contains(jdbcType)) {
                decimalSize = rs.getInt(IDX_COL_DECSIZE);
                if (decimalSize.equals(-127)) {
                    decimalSize = null;
                }
            }
        }
        final StringBuilder sb = new StringBuilder();
        sb.append(type);
        if (size != null) {
            sb.append("(").append(size);
            if (decimalSize != null) {
                sb.append(", ");
                sb.append(decimalSize);
            }
            sb.append(")");
        }
        type = sb.toString();
        return type;
    }

}
