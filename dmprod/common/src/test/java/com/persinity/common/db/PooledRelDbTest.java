/**
 * Copyright (c) 2015 Persinity Inc.
 */

package com.persinity.common.db;

import static org.easymock.EasyMock.createNiceMock;
import static org.easymock.EasyMock.createStrictMock;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.expectLastCall;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.verify;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.sql.Connection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;

import com.persinity.common.db.metainfo.Schema;

/**
 * @author dyordanov
 */
public class PooledRelDbTest {

    @Before
    public void setUp() throws Exception {
        nicePool = createNiceMock(RelDbPool.class);
    }

    @Test(expected = NullPointerException.class)
    public void testPooledRelDb_InvalidInput1() {
        new PooledRelDb(null, createNiceMock(RelDb.class));
    }

    @Test(expected = NullPointerException.class)
    public void testPooledRelDb_InvalidInput2() {
        new PooledRelDb(nicePool, null);
    }

    @Test
    public void testMetaInfo() throws Exception {
        final RelDb relDb = createStrictMock(RelDb.class);
        final Schema metaInfo = createNiceMock(Schema.class);
        expect(relDb.metaInfo()).andReturn(metaInfo);
        replay(relDb);

        final PooledRelDb testee = new PooledRelDb(nicePool, relDb);
        final Schema actualMetaInfo = testee.metaInfo();

        verify(relDb);
        assertEquals(metaInfo, actualMetaInfo);
    }

    @Test
    public void testExecuteDmdl() throws Exception {
        final RelDb relDb = createStrictMock(RelDb.class);
        final String sql = "UPDATE emp SET ename = 'Knyaz Boris' WHERE empid = 66";
        final int expected = 1;
        expect(relDb.executeDmdl(sql)).andReturn(expected);
        replay(relDb);

        final PooledRelDb testee = new PooledRelDb(nicePool, relDb);
        final int actual = testee.executeDmdl(sql);

        verify(relDb);
        assertEquals(expected, actual);
    }

    @Test
    public void testExecuteSp() throws Exception {
        final RelDb relDb = createStrictMock(RelDb.class);
        final String signature = "signature";
        final List<?> params = createNiceMock(List.class);
        relDb.executeSp(signature, params);
        expectLastCall();
        replay(relDb);

        final PooledRelDb testee = new PooledRelDb(nicePool, relDb);
        testee.executeSp(signature, params);

        verify(relDb);
    }

    @Test
    public void testExecuteNumericSf() throws Exception {
        final RelDb relDb = createStrictMock(RelDb.class);
        final String signature = "signature";
        final List<?> params = createNiceMock(List.class);
        final Long expected = 1L;
        expect(relDb.executeNumericSf(signature, params)).andReturn(expected);
        replay(relDb);

        final PooledRelDb testee = new PooledRelDb(nicePool, relDb);
        final Long actual = testee.executeNumericSf(signature, params);

        verify(relDb);
        assertEquals(expected, actual);
    }

    @Test
    public void testClose() throws Exception {
        final RelDb relDb = createNiceMock(RelDb.class);
        final RelDbPool pool = createStrictMock(RelDbPool.class);
        pool.remove(relDb);
        expectLastCall();
        replay(relDb, pool);

        final PooledRelDb testee = new PooledRelDb(pool, relDb);
        testee.close();

        verify(pool, relDb);
    }

    @Test
    public void testIsDbOutputEnabled() throws Exception {
        final RelDb relDb = createStrictMock(RelDb.class);
        final Boolean expected = true;
        expect(relDb.isDbOutputEnabled()).andReturn(expected);
        replay(relDb);

        final PooledRelDb testee = new PooledRelDb(nicePool, relDb);
        final Boolean actual = testee.isDbOutputEnabled();

        verify(relDb);
        assertEquals(expected, actual);
    }

    @Test
    public void testCommit() throws Exception {
        final RelDb relDb = createStrictMock(RelDb.class);
        relDb.commit();
        expectLastCall();
        replay(relDb);

        final PooledRelDb testee = new PooledRelDb(nicePool, relDb);
        testee.commit();

        verify(relDb);
    }

    @Test
    public void testRollback() throws Exception {
        final RelDb relDb = createStrictMock(RelDb.class);
        relDb.rollback();
        expectLastCall();
        replay(relDb);

        final PooledRelDb testee = new PooledRelDb(nicePool, relDb);
        testee.rollback();

        verify(relDb);
    }

    @Test
    public void testQuery() throws Exception {
        final RelDb relDb = createStrictMock(RelDb.class);
        final String sql = "SELECT address FROM emp WHERE empid = dataMotionAuthor";
        final Iterator<Object[]> expected = createNiceMock(Iterator.class);
        expect(relDb.query(sql)).andReturn(expected);
        replay(relDb);

        final PooledRelDb testee = new PooledRelDb(nicePool, relDb);
        final Iterator<Object[]> actual = testee.query(sql);

        verify(relDb);
        assertEquals(expected, actual);
    }

    @Test
    public void testExecuteScript() throws Exception {
        final RelDb relDb = createStrictMock(RelDb.class);
        final String scriptName = "script.sql";
        relDb.executeScript(scriptName);
        expectLastCall();
        replay(relDb);

        final PooledRelDb testee = new PooledRelDb(nicePool, relDb);
        testee.executeScript(scriptName);

        verify(relDb);
    }

