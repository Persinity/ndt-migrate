/**
 * Copyright (c) 2015 Persinity Inc.
 */
package com.persinity.ndt.etlmodule.relational.migrate;

import static com.persinity.common.collection.CollectionUtils.addPadded;
import static com.persinity.common.invariant.Invariant.assertArg;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.google.common.base.Function;
import com.google.common.base.Functions;
import com.persinity.common.MathUtil;
import com.persinity.common.collection.DirectedEdge;
import com.persinity.common.db.RelDb;
import com.persinity.common.db.metainfo.Col;
import com.persinity.common.db.metainfo.constraint.PK;
import com.persinity.ndt.db.TransactionId;
import com.persinity.ndt.dbagent.relational.AgentSqlStrategy;
import com.persinity.ndt.dbagent.relational.SchemaInfo;
import com.persinity.ndt.etlmodule.relational.common.RelTransferFunc;
import com.persinity.ndt.transform.InsertOnFailureTemplateFunc;
import com.persinity.ndt.transform.ParamDmlFunc;
import com.persinity.ndt.transform.ParamDmlLoadFunc;
import com.persinity.ndt.transform.ParamQryFunc;
import com.persinity.ndt.transform.TupleFunc;
import com.persinity.ndt.transform.UpdateParamDmlFunc;

/**
 * Transfer trlog entries from source to destination in the beginning of TransferWindow.
 * Set destination trlog entries status to {@link SchemaInfo.TrlogStatusType#L} - Loading.
 * Set source trlog entries status to {@link SchemaInfo.TrlogStatusType#P} - Processing.
 *
 * @author Ivan Dachev
 */
public class PreMigrateRelTransferFunc extends RelTransferFunc {

    public PreMigrateRelTransferFunc(final List<? extends TransactionId> tids,
            final DirectedEdge<SchemaInfo, SchemaInfo> schemas, final AgentSqlStrategy sqlStrategy) {
        super(tids, schemas, sqlStrategy);
    }

    @SuppressWarnings("ConstantConditions")
    @Override
    public Integer apply(final DirectedEdge<RelDb, RelDb> dataBridge) {
        final List<String> tids = getTids();
        final int tidsSize = MathUtil.ceilingByPowerOfTwo(tids.size());
        createTrlogEtlFunctions(tidsSize);

        final List<String> params = new ArrayList<>();
        addPadded(params, tids, tidsSize);

        createTrlogSrcUpdateFunctions(tidsSize);

        final DirectedEdge<RelDb, List<?>> dbToParams = new DirectedEdge<RelDb, List<?>>(dataBridge.src(), params);

        final Iterator<Map<String, Object>> etRsIt = etFunc.apply(dbToParams);

        int res = loadF.apply(new DirectedEdge<>(dataBridge.dst(), etRsIt));

        res += srcUpdateF.apply(dbToParams);

        dataBridge.src().commit();
        dataBridge.dst().commit();

        return res;
    }

    private void createTrlogEtlFunctions(final int tidsSize) {
        final SchemaInfo srcSchema = getSchemas().src();

        final Set<Col> srcTrlogCols = srcSchema.getTableCols(SchemaInfo.TAB_TRLOG);
        assertArg(!srcTrlogCols.isEmpty(), "Failed to find src trlog entity columns: {}", SchemaInfo.TAB_TRLOG);
        final Set<Col> dstTrlogCols = getSchemas().dst().getTableCols(SchemaInfo.TAB_TRLOG);
        assertArg(!dstTrlogCols.isEmpty(), "Failed to find dst trlog entity columns: {}", SchemaInfo.TAB_TRLOG);

        final PK dstTrlogPk = getSchemas().dst().getTablePk(SchemaInfo.TAB_TRLOG);
        assertArg(dstTrlogPk != null, "Could not find PK for trlog table: {}", SchemaInfo.TAB_TRLOG);

        List<Col> pkCols = new ArrayList<>(dstTrlogPk.getColumns());

        final LinkedHashSet<Col> clogIntCols = new LinkedHashSet<>(srcTrlogCols);
        clogIntCols.retainAll(dstTrlogCols);
        final LinkedList<Col> cols = new LinkedList<>(clogIntCols);

        assertArg(!cols.isEmpty(), "Intersection between src trlogCols: {} and dst trlogCols: {} is empty",
                srcTrlogCols, dstTrlogCols);

        final String extractSql = getSqlStrategy().trlogExtractQuery(SchemaInfo.TAB_TRLOG, cols, tidsSize);
        final String updateSql = getSqlStrategy().updateStatement(SchemaInfo.TAB_TRLOG, cols, pkCols);
        final String insertSql = getSqlStrategy().insertStatement(SchemaInfo.TAB_TRLOG, cols);

        extractF = new ParamQryFunc(cols, extractSql);
        transformF = new TupleFunc() {
            @Override
            public Iterator<Map<String, Object>> apply(final Iterator<Map<String, Object>> mapIterator) {
                return new Iterator<Map<String, Object>>() {
                    @Override
                    public boolean hasNext() {
                        return mapIterator.hasNext();
                    }

                    @Override
                    public Map<String, Object> next() {
                        final Map<String, Object> res = mapIterator.next();
                        final Map<String, Object> resChanged = new HashMap<>();
                        for (String key : res.keySet()) {
                            Object value = res.get(key);
                            if (key.equals(SchemaInfo.COL_STATUS)) {
                                value = SchemaInfo.TrlogStatusType.L.toString();
                            }
                            resChanged.put(key, value);
                        }
                        return resChanged;
                    }

                    @Override
                    public void remove() {
                        throw new UnsupportedOperationException();
                    }
                };
            }
        };
        loadF = new ParamDmlLoadFunc(
                new InsertOnFailureTemplateFunc(insertSql, cols, new UpdateParamDmlFunc(updateSql, cols, pkCols)));

        etFunc = Functions.compose(transformF, extractF);
    }

    private void createTrlogSrcUpdateFunctions(final int tidsSize) {
        final SchemaInfo srcSchema = getSchemas().src();

        final Set<Col> srcTrlogCols = srcSchema.getTableCols(SchemaInfo.TAB_TRLOG);
        assertArg(!srcTrlogCols.isEmpty(), "Failed to find src trlog entity columns: {}", srcTrlogCols);

        final LinkedList<Col> cols = new LinkedList<>(srcTrlogCols);

        final String updateSql = getSqlStrategy()
                .trlogUpdateStatus(SchemaInfo.TAB_TRLOG, SchemaInfo.TrlogStatusType.P, tidsSize);
        srcUpdateF = new ParamDmlFunc(updateSql, cols);
    }

    private ParamQryFunc extractF;
    private TupleFunc transformF;
    private ParamDmlLoadFunc loadF;
    private Function<DirectedEdge<RelDb, List<?>>, Iterator<Map<String, Object>>> etFunc;
    private ParamDmlFunc srcUpdateF;
}
