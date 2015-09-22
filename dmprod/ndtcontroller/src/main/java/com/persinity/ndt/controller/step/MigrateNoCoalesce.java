/**
 * Copyright (c) 2015 Persinity Inc.
 */
package com.persinity.ndt.controller.step;

import java.util.Collection;
import java.util.Map;

import com.persinity.common.collection.DirectedEdge;
import com.persinity.ndt.controller.script.Step;
import com.persinity.ndt.dbagent.relational.AgentSqlStrategy;
import com.persinity.ndt.dbagent.relational.SchemaInfo;
import com.persinity.ndt.dbdiff.SchemaDiffGenerator;
import com.persinity.ndt.dbdiff.TransformEntity;
import com.persinity.ndt.etlmodule.relational.TransformInfo;
import com.persinity.ndt.etlmodule.relational.common.RelTransformMapFactory;
import com.persinity.ndt.etlmodule.relational.common.TransformMapFactory;

/**
 * Implement migrate step without coalesce used
 * until rolling target to consistent state is done.
 * <p/>
 * We need to apply all statements captured by the triggers
 * while the initial backup/copy tool was working.
 * <p/>
 * Otherwise the coalesce phase could remove some deletes
 * that need to be executed in order to bring destination to
 * consistent state. That could happen when the backup/copy tool
 * finished right before the delete statement was committed at
 * source and captured from our triggers.
 *
 * @author Ivan Dachev
 */
public class MigrateNoCoalesce extends Migrate {

    /**
     * @param prev
     *         Step or {@code null} if the first step
     * @param delaySecs
     *         from previous step
     * @param ctx
     */
    public MigrateNoCoalesce(final Step prev, final int delaySecs, final Map<Object, Object> ctx) {
        super(prev, delaySecs, ctx);
    }

    @Override
    protected String getStartMsg() {
        return null;
    }

    @Override
    protected Map<String, TransformInfo> getMigrateMap(final DirectedEdge<SchemaInfo, SchemaInfo> appSchemas,
            final DirectedEdge<SchemaInfo, SchemaInfo> ndtSchemas, final AgentSqlStrategy sqlStrategy) {
        final SchemaDiffGenerator sdg = ContextUtil.getSchemaDiffGenerator(getCtx());
        final Collection<TransformEntity> transformEntities = sdg.generateDiff(appSchemas, sqlStrategy);
        final TransformMapFactory tmf = new RelTransformMapFactory(ndtSchemas, sqlStrategy, transformEntities,
                getWindowSize(), TransformInfo.MIGRATE_DONT_COALESCE);
        final Map<String, TransformInfo> result = tmf.getMigrateNoCoalesceMap();
        return result;
    }

}
