/**
 * Copyright (c) 2015 Persinity Inc.
 */

package com.persinity.common.db;

import java.util.Collection;
import java.util.List;
import java.util.Set;

import com.persinity.common.db.metainfo.Col;
import com.persinity.common.db.metainfo.constraint.Constraint;

/**
 * Strategy for SQL that abstracts RDBMS dialect differences.
 *
 * @author dyordanov
 */
public interface SqlStrategy {
    /**
     * @return query for FK meta-info
     */
    String tableConstraintsInfo();

    /**
     * @return query for PK meta-info
     */
    String tableForPkInfo();

    /**
     * @param tableName
     * @param cols
     * @param pkColName
     * @return SQL for creating a table with simple PK.
     */
    String createTable(String tableName, Set<Col> cols, String pkColName);

    /**
     * DELETE FROM {dstEntity} WHERE {pid} IN (?, ?)
     *
     * @param dstEntity
     * @param ids
     * @return
     */
    String deleteStatement(String dstEntity, List<Col> ids);

    /**
     * DELETE FROM {dstEntity}
     *
     * @param dstEntity
     * @return
     */
    String deleteAllStatement(String dstEntity);

    /**
     * ALTER TABLE tab DISABLE CONSTRAINT cons;
     *
     * @param fk
     * @return
     */
    String disableConstraint(Constraint fk);

    /**
     * @param tableName
     * @return SQL for dropping a table
     */
    String dropTable(String tableName);

    /**
     * @param triggerName
     * @return SQL for dropping a trigger
     */
    String dropTrigger(String triggerName);

    /**
     * ALTER TABLE tab ENABLE CONSTRAINT cons;
     *
     * @param cons
     * @return
     */
    String enableConstraint(Constraint cons);

    /**
     * @return the max allowed name length, e.g. 30 for Oracle DB objects.
     */
    int getMaxNameLength();

    /**
     * Forms SQL statement for inserting record into a table:<BR>
     * INSERT INTO clog_emp (gid, tid, ctype, empid, ename) VALUES (?, ?, ?, ?, ?)
     *
     * @param tableName
     *         The name of the table.
     * @param cols
     *         Table columns
     * @return SQL for inserting a record
     */
    String insertStatement(String tableName, List<Col> cols);

    /**
     * Forms SQL statement for selecting all records from a table :<BR>
     * SELECT * FROM table_name
     *
     * @param tableName
     *         The name of the table.
     * @return SQL for select all
     */
    String selectAllStatement(String tableName);

    /**
     * Forms SQL statement for selecting records from a table :<BR>
     * SELECT col1, col2, col3 FROM table_name
     *
     * @param tableName
     *         The name of the table.
     * @param cols
     *         Table columns
     * @return SQL for selecting a record
     */
    String selectStatement(String tableName, List<Col> cols);

    /**
     * @param tableName
     *         table name to create index
     * @param colName
     *         column name
     * @return statement to create an index for given table and column
     */
    String createIndex(String tableName, String colName);

    /**
     * @param packageName
     * @return SQL for dropping a package
     */
    String dropPackage(String packageName);

    /**
     * @param privs
     * @param onObject
     * @param toUser
     * @return SQL for granting the supplied privileges on the supplied object to the supplied user.
     */
    String grantPrivs(Collection<String> privs, String onObject, String toUser);

    /**
     * Forms SQL statement for updating record into a table:<BR>
     * UPDATE tab SET col1=?, col2=?, ... coln=? WHERE id=?
     *
     * @param tableName
     *         The name of the table.
     * @param cols
     *         Table columns
     * @param ids
     *         Adds filter by given id columns
     * @return SQL for updating a record
     */
    String updateStatement(String tableName, List<Col> cols, final List<Col> ids);

    /**
     * Wraps the supplied column into DB specific COUNT function, e.g. COUNT(empid) for Oracle.
     *
     * @param colName
     * @return
     */
    String count(String colName);

    /**
     * Wraps the supplied string into DB specific DISTINCT clause, e.g. DISTINCT empid for Oracle.
     *
     * @param string
     * @return
     */
    String distinct(String string);

    /**
     * Wraps the supplied column into DB specific MAX function, so that it does not return NULL values, e.g.
     * NVL(MAX(col), 0) for Oracle
     *
     * @param colName
     * @return
     */
    String max(String colName);

    /**
     * Wraps the supplied column into DB specific MIN function, so that it does not return NULL values, e.g.
     * NVL(MIN(col), 0) for Oracle
     *
     * @param colName
     * @return
     */
    String min(String colName);

    /**
     * Wraps the supplied column into DB specific hash function, e.g. MOD(col, divisor) for Oracle.
     *
     * @param colName
     * @param divisor
     *         Value for the modulo divisor
     * @return The DB specific analog of colName % divisor
     */
    String mod(String colName, String divisor);

    /**
     * @param cols
     * @return statement to calculate hash numeric value of given columns
     */
    String hash(List<Col> cols);

    /**
     * @param cause
     *         to verify for integrity constraint violation
     * @return true if it is a violation
     */
    public boolean isIntegrityConstraintViolation(final Throwable cause);

    /**
     * @param cause
     *         to verify for access rule violation
     * @return true if match
     */
    public boolean isAccessRuleViolation(final Throwable cause);

    String COL_TABLE_NAME = "table_name";
    String COL_CONSTRAINT_NAME = "constraint_name";
    String COL_COLUMN_NAME = "column_name";
    String COL_REF_CONSTRAINT_NAME = "r_constraint_name";
}
