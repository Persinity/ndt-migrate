/**
 * Copyright (c) 2015 Persinity Inc.
 */
package com.persinity.ndt.dbagent.relational.oracle;

import static com.persinity.ndt.dbagent.relational.AgentSqlStrategy.CTYPES_INS_UPD_DEL;
import static org.easymock.EasyMock.createNiceMock;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.replay;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

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
import com.persinity.common.db.metainfo.constraint.PK;
import com.persinity.ndt.dbagent.relational.AgentSqlStrategy;
import com.persinity.ndt.dbagent.relational.SchemaInfo;

/**
 * @author Doichin Yordanov
 */
public class OracleAgentSqlStrategyTest {

    @Before
    public void setUp() {
        expectedProps = Config.loadPropsFrom("dbagent-unittest.properties");
        testee = new OracleAgentSqlStrategy();
        tableCols = new LinkedHashSet<>();
        final Col col1 = new Col("id", "NUMBER(1)", false);
        tableCols.add(col1);
        tableCols.add(new Col("name", "VARCHAR2(20)", true));
        tablePk = new PK(TABLE_NAME, Collections.singleton(col1));

        pidCols = Arrays.asList(new Col(PID_COL_NAME_1), new Col(PID_COL_NAME_2));
        pidSingleCols = Collections.singletonList(new Col(PID_COL_NAME_1));
    }

    @Test
    public void testNextWindow() {
        final String actual = testee.nextWindow();
        final String expected = "SELECT trlog.tid, trlog.last_gid, trlog.tab_name\n" + " FROM \n" + "  (SELECT * FROM ("
                + "SELECT tid, MAX(last_gid) AS max_last_gid " + "FROM trlog WHERE status = 'R' "
                + "GROUP BY tid ORDER BY max_last_gid) WHERE rownum <= ?) torder\n"
                + "  INNER JOIN trlog ON (torder.tid = trlog.tid)\n" + " ORDER BY torder.max_last_gid, trlog.last_gid";
        assertEquals(expected, actual);
    }

    @Test
    public void testUnprocessedTidsCnt() throws Exception {
        final String actual = testee.countUnprocessedTids();
        final String expected = "SELECT COUNT(DISTINCT tid) AS cnt FROM trlog WHERE status = 'R'";
        assertEquals(expected, actual);
    }

    @Test
    public void testGetMaxGidStatement() throws Exception {
        final String actual = testee.getMaxGidStatement();
        final String expected = "SELECT NVL(MAX(last_gid), 0) FROM trlog";
        assertEquals(actual, expected);
    }

    /**
     * Test method for {@link OracleAgentSqlStrategy#createCdcClogTrigger(String, String, String, Set, PK, String)}
     */
    @Test
    public void testCreateCdcClogTrigger() {
        final String actual = testee
                .createCdcClogTrigger(NDT_USER_NAME, TRG_CLOG_NAME, TABLE_NAME, tableCols, tablePk, CLOG_NAME);
        final String expected = expectedProps.getProperty("sql.clog_trigger");
        assertEquals(expected, actual);
    }

    /**
     * Test method for
     * {@link OracleAgentSqlStrategy#createCdcTrlogTrigger(String, String, String, String, String)}
     */
    @Test
    public void testCreateCdcTrlogTrigger() {
        final String actual = testee
                .createCdcTrlogTrigger(NDT_USER_NAME, TRG_TRLOG_NAME, TABLE_NAME, CLOG_NAME, TRLOG_NAME);
        final String expected = expectedProps.getProperty("sql.trlog_trigger");
        assertEquals(expected, actual);
    }

    /**
     * Test method for {@link OracleAgentSqlStrategy#clogGcStatement(String, String)}
     */
    @Test
    public void testClogGcStatement() {
        final String actual = testee.clogGcStatement("clog_emp", "trlog");
        final String expected = "DELETE FROM clog_emp WHERE gid IN "
                + "(SELECT gid FROM clog_emp LEFT JOIN trlog ON (clog_emp.tid = trlog.tid AND trlog.tab_name = 'clog_emp')"
                + " WHERE trlog.last_gid IS NULL)";
        assertEquals(expected, actual);
    }

