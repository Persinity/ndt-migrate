/**
 * Copyright (c) 2015 Persinity Inc.
 */
package com.persinity.ndt.dbagent.relational.impl;

import com.google.common.base.Function;
import com.persinity.common.db.RelDb;
import com.persinity.common.fp.Functor;
import com.persinity.ndt.dbagent.relational.AgentSqlStrategy;
import com.persinity.ndt.dbagent.relational.SchemaInfo;
import com.persinity.ndt.transform.RelFunc;

/**
 * Functor that returns function, which cleanups CLOG garbage entities.
 *
 * @author Ivan Dachev
 */
public class ClogGcFunctor implements Functor<RelDb, RelDb, String, Function<RelDb, RelDb>> {

    public ClogGcFunctor(final SchemaInfo schema, final AgentSqlStrategy strategy) {
        assert schema != null && strategy != null;
        this.schema = schema;
        this.strategy = strategy;
    }

    @Override
    public Function<RelDb, RelDb> apply(final String tableName) {
        // TODO can be optimized to create ranges of GIDs to be cleanup in parallel
        // make one clogGcStatement to return all GIDs for GC
        // make one DELETE FROM clog_... WHERE gid IN (?, ?, ...)
        final String clogTableName = schema.getClogTableName(tableName);
        return new RelFunc(strategy.clogGcStatement(clogTableName, SchemaInfo.TAB_TRLOG));
    }

    private final SchemaInfo schema;
    private final AgentSqlStrategy strategy;
}
