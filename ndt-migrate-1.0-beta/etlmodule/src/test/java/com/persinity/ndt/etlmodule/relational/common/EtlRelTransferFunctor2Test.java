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
package com.persinity.ndt.etlmodule.relational.common;

import static org.easymock.EasyMock.and;
import static org.easymock.EasyMock.anyObject;
import static org.easymock.EasyMock.capture;
import static org.easymock.EasyMock.createMock;
import static org.easymock.EasyMock.createNiceMock;
import static org.easymock.EasyMock.eq;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.isA;
import static org.easymock.EasyMock.newCapture;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.verify;

import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.easymock.Capture;
import org.junit.Assert;
import org.junit.Test;

import com.google.common.collect.Lists;
import com.persinity.common.collection.CollectionUtils;
import com.persinity.common.collection.DirectedEdge;
import com.persinity.common.collection.Pool;
import com.persinity.common.db.RelDb;
import com.persinity.common.db.metainfo.Col;
import com.persinity.common.db.metainfo.In;
import com.persinity.common.db.metainfo.SqlFilter;
import com.persinity.ndt.db.TransactionId;
import com.persinity.ndt.dbagent.relational.AgentSqlStrategy;
import com.persinity.ndt.dbagent.relational.SchemaInfo;
import com.persinity.ndt.dbagent.relational.StringTid;
import com.persinity.ndt.etlmodule.relational.Partitioner;
import com.persinity.ndt.etlmodule.relational.RelTransferWindow;
import com.persinity.ndt.etlmodule.relational.TransformInfo;
import com.persinity.ndt.etlmodule.relational.migrate.ClogCoalesceTupleFunc;
import com.persinity.ndt.transform.EntitiesDag;
import com.persinity.ndt.transform.ParamDmlFunc;
import com.persinity.ndt.transform.ParamDmlLoadFunc;
import com.persinity.ndt.transform.ParamQryFunc;
import com.persinity.ndt.transform.RelLoadFunc;
import com.persinity.ndt.transform.TransferFunc;
import com.persinity.ndt.transform.TransferWindow;
import com.persinity.ndt.transform.TupleFunc;

/**
 * TODO merge with EtlRelTransferFunctorTest
 *
 * @author Doichin Yordanov
 */
public class EtlRelTransferFunctor2Test {

    private static final String DST_TABLE = "cclog_emp";
    private static final String SRC_TABLE = "clog_emp";
    private static final Col SRC_PID_COL = new Col("empid");
    private static final Col DST_ID_COL = new Col("empid");
    private static final Col TID_COL = new Col("tid");
    private static final List<? extends TransactionId> TIDS = Arrays.asList(new StringTid("T1"), new StringTid("T2"));
    private static final List<String> EXPECTED_TIDS = CollectionUtils.quote(CollectionUtils.stringListOf(TIDS));
    private static final Partitioner.PartitionData PARTITIONS = new Partitioner.PartitionData(
            Arrays.asList(new DirectedEdge<>(1, 2), new DirectedEdge<>(3, 4)), 5);
    private static final String EXTRACT_SQL = "SELECT tid, gid, ctype, empid, ename FROM clog_emp WHERE (pid BETWEEN (? AND ?)) AND (tid IN (?, ?))";
    private static final String LOAD_SQL = "INSERT INTO cclog_emp (tid, gid, ctype, empid, ename) VALUES (?, ?, ?, ?, ?)";
    private static final List<Col> COLS = Arrays
            .asList(new Col("tid"), new Col("gid"), new Col("ctype"), new Col("empid"), new Col("ename"));