    /**
     * Test method for {@link OracleAgentSqlStrategy#clogExtractQuery(String, List, List, int, List)}
     */
    @Test
    public void testClogExtractQuery() {
        final List<Col> cols = Arrays.asList(new Col("gid"), new Col("tid"), new Col("ctype"), new Col(PID_COL_NAME_1),
                new Col(PID_COL_NAME_2), new Col("ename"), new Col("sal"));
        // Note how the "?" params for TID are rounded to the nearest i^2 number: 3 -> 4 params
        final String expected = "SELECT gid, tid, ctype, empid1, empid2, ename, sal FROM clog_testtable "
                + "WHERE (ctype IN ('I', 'U', 'D')) AND (MOD(ORA_HASH(''||NVL(empid1, 0)||NVL(empid2, 0)), ?) "
                + "BETWEEN ? AND ?) AND (tid IN (?, ?, ?, ?)) ORDER BY empid1, empid2, gid";
        final String actual = testee.clogExtractQuery(CLOG_NAME, cols, pidCols, TID_COUNT, CTYPES_INS_UPD_DEL);
        assertEquals(expected, actual);
    }

    /**
     * Test method for {@link OracleAgentSqlStrategy#clogExtractQuery(String, List, List, int, List)}
     */
    @Test
    public void testClogExtractQuery_OneIdCol() {
        final List<Col> cols = Arrays.asList(new Col("gid"), new Col("tid"), new Col("ctype"), new Col(PID_COL_NAME_1),
                new Col(PID_COL_NAME_2), new Col("ename"), new Col("sal"));
        // Note how the "?" params for TID are rounded to the nearest i^2 number: 3 -> 4 params
        final String expected = "SELECT gid, tid, ctype, empid1, empid2, ename, sal FROM clog_testtable "
                + "WHERE (ctype IN ('I', 'U', 'D')) AND (MOD(ORA_HASH(''||NVL(empid1, 0)), ?) BETWEEN ? AND ?) AND (tid IN (?, ?, ?, ?)) "
                + "ORDER BY empid1, gid";
        final String actual = testee.clogExtractQuery(CLOG_NAME, cols, pidSingleCols, TID_COUNT, CTYPES_INS_UPD_DEL);
        assertEquals(expected, actual);
    }

    /**
     * Test method for {@link OracleAgentSqlStrategy#trlogExtractQuery(String, List, int)}
     */
    @Test
    public void testTrlogExtractQuery() {
        final List<Col> cols = Arrays
                .asList(new Col("tid"), new Col("last_gid"), new Col("table_name"), new Col("status"));
        final String expected = "SELECT tid, last_gid, table_name, status FROM trlog WHERE tid IN (?, ?, ?, ?) ORDER BY last_gid";
        final String actual = testee.trlogExtractQuery(TRLOG_NAME, cols, TID_COUNT);
        assertEquals(expected, actual);
    }

    /**
     * Test method for {@link OracleAgentSqlStrategy#trlogCleanupStatement(String, int)}
     */
    @Test
    public void testTrlogCleanupStatement() {
        final String expected = "DELETE FROM trlog WHERE tid IN (?, ?, ?, ?)";
        final String actual = testee.trlogCleanupStatement(TRLOG_NAME, TID_COUNT);
        assertEquals(expected, actual);
    }

    /**
     * Test method for {@link OracleAgentSqlStrategy#trlogUpdateStatus(String, SchemaInfo.TrlogStatusType, int)}
     */
    @Test
    public void testTrlogUpdateStatus() {
        final String expected = "UPDATE trlog SET status = 'L' WHERE tid IN (?, ?, ?, ?)";
        final String actual = testee.trlogUpdateStatus(TRLOG_NAME, SchemaInfo.TrlogStatusType.L, TID_COUNT);
        assertEquals(expected, actual);
    }

