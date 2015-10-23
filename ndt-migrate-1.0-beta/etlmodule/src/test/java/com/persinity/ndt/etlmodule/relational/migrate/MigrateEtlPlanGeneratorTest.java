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
package com.persinity.ndt.etlmodule.relational.migrate;

import static org.easymock.EasyMock.anyInt;
import static org.easymock.EasyMock.anyString;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.verify;

import java.util.Arrays;
import java.util.List;

import org.easymock.EasyMockSupport;
import org.junit.Assert;
import org.junit.Test;

import com.persinity.common.collection.DirectedEdge;
import com.persinity.common.collection.Pool;
import com.persinity.common.db.RelDb;
import com.persinity.ndt.dbagent.relational.AgentSqlStrategy;
import com.persinity.ndt.dbagent.relational.SchemaInfo;
import com.persinity.ndt.dbagent.relational.StringTid;
import com.persinity.ndt.etlmodule.EtlPlanDag;
import com.persinity.ndt.etlmodule.TransferFunctor;
import com.persinity.ndt.etlmodule.relational.Partitioner;
import com.persinity.ndt.etlmodule.relational.TransferFunctorFactory;
import com.persinity.ndt.etlmodule.relational.TransformInfo;
import com.persinity.ndt.etlmodule.relational.common.EtlRelTransferFunctor;
import com.persinity.ndt.etlmodule.relational.common.PostTransferFunctor;
import com.persinity.ndt.transform.EntitiesDag;
import com.persinity.ndt.transform.TransferWindow;

/**
 * @author Doichin Yordanov
 */
public class MigrateEtlPlanGeneratorTest extends EasyMockSupport {

    private static final String ENTITY3 = "entity3";
    private static final String ENTITY2 = "entity2";
    private static final String ENTITY1 = "entity1";

    /**
     * Test method for {@link MigrateEtlPlanGenerator#newEtlPlan(com.persinity.ndt.transform.TransferWindow)}
     */
    @SuppressWarnings("unchecked")
    @Test
    public void testNewEtlPlan() {
        @SuppressWarnings("unchecked")
        final TransferWindow<RelDb, RelDb> tWin = createMock(TransferWindow.class);

        final DirectedEdge<Pool<RelDb>, Pool<RelDb>> dataPoolBridge = createMock(DirectedEdge.class);
        expect(tWin.getDataPoolBridge()).andReturn(dataPoolBridge).anyTimes();

        final Pool<RelDb> dst = createMock(Pool.class);
        expect(dataPoolBridge.dst()).andReturn(dst).anyTimes();
        final RelDb dstRelDb = createMock(RelDb.class);
        expect(dst.get()).andReturn(dstRelDb).anyTimes();

        final Pool<RelDb> src = createMock(Pool.class);
        expect(dataPoolBridge.src()).andReturn(src).anyTimes();
        final RelDb srcRelDb = createMock(RelDb.class);
        expect(src.get()).andReturn(srcRelDb).anyTimes();

        final EntitiesDag dstEntityTree = new EntitiesDag(Arrays.asList(ENTITY1, ENTITY2, ENTITY3));
        expect(tWin.getDstEntitiesDag()).andStubReturn(dstEntityTree);
        @SuppressWarnings("rawtypes")
        final List tids = Arrays.asList(new StringTid("t1"), new StringTid("t2"));
        expect(tWin.getSrcTids()).andStubReturn(tids);

        replay(tWin, dataPoolBridge, src, dst, srcRelDb, dstRelDb);

        final DirectedEdge<SchemaInfo, SchemaInfo> schemas = createNiceMock(DirectedEdge.class);
        final SchemaInfo srcSchemaInfo = createNiceMock(SchemaInfo.class);
        expect(schemas.src()).andStubReturn(srcSchemaInfo);
        final AgentSqlStrategy sqlStrategy = createNiceMock(AgentSqlStrategy.class);
        expect(sqlStrategy.trlogCleanupStatement(anyString(), anyInt()))
                .andStubReturn("DELETE FROM trlog WHERE whocares");

        final TransferFunctorFactory etlFunctorFactory = createNiceMock(TransferFunctorFactory.class);
        @SuppressWarnings("unchecked")
        final TransformInfo tInfo = createNiceMock(TransformInfo.class);
        final Partitioner pidPartitioner = createNiceMock(Partitioner.class);
        final TransferFunctor<RelDb, RelDb> etlFunctor1 = new EtlRelTransferFunctor(ENTITY1, tInfo, pidPartitioner,
                tWin, schemas, sqlStrategy);
        final TransferFunctor<RelDb, RelDb> etlFunctor2 = new EtlRelTransferFunctor(ENTITY2, tInfo, pidPartitioner,
                tWin, schemas, sqlStrategy);
        final TransferFunctor<RelDb, RelDb> etlFunctor3 = new EtlRelTransferFunctor(ENTITY3, tInfo, pidPartitioner,
                tWin, schemas, sqlStrategy);

        expect(etlFunctorFactory.newEntityTransferFunctor(ENTITY1, tWin)).andReturn(etlFunctor1);
        expect(etlFunctorFactory.newEntityTransferFunctor(ENTITY2, tWin)).andReturn(etlFunctor2);
        expect(etlFunctorFactory.newEntityTransferFunctor(ENTITY3, tWin)).andReturn(etlFunctor3);

        replay(srcSchemaInfo, schemas, sqlStrategy);

        final TransferFunctor<RelDb, RelDb> preEtlFunctor = new PreMigrateRelTransferFunctor(tWin, schemas,
                sqlStrategy);
        expect(etlFunctorFactory.newPreWindowTransferFunctor(tWin)).andReturn(preEtlFunctor);

        final TransferFunctor<RelDb, RelDb> postEtlFunctor = new PostTransferFunctor(tWin, schemas, sqlStrategy);
        expect(etlFunctorFactory.newPostWindowTransferFunctor(tWin)).andReturn(postEtlFunctor);

        final EtlPlanDag<RelDb, RelDb> expected = new EtlPlanDag<>(
                Arrays.asList(preEtlFunctor, etlFunctor1, etlFunctor2, etlFunctor3, postEtlFunctor));

        replay(etlFunctorFactory);

        final MigrateEtlPlanGenerator testee = new MigrateEtlPlanGenerator(etlFunctorFactory);
        final EtlPlanDag<RelDb, RelDb> actual = testee.newEtlPlan(tWin);

        verify(tWin, etlFunctorFactory);
        Assert.assertEquals(expected.vertexSet(), actual.vertexSet());

        // TODO add verification for edges
    }
}
