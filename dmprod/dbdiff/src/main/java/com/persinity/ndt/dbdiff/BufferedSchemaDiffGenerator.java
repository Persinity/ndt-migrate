/**
 * Copyright (c) 2015 Persinity Inc.
 */

package com.persinity.ndt.dbdiff;

import static com.persinity.common.invariant.Invariant.notNull;

import java.util.Collection;

import com.persinity.common.collection.DirectedEdge;
import com.persinity.ndt.dbagent.relational.AgentSqlStrategy;
import com.persinity.ndt.dbagent.relational.SchemaInfo;

/**
 * @author dyordanov
 */
public class BufferedSchemaDiffGenerator implements SchemaDiffGenerator {

    public BufferedSchemaDiffGenerator(final SchemaDiffGenerator delegate) {
        notNull(delegate);
        this.delegate = delegate;
    }

    @Override
    public synchronized Collection<TransformEntity> generateDiff(final DirectedEdge<SchemaInfo, SchemaInfo> dbSources,
            final AgentSqlStrategy sqlStrategy) {
        if (cache == null) {
            cache = delegate.generateDiff(dbSources, sqlStrategy);
        }
        return cache;
    }

    private Collection<TransformEntity> cache;
    private final SchemaDiffGenerator delegate;

}
