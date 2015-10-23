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
package com.persinity.ndt.etlmodule.relational.transform;

import static com.persinity.common.StringUtils.format;
import static com.sun.tools.internal.ws.wsdl.parser.Util.fail;
import static org.easymock.EasyMock.anyString;
import static org.easymock.EasyMock.expect;
import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThat;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.easymock.EasyMock;
import org.easymock.EasyMockSupport;
import org.junit.Test;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Sets;
import com.persinity.common.collection.DirectedEdge;
import com.persinity.common.collection.Pool;
import com.persinity.common.db.RelDb;
import com.persinity.common.db.metainfo.Col;
import com.persinity.common.db.metainfo.FKEdge;
import com.persinity.common.db.metainfo.constraint.FK;
import com.persinity.common.db.metainfo.constraint.PK;
import com.persinity.common.db.metainfo.constraint.Unique;
import com.persinity.ndt.dbagent.relational.AgentSqlStrategy;
import com.persinity.ndt.dbagent.relational.SchemaInfo;
import com.persinity.ndt.dbagent.relational.StringTid;
import com.persinity.ndt.etlmodule.EtlPlanDag;
import com.persinity.ndt.etlmodule.EtlPlanEdge;
import com.persinity.ndt.etlmodule.TransferFunctor;
import com.persinity.ndt.etlmodule.relational.Partitioner;
import com.persinity.ndt.etlmodule.relational.TransformInfo;
import com.persinity.ndt.etlmodule.relational.common.EtlRelTransferFunctor;
import com.persinity.ndt.etlmodule.relational.common.NoOpsRelTransferFunctor;
import com.persinity.ndt.etlmodule.relational.common.PostTransferFunctor;
import com.persinity.ndt.transform.EntitiesDag;
import com.persinity.ndt.transform.TransferWindow;

/**
 * @author Ivan Dachev
 */
public class TransformEtlPlanGeneratorTest extends EasyMockSupport {

