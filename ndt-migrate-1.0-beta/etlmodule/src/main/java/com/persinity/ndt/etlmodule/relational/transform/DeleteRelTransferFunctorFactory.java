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

import java.util.Map;

import com.persinity.common.collection.DirectedEdge;
import com.persinity.common.db.RelDb;
import com.persinity.ndt.dbagent.relational.AgentSqlStrategy;
import com.persinity.ndt.dbagent.relational.SchemaInfo;
import com.persinity.ndt.etlmodule.TransferFunctor;
import com.persinity.ndt.etlmodule.relational.Partitioner;
import com.persinity.ndt.etlmodule.relational.TransformInfo;
import com.persinity.ndt.etlmodule.relational.common.EtlRelTransferFunctor;
import com.persinity.ndt.etlmodule.relational.common.NoOpsRelTransferFunctor;
import com.persinity.ndt.etlmodule.relational.common.PostTransferFunctor;
import com.persinity.ndt.transform.TransferWindow;

/**
 * @author Ivan Dachev
 */
public class DeleteRelTransferFunctorFactory extends TransformFunctorFactory {

    public DeleteRelTransferFunctorFactory(final Map<String, TransformInfo> deleteMap, final Partitioner pidPartitioner,
            final DirectedEdge<SchemaInfo, SchemaInfo> schemas, final AgentSqlStrategy sqlStrategy) {
        super(deleteMap, pidPartitioner, schemas, sqlStrategy);
    }

    @Override
    public TransferFunctor<RelDb, RelDb> newPreWindowTransferFunctor(final TransferWindow<RelDb, RelDb> win) {
        return new NoOpsRelTransferFunctor("<PreDelete>", win, schemas, sqlStrategy);
    }

    @Override
    public TransferFunctor<RelDb, RelDb> newEntityTransferFunctor(final String dstEntity,
            final TransferWindow<RelDb, RelDb> win) {
        if (isDstEntityAffected(dstEntity, win.getAffectedSrcEntities())) {
            return new EtlRelTransferFunctor(dstEntity, etlMap.get(dstEntity), pidPartitioner, win, schemas,
                    sqlStrategy);
        } else {
            return new NoOpsRelTransferFunctor(dstEntity, win, schemas, sqlStrategy);
        }
    }

    @Override
    public TransferFunctor<RelDb, RelDb> newPostWindowTransferFunctor(final TransferWindow<RelDb, RelDb> win) {
        return new PostTransferFunctor(win, schemas, sqlStrategy);
    }
}
