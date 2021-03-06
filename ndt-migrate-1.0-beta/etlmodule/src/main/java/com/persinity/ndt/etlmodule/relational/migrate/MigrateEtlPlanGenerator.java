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

import java.util.Map;

import com.persinity.common.collection.DirectedEdge;
import com.persinity.common.collection.GraphUtils;
import com.persinity.common.db.RelDb;
import com.persinity.ndt.dbagent.relational.AgentSqlStrategy;
import com.persinity.ndt.dbagent.relational.SchemaInfo;
import com.persinity.ndt.etlmodule.EtlPlanDag;
import com.persinity.ndt.etlmodule.EtlPlanGenerator;
import com.persinity.ndt.etlmodule.relational.TransferFunctorFactory;
import com.persinity.ndt.etlmodule.relational.TransformInfo;
import com.persinity.ndt.etlmodule.relational.common.BaseEtlPlanGenerator;
import com.persinity.ndt.etlmodule.relational.common.SizePartitioner;
import com.persinity.ndt.transform.TransferWindow;

/**
 * {@link EtlPlanGenerator} for building extract-transform-load plan for migration between relational databases.
 *
 * @author Doichin Yordanov
 */
public class MigrateEtlPlanGenerator extends BaseEtlPlanGenerator {

    /**
     * @param transformMap
     *         Destination entity to {@link TransformInfo} map.
     * @param etlInstructionSize
     *         Changes for how many records are to be transferred per single ETL instruction.
     * @param schemas
     * @param sqlStrategy
     */
    public MigrateEtlPlanGenerator(final Map<String, TransformInfo> transformMap, final int etlInstructionSize,
            final DirectedEdge<SchemaInfo, SchemaInfo> schemas, final AgentSqlStrategy sqlStrategy) {
        this(new MigrateTransferFunctorFactory(transformMap, new SizePartitioner(etlInstructionSize, sqlStrategy),
                schemas, sqlStrategy));
    }

    /**
     * @param etlFunctorFactory
     */
    public MigrateEtlPlanGenerator(final TransferFunctorFactory etlFunctorFactory) {
        super(etlFunctorFactory);
    }

    @Override
    public EtlPlanDag<RelDb, RelDb> newEtlPlan(final TransferWindow<RelDb, RelDb> transferWindow) {
        return newEtlPlan(transferWindow, getTransferFunctorFactory(), GraphUtils.REVERSED);
    }
}
