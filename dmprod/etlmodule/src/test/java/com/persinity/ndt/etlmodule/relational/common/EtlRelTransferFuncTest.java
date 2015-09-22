/**
 * Copyright (c) 2015 Persinity Inc.
 */
package com.persinity.ndt.etlmodule.relational.common;

import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.expectLastCall;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.easymock.EasyMock;
import org.easymock.EasyMockSupport;
import org.hamcrest.CoreMatchers;
import org.junit.Before;
import org.junit.Test;

import com.persinity.common.collection.DirectedEdge;
import com.persinity.common.db.RelDb;
import com.persinity.common.db.metainfo.Col;
import com.persinity.ndt.db.TransactionId;
import com.persinity.ndt.dbagent.relational.AgentSqlStrategy;
import com.persinity.ndt.dbagent.relational.SchemaInfo;
import com.persinity.ndt.dbagent.relational.StringTid;
import com.persinity.ndt.transform.ParamDmlFunc;
import com.persinity.ndt.transform.ParamDmlLoadFunc;
import com.persinity.ndt.transform.ParamQryFunc;
import com.persinity.ndt.transform.RelExtractFunc;
import com.persinity.ndt.transform.RelLoadFunc;
import com.persinity.ndt.transform.RepeaterTupleFunc;
import com.persinity.ndt.transform.TupleFunc;

/**
 * @author Ivan Dachev
 */
public class EtlRelTransferFuncTest extends EasyMockSupport {

    @Before
    public void setUp() {

        List<TransactionId> tids1 = new LinkedList<>();
        tids1.add(new StringTid("l"));

        List<TransactionId> tids2 = new LinkedList<>();
        tids2.add(new StringTid("r"));

        DirectedEdge<SchemaInfo, SchemaInfo> schemas = EasyMock.createNiceMock(DirectedEdge.class);
        AgentSqlStrategy sqlStrategy = EasyMock.createNiceMock(AgentSqlStrategy.class);

        List<Col> cols = new LinkedList<>();
        cols.add(new Col("test"));
        RelExtractFunc extractF = new ParamQryFunc(cols, "test");
        TupleFunc transformF = new RepeaterTupleFunc();
        RelLoadFunc loadF = new ParamDmlLoadFunc(new ParamDmlFunc("test"));
        TupleFunc transformF2 = new RepeaterTupleFunc();
        RelLoadFunc loadF2 = new ParamDmlLoadFunc(new ParamDmlFunc("test2"));
        RelExtractFunc extractF2 = new ParamQryFunc(cols, "test2");
        int rowsInWin = 5;
        int rowsInWin2 = 4;
        DirectedEdge<Integer, Integer> idRange = new DirectedEdge<>(1, 10);
        DirectedEdge<Integer, Integer> idRange2 = new DirectedEdge<>(2, 11);

        testee = new EtlRelTransferFunc(extractF, transformF, loadF, rowsInWin, idRange, tids1, schemas, sqlStrategy);
        same = new EtlRelTransferFunc(extractF, transformF, loadF, rowsInWin, idRange, tids1, schemas, sqlStrategy);
        other = new EtlRelTransferFunc(extractF, transformF, loadF, rowsInWin, idRange, tids2, schemas, sqlStrategy);

        diffTids = new EtlRelTransferFunc(extractF, transformF, loadF, rowsInWin, idRange, tids2, schemas, sqlStrategy);
        diffIdRange = new EtlRelTransferFunc(extractF, transformF, loadF, rowsInWin, idRange2, tids1, schemas,
                sqlStrategy);
        diffRowsInWin = new EtlRelTransferFunc(extractF, transformF, loadF, rowsInWin2, idRange, tids1, schemas,
                sqlStrategy);
        diffTransformFunc = new EtlRelTransferFunc(extractF, transformF2, loadF, rowsInWin, idRange, tids1, schemas,
                sqlStrategy);
        diffLoadFunc = new EtlRelTransferFunc(extractF, transformF, loadF2, rowsInWin, idRange, tids1, schemas,
                sqlStrategy);
        diffExtractFunc = new EtlRelTransferFunc(extractF2, transformF, loadF, rowsInWin, idRange, tids1, schemas,
                sqlStrategy);

        diffClass = new X(extractF, transformF, loadF, rowsInWin, idRange, tids1, schemas, sqlStrategy);
    }

    @SuppressWarnings("unchecked")
    @Test
    public void testApply() throws Exception {
        final RelExtractFunc extractF = createMock(RelExtractFunc.class);
        final TupleFunc transformF = createMock(TupleFunc.class);
        final RelLoadFunc loadF = createMock(RelLoadFunc.class);
        final int rangeModBase = 103;
        final DirectedEdge<Integer, Integer> idRange = new DirectedEdge<>(101, 102);
        final List<? extends TransactionId> tids = Arrays.asList(new StringTid("tid1"), new StringTid("tid2"));
        final DirectedEdge<SchemaInfo, SchemaInfo> schemas = createMock(DirectedEdge.class);
        final AgentSqlStrategy sqlStrategy = createMock(AgentSqlStrategy.class);
        final DirectedEdge<RelDb, RelDb> dataBridge = createMock(DirectedEdge.class);

        final RelDb srcDb = createMock(RelDb.class);
        expect(dataBridge.src()).andReturn(srcDb).times(2);
        final RelDb dstDb = createMock(RelDb.class);
        expect(dataBridge.dst()).andReturn(dstDb).times(2);

        srcDb.commit();
        expectLastCall();
        dstDb.commit();
        expectLastCall();

        final List<?> params = Arrays.asList(103, 101, 102, "tid1", "tid2");
        final DirectedEdge<RelDb, List<?>> dbToParams = new DirectedEdge<RelDb, List<?>>(srcDb, params);

        final Iterator<Map<String, Object>> etRsIt = createMock(Iterator.class);
        expect(extractF.apply(dbToParams)).andReturn(etRsIt);

        expect(transformF.apply(etRsIt)).andReturn(etRsIt);

        final DirectedEdge<RelDb, Iterator<Map<String, Object>>> loadArgs = new DirectedEdge<>(dstDb, etRsIt);

        expect(loadF.apply(loadArgs)).andReturn(5);

        replayAll();

        final EtlRelTransferFunc transformEtlFunc = new EtlRelTransferFunc(extractF, transformF, loadF, rangeModBase,
                idRange, tids, schemas, sqlStrategy);
        final Integer res = transformEtlFunc.apply(dataBridge);
        final String toStr = transformEtlFunc.toString();
        final RelExtractFunc getExtractF = transformEtlFunc.getExtractFunction();
        final TupleFunc getTransformF = transformEtlFunc.getTransformFunction();
        final RelLoadFunc getLoadF = transformEtlFunc.getLoadFunction();

        verifyAll();

        assertThat(res, is(5));
        assertTrue(toStr.contains("[101->102 / 103]"));
        assertTrue(extractF == getExtractF);
        assertTrue(transformF == getTransformF);
        assertTrue(loadF == getLoadF);
    }

    @Test
    public void testHashCode() throws Exception {

        assertThat(testee.hashCode(), CoreMatchers.is(not(0)));

        assertEquals(testee.hashCode(), same.hashCode());
    }

    @Test
    public void testEquals() throws Exception {

        assertEquals(testee, testee);
        assertEquals(testee, same);

        assertNotEquals(testee, null);
        assertNotEquals(testee, diffTids);
        assertNotEquals(testee, diffIdRange);
        assertNotEquals(testee, diffRowsInWin);

        assertNotEquals(testee, diffTransformFunc);
        assertNotEquals(testee, diffLoadFunc);
        assertNotEquals(testee, diffExtractFunc);

        assertNotEquals(testee, diffClass);
    }

    @Test
    public void testToString() throws Exception {
        assertTrue(testee.toString().matches("EtlRelTransferFunc@[0-9a-f]+\\(\\[l\\]\\) \\[1->10 / 5\\] "
                + "ParamQryFunc\\(SELECT \\.\\.\\. FROM test\\.\\.\\.\\) RepeaterTupleFunc -> ParamDmlLoadFunc\\(test\\(test\\)\\.\\.\\.\\)"));

        assertTrue(other.toString().matches("EtlRelTransferFunc@[0-9a-f]+\\(\\[r\\]\\) \\[1->10 / 5\\] "
                + "ParamQryFunc\\(SELECT \\.\\.\\. FROM test\\.\\.\\.\\) RepeaterTupleFunc -> ParamDmlLoadFunc\\(test\\(test\\)\\.\\.\\.\\)"));
    }

    private Object testee;
    private Object same;
    private Object other;
    private Object diffTids;
    private Object diffIdRange;
    private Object diffRowsInWin;
    private Object diffTransformFunc;
    private Object diffLoadFunc;
    private Object diffExtractFunc;
    private Object diffClass;

    private class X extends EtlRelTransferFunc {
        public X(RelExtractFunc extractF, TupleFunc transformF, RelLoadFunc loadF, int rangeModBase,
                DirectedEdge<Integer, Integer> idRange, List<? extends TransactionId> tids,
                DirectedEdge<SchemaInfo, SchemaInfo> schemas, AgentSqlStrategy sqlStrategy) {
            super(extractF, transformF, loadF, rangeModBase, idRange, tids, schemas, sqlStrategy);
        }
    }
}