/**
 * Copyright (c) 2015 Persinity Inc.
 */
package com.persinity.ndt.etlmodule.relational.common;

import static org.easymock.EasyMock.and;
import static org.easymock.EasyMock.anyObject;
import static org.easymock.EasyMock.capture;
import static org.easymock.EasyMock.eq;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.expectLastCall;
import static org.easymock.EasyMock.newCapture;
import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThat;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.easymock.Capture;
import org.easymock.EasyMock;
import org.easymock.EasyMockSupport;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.persinity.common.collection.DirectedEdge;
import com.persinity.common.collection.Pool;
import com.persinity.common.db.RelDb;
import com.persinity.common.db.metainfo.Col;
import com.persinity.common.db.metainfo.In;
import com.persinity.common.db.metainfo.SqlFilter;
import com.persinity.ndt.dbagent.relational.AgentSqlStrategy;
import com.persinity.ndt.dbagent.relational.SchemaInfo;
import com.persinity.ndt.dbagent.relational.StringTid;
import com.persinity.ndt.etlmodule.relational.Partitioner;
import com.persinity.ndt.etlmodule.relational.TransformInfo;
import com.persinity.ndt.transform.RelExtractFunc;
import com.persinity.ndt.transform.RelLoadFunc;
import com.persinity.ndt.transform.TransferFunc;
import com.persinity.ndt.transform.TransferWindow;
import com.persinity.ndt.transform.TupleFunc;

/**
 * @author Ivan Dachev
 */
public class EtlRelTransferFunctorTest extends EasyMockSupport {

    @SuppressWarnings("unchecked")
    @Test
    public void testApply() throws Exception {
        final Partitioner pidPartitioner = createMock(Partitioner.class);
        @SuppressWarnings("rawtypes")
        final TransferWindow tWindow = createMock(TransferWindow.class);
        final DirectedEdge<SchemaInfo, SchemaInfo> schemas = createMock(DirectedEdge.class);
        final AgentSqlStrategy sqlStrategy = createMock(AgentSqlStrategy.class);

        final RelExtractFunc extractF = createMock(RelExtractFunc.class);
        final TupleFunc transformF = createMock(TupleFunc.class);
        final RelLoadFunc loadF = createMock(RelLoadFunc.class);

        final DirectedEdge<String, String> entityMapping = createMock(DirectedEdge.class);
        expect(entityMapping.src()).andReturn("stable");

        final DirectedEdge<Set<Col>, Set<Col>> colsMap = createMock(DirectedEdge.class);
        final Set<Col> cols = Sets.newHashSet(new Col("spid"));
        final List<Col> colsList = Lists.newArrayList(cols);
        expect(colsMap.src()).andReturn(cols);

        final DirectedEdge<Pool<RelDb>, Pool<RelDb>> dataBridge = createMock(DirectedEdge.class);

        final RelDb srcDb = createMock(RelDb.class);
        final Pool<RelDb> srcDbPool = createMock(Pool.class);
        expect(dataBridge.src()).andReturn(srcDbPool);
        expect(srcDbPool.get()).andReturn(srcDb);
        srcDb.close();
        expectLastCall();

        expect(tWindow.getDataPoolBridge()).andReturn(dataBridge);

        final List<StringTid> srcTids = Arrays.asList(new StringTid("tif1"), new StringTid("tif2"));
        expect((List<StringTid>) tWindow.getSrcTids()).andReturn(srcTids);

        final Capture<SqlFilter<?>> sqlFilterCapture = newCapture();
        final Partitioner.PartitionData idRanges = new Partitioner.PartitionData(
                Arrays.asList(new DirectedEdge<>(101, 102), new DirectedEdge<>(103, 105)), 106);
        expect(pidPartitioner.partition(anyObject(RelDb.class), eq("stable"), eq(colsList),
                and(capture(sqlFilterCapture), EasyMock.isA(In.class)))).andReturn(idRanges);

        final TransformInfo transformInfo = new TransformInfo(entityMapping, colsMap, extractF, transformF, loadF, 8);

        replayAll();

        final EtlRelTransferFunctor transformEtlFunctor = new EtlRelTransferFunctor("dtable", transformInfo,
                pidPartitioner, tWindow, schemas, sqlStrategy);
        final Set<TransferFunc<RelDb, RelDb>> res = transformEtlFunctor.apply(null);

        verifyAll();

        assertThat(res, notNullValue());
        assertThat(res.size(), is(2));

        final Iterator<TransferFunc<RelDb, RelDb>> iter = res.iterator();
        final TransferFunc<RelDb, RelDb> func1 = iter.next();
        final TransferFunc<RelDb, RelDb> func2 = iter.next();
        assertFalse(iter.hasNext());

        assertThat(func1, instanceOf(EtlRelTransferFunc.class));
        assertThat(((EtlRelTransferFunc) func1).getRangeModBase(), is(106));
        assertThat(((EtlRelTransferFunc) func1).getIdRange(),
                CoreMatchers.anyOf(is(new DirectedEdge<>(101, 102)), is(new DirectedEdge<>(103, 105))));

        assertThat(func2, instanceOf(EtlRelTransferFunc.class));
        assertThat(((EtlRelTransferFunc) func2).getRangeModBase(), is(106));
        assertThat(((EtlRelTransferFunc) func1).getIdRange(),
                CoreMatchers.anyOf(is(new DirectedEdge<>(101, 102)), is(new DirectedEdge<>(103, 105))));
    }

