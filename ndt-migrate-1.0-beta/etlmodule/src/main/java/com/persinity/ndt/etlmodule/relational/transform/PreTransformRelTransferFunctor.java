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

import java.util.Collections;
import java.util.Set;

import com.persinity.common.collection.DirectedEdge;
import com.persinity.common.db.RelDb;
import com.persinity.ndt.dbagent.relational.AgentSqlStrategy;
import com.persinity.ndt.dbagent.relational.SchemaInfo;
import com.persinity.ndt.etlmodule.relational.common.RelTransferFunctor;
import com.persinity.ndt.etlmodule.relational.migrate.PreMigrateRelTransferFunc;
import com.persinity.ndt.transform.TransferFunc;
import com.persinity.ndt.transform.TransferWindow;

/**
 * Build {@link PreMigrateRelTransferFunc}
 *
 * @author Ivan Dachev
 */
public class PreTransformRelTransferFunctor extends RelTransferFunctor {

    public PreTransformRelTransferFunctor(final TransferWindow<RelDb, RelDb> tWindow,
            final DirectedEdge<SchemaInfo, SchemaInfo> schemas, final AgentSqlStrategy sqlStrategy) {
        super(tWindow, schemas, sqlStrategy);
    }

    @Override
    public Set<TransferFunc<RelDb, RelDb>> apply(final Void aVoid) {
        final TransferFunc<RelDb, RelDb> postMigrateRelTransferFunc = new PreTransformRelTransferFunc(
                getTransferWindow().getSrcTids(), getSchemas(), getSqlStrategy());
        return Collections.singleton(postMigrateRelTransferFunc);
    }
}
