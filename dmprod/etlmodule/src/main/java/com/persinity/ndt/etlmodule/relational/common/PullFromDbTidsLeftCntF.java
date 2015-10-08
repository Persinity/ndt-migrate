/**
 * Copyright (c) 2015 Persinity Inc.
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