    /**
     * Test method for {@link EtlRelTransferFunctor#apply(java.lang.Void)}.
     */
    @SuppressWarnings("unchecked")
    @Test
    public void testApply() {
        final Capture<SqlFilter<?>> sqlFilterCapture = newCapture();
        final Partitioner pidPartitioner = mockPartitioner(sqlFilterCapture);
        final TransferWindow<RelDb, RelDb> tWin = mockWindow();
        final TransformInfo tInfo = stubTransformInfo();
        final DirectedEdge<SchemaInfo, SchemaInfo> schemas = createMock(DirectedEdge.class);
        final AgentSqlStrategy sqlStrategy = createMock(AgentSqlStrategy.class);
        final EtlRelTransferFunctor testee = new EtlRelTransferFunctor(DST_TABLE, tInfo, pidPartitioner, tWin, schemas,
                sqlStrategy);

        final Set<TransferFunc<RelDb, RelDb>> actual = testee.apply(null);

        verify(pidPartitioner);
        Assert.assertTrue(sqlFilterCapture.getValue() instanceof In);
        @SuppressWarnings("unchecked")
        final In<TransactionId> sqlFilterCaptured = (In<TransactionId>) sqlFilterCapture.getValue();
        Assert.assertEquals(sqlFilterCaptured.getCol().getName(), TID_COL.getName());
        Assert.assertEquals(EXPECTED_TIDS, sqlFilterCaptured.getValue());

        Assert.assertEquals(PARTITIONS.getPartition().size(), actual.size());
        final List<DirectedEdge<Integer, Integer>> partitionsToCheck = new LinkedList<>(PARTITIONS.getPartition());
        for (final TransferFunc<RelDb, RelDb> transferF : actual) {
            Assert.assertTrue(transferF instanceof EtlRelTransferFunc);
            final EtlRelTransferFunc etlF = (EtlRelTransferFunc) transferF;
            Assert.assertTrue(partitionsToCheck.contains(etlF.getIdRange()));
            partitionsToCheck.remove(etlF.getIdRange());

            final ParamQryFunc extractF = (ParamQryFunc) etlF.getExtractFunction();
            Assert.assertEquals(EXTRACT_SQL, extractF.getSql());

            final ParamDmlLoadFunc loadF = (ParamDmlLoadFunc) etlF.getLoadFunction();
            Assert.assertEquals(LOAD_SQL, loadF.getParamDmlFunc().getSql());
        }
    }

    private TransformInfo stubTransformInfo() {
        final DirectedEdge<String, String> entityMapping = new DirectedEdge<>(SRC_TABLE, DST_TABLE);
        final DirectedEdge<Set<Col>, Set<Col>> colsMapping = new DirectedEdge<>(Collections.singleton(SRC_PID_COL),
                Collections.singleton(DST_ID_COL));
        final DirectedEdge<String, String> sqlMapping = new DirectedEdge<>(EXTRACT_SQL, LOAD_SQL);
        final ParamQryFunc extractF = new ParamQryFunc(COLS, sqlMapping.src());
        final TupleFunc transformF = new ClogCoalesceTupleFunc(colsMapping.src());
        final RelLoadFunc loadF = new ParamDmlLoadFunc(new ParamDmlFunc(sqlMapping.dst(), COLS));
        final TransformInfo transformInfo = new TransformInfo(entityMapping, colsMapping, extractF, transformF, loadF,
                8);
        return transformInfo;
    }

    private TransferWindow<RelDb, RelDb> mockWindow() {

        final Pool<RelDb> destination = createMock(Pool.class);
        final RelDb dstRelDb = createMock(RelDb.class);
        expect(destination.get()).andReturn(dstRelDb);

        final Pool<RelDb> source = createMock(Pool.class);
        final RelDb srcRelDb = createMock(RelDb.class);
        expect(source.get()).andReturn(srcRelDb);

        replay(destination, source);

        final DirectedEdge<Pool<RelDb>, Pool<RelDb>> dataPoolBridge = new DirectedEdge<>(source, destination);

        final EntitiesDag entities = new EntitiesDag(Collections.singletonList(DST_TABLE));
        return new RelTransferWindow(dataPoolBridge, TIDS, entities.vertexSet(), entities);
    }

    private Partitioner mockPartitioner(final Capture<SqlFilter<?>> sqlFilterCapture) {
        final Partitioner pidPartitioner = createNiceMock(Partitioner.class);
        expect(pidPartitioner.partition(anyObject(RelDb.class), eq(SRC_TABLE), eq(Lists.newArrayList(SRC_PID_COL)),
                and(capture(sqlFilterCapture), isA(In.class)))).andReturn(PARTITIONS);
        replay(pidPartitioner);

        return pidPartitioner;
    }
}
