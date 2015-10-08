/**
 * Copyright (c) 2015 Persinity Inc.
 */
package com.persinity.ndt.etlmodule.relational.common;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.log4j.Logger;

import com.google.common.collect.Lists;
import com.persinity.common.Resource;
import com.persinity.common.collection.DirectedEdge;
import com.persinity.common.db.RelDb;
import com.persinity.common.db.metainfo.Col;
import com.persinity.common.logging.Log4jLogger;
import com.persinity.ndt.dbagent.relational.AgentSqlStrategy;
import com.persinity.ndt.dbagent.relational.SchemaInfo;
import com.persinity.ndt.etlmodule.relational.Partitioner;
import com.persinity.ndt.etlmodule.relational.TransformInfo;
import com.persinity.ndt.transform.RelExtractFunc;
import com.persinity.ndt.transform.RelLoadFunc;
import com.persinity.ndt.transform.TransferFunc;
import com.persinity.ndt.transform.TransferWindow;
import com.persinity.ndt.transform.TupleFunc;

/**
 * Creates transform {@link TransferFunc} sets to be executed in parallel per entity. Each function is working on same
 * entity but different PK ID ranges.
 *
 * @author Ivan Dachev
 */
public class EtlRelTransferFunctor extends IdRangeRelTransferFunctor {

    /**
     * @param dstEntity
     *         That this functor will create {@link TransferFunc}s for.
     * @param transformInfo
     *         Source to Destination mapping information
     * @param pidPartitioner
     *         {@link Partitioner} used to partition the leading source entity change records for parallel
     *         processing.
     * @param tWindow
     *         {@link TransferWindow} composed into all generated {@link TransferFunc}s, so that they can extract
     *         data relevant to the window.
     * @param schemas
     * @param sqlStrategy
     */
    public EtlRelTransferFunctor(final String dstEntity, final TransformInfo transformInfo,
            final Partitioner pidPartitioner, final TransferWindow<RelDb, RelDb> tWindow,
            final DirectedEdge<SchemaInfo, SchemaInfo> schemas, final AgentSqlStrategy sqlStrategy) {
        super(dstEntity, transformInfo, pidPartitioner, tWindow, schemas, sqlStrategy);
    }

    @Override
    public Set<TransferFunc<RelDb, RelDb>> apply(final Void aVoid) {
        final TransformInfo transformInfo = getTransformInfo();
        final RelExtractFunc extractF = transformInfo.getExtractFunc();
        final TupleFunc transformF = transformInfo.getTransformFunc();
        final RelLoadFunc loadF = transformInfo.getLoadFunc();

        final String sourceEntity = transformInfo.getEntityMapping().src();
        final List<Col> keyCols = Lists.newArrayList(transformInfo.getColumnsMapping().src());

        resource.accessAndClose(
                new Resource.Accessor<RelDb, Void>(getTransferWindow().getDataPoolBridge().src().get(), null) {
                    @Override
                    public Void access(final RelDb resource) throws Exception {
                        calculateRanges(resource, sourceEntity, keyCols);
                        return null;
                    }
                });

        final Set<TransferFunc<RelDb, RelDb>> etlFs = new HashSet<>();
        for (final DirectedEdge<Integer, Integer> range : getIdRange().getPartition()) {
            final TransferFunc<RelDb, RelDb> etlF = new EtlRelTransferFunc(extractF, transformF, loadF,
                    getIdRange().getModBase(), range, getPaddedTids(), getSchemas(), getSqlStrategy());
            log.debug("Created {} for {}", etlF, this);
            etlFs.add(etlF);
        }

        log.debug("Returned {} funcs for {}", etlFs.size(), this);

        return etlFs;
    }

    private static final Log4jLogger log = new Log4jLogger(Logger.getLogger(EtlRelTransferFunctor.class));
    private final static Resource resource = new Resource();
}