    @Test
    public void testExecutePreparedQuery() throws Exception {
        final RelDb relDb = createStrictMock(RelDb.class);
        final String sql = "SELECT address FROM emp WHERE empid = dataMotionAuthor";
        final List<?> params = createNiceMock(List.class);
        final Iterator<Map<String, Object>> expected = createNiceMock(Iterator.class);
        expect(relDb.executePreparedQuery(sql, params)).andReturn(expected);
        replay(relDb);

        final PooledRelDb testee = new PooledRelDb(nicePool, relDb);
        final Iterator<Map<String, Object>> actual = testee.executePreparedQuery(sql, params);

        verify(relDb);
        assertEquals(expected, actual);
    }

    @Test
    public void testExecuteQuery() throws Exception {
        final RelDb relDb = createStrictMock(RelDb.class);
        final String sql = "SELECT address FROM emp WHERE empid = dataMotionAuthor";
        final Iterator<Map<String, Object>> expected = createNiceMock(Iterator.class);
        expect(relDb.executeQuery(sql)).andReturn(expected);
        replay(relDb);

        final PooledRelDb testee = new PooledRelDb(nicePool, relDb);
        final Iterator<Map<String, Object>> actual = testee.executeQuery(sql);

        verify(relDb);
        assertEquals(expected, actual);
    }

    @Test
    public void testExecutePreparedDml() throws Exception {
        final RelDb relDb = createStrictMock(RelDb.class);
        final String sql = "UPDATE some";
        final List<?> params = createNiceMock(List.class);
        final Integer expected = 1;
        expect(relDb.executePreparedDml(sql, params)).andReturn(expected);
        replay(relDb);

        final PooledRelDb testee = new PooledRelDb(nicePool, relDb);
        final Integer actual = testee.executePreparedDml(sql, params);

        verify(relDb);
        assertEquals(expected, actual);
    }

    @Test
    public void testGetInt() throws Exception {
        final RelDb relDb = createStrictMock(RelDb.class);
        final String sql = "SELECT 1 FROM dual";
        final Integer expected = 1;
        expect(relDb.getInt(sql)).andReturn(expected);
        replay(relDb);

        final PooledRelDb testee = new PooledRelDb(nicePool, relDb);
        final Integer actual = testee.getInt(sql);

        verify(relDb);
        assertEquals(expected, actual);
    }

    @Test
    public void testGetLong() throws Exception {
        final RelDb relDb = createStrictMock(RelDb.class);
        final String sql = "SELECT 1 FROM dual";
        final Long expected = 1L;
        expect(relDb.getLong(sql)).andReturn(expected);
        replay(relDb);

        final PooledRelDb testee = new PooledRelDb(nicePool, relDb);
        final Long actual = testee.getLong(sql);

        verify(relDb);
        assertEquals(expected, actual);
    }

    @Test
    public void testGetUserName() throws Exception {
        final RelDb relDb = createStrictMock(RelDb.class);
        final String expected = "da_user";
        expect(relDb.getUserName()).andReturn(expected);
        replay(relDb);

        final PooledRelDb testee = new PooledRelDb(nicePool, relDb);
        final String actual = testee.getUserName();

        verify(relDb);
        assertEquals(expected, actual);
    }

    @Test
    public void testGetSqlStrategy() throws Exception {
        final RelDb relDb = createStrictMock(RelDb.class);
        final SqlStrategy expected = createNiceMock(SqlStrategy.class);
        expect(relDb.getSqlStrategy()).andReturn(expected);
        replay(relDb);

        final PooledRelDb testee = new PooledRelDb(nicePool, relDb);
        final SqlStrategy actual = testee.getSqlStrategy();

        verify(relDb);
        assertEquals(expected, actual);
    }

    @Test
    public void testToString() throws Exception {
        final RelDb relDb = createNiceMock(RelDb.class);
        final String expected = relDb.toString();

        final PooledRelDb testee = new PooledRelDb(nicePool, relDb);
        final String actual = testee.toString();

        assertEquals(expected, actual);
    }

    @Test
    public void testHashCode() throws Exception {
        final RelDb relDb = createNiceMock(RelDb.class);
        final Integer expected = relDb.hashCode();

        final PooledRelDb testee = new PooledRelDb(nicePool, relDb);
        final Integer actual = testee.hashCode();

        assertEquals(expected, actual);
    }

    @Test
    public void testEquals() throws Exception {
        final RelDb relDb = createNiceMock(RelDb.class);

        final PooledRelDb testee = new PooledRelDb(nicePool, relDb);

        assertTrue(testee.equals(relDb));
    }

    @Test
    public void testGetConnection() throws Exception {
        final SimpleRelDb relDb = createStrictMock(SimpleRelDb.class);
        final Connection expected = createNiceMock(Connection.class);
        expect(relDb.getConnection()).andReturn(expected);
        replay(relDb);

        final PooledRelDb testee = new PooledRelDb(nicePool, relDb);
        final Connection actual = testee.getConnection();

        verify(relDb);
        assertEquals(expected, actual);
    }

    private RelDbPool nicePool;
}