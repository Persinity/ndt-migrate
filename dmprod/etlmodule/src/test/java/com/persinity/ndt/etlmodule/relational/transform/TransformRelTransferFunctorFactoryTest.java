/**
 * Copyright (c) 2015 Persinity Inc.
 */
package com.persinity.ndt.etlmodule.relational.transform;

import static org.easymock.EasyMock.expect;
import static org.hamcrest.CoreMatchers.instanceOf;
import static org.junit.Assert.assertThat;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.easymock.EasyMockSupport;
import org.junit.Before;
import org.junit.Test;

import com.google.common.collect.ImmutableMap;
import com.persinity.common.collection.DirectedEdge;
import com.persinity.common.collection.Pool;
import com.persinity.common.db.RelDb;
import com.persinity.ndt.dbagent.relational.AgentSqlStrategy;
import com.persinity.ndt.dbagent.relational.SchemaInfo;
import com.persinity.ndt.etlmodule.TransferFunctor;
import com.persinity.ndt.etlmodule.relational.Partitioner;
import com.persinity.ndt.etlmodule.relational.TransformInfo;
import com.persinity.ndt.etlmodule.relational.common.EtlRelTransferFunctor;
import com.persinity.ndt.etlmodule.relational.common.NoOpsRelTransferFunctor;
import com.persinity.ndt.transform.TransferWindow;

/**
 * @author Ivan Dachev
 */
public class TransformRelTransferFunctorFactoryTest extends EasyMockSupport {

    @SuppressWarnings("unchecked")
    @Before
    public void setUp() {
        final TransformInfo tableTInfo = createMock(TransformInfo.class);
        expect(tableTInfo.getEntityMapping()).andStubReturn(new DirectedEdge<>("table", "table"));
        final TransformInfo table2TInfo = createMock(TransformInfo.class);
        expect(table2TInfo.getEntityMapping()).andStubReturn(new DirectedEdge<>("table2", "table2"));
        transformMap = new HashMap<>(ImmutableMap.of("table", tableTInfo, "table2", table2TInfo));

        pidPartitioner = createMock(Partitioner.class);
        schemas = createMock(DirectedEdge.class);
        sqlStrategy = createMock(AgentSqlStrategy.class);
        win = createMock(TransferWindow.class);

        dataPoolBridge = createMock(DirectedEdge.class);
        expect(win.getDataPoolBridge()).andReturn(dataPoolBridge).anyTimes();

        srcDataPool = createMock(Pool.class);
        expect(dataPoolBridge.src()).andReturn(srcDataPool).anyTimes();
        srcRelDb = createMock(RelDb.class);
        expect(srcDataPool.get()).andReturn(srcRelDb).anyTimes();

        dstDataPool = createMock(Pool.class);
        expect(dataPoolBridge.dst()).andReturn(dstDataPool).anyTimes();
        dstRelDb = createMock(RelDb.class);
        expect(dstDataPool.get()).andReturn(dstRelDb).anyTimes();
    }

    @Test
    public void testNewPreWindowEtlFunctor() throws Exception {
        replayAll();

        final MergeRelTransferFunctorFactory testee = new MergeRelTransferFunctorFactory(transformMap, pidPartitioner,
                schemas, sqlStrategy);

        final TransferFunctor<RelDb, RelDb> res = testee.newPreWindowTransferFunctor(win);

        verifyAll();

        assertThat(res, instanceOf(PreTransformRelTransferFunctor.class));
    }

    @Test
    public void testNewEntityEtlFunctor() throws Exception {
        expect(win.getAffectedSrcEntities()).andReturn(Collections.singleton("table"));

        replayAll();

        final MergeRelTransferFunctorFactory testee = new MergeRelTransferFunctorFactory(transformMap, pidPartitioner,
                schemas, sqlStrategy);

        final TransferFunctor<RelDb, RelDb> res = testee.newEntityTransferFunctor("table", win);

        verifyAll();

        assertThat(res, instanceOf(EtlRelTransferFunctor.class));
    }

    @Test
    public void testNewEntityEtlFunctor_NoOpsForUnaffectedEntity() throws Exception {
        expect(win.getAffectedSrcEntities()).andReturn(Collections.singleton("table"));

        replayAll();

        final MergeRelTransferFunctorFactory testee = new MergeRelTransferFunctorFactory(transformMap, pidPartitioner,
                schemas, sqlStrategy);

        final TransferFunctor<RelDb, RelDb> res = testee.newEntityTransferFunctor("table2", win);

        verifyAll();

        assertThat(res, instanceOf(NoOpsRelTransferFunctor.class));
    }

    @Test
    public void testNewPostWindowEtlFunctor() throws Exception {
        replayAll();

        final MergeRelTransferFunctorFactory testee = new MergeRelTransferFunctorFactory(transformMap, pidPartitioner,
                schemas, sqlStrategy);

        final TransferFunctor<RelDb, RelDb> res = testee.newPostWindowTransferFunctor(win);

        verifyAll();

        assertThat(res, instanceOf(NoOpsRelTransferFunctor.class));
    }

    private TransferWindow<RelDb, RelDb> win;
    private Map<String, TransformInfo> transformMap;
    private DirectedEdge<SchemaInfo, SchemaInfo> schemas;
    private Partitioner pidPartitioner;
    private AgentSqlStrategy sqlStrategy;
    private DirectedEdge<Pool<RelDb>, Pool<RelDb>> dataPoolBridge;
    private Pool<RelDb> srcDataPool;
    private RelDb srcRelDb;
    private Pool<RelDb> dstDataPool;
    private RelDb dstRelDb;
}
