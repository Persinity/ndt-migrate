/**
 * Copyright (c) 2015 Persinity Inc.
 */
package com.persinity.ndt.etlmodule.relational.transform;

import static com.persinity.common.collection.CollectionUtils.addPadded;
import static com.persinity.common.invariant.Invariant.assertArg;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import com.persinity.common.MathUtil;
import com.persinity.common.collection.DirectedEdge;
import com.persinity.common.db.RelDb;
import com.persinity.common.db.metainfo.Col;
import com.persinity.ndt.db.TransactionId;
import com.persinity.ndt.dbagent.relational.AgentSqlStrategy;
import com.persinity.ndt.dbagent.relational.SchemaInfo;
import com.persinity.ndt.etlmodule.relational.common.RelTransferFunc;
import com.persinity.ndt.transform.ParamDmlFunc;

/**
 * Set status of trlog entries to {@link SchemaInfo.TrlogStatusType#P} - Processing.
 *
 * @author Ivan Dachev
 */
public class PreTransformRelTransferFunc extends RelTransferFunc {

    public PreTransformRelTransferFunc(final List<? extends TransactionId> tids,
            final DirectedEdge<SchemaInfo, SchemaInfo> schemas, final AgentSqlStrategy sqlStrategy) {
        super(tids, schemas, sqlStrategy);
    }

    @SuppressWarnings("ConstantConditions")
    @Override
    public Integer apply(final DirectedEdge<RelDb, RelDb> dataBridge) {
        final List<String> tids = getTids();
        final int tidsSize = MathUtil.ceilingByPowerOfTwo(tids.size());
        final ParamDmlFunc updateF = createTrlogUpdateStatusFunctions(tidsSize);

        final List<String> params = new ArrayList<>();
        addPadded(params, tids, tidsSize);

        final DirectedEdge<RelDb, List<?>> dbToParams = new DirectedEdge<RelDb, List<?>>(dataBridge.src(), params);

        final int res = updateF.apply(dbToParams);

        dataBridge.src().commit();

        return res;
    }

    private ParamDmlFunc createTrlogUpdateStatusFunctions(final int tidsSize) {
        // here use the source schema as it is the ndt one

        final SchemaInfo srcSchema = getSchemas().src();

        final Set<Col> srcTrlogCols = srcSchema.getTableCols(SchemaInfo.TAB_TRLOG);
        assertArg(!srcTrlogCols.isEmpty(), "Failed to find src trlog entity columns: {}", srcTrlogCols);

        final LinkedList<Col> cols = new LinkedList<>(srcTrlogCols);

        final String updateSql = getSqlStrategy()
                .trlogUpdateStatus(SchemaInfo.TAB_TRLOG, SchemaInfo.TrlogStatusType.P, tidsSize);
        return new ParamDmlFunc(updateSql, cols);
    }
}