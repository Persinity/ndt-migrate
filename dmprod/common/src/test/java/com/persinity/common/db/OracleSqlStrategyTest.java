/**
 * Copyright (c) 2015 Persinity Inc.
 */

package com.persinity.common.db;

import static org.junit.Assert.assertEquals;

import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Properties;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;

import com.persinity.common.Config;
import com.persinity.common.db.metainfo.Col;
import com.persinity.common.db.metainfo.constraint.Constraint;
import com.persinity.common.db.metainfo.constraint.PK;

/**
 * @author dyordanov
 */
public class OracleSqlStrategyTest {

    @Before
    public void setUp() {
        expectedProps = Config.loadPropsFrom("sqlstrategy-unittest.properties");
        testee = new OracleSqlStrategy();
        tableCols = new LinkedHashSet<>();
        final Col col1 = new Col("id", "NUMBER(1)", false);
        tableCols.add(col1);
        tableCols.add(new Col("name", "VARCHAR2(20)", true));
        pidCols = Arrays.asList(new Col(PID_COL_NAME_1), new Col(PID_COL_NAME_2));
        pidSingleCols = Collections.singletonList(new Col(PID_COL_NAME_1));
    }

    @Test
    public void testBuildColClause_EmptyInput() throws Exception {
        final String actual = SqlUtil.buildColClause(Collections.<Col>emptyList());
        assertEquals("", actual);
    }

    @Test
    public void testBuildColClause() throws Exception {
        final String actual = SqlUtil.buildColClause(pidCols);
        assertEquals(PID_COL_NAME_1 + ", " + PID_COL_NAME_2, actual);
    }

    /**
     * Test method for {@link OracleSqlStrategy#dropTrigger(String)}.
     */
    @Test
    public void testDropTrigger() {
        final String actual = testee.dropTrigger(TRG_NAME);
        final String expected = "DROP TRIGGER " + TRG_NAME;
        assertEquals(expected, actual);
    }

    @Test
    public void testDropPackage() throws Exception {
        final String actual = testee.dropPackage("test");
        final String expected = "DROP PACKAGE test";
        assertEquals(expected, actual);
    }

    /**
     * Test method for {@link OracleSqlStrategy#createTable(String, java.util.Set, String)}
     */
    @Test
    public void testCreateTable() {
        final String actual = testee.createTable(TABLE_NAME, tableCols, "id");
        final String expected = expectedProps.getProperty("sql.testtable");

        System.out.println(expected);
        System.out.println(actual);

        assertEquals(expected, actual);
    }

    /**
     * Test method for {@link OracleSqlStrategy#dropTable(String)}
     */
    @Test
    public void testDropTable() {
        final String actual = testee.dropTable(TABLE_NAME);
        final String expected = "DROP TABLE " + TABLE_NAME;
        assertEquals(expected, actual);
    }

    /**
     * Test method for {@link OracleSqlStrategy#insertStatement(String, List)
     */
    @Test
    public void testInsertStatement() {
        final List<Col> cols = Arrays.asList(new Col(PID_COL_NAME_1), new Col("ename"), new Col("sal"));
        final String expected = "INSERT INTO " + TABLE_NAME + " (empid1, ename, sal) VALUES (?, ?, ?)";
        final String actual = testee.insertStatement(TABLE_NAME, cols);
        assertEquals(expected, actual);
    }

    /**
     * Test method for {@link OracleSqlStrategy#selectAllStatement(String)}
     */
    @Test
    public void testSelectAllStatement() {
        final String actual = testee.selectAllStatement("table");
        assertEquals("SELECT * FROM table", actual);
    }

    /**
     * Test method for {@link OracleSqlStrategy#selectStatement(String, List)}
     */
    @Test
    public void testSelectStatement() {
        final Col c1 = new Col("col1", "integer", true);
        String actual = testee.selectStatement("table", Collections.singletonList(c1));
        assertEquals("SELECT col1 FROM table", actual);

        final Col c2 = new Col("col2", "string", true);
        actual = testee.selectStatement("table", Arrays.asList(c1, c2));
        assertEquals("SELECT col1,col2 FROM table", actual);
    }

    /**
     * Test method for {@link OracleSqlStrategy#deleteStatement(String, List)}
     */
    @Test
    public void testDeleteStatement() {
        final String dstEntity = "emp";
        final String actual = testee.deleteStatement(dstEntity, pidCols);
        final String expected = "DELETE FROM emp WHERE (empid1 = ?) AND (empid2 = ?)";
        assertEquals(expected, actual);
    }

    /**
     * Test method for {@link OracleSqlStrategy#deleteStatement(String, List)}
     */
    @Test
    public void testDeleteStatement_OnePidCol() {
        final String dstEntity = "emp";
        final String actual = testee.deleteStatement(dstEntity, pidSingleCols);
        final String expected = "DELETE FROM emp WHERE empid1 = ?";
        assertEquals(expected, actual);
    }