    /**
     * Test method for {@link OracleAgentSqlStrategy#count(String)
     */
    @Test
    public void testCount() {
        final String actual = testee.count("1");
        final String expected = "COUNT(1)";
        assertEquals(expected, actual);
    }

    /**
     * Test method for {@link OracleAgentSqlStrategy#max(String)
     */
    @Test
    public void testMax() {
        final String actual = testee.max("sal");
        final String expected = "NVL(MAX(sal), 0)";
        assertEquals(expected, actual);
    }

    /**
     * Test method for {@link OracleAgentSqlStrategy#min(String)
     */
    @Test
    public void testMin() {
        final String actual = testee.min("sal");
        final String expected = "NVL(MIN(sal), 0)";
        assertEquals(expected, actual);
    }

    /**
     * Test method for {@link OracleAgentSqlStrategy#mod(String, String)}
     */
    @Test
    public void testMod() {
        final String actual = testee.mod("empid", "4");
        final String expected = "MOD(empid, 4)";
        assertEquals(expected, actual);
    }

    @Test
    public void testEqualsHashCode() {
        final DbAgentConfig config1 = createNiceMock(DbAgentConfig.class);
        final DbAgentConfig config2 = createNiceMock(DbAgentConfig.class);
        expect(config1.getClogTriggerTemplate()).andStubReturn("CLOG_TEMPL1");
        expect(config1.getTrlogTriggerTemplate()).andStubReturn("TRLOG_TEMPL1");
        expect(config2.getClogTriggerTemplate()).andStubReturn("CLOG_TEMPL2");
        expect(config2.getTrlogTriggerTemplate()).andStubReturn("TRLOG_TEMPL2");

        final Object o3 = createNiceMock(AgentSqlStrategy.class);
        replay(config1, config2, o3);

        final OracleAgentSqlStrategy o11 = new OracleAgentSqlStrategy(config1);
        final OracleAgentSqlStrategy o12 = new OracleAgentSqlStrategy(config1);
        final OracleAgentSqlStrategy o2 = new OracleAgentSqlStrategy(config2);

        assertEquals(o11, o11);
        assertEquals(o11.hashCode(), o11.hashCode());
        assertEquals(o11, o12);
        assertEquals(o11.hashCode(), o12.hashCode());
        assertEquals(o12, o11);
        assertNotEquals(o11, null);
        assertNotEquals(o11, o2);
        assertNotEquals(o11, o3);
    }

    /**
     * Test method for {@link OracleAgentSqlStrategy#hash(List)}
     */
    @Test
    public void testHash() {
        final String actual = testee.hash(pidCols);
        final String expected = "ORA_HASH(''||NVL(empid1, 0)||NVL(empid2, 0))";
        assertEquals(expected, actual);
    }

    /**
     * Test method for {@link OracleAgentSqlStrategy#hash(List)}
     */
    @Test
    public void testHash_OnePidCol() {
        final String actual = testee.hash(pidSingleCols);
        final String expected = "ORA_HASH(''||NVL(empid1, 0))";
        assertEquals(expected, actual);
    }


    private static final String NDT_USER_NAME = "ndtsrc";
    private static final String TABLE_NAME = "testtable";
    private static final String TRG_CLOG_NAME = "trg_clog_testtable";
    private static final String TRG_TRLOG_NAME = "trg_trlog_testtable";
    private static final String CLOG_NAME = "clog_testtable";
    private static final String TRLOG_NAME = "trlog";
    private static final String PID_COL_NAME_1 = "empid1";
    private static final String PID_COL_NAME_2 = "empid2";
    private static final int TID_COUNT = 3;

    private OracleAgentSqlStrategy testee;
    private Properties expectedProps;
    private Set<Col> tableCols;
    private PK tablePk;
    private List<Col> pidCols;
    private List<Col> pidSingleCols;
}
