/**
 * Copyright (c) 2015 Persinity Inc.
 */

package com.persinity.ndt.controller.step;

import static com.persinity.common.StringUtils.format;

import java.util.Map;

import com.persinity.common.Resource;
import com.persinity.common.collection.DirectedEdge;
import com.persinity.common.collection.Pool;
import com.persinity.common.db.RelDb;
import com.persinity.common.db.RelDbPool;
import com.persinity.ndt.controller.script.Step;
import com.persinity.ndt.etlmodule.WindowGenerator;
import com.persinity.ndt.etlmodule.relational.transform.InConsistentTransformWindowGenerator;
import com.persinity.ndt.transform.EntitiesDag;

/**
 * Merges the initially transferred target entities data with the deltas migrated during it and enables target consistency checks.
 *
 * @author dyordanov
 */
public class RollTargetConsistent extends Transform {
    /**
     * @param prev
     *         Step or {@code null} if the first step
     * @param delaySecs
     *         from previous step
     * @param ctx
     */
    public RollTargetConsistent(final Step prev, final int delaySecs, final Map<Object, Object> ctx) {
        super(prev, delaySecs, ctx);
    }

    @Override
    protected WindowGenerator<RelDb, RelDb> getTransformWinGen(
            final DirectedEdge<Pool<RelDb>, Pool<RelDb>> dataPoolBridge,
            EntitiesDag entityDag) {
        final WindowGenerator<RelDb, RelDb> result = InConsistentTransformWindowGenerator
                .newInstance(getSrcClogAgent(), getDstClogAgent(), dataPoolBridge, entityDag, getSqlStrategy(),
                        getWindowSize());
        result.stopWhenFeedExhausted();
        return result;
    }

    @Override
    protected String getStartMsg() {
        return format("Rolling Destination DB \"{}\" to consistent state...",
                ((RelDbPool) getDstAppDbPool()).metaInfo().getUserName());
    }

    @Override
    protected String getStopMsg() {
        return format("Destination DB \"{}\" is in consistent state.",
                ((RelDbPool) getDstAppDbPool()).metaInfo().getUserName());
    }

    @Override
    protected void preExecute() {
    }

    @Override
    protected void postExecute() {
        resource.accessAndClose(new Resource.Accessor<RelDb, Void>(getDstAppDbPool().get(), null) {
            @Override
            public Void access(final RelDb resource) throws Exception {
                getProcessor().process(getDstSchemaAgent().enableIntegrity(), resource);
                return null;
            }
        });
    }

    private final Resource resource = new Resource();
}
