/**
 * Copyright (c) 2015 Persinity Inc.
 */
package com.persinity.ndt.etlmodule.relational.common;

import static com.persinity.common.StringUtils.format;
import static com.persinity.common.collection.CollectionUtils.addPadded;
import static com.persinity.common.collection.CollectionUtils.stringListOf;
import static com.persinity.common.invariant.Invariant.notNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import com.persinity.common.collection.DirectedEdge;
import com.persinity.common.db.RelDb;
import com.persinity.common.db.metainfo.Col;
import com.persinity.common.db.metainfo.In;
import com.persinity.common.db.metainfo.SqlFilter;
import com.persinity.ndt.db.TransactionId;
import com.persinity.ndt.dbagent.relational.AgentSqlStrategy;
import com.persinity.ndt.dbagent.relational.SchemaInfo;
import com.persinity.ndt.etlmodule.relational.Partitioner;
import com.persinity.ndt.etlmodule.relational.TransformInfo;
import com.persinity.ndt.transform.TransferFunc;
import com.persinity.ndt.transform.TransferWindow;

/**
 * Extends {@link RelTransferFunctor} with ID range implementation.
 * <p/>
 * Used to generate parallel transfer functions working on same entity and different PK ID ranges.
 *
 * @author Ivan Dachev
 */
public abstract class IdRangeRelTransferFunctor extends RelTransferFunctor {

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
    public IdRangeRelTransferFunctor(final String dstEntity, final TransformInfo transformInfo,
            final Partitioner pidPartitioner, final TransferWindow<RelDb, RelDb> tWindow,
            final DirectedEdge<SchemaInfo, SchemaInfo> schemas, final AgentSqlStrategy sqlStrategy) {
        super(tWindow, schemas, sqlStrategy);

        notNull(dstEntity, "dstEntity");
        notNull(transformInfo, "transformInfo");
        notNull(pidPartitioner, "pidPartitioner");

        this.dstEntity = dstEntity;
        this.transformInfo = transformInfo;
        this.pidPartitioner = pidPartitioner;
    }

    /**
     * Should be called on {@link RelTransferFunctor#apply}
     *
     * @param db
     * @param entity
     * @param keyCols
     */
    protected void calculateRanges(final RelDb db, final String entity, final List<Col> keyCols) {
        @SuppressWarnings("unchecked")
        final List<TransactionId> tids = (List<TransactionId>) getTransferWindow().getSrcTids();
        final List<String> tidStrings = stringListOf(tids);
        final SqlFilter<?> tidFilter = new In<>(TID_COL, tidStrings);
        partitionData = pidPartitioner.partition(db, entity, keyCols, tidFilter);
        paddedTids = new ArrayList<>();
        addPadded(paddedTids, tids, transformInfo.getMaxTidsCount());
    }

    @Override
    public String toString() {
        if (toString == null) {
            toString = format("{} {} {}", super.toString(), getDstEntity(), getTransformInfo());
        }
        return toString;
    }

    @Override
    public boolean equals(final Object obj) {
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof IdRangeRelTransferFunctor)) {
            return false;
        }
        final IdRangeRelTransferFunctor that = (IdRangeRelTransferFunctor) obj;
        return super.equals(that) && getDstEntity().equals(that.getDstEntity()) && getTransformInfo()
                .equals(that.getTransformInfo()) && getPidPartitioner().equals(that.getPidPartitioner());
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), getDstEntity(), getTransformInfo(), getPidPartitioner());
    }

    public String getDstEntity() {
        return dstEntity;
    }

    public TransformInfo getTransformInfo() {
        return transformInfo;
    }

    public Partitioner getPidPartitioner() {
        return pidPartitioner;
    }

    public List<TransactionId> getPaddedTids() {
        return paddedTids;
    }

    public Partitioner.PartitionData getIdRange() {
        return partitionData;
    }

    private static final Col TID_COL = new Col(SchemaInfo.COL_TID);

    private final String dstEntity;
    private final TransformInfo transformInfo;
    private final Partitioner pidPartitioner;

    private List<TransactionId> paddedTids;
    private Partitioner.PartitionData partitionData;
    private String toString;
}
