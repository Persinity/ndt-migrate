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

import static com.persinity.common.invariant.Invariant.notNull;

import com.persinity.common.Resource;
import com.persinity.common.db.RelDb;
import com.persinity.ndt.dbagent.relational.AgentSqlStrategy;
import com.persinity.ndt.transform.QueryFunc;

/**
 * @author dyordanov
 */
public class PullFromDbTidsLeftCntF implements TidsLeftCntF {

    public PullFromDbTidsLeftCntF(final AgentSqlStrategy sqlStrategy) {
        notNull(sqlStrategy);
        qryFunc = new QueryFunc(sqlStrategy.countUnprocessedTids());
    }

    @Override
    public Long apply(final BaseWindowGenerator input) {

        final Long result = resource
                .accessAndClose(new Resource.Accessor<RelDb, Long>(input.getDataPoolBridge().src().get(), null) {
                    @Override
                    public Long access(final RelDb resource) throws Exception {
                        return ((Number) qryFunc.apply(resource).next().get(CNT)).longValue();
                    }
                });

        return result;
    }

    static final String CNT = "cnt";
    final QueryFunc qryFunc;
    final Resource resource = new Resource();

}