    @SuppressWarnings("unchecked")
    @Test
    public void testNewEtlPlan() throws Exception {
        final Partitioner pidPartitioner = createMock(Partitioner.class);
        @SuppressWarnings("rawtypes")
        final TransferWindow tWindow = createMock(TransferWindow.class);
        final DirectedEdge<SchemaInfo, SchemaInfo> schemas = createMock(DirectedEdge.class);
        final SchemaInfo srcSchemaInfo = createNiceMock(SchemaInfo.class);
        expect(schemas.src()).andStubReturn(srcSchemaInfo);
        final AgentSqlStrategy sqlStrategy = createMock(AgentSqlStrategy.class);
        expect(sqlStrategy.trlogCleanupStatement(anyString(), EasyMock.anyInt()))
                .andStubReturn("DELETE FROM trlog WHERE doesnt matter");

        final EntitiesDag entitiesDag = new EntitiesDag();
        entitiesDag.addVertex("emp");
        entitiesDag.addVertex("dep");
        entitiesDag.addVertex("kid");
        final Unique depPk = new PK("dep", Collections.singleton(new Col("dept_id")));
        final FK emp2depFk = new FK("fk_emp2dept", "emp", Collections.singleton(new Col("dept_id")), depPk);
        entitiesDag.addEdge("emp", "dep", new FKEdge("emp", emp2depFk, "dep"));
        expect(tWindow.getAffectedSrcEntities()).andStubReturn(Sets.newHashSet("emp", "dep"));
        expect(tWindow.getDstEntitiesDag()).andStubReturn(entitiesDag);
        @SuppressWarnings("rawtypes")
        final List tids = Arrays.asList(new StringTid("t1"), new StringTid("t2"));
        expect(tWindow.getSrcTids()).andStubReturn(tids);

        final DirectedEdge<Pool<RelDb>, Pool<RelDb>> dataPoolBridge = createMock(DirectedEdge.class);
        expect(tWindow.getDataPoolBridge()).andReturn(dataPoolBridge).anyTimes();

        final Pool<RelDb> srcDataPool = createMock(Pool.class);
        expect(dataPoolBridge.src()).andReturn(srcDataPool).anyTimes();
        final RelDb srcRelDb = createMock(RelDb.class);
        expect(srcDataPool.get()).andReturn(srcRelDb).anyTimes();

        final Pool<RelDb> dstDataPool = createMock(Pool.class);
        expect(dataPoolBridge.dst()).andReturn(dstDataPool).anyTimes();
        final RelDb dstRelDb = createMock(RelDb.class);
        expect(dstDataPool.get()).andReturn(dstRelDb).anyTimes();

        final TransformInfo empTInfo = createMock(TransformInfo.class);
        expect(empTInfo.getEntityMapping()).andStubReturn(new DirectedEdge<>("emp", "emp"));
        final TransformInfo depTInfo = createMock(TransformInfo.class);
        expect(depTInfo.getEntityMapping()).andStubReturn(new DirectedEdge<>("dep", "dep"));
        final TransformInfo kidTInfo = createMock(TransformInfo.class);
        expect(kidTInfo.getEntityMapping()).andStubReturn(new DirectedEdge<>("kid", "kid"));
        final Map<String, TransformInfo> mergeMap = new HashMap<>(
                ImmutableMap.of("emp", empTInfo, "dep", depTInfo, "kid", kidTInfo));

        empDInfo = createMock(TransformInfo.class);
        expect(empDInfo.getEntityMapping()).andStubReturn(new DirectedEdge<>("emp", "emp"));
        depDInfo = createMock(TransformInfo.class);
        expect(depDInfo.getEntityMapping()).andStubReturn(new DirectedEdge<>("dep", "dep"));
        final Map<String, TransformInfo> deleteMap = new HashMap<>(
                ImmutableMap.of("emp", empDInfo, "dep", depDInfo, "kid", kidTInfo));

        replayAll();

        final TransformEtlPlanGenerator transformEtlPlanGenerator = new TransformEtlPlanGenerator(mergeMap, deleteMap,
                pidPartitioner, schemas, sqlStrategy);
        final String toStr = transformEtlPlanGenerator.toString();

        final EtlPlanDag<RelDb, RelDb> etlPlanDag = transformEtlPlanGenerator.newEtlPlan(tWindow);

        verifyAll();

        assertFalse(toStr.isEmpty());

        assertThat(etlPlanDag, notNullValue());

        assertThat(etlPlanDag.getRootSourceVertex(), instanceOf(PreTransformRelTransferFunctor.class));
        assertThat(etlPlanDag.getBaseSinkVertex(), instanceOf(PostTransferFunctor.class));

        assertExpectedVertexes(etlPlanDag);
        assertExpectedEdges(etlPlanDag);
    }

    @SuppressWarnings("ConstantConditions")
    private void assertExpectedEdges(final EtlPlanDag<RelDb, RelDb> etlPlanDag) {
        assertThat(etlPlanDag.edgeSet().size(), is(11));

        /**
         * <pre>
         *       PRE_TRANSFORM
         *       /           \
         * TRANSFORM_DEP  NO_OP(kid)
         *      |            |
         * TRANSFORM_EMP     |
         *        \          |
         *   NO_OP(POST_TRANSFORM)
         *            |
         *     NO_OP(PRE_DELETE)
         *        /        \
         *   DELETE_EMP  NO_OP(kid)
         *       |         |
         *   DELETE_DEP    |
         *         \      /
         *         POST_DEL
         * </pre>
         */
        final List<DirectedEdge<String, String>> expectedSimpleEdges = Arrays
                .asList(new DirectedEdge<>(PRE_TRANSFORM, TRANSFORM_DEP), new DirectedEdge<>(PRE_TRANSFORM, NO_OP)/*no op for kid transform*/,
                        new DirectedEdge<>(TRANSFORM_DEP, TRANSFORM_EMP), new DirectedEdge<>(TRANSFORM_EMP, NO_OP),
                        new DirectedEdge<>(NO_OP, NO_OP)/*no op for kid transform*/, new DirectedEdge<>(NO_OP, NO_OP),
                        new DirectedEdge<>(NO_OP, DELETE_EMP), new DirectedEdge<>(NO_OP, NO_OP)/*no op for kid del*/,
                        new DirectedEdge<>(DELETE_EMP, DELETE_DEP), new DirectedEdge<>(DELETE_DEP, POST_DEL),
                        new DirectedEdge<>(NO_OP, POST_DEL)/*no op for kid del*/);

        assertThat(etlPlanDag.edgeSet().size(), is(expectedSimpleEdges.size()));

        final ArrayList<DirectedEdge<String, String>> notFoundExpectedSimpleEdges = new ArrayList<>();
        for (final DirectedEdge<String, String> expectedSimpleEdge : expectedSimpleEdges) {
            boolean found = false;
            for (final EtlPlanEdge<RelDb, RelDb> edge : etlPlanDag.edgeSet()) {
                found = simpleVertexMatcher(expectedSimpleEdge.src(), edge.src()) && simpleVertexMatcher(
                        expectedSimpleEdge.dst(), edge.dst());
                if (found) {
                    break;
                }
            }
            if (!found) {
                notFoundExpectedSimpleEdges.add(expectedSimpleEdge);
            }
        }
        if (notFoundExpectedSimpleEdges.size() > 0) {
            fail(format("Failed to find edges: {}", notFoundExpectedSimpleEdges));
        }
    }

