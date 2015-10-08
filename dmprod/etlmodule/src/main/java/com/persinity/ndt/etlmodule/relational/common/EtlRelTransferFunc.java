/**
 * Copyright (c) 2015 Persinity Inc.
 */
package com.persinity.ndt.etlmodule.relational.common;

import static com.persinity.common.StringUtils.format;
import static com.persinity.common.invariant.Invariant.notEmpty;
import static com.persinity.common.invariant.Invariant.notNull;

import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import org.apache.log4j.Logger;

import com.google.common.base.Function;
import com.google.common.base.Functions;
import com.persinity.common.collection.DirectedEdge;
import com.persinity.common.db.RelDb;
import com.persinity.common.logging.Log4jLogger;
import com.persinity.ndt.db.TransactionId;
import com.persinity.ndt.dbagent.relational.AgentSqlStrategy;
import com.persinity.ndt.dbagent.relational.SchemaInfo;
import com.persinity.ndt.transform.RelExtractFunc;
import com.persinity.ndt.transform.RelLoadFunc;
import com.persinity.ndt.transform.TupleFunc;

/**
 * TODO abstract this so it can be used in PreMigrateRelTransferFunc where we have ETL too.
 * Reverse the child class to build the parameters and it will be named: IdRangeEtlRelTransferFunc
 *
 * @author Ivan Dachev
 */
public class EtlRelTransferFunc extends IdRangeRelTransferFunc {

    /**
     * @param extractF
     * @param transformF
     * @param loadF
     * @param rangeModBase
     *         the mod base used to calculate ranges
     * @param idRange
     *         the range of ID values or their hash codes of the records
     * @param tids
     *         window TIDs, padded with last TID to fit power of two nums for stmt caching
     * @param schemas
     * @param sqlStrategy
     */
    public EtlRelTransferFunc(final RelExtractFunc extractF, final TupleFunc transformF, final RelLoadFunc loadF,
            final int rangeModBase, final DirectedEdge<Integer, Integer> idRange,
            final List<? extends TransactionId> tids, final DirectedEdge<SchemaInfo, SchemaInfo> schemas,
            final AgentSqlStrategy sqlStrategy) {
        super(rangeModBase, idRange, tids, schemas, sqlStrategy);

        notNull(extractF);
        notNull(transformF);
        notNull(loadF);
        notNull(idRange);
        notEmpty(tids);
        notNull(schemas);
        notNull(sqlStrategy);

        this.extractF = extractF;
        this.transformF = transformF;
        this.loadF = loadF;
        etFunc = Functions.compose(transformF, extractF);
    }

    /**
     * @return The function used for extracting data.
     */
    public RelExtractFunc getExtractFunction() {
        return extractF;
    }

    /**
     * @return The function used for extracting data.
     */
    public TupleFunc getTransformFunction() {
        return transformF;
    }

    /**
     * @return The function used for loading data.
     */
    public RelLoadFunc getLoadFunction() {
        return loadF;
    }

    @SuppressWarnings("ConstantConditions")
    @Override
    public Integer apply(final DirectedEdge<RelDb, RelDb> dataBridge) {
        log.debug("Applying {} on {}", this, dataBridge);

        final List<Object> params = prepareParams();
        final DirectedEdge<RelDb, List<?>> dbToParams = new DirectedEdge<RelDb, List<?>>(dataBridge.src(), params);
        final Iterator<Map<String, Object>> etRsIt = etFunc.apply(dbToParams);

        final int res = loadF.apply(new DirectedEdge<>(dataBridge.dst(), etRsIt));
        log.debug("Applied res: {}", res);

        dataBridge.src().commit();
        dataBridge.dst().commit();

        return res;
    }

    @Override
    public boolean equals(Object object) {

        if (this == object) {
            return true;
        }
        if (!(object instanceof EtlRelTransferFunc)) {
            return false;
        }

        EtlRelTransferFunc that = (EtlRelTransferFunc) object;
        return super.equals(that) &&
                Objects.equals(getExtractFunction(), that.getExtractFunction()) &&
                Objects.equals(getTransformFunction(), that.getTransformFunction()) &&
                Objects.equals(getLoadFunction(), that.getLoadFunction());
    }

    @Override
    public int hashCode() {
        if (hashCode == null) {
            hashCode = Objects.hash(super.hashCode(), getExtractFunction(), getTransformFunction(), getLoadFunction());
        }
        return hashCode;
    }

    @Override
    public String toString() {
        if (toString == null) {
            toString = format("{} {} {} -> {}", super.toString(), getExtractFunction(), getTransformFunction(),
                    getLoadFunction());
        }
        return toString;
    }

    private final RelExtractFunc extractF;
    private final TupleFunc transformF;
    private final RelLoadFunc loadF;

    private Function<DirectedEdge<RelDb, List<?>>, Iterator<Map<String, Object>>> etFunc;

    private Integer hashCode;
    private String toString;

    private static final Log4jLogger log = new Log4jLogger(Logger.getLogger(EtlRelTransferFunc.class));
}
