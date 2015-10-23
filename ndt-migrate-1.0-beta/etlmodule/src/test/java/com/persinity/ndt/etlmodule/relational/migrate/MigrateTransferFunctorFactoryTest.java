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

import static org.easymock.EasyMock.expect;

import java.util.Collections;
import java.util.Map;
import java.util.Set;

import org.easymock.EasyMockSupport;
import org.junit.Assert;
import org.junit.Test;

import com.google.common.base.Function;
import com.persinity.common.collection.DirectedEdge;
import com.persinity.common.collection.Pool;
import com.persinity.common.db.RelDb;
import com.persinity.ndt.dbagent.relational.AgentSqlStrategy;
import com.persinity.ndt.dbagent.relational.SchemaInfo;
import com.persinity.ndt.etlmodule.relational.Partitioner;
import com.persinity.ndt.etlmodule.relational.TransformInfo;
import com.persinity.ndt.etlmodule.relational.common.EtlRelTransferFunctor;
import com.persinity.ndt.transform.TransferFunc;
import com.persinity.ndt.transform.TransferWindow;

/**
 * @author Doichin Yordanov
 */
public class MigrateTransferFunctorFactoryTest extends EasyMockSupport {

    private static final String DST_ENTITY = "emp";

    /**
     * Test method for
     * {@link MigrateTransferFunctorFactory#newEntityTransferFunctor(java.lang.String, com.persinity.ndt.transform.TransferWindow)}
     */
    @SuppressWarnings("unchecked")
    @Test
    public void testNewEtlFunctor() {
        final Partitioner pidPartitioner = createNiceMock(Partitioner.class);
        @SuppressWarnings("unchecked")
        final Map<String, TransformInfo> transformMap = createMock(Map.class);
        final TransformInfo transformInfo = createMock(TransformInfo.class);
        final DirectedEdge<SchemaInfo, SchemaInfo> schemas = createMock(DirectedEdge.class);
        final AgentSqlStrategy sqlStrategy = createMock(AgentSqlStrategy.class);
        final MigrateTransferFunctorFactory testee = new MigrateTransferFunctorFactory(transformMap, pidPartitioner,
                schemas, sqlStrategy);

        expect(transformMap.get("emp")).andReturn(transformInfo);

        @SuppressWarnings("unchecked")
        final TransferWindow<RelDb, RelDb> win = createNiceMock(TransferWindow.class);

        final DirectedEdge<Pool<RelDb>, Pool<RelDb>> dataPoolBridge = createMock(DirectedEdge.class);
        expect(win.getDataPoolBridge()).andReturn(dataPoolBridge).anyTimes();

        final Pool<RelDb> dst = createMock(Pool.class);
        expect(dataPoolBridge.dst()).andReturn(dst).anyTimes();
        final RelDb dstRelDb = createMock(RelDb.class);
        expect(dst.get()).andReturn(dstRelDb).anyTimes();

        final Pool<RelDb> src = createMock(Pool.class);
        expect(dataPoolBridge.src()).andReturn(src).anyTimes();
        final RelDb srcRelDb = createMock(RelDb.class);
        expect(src.get()).andReturn(srcRelDb).anyTimes();

        expect(win.getAffectedSrcEntities()).andReturn(Collections.singleton("emp"));

        replayAll();

        final Function<Void, Set<TransferFunc<RelDb, RelDb>>> actual = testee.newEntityTransferFunctor(DST_ENTITY, win);
        final Function<Void, Set<TransferFunc<RelDb, RelDb>>> expected = new EtlRelTransferFunctor(DST_ENTITY,
                transformInfo, pidPartitioner, win, schemas, sqlStrategy);

        Assert.assertEquals(expected, actual);
    }

}
