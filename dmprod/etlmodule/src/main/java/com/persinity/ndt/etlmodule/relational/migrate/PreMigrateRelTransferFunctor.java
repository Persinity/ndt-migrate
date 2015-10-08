/**
 * Copyright (c) 2015 Persinity Inc.
 */
package com.persinity.ndt.etlmodule.relational.migrate;

import java.util.Collections;
import java.util.Set;

import com.persinity.common.collection.DirectedEdge;
import com.persinity.common.db.RelDb;
import com.persinity.ndt.dbagent.relational.AgentSqlStrategy;
import com.persinity.ndt.dbagent.relational.SchemaInfo;
import com.persinity.ndt.etlmodule.relational.common.RelTransferFunctor;
import com.persinity.ndt.transform.TransferFunc;
import com.persinity.ndt.transform.TransferWindow;

/**
 * Build {@link PreMigrateRelTransferFunc}
 *
 * @author Ivan Dachev
 */
public class PreMigrateRelTransferFunctor extends RelTransferFunctor {

    public PreMigrateRelTransferFunctor(final TransferWindow<RelDb, RelDb> tWindow,
            final DirectedEdge<SchemaInfo, SchemaInfo> schemas, final AgentSqlStrategy sqlStrategy) {
        super(tWindow, schemas, sqlStrategy);
    }

    @Override
    public Set<TransferFunc<RelDb, RelDb>> apply(final Void aVoid) {
        final TransferFunc<RelDb, RelDb> preMigrateRelTransferFunc = new PreMigrateRelTransferFunc(
                getTransferWindow().getSrcTids(), getSchemas(), getSqlStrategy());
        return Collections.singleton(preMigrateRelTransferFunc);
    }
}