    @SuppressWarnings("unchecked")
    @Test
    public void testEqualsHashCode() {
        final String dstTable1 = "dstTable1";
        final TransferWindow<RelDb, RelDb> tWindow1 = createNiceMock(TransferWindow.class);

        final DirectedEdge<Pool<RelDb>, Pool<RelDb>> dataPoolBridge = createNiceMock(DirectedEdge.class);
        expect(tWindow1.getDataPoolBridge()).andReturn(dataPoolBridge).anyTimes();

        final Pool<RelDb> dataPool1 = createNiceMock(Pool.class);
        expect(dataPoolBridge.src()).andReturn(dataPool1).anyTimes();
        final RelDb relDb1 = createNiceMock(RelDb.class);
        expect(dataPool1.get()).andReturn(relDb1).anyTimes();

        final Pool<RelDb> dataPool2 = createNiceMock(Pool.class);
        expect(dataPoolBridge.dst()).andReturn(dataPool2).anyTimes();
        final RelDb relDb2 = createNiceMock(RelDb.class);
        expect(dataPool2.get()).andReturn(relDb2).anyTimes();

        final TransformInfo transformInfo1 = createNiceMock(TransformInfo.class);
        final Partitioner pidParttnr1 = createNiceMock(Partitioner.class);
        final String dstTable2 = "dstTable2";

        final TransferWindow<RelDb, RelDb> tWindow2 = createNiceMock(TransferWindow.class);
        expect(tWindow2.getDataPoolBridge()).andReturn(dataPoolBridge).anyTimes();

        final TransformInfo transformInfo2 = createNiceMock(TransformInfo.class);
        final Partitioner pidParttnr2 = createNiceMock(Partitioner.class);

        EasyMock.replay(tWindow1, dataPoolBridge, dataPool1, relDb1, dataPool2, relDb2, transformInfo1, pidParttnr1,
                tWindow2, transformInfo2, pidParttnr2);

        final DirectedEdge<SchemaInfo, SchemaInfo> schemas = createMock(DirectedEdge.class);
        final AgentSqlStrategy sqlStrategy = createMock(AgentSqlStrategy.class);

        final EtlRelTransferFunctor o11 = new EtlRelTransferFunctor(dstTable1, transformInfo1, pidParttnr1, tWindow1,
                schemas, sqlStrategy);
        final EtlRelTransferFunctor o12 = new EtlRelTransferFunctor(dstTable1, transformInfo1, pidParttnr1, tWindow1,
                schemas, sqlStrategy);
        final EtlRelTransferFunctor o2111 = new EtlRelTransferFunctor(dstTable2, transformInfo1, pidParttnr1, tWindow1,
                schemas, sqlStrategy);
        final EtlRelTransferFunctor o2211 = new EtlRelTransferFunctor(dstTable2, transformInfo2, pidParttnr1, tWindow1,
                schemas, sqlStrategy);
        final EtlRelTransferFunctor o2221 = new EtlRelTransferFunctor(dstTable2, transformInfo2, pidParttnr1, tWindow2,
                schemas, sqlStrategy);
        final EtlRelTransferFunctor o2222 = new EtlRelTransferFunctor(dstTable2, transformInfo2, pidParttnr2, tWindow2,
                schemas, sqlStrategy);

        Assert.assertEquals(o11, o11);
        Assert.assertEquals(o11.hashCode(), o11.hashCode());
        Assert.assertEquals(o11, o12);
        Assert.assertEquals(o11.hashCode(), o12.hashCode());
        Assert.assertEquals(o12, o11);

        Assert.assertNotEquals(o11, null);
        Assert.assertNotEquals(o11, o2111);
        Assert.assertNotEquals(o11, o2211);
        Assert.assertNotEquals(o11, o2221);
        Assert.assertNotEquals(o11, o2222);
    }
}
