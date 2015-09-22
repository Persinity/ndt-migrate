/**
 * Copyright (c) 2015 Persinity Inc.
 */
package com.persinity.ndt.etlmodule.relational.common;

import static com.persinity.common.invariant.Invariant.notEmpty;

import java.util.List;

import com.persinity.common.StringUtils;
import com.persinity.common.collection.DirectedEdge;
import com.persinity.common.db.RelDb;
import com.persinity.ndt.db.TransactionId;
import com.persinity.ndt.dbagent.relational.AgentSqlStrategy;
import com.persinity.ndt.dbagent.relational.SchemaInfo;

/**
 * No operation function.
 *
 * @author Ivan Dachev
 */
public class NoOpsRelTransferFunc extends RelTransferFunc {

    public NoOpsRelTransferFunc(final String context, final List<? extends TransactionId> tids,
            final DirectedEdge<SchemaInfo, SchemaInfo> schemas, final AgentSqlStrategy sqlStrategy) {
        super(tids, schemas, sqlStrategy);
        notEmpty(context);
        this.context = context;
    }

    @Override
    public Integer apply(final DirectedEdge<RelDb, RelDb> dataBridge) {
        return 0;
    }

    @Override
    public boolean equals(Object object) {
        return this == object;
    }

    @Override
    public int hashCode() {
        return System.identityHashCode(this);
    }

    @Override
    public String toString() {
        if (toString == null) {
            toString = StringUtils.format("{}({})", super.toString(), context);
        }
        return toString;
    }

    private final String context;
    private String toString;
}
