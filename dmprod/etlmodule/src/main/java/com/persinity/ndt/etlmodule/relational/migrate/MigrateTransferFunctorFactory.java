/**
 * Copyright (c) 2015 Persinity Inc.
 */
package com.persinity.ndt.etlmodule.relational.migrate;

import static com.persinity.common.StringUtils.format;

import java.util.Map;

import com.persinity.common.collection.DirectedEdge;
import com.persinity.common.db.RelDb;
import com.persinity.ndt.dbagent.relational.AgentSqlStrategy;
import com.persinity.ndt.dbagent.relational.SchemaInfo;
import com.persinity.ndt.etlmodule.TransferFunctor;
import com.persinity.ndt.etlmodule.relational.Partitioner;
import com.persinity.ndt.etlmodule.relational.TransferFunctorFactory;
import com.persinity.ndt.etlmodule.relational.TransformInfo;
import com.persinity.ndt.etlmodule.relational.common.EtlRelTransferFunctor;
import com.persinity.ndt.etlmodule.relational.common.NoOpsRelTransferFunctor;
import com.persinity.ndt.transform.TransferWindow;

/**
 * @author Doichin Yordanov
 */
public class MigrateTransferFunctorFactory implements TransferFunctorFactory {

    private final Map<String, TransformInfo> transformMap;
    private final Partitioner pidPartitioner;
    private final DirectedEdge<SchemaInfo, SchemaInfo> schemas;
    private final AgentSqlStrategy sqlStrategy;

    private String toString;

    public MigrateTransferFunctorFactory(final Map<String, TransformInfo> transformMap,
            final Partitioner pidPartitioner, final DirectedEdge<SchemaInfo, SchemaInfo> schemas,
            final AgentSqlStrategy sqlStrategy) {

        this.transformMap = transformMap;
        this.pidPartitioner = pidPartitioner;
        this.schemas = schemas;
        this.sqlStrategy = sqlStrategy;
    }

    @Override
    public TransferFunctor<RelDb, RelDb> newPreWindowTransferFunctor(final TransferWindow<RelDb, RelDb> win) {
        return new PreMigrateRelTransferFunctor(win, schemas, sqlStrategy);
    }

    @Override
    public TransferFunctor<RelDb, RelDb> newEntityTransferFunctor(final String dstEntity,
            final TransferWindow<RelDb, RelDb> win) {
        final TransformInfo transformInfo = transformMap.get(dstEntity);
        if (win.getAffectedSrcEntities().contains(dstEntity)) {
            return new EtlRelTransferFunctor(dstEntity, transformInfo, pidPartitioner, win, schemas, sqlStrategy);
        } else {
            return new NoOpsRelTransferFunctor(dstEntity, win, schemas, sqlStrategy);
        }
    }

    @Override
    public TransferFunctor<RelDb, RelDb> newPostWindowTransferFunctor(final TransferWindow<RelDb, RelDb> win) {
        return new PostMigrateRelTransferFunctor(win, schemas, sqlStrategy);
    }

    @Override
    public String toString() {
        if (toString == null) {
            toString = format("{}{}", this.getClass().getSimpleName(), pidPartitioner.toString());
        }
        return toString;
    }

}
