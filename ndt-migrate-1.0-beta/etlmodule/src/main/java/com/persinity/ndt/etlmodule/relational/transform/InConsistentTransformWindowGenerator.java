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

import com.google.common.base.Function;
import com.persinity.common.collection.DirectedEdge;
import com.persinity.common.collection.Pool;
import com.persinity.common.db.RelDb;
import com.persinity.ndt.dbagent.ClogAgent;
import com.persinity.ndt.dbagent.relational.AgentSqlStrategy;
import com.persinity.ndt.etlmodule.WindowGenerator;
import com.persinity.ndt.etlmodule.relational.common.BaseWindowGenerator;
import com.persinity.ndt.etlmodule.relational.common.PullFromWinGenTidsLeftCntF;
import com.persinity.ndt.transform.EntitiesDag;

/**
 * {@link WindowGenerator} for transformations of data between staging area and inconsistent target schema.
 *
 * @author dyordanov
 */
public class InConsistentTransformWindowGenerator extends BaseWindowGenerator implements WindowGenerator<RelDb, RelDb> {

    private InConsistentTransformWindowGenerator(final DirectedEdge<Pool<RelDb>, Pool<RelDb>> dataPoolBridge,
            final EntitiesDag dag, final AgentSqlStrategy sqlStrategy, final long gidSentinel, final int windowSize) {
        super(dataPoolBridge, new PullFromWinGenTidsLeftCntF(gidSentinel), new RelationIgnorantEntityDagFunc(dag),
                sqlStrategy, windowSize);
    }

    public static InConsistentTransformWindowGenerator newInstance(final ClogAgent<Function<RelDb, RelDb>> srcClogAgent,
            final ClogAgent<Function<RelDb, RelDb>> dstClogAgent,
            final DirectedEdge<Pool<RelDb>, Pool<RelDb>> dataBridge, final EntitiesDag dag,
            final AgentSqlStrategy sqlStrategy, final int windowSize) {

        final Long srcLastGid = srcClogAgent.getLastGid();
        final Long dstLastGid = dstClogAgent.getLastGid();
        final long gidSentinel = Math.max(srcLastGid, dstLastGid);
        return new InConsistentTransformWindowGenerator(dataBridge, dag, sqlStrategy, gidSentinel, windowSize);
    }

}
