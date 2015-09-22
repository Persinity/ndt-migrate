/**
 * Copyright (c) 2015 Persinity Inc.
 */
package com.persinity.ndt.etlmodule.relational.migrate;

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
import com.persinity.ndt.etlmodule.relational.common.PostRelTransferFunc;
import com.persinity.ndt.transform.ParamDmlFunc;

/**
 * Cleanup source trlog entries/
 * Set status of destination trlog entries to {@link SchemaInfo.TrlogStatusType#R} - Ready for process.
 *
 * @author Ivan Dachev
 */
public class PostMigrateRelTransferFunc extends PostRelTransferFunc {

    public PostMigrateRelTransferFunc(final List<? extends TransactionId> tids,
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
        final DirectedEdge<RelDb, List<?>> dbToParams = new DirectedEdge<RelDb, List<?>>(dataBridge.dst(), params);
        int res = updateF.apply(dbToParams);
        res += super.apply(dataBridge); // this will commit both src/dst DB connections
        return res;
    }

    private ParamDmlFunc createTrlogUpdateStatusFunctions(final int tidsSize) {
        // here use the destination schema as it is the ndt one

        final SchemaInfo dstSchema = getSchemas().dst();

        final Set<Col> dstTrlogCols = dstSchema.getTableCols(SchemaInfo.TAB_TRLOG);
        assertArg(!dstTrlogCols.isEmpty(), "Failed to find dst trlog entity columns: {}", dstTrlogCols);

        final LinkedList<Col> cols = new LinkedList<>(dstTrlogCols);

        final String updateSql = getSqlStrategy()
                .trlogUpdateStatus(SchemaInfo.TAB_TRLOG, SchemaInfo.TrlogStatusType.R, tidsSize);
        return new ParamDmlFunc(updateSql, cols);
    }
}