    private void assertExpectedVertexes(final EtlPlanDag<RelDb, RelDb> etlPlanDag) {
        assertThat(etlPlanDag.vertexSet().size(), is(10));

        final List<String> expectedSimpleVertexes = Arrays
                .asList(PRE_TRANSFORM, NO_OP, TRANSFORM_EMP, TRANSFORM_DEP, DELETE_DEP, DELETE_EMP, POST_DEL);

        int countNoOps = 0;
        for (final TransferFunctor v : etlPlanDag.vertexSet()) {
            boolean found = false;
            for (final String expectedSimpleV : expectedSimpleVertexes) {
                found = simpleVertexMatcher(expectedSimpleV, v);
                if (found) {
                    if (expectedSimpleV.equals(NO_OP)) {
                        countNoOps += 1;
                    }
                    break;
                }
            }
            if (!found) {
                fail(format("Failed to find vertex: {}", v));
            }
        }

        // 2 - for post-merge and pre-delete are no ops for now,
        // 2 - for merge/delete for unaffected kid table
        assertThat(countNoOps, is(4));
    }

    private boolean simpleVertexMatcher(final String simpleV, final TransferFunctor v) {
        switch (simpleV) {
        case PRE_TRANSFORM:
            if (v instanceof PreTransformRelTransferFunctor) {
                return true;
            }
            break;
        case NO_OP:
            if (v instanceof NoOpsRelTransferFunctor) {
                return true;
            }
            break;
        case TRANSFORM_EMP:
            if (v instanceof EtlRelTransferFunctor) {
                if (((EtlRelTransferFunctor) v).getDstEntity().equals("emp")) {
                    return true;
                }
            }
            break;
        case TRANSFORM_DEP:
            if (v instanceof EtlRelTransferFunctor) {
                if (((EtlRelTransferFunctor) v).getDstEntity().equals("dep")) {
                    return true;
                }
            }
            break;
        case DELETE_EMP:
            if (v instanceof EtlRelTransferFunctor) {
                if (((EtlRelTransferFunctor) v).getDstEntity().equals("emp") && ((EtlRelTransferFunctor) v)
                        .getTransformInfo().equals(empDInfo)) {
                    return true;
                }
            }
            break;
        case DELETE_DEP:
            if (v instanceof EtlRelTransferFunctor) {
                if (((EtlRelTransferFunctor) v).getDstEntity().equals("dep") && ((EtlRelTransferFunctor) v)
                        .getTransformInfo().equals(depDInfo)) {
                    return true;
                }
            }
            break;
        case POST_DEL:
            if (v instanceof PostTransferFunctor) {
                return true;
            }
            break;
        }
        return false;
    }

    final String NO_OP = "no_op";

    final String TRANSFORM_EMP = "t_emp";
    final String TRANSFORM_DEP = "t_dep";
    final String DELETE_EMP = "d_emp";
    final String DELETE_DEP = "d_dep";

    final String PRE_TRANSFORM = "pre_transform";
    final String POST_DEL = "post_del";

    // TODO this is ugly think for better way to detect that a EtlRelTransferFunctor is for delete
    private TransformInfo empDInfo;
    private TransformInfo depDInfo;
}
