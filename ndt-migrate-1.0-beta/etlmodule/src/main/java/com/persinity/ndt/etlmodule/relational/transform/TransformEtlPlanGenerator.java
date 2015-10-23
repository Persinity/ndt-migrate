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
import static com.persinity.common.collection.GraphUtils.NOT_REVERSED;
import static com.persinity.common.collection.GraphUtils.REVERSED;
import static com.persinity.common.collection.GraphUtils.addGraphToGraph;

import java.util.Map;

import com.persinity.common.collection.DirectedEdge;
import com.persinity.common.db.RelDb;
import com.persinity.ndt.dbagent.relational.AgentSqlStrategy;
import com.persinity.ndt.dbagent.relational.SchemaInfo;
import com.persinity.ndt.etlmodule.EtlPlanDag;
import com.persinity.ndt.etlmodule.EtlPlanEdge;
import com.persinity.ndt.etlmodule.EtlPlanGenerator;
import com.persinity.ndt.etlmodule.TransferFunctor;
import com.persinity.ndt.etlmodule.relational.Partitioner;
import com.persinity.ndt.etlmodule.relational.TransferFunctorFactory;
import com.persinity.ndt.etlmodule.relational.TransformInfo;
import com.persinity.ndt.etlmodule.relational.common.BaseEtlPlanGenerator;
import com.persinity.ndt.etlmodule.relational.common.SizePartitioner;
import com.persinity.ndt.transform.TransferWindow;

/**
 * {@link EtlPlanGenerator} for building ETL plan for transform stage from CCLOG to target entities.
 * <p/>
 * It builds two graphs one for transform stage and one for delete. These two graphs are connected in one, where the
 * transform one is executed before delete one. The delete graph is a reverse one to honor the relational constraints.
 *
 * @author Ivan Dachev
 */
public class TransformEtlPlanGenerator extends BaseEtlPlanGenerator {

    /**
     * @param mergeMap
     * @param deleteMap
     * @param etlInstructionSize
     *         Changes for how many records are to be transferred per single ETL instruction.
     * @param schemas
     * @param sqlStrategy
     */
    public TransformEtlPlanGenerator(final Map<String, TransformInfo> mergeMap,
            final Map<String, TransformInfo> deleteMap, final int etlInstructionSize,
            final DirectedEdge<SchemaInfo, SchemaInfo> schemas, final AgentSqlStrategy sqlStrategy) {
        this(mergeMap, deleteMap, new SizePartitioner(etlInstructionSize, sqlStrategy), schemas, sqlStrategy);
    }

    /**
     * @param mergeMap
     * @param deleteMap
     * @param partitioner
     * @param schemas
     * @param sqlStrategy
     */
    public TransformEtlPlanGenerator(final Map<String, TransformInfo> mergeMap,
            final Map<String, TransformInfo> deleteMap, final Partitioner partitioner,
            final DirectedEdge<SchemaInfo, SchemaInfo> schemas, final AgentSqlStrategy sqlStrategy) {
        this(new MergeRelTransferFunctorFactory(mergeMap, partitioner, schemas, sqlStrategy),
                new DeleteRelTransferFunctorFactory(deleteMap, partitioner, schemas, sqlStrategy));
    }

    /**
     * @param transformEtlFunctorFactory
     *         Used for creating ETL functors for given transaction window and entity
     */
    public TransformEtlPlanGenerator(final TransferFunctorFactory transformEtlFunctorFactory,
            final TransferFunctorFactory deleteEtlFunctorFactory) {
        super(transformEtlFunctorFactory);
        this.deleteEtlFunctorFactory = deleteEtlFunctorFactory;
    }

    /**
     * Builds transform and delete ETL plan DAGs and return combination of them:
     * <p/>
     * <p/>
     * <pre>
     *  transform       delete
     *     pre           pre
     *    / | \          / \
     *   1  2  3        4  5
     *   \ / \/        / \/ \
     *    4  5        1  2  3
     *    \ /          \ | /
     *    post         post
     * </pre>
     * <p/>
     * The result ETL plan DAG:
     * <p/>
     * <p/>
     * <pre>
     *     pre
     *    / | \
     *   1  2  3
     *   \ / \/      transform
     *    4  5
     *    \ /
     *    post
     *     |
     *    pre
     *    / \
     *    4  5
     *   / \ /\       delete
     *  1  2  3
     *  \  | /
     *   post
     * </pre>
     *
     * @param transferWindow
     *         to build ETL plan for
     * @return ETL plan DAG for transform and delete
     */
    @Override
    public EtlPlanDag<RelDb, RelDb> newEtlPlan(final TransferWindow<RelDb, RelDb> transferWindow) {
        final EtlPlanDag<RelDb, RelDb> transformDag = newEtlPlan(transferWindow, getTransferFunctorFactory(), REVERSED);
        final EtlPlanDag<RelDb, RelDb> deleteDag = newEtlPlan(transferWindow, deleteEtlFunctorFactory, NOT_REVERSED);

        final TransferFunctor<RelDb, RelDb> transformSinkV = transformDag.getBaseSinkVertex();
        final TransferFunctor<RelDb, RelDb> deleteSourceV = deleteDag.getRootSourceVertex();
        final EtlPlanEdge<RelDb, RelDb> edge = buildEdgeF.apply(new DirectedEdge<>(transformSinkV, deleteSourceV));

        addGraphToGraph(deleteDag, transformDag);
        transformDag.addEdge(transformSinkV, deleteSourceV, edge);
        transformDag.setBaseSinkVertex(deleteDag.getBaseSinkVertex());

        return transformDag;
    }

    @Override
    public String toString() {
        if (toString == null) {
            toString = format("{}({})({})", this.getClass().getSimpleName(), getTransferFunctorFactory(),
                    deleteEtlFunctorFactory);
        }
        return toString;
    }

    private final TransferFunctorFactory deleteEtlFunctorFactory;
    private String toString;
}