    @Test
    public void testDisableConstraint() {
        final Constraint cons = new PK("pk_emp", "emp", tableCols);
        final String sql = testee.disableConstraint(cons);
        assertEquals("ALTER TABLE emp DISABLE CONSTRAINT pk_emp", sql);
    }

    @Test
    public void testEnableConstraint() {
        final Constraint cons = new PK("pk_emp", "emp", tableCols);
        final String sql = testee.enableConstraint(cons);
        assertEquals("ALTER TABLE emp ENABLE CONSTRAINT pk_emp", sql);
    }

    /**
     * Test method for {@link OracleSqlStrategy#createIndex(String, String)}
     */
    @Test
    public void testCreateIndex() {
        final String actual = testee.createIndex("table", "col1");
        assertEquals("CREATE INDEX index_table_col1 ON table (col1)", actual);
    }

    /**
     * Test method for {@link OracleSqlStrategy#deleteAllStatement(String)}
     */
    @Test
    public void testDeleteAllStatement() {
        final String dstEntity = "entity";
        final String actual = testee.deleteAllStatement(dstEntity);
        final String expected = "DELETE FROM entity";
        assertEquals(expected, actual);
    }

    @Test
    public void testGrantPrivs() throws Exception {
        final List<String> privs = Arrays.asList("SELECT", "INSERT", "UPDATE", "DELETE");
        final String actual = testee.grantPrivs(privs, "clog_emp", "testapp");
        final String expected = "GRANT SELECT, INSERT, UPDATE, DELETE ON clog_emp TO testapp";
        assertEquals(expected, actual);
    }

    @Test
    public void testTabConstraintsInfo() {
        final String actual = testee.tableConstraintsInfo();
        final String expected = "SELECT c.constraint_name AS constraint_name, " + "cc.table_name AS table_name, "
                + "cc.column_name AS column_name, " + "c.r_constraint_name AS r_constraint_name "
                + "FROM user_constraints c INNER JOIN user_cons_columns cc "
                + "ON (c.constraint_name = cc.constraint_name AND c.table_name = cc.table_name)"
                + "  WHERE c.table_name = ? AND c.constraint_type = ? " + "ORDER BY cc.position";
        assertEquals(expected, actual);
    }

    @Test
    public void testTabForPkInfo() {
        final String actual = testee.tableForPkInfo();
        final String expected = "SELECT table_name AS table_name FROM user_constraints WHERE constraint_name = ?";
        assertEquals(expected, actual);
    }

    /**
     * Test method for {@link OracleSqlStrategy#updateStatement(String, List, List)
     */
    @Test
    public void testUpdateStatement() {
        final List<Col> cols = Arrays
                .asList(new Col(PID_COL_NAME_1), new Col(PID_COL_NAME_2), new Col("ename"), new Col("sal"));
        final List<Col> pidCols = Arrays.asList(new Col(PID_COL_NAME_1), new Col(PID_COL_NAME_2));
        final String expected = "UPDATE " + TABLE_NAME
                + " SET empid1 = ?, empid2 = ?, ename = ?, sal = ? WHERE (empid1 = ?) AND (empid2 = ?)";
        final String actual = testee.updateStatement(TABLE_NAME, cols, pidCols);
        assertEquals(expected, actual);
    }

    /**
     * Test method for {@link OracleSqlStrategy#updateStatement(String, List, List)
     */
    @Test
    public void testUpdateStatement_OneCol() {
        final String expected = "UPDATE " + TABLE_NAME + " SET empid1 = ? WHERE empid1 = ?";
        final String actual = testee.updateStatement(TABLE_NAME, pidSingleCols, pidSingleCols);
        assertEquals(expected, actual);
    }

    /**
     * Test method for {@link OracleSqlStrategy#updateStatement(String, List, List)
     */
    @Test
    public void testUpdateStatement_OnePidCol() {
        final List<Col> colsSinglePid = Arrays.asList(new Col(PID_COL_NAME_1), new Col("ename"), new Col("sal"));
        final String expected = "UPDATE " + TABLE_NAME + " SET empid1 = ?, ename = ?, sal = ? WHERE empid1 = ?";
        final String actual = testee.updateStatement(TABLE_NAME, colsSinglePid, pidSingleCols);
        assertEquals(expected, actual);
    }

    private static final String TRG_NAME = "trg_testtable";
    private static final String TABLE_NAME = "testtable";
    private static final String PID_COL_NAME_1 = "empid1";
    private static final String PID_COL_NAME_2 = "empid2";

    private OracleSqlStrategy testee;
    private Set<Col> tableCols;
    private Properties expectedProps;
    private List<Col> pidCols;
    private List<Col> pidSingleCols;
}