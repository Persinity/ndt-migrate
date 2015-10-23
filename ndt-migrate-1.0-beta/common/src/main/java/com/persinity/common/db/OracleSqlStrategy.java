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
import static com.persinity.common.collection.CollectionUtils.implode;
import static com.persinity.common.invariant.Invariant.notEmpty;

import java.sql.SQLException;
import java.util.Collection;
import java.util.List;
import java.util.Set;

import com.google.common.base.Function;
import com.persinity.common.collection.CollectionUtils;
import com.persinity.common.db.metainfo.And;
import com.persinity.common.db.metainfo.Col;
import com.persinity.common.db.metainfo.Params;
import com.persinity.common.db.metainfo.constraint.Constraint;

/**
 * @author dyordanov
 */
public class OracleSqlStrategy implements SqlStrategy {

    public OracleSqlStrategy() {
        trimmer = new Trimmer();
    }

    @Override
    public String createTable(final String tableName, final Set<Col> cols, final String pkColName) {

        assert tableName != null && !tableName.isEmpty();
        assert cols != null && !cols.isEmpty();

        final StringBuilder sb = new StringBuilder();
        sb.append("CREATE TABLE ").append(tableName).append(" (");
        boolean firstRowFlg = true;
        for (final Col col : cols) {
            if (!firstRowFlg) {
                sb.append(", ");
            } else {
                firstRowFlg = false;
            }

            sb.append("\n\t\t\t").append(col.getName()).append(" ").append(col.getType());
            if (pkColName.equals(col.getName())) {
                sb.append(" ").append("CONSTRAINT pk_").append(tableName).append(" PRIMARY KEY");
            }
        }
        sb.append("\n\t\t)");

        final String sql = sb.toString();
        return sql;
    }

    @Override
    public String disableConstraint(final Constraint cons) {
        assert cons != null;
        return format(CONSTRAINT_DISABLE_TEMPLATE, cons.getTable(), cons.getName());
    }

    @Override
    public String dropTable(final String tableName) {
        assert tableName != null && !tableName.isEmpty();

        final String result = format("DROP TABLE {}", tableName);
        return result;
    }

    @Override
    public String dropTrigger(final String triggerName) {
        assert triggerName != null && !triggerName.isEmpty();
        final String result = format("DROP TRIGGER {}", triggerName);
        return result;
    }

    @Override
    public String enableConstraint(final Constraint cons) {
        assert cons != null;
        return format(CONSTRAINT_ENABLE_TEMPLATE, cons.getTable(), cons.getName());
    }

    @Override
    public int getMaxNameLength() {
        return 25;
    }

    @Override
    public String insertStatement(final String tableName, final List<Col> cols) {
        assert tableName != null && !tableName.trim().isEmpty();
        assert cols != null && !cols.isEmpty();

        final String colClause = SqlUtil.buildColClause(cols);
        final String sql = format("INSERT INTO {} ({}) VALUES ({})", tableName, colClause,
                new Params(cols.size(), Params.ParameterCount.EXACT));
        return sql;
    }

    @Override
    public String selectAllStatement(final String tableName) {
        assert tableName != null && !tableName.trim().isEmpty();

        return format("SELECT * FROM {}", tableName);
    }

    @Override
    public String selectStatement(final String tableName, final List<Col> cols) {
        assert tableName != null && !tableName.trim().isEmpty();
        assert cols != null && !cols.isEmpty();

        final String colsList = implode(cols, ",", new Function<Col, String>() {
            @Override
            public String apply(final Col col) {
                return col.getName();
            }
        });

        return format("SELECT {} FROM {}", colsList, tableName);
    }

    @Override
    public String deleteStatement(final String tableName, final List<Col> ids) {
        assert tableName != null && !tableName.trim().isEmpty();
        assert ids != null && !ids.isEmpty();

        return format("DELETE FROM {} WHERE {}", tableName, new And(SqlUtil.toEqualParams(ids)));
    }

    @Override
    public String deleteAllStatement(final String tableName) {
        assert tableName != null && !tableName.trim().isEmpty();

        return format("DELETE FROM {}", tableName);
    }

    @Override
    public String grantPrivs(final Collection<String> privs, final String onObject, final String toUser) {
        notEmpty(privs);
        notEmpty(onObject);
        notEmpty(toUser);

        return format(GRANT_PRIVS_TEMPLATE, CollectionUtils.implode(privs, ", "), onObject, toUser);
    }

    @Override
    public String tableConstraintsInfo() {
        return TAB_CONSTRAINTS_QRY;
    }

    @Override
    public String tableForPkInfo() {
        return TABNAME_FOR_PKNAME_QRY;
    }

    @Override
    public String dropPackage(final String packageName) {
        notEmpty(packageName);
        return format("DROP PACKAGE {}", packageName);
    }

    @Override
    public String updateStatement(final String tableName, final List<Col> cols, final List<Col> ids) {
        assert tableName != null && !tableName.trim().isEmpty();
        assert cols != null && !cols.isEmpty();
        assert ids != null && !ids.isEmpty();

        final String setColList = implode(cols, ", ", new Function<Col, String>() {
            @Override
            public String apply(final Col col) {
                return format("{} = ?", col.getName());
            }
        });
        final String sql = format("UPDATE {} SET {} WHERE {}", tableName, setColList,
                new And(SqlUtil.toEqualParams(ids)));
        return sql;
    }

    @Override
    public String createIndex(final String tableName, final String colName) {
        notEmpty(tableName);
        notEmpty(colName);

        final String indexName = trimmer.trim(format("index_{}_{}", tableName, colName), getMaxNameLength());

        return format("CREATE INDEX {} ON {} ({})", indexName, tableName, colName);
    }

    @Override
    public String count(final String colName) {
        return format("COUNT({})", colName);
    }

    @Override
    public String distinct(final String string) {
        return format("DISTINCT {}", string);
    }

    @Override
    public String max(final String colName) {
        return format("NVL(MAX({}), 0)", colName);
    }

    @Override
    public String min(final String colName) {
        return format("NVL(MIN({}), 0)", colName);
    }

    @Override
    public String mod(final String colName, final String divisor) {
        return format("MOD({}, {})", colName, divisor);
    }

    @Override
    public String hash(final List<Col> cols) {
        assert cols != null && !cols.isEmpty();

        final String colsConcatenation = implode(cols, "||", new Function<Col, String>() {
            @Override
            public String apply(final Col col) {
                return format("NVL({}, 0)", col.getName());
            }
        });

        return format("ORA_HASH(''||{})", colsConcatenation);
    }

    @Override
    public boolean isIntegrityConstraintViolation(final Throwable cause) {
        // check in SQL state for 23: integrity constraint violation
        return extractSqlState(cause).startsWith("23");
    }

    @Override
    public boolean isAccessRuleViolation(final Throwable cause) {
        // check in SQL state for 42: syntax error or access rule violation
        return extractSqlState(cause).startsWith("42");
    }

    private String extractSqlState(final Throwable cause) {
        String sqlState = null;
        if (cause instanceof SQLException) {
            sqlState = ((SQLException) cause).getSQLState();
        }
        if (sqlState == null) {
            sqlState = "";
        }
        return sqlState;
    }

    private static final String GRANT_PRIVS_TEMPLATE = "GRANT {} ON {} TO {}";
    private static final String CONSTRAINT_ENABLE_TEMPLATE = "ALTER TABLE {} ENABLE CONSTRAINT {}";
    private static final String CONSTRAINT_DISABLE_TEMPLATE = "ALTER TABLE {} DISABLE CONSTRAINT {}";
    private static final String TAB_CONSTRAINTS_QRY =
            "SELECT c.constraint_name AS " + SqlStrategy.COL_CONSTRAINT_NAME + ", cc.table_name AS "
                    + SqlStrategy.COL_TABLE_NAME + ", cc.column_name AS " + SqlStrategy.COL_COLUMN_NAME
                    + ", c.r_constraint_name AS " + SqlStrategy.COL_REF_CONSTRAINT_NAME + " FROM user_constraints c "
                    + "INNER JOIN user_cons_columns cc "
                    + "ON (c.constraint_name = cc.constraint_name AND c.table_name = cc.table_name)"
                    + "  WHERE c.table_name = ? AND c.constraint_type = ? " + "ORDER BY cc.position";
    private static final String TABNAME_FOR_PKNAME_QRY = "SELECT table_name AS " + SqlStrategy.COL_TABLE_NAME +
            " FROM user_constraints WHERE constraint_name = ?";

    private final Trimmer trimmer;
}
