/**
 * Copyright (c) 2015 Persinity Inc.
 */
package com.persinity.ndt.etlmodule.relational.common;

import static com.persinity.common.invariant.Invariant.assertArg;
import static com.persinity.ndt.dbagent.relational.AgentSqlStrategy.CTYPES_INS_UPD_DEL;
import static java.util.Arrays.asList;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.persinity.common.MathUtil;
import com.persinity.common.collection.DirectedEdge;
import com.persinity.common.db.RelDb;
import com.persinity.common.db.metainfo.Col;
import com.persinity.common.db.metainfo.SqlFilter;
import com.persinity.common.invariant.NotNull;
import com.persinity.common.logging.Log4jLogger;
import com.persinity.common.metrics.MetricCounterFunc;
import com.persinity.ndt.dbagent.relational.AgentSqlStrategy;
import com.persinity.ndt.dbagent.relational.SchemaInfo;
import com.persinity.ndt.dbdiff.TransformEntity;
import com.persinity.ndt.etlmodule.relational.TransformInfo;
import com.persinity.ndt.etlmodule.relational.migrate.ClogCoalesceTupleFunc;
import com.persinity.ndt.etlmodule.relational.transform.FilterTupleFunc;
import com.persinity.ndt.transform.InsertOnFailureTemplateFunc;
import com.persinity.ndt.transform.ParamDmlFunc;
import com.persinity.ndt.transform.ParamDmlLoadFunc;
import com.persinity.ndt.transform.ParamQryFunc;
import com.persinity.ndt.transform.RelExtractFunc;
import com.persinity.ndt.transform.RelLoadFunc;
import com.persinity.ndt.transform.RelLoadFuncComposition;
import com.persinity.ndt.transform.RepeaterTupleFunc;
import com.persinity.ndt.transform.TupleFunc;
import com.persinity.ndt.transform.TupleFuncComposition;
import com.persinity.ndt.transform.UpdateParamDmlFunc;

/**
 * Generate transform maps for migrate, merge and delete.
 *
 * @author Ivan Dachev
 */
public class RelTransformMapFactory implements TransformMapFactory {

    public RelTransformMapFactory(final DirectedEdge<SchemaInfo, SchemaInfo> schemas,
            final AgentSqlStrategy sqlStrategy, final Collection<TransformEntity> transformEntities,
            final int maxTidsCount) {
        this(schemas, sqlStrategy, transformEntities, maxTidsCount, TransformInfo.MIGRATE_COALESCE);
    }

    public RelTransformMapFactory(final DirectedEdge<SchemaInfo, SchemaInfo> schemas,
            final AgentSqlStrategy sqlStrategy, final Collection<TransformEntity> transformEntities,
            final int maxTidsCount, final boolean migrateCoalesce) {
        new NotNull("schemas", "sqlStrategy", "transformEntities").enforce(schemas, sqlStrategy, transformEntities);
        this.schemas = schemas;
        this.sqlStrategy = sqlStrategy;
        this.transformEntities = transformEntities;
        // TODO here we make ceilingByPowerOfTwo because we know that sqlStrategy
        // is using it to make the statements, that is some kind of spaghetti mix
        // my proposal is to remove the hidden using of ceilingByPowerOfTwo in AgentSqlStrategy impl
        // and always pass the required size to create the statements
        this.maxTidsCount = MathUtil.ceilingByPowerOfTwo(maxTidsCount);
        this.migrateCoalesce = migrateCoalesce;
    }

    @Override
    public synchronized Map<String, TransformInfo> getMigrateMap() {
        if (migrateMap == null) {
            final HashMap<String, TransformInfo> res = new HashMap<>();

            for (final TransformEntity transformEntity : transformEntities) {
                final TransformInfo migrateTransformInfo = newMigrateTransformInfo(transformEntity, maxTidsCount,
                        migrateCoalesce);
                final String dstEntity = migrateTransformInfo.getEntityMapping().dst();
                res.put(dstEntity, migrateTransformInfo);
            }

            migrateMap = ImmutableMap.copyOf(res);
        }

        return migrateMap;
    }

    @Override
    public synchronized Map<String, TransformInfo> getMigrateNoCoalesceMap() {
        if (migrateNoCoalesceMap == null) {
            final HashMap<String, TransformInfo> res = new HashMap<>();

            for (final TransformEntity transformEntity : transformEntities) {
                final TransformInfo migrateTransformInfo = newMigrateTransformInfo(transformEntity, maxTidsCount,
                        TransformInfo.MIGRATE_DONT_COALESCE);
                final String dstEntity = migrateTransformInfo.getEntityMapping().dst();
                res.put(dstEntity, migrateTransformInfo);
            }

            migrateNoCoalesceMap = ImmutableMap.copyOf(res);
        }

        return migrateNoCoalesceMap;
    }

    @Override
    public synchronized Map<String, TransformInfo> getMergeMap() {
        if (mergeMap == null) {
            final HashMap<String, TransformInfo> res = new HashMap<>();

            for (final TransformEntity transformEntity : transformEntities) {
                final String targetEntity = transformEntity.getTargetEntity();
                final TransformInfo mergeTransformInfo = newMergeTransformInfo(transformEntity, maxTidsCount);
                res.put(targetEntity, mergeTransformInfo);
            }

            mergeMap = ImmutableMap.copyOf(res);
        }

        return mergeMap;
    }

    @Override
    public synchronized Map<String, TransformInfo> getDeleteMap() {
        if (deleteMap == null) {
            final HashMap<String, TransformInfo> res = new HashMap<>();

            for (final TransformEntity transformEntity : transformEntities) {
                final String targetEntity = transformEntity.getTargetEntity();
                final TransformInfo delTransformInfo = newDeleteTransformInfo(transformEntity, maxTidsCount);
                res.put(targetEntity, delTransformInfo);
            }

            deleteMap = ImmutableMap.copyOf(res);
        }

        return deleteMap;
    }

    private TransformInfo newMigrateTransformInfo(final TransformEntity transformEntity, final int tidCount,
            final boolean enableCoalesce) {
        final String sourceEntity = transformEntity.getSourceLeadingEntity();

        final String srcClogEntity = schemas.src().getClogTableName(sourceEntity);
        assertArg(srcClogEntity != null, "Failed to find src clog table name for: {}", sourceEntity);
        final String dstClogEntity = schemas.dst().getClogTableName(sourceEntity);
        assertArg(dstClogEntity != null, "Failed to find dst clog table name for: {}", sourceEntity);

        final Set<Col> srcClogCols = schemas.src().getTableCols(srcClogEntity);
        assertArg(!srcClogCols.isEmpty(), "Failed to find src clog entity columns: {}", srcClogEntity);
        final Set<Col> dstClogCols = schemas.dst().getTableCols(dstClogEntity);
        assertArg(!dstClogCols.isEmpty(), "Failed to find dst clog entity columns: {}", dstClogEntity);

        final LinkedList<Col> cols = intersectCols(srcClogCols, dstClogCols);
        assertArg(!cols.isEmpty(), "Intersection between src clogCols: {} and dst clogCols: {} is empty", srcClogCols,
                dstClogCols);

        final DirectedEdge<String, String> entityMapping = new DirectedEdge<>(srcClogEntity, dstClogEntity);
        final DirectedEdge<Set<Col>, Set<Col>> colsMapping = getSourceLeadingColsToTargetCols(srcClogEntity,
                transformEntity.getSourceLeadingColumns(), dstClogEntity);

        final String clogExtractQry = sqlStrategy
                .clogExtractQuery(srcClogEntity, cols, Lists.newArrayList(colsMapping.src()), tidCount,
                        CTYPES_INS_UPD_DEL);
        final DirectedEdge<String, String> sqlMapping = new DirectedEdge<>(clogExtractQry,
                sqlStrategy.insertStatement(dstClogEntity, cols));

        final ParamQryFunc extractF = new ParamQryFunc(cols, sqlMapping.src());
        final TupleFunc transformF;
        if (enableCoalesce) {
            transformF = new ClogCoalesceTupleFunc(colsMapping.src());
        } else {
            transformF = new RepeaterTupleFunc();
        }
        final RelLoadFunc loadF = new RelLoadFuncComposition(new MetricCounterFunc(RelMetrics.COUNTER_MIGRATE_ROWS),
                new ParamDmlLoadFunc(new InsertOnFailureTemplateFunc(sqlMapping.dst(), cols, SILENCE_FUNC)));

        return new TransformInfo(entityMapping, colsMapping, extractF, transformF, loadF, tidCount);
    }

    private TransformInfo newMergeTransformInfo(final TransformEntity transformEntity, final int tidCount) {
        final String sourceEntity = transformEntity.getSourceLeadingEntity();
        final String targetEntity = transformEntity.getTargetEntity();

        final String cclogEntity = schemas.src().getClogTableName(sourceEntity);
        assertArg(cclogEntity != null, "Failed to find dst clog table name for: {}", sourceEntity);

        final DirectedEdge<String, String> entityMapping = new DirectedEdge<>(cclogEntity, targetEntity);
        final DirectedEdge<Set<Col>, Set<Col>> colsMapping = getSourceLeadingColsToTargetCols(cclogEntity,
                transformEntity.getSourceLeadingColumns(), targetEntity);

        final List<Col> dstCols = new LinkedList<>(schemas.dst().getTableCols(targetEntity));
        assertArg(!dstCols.isEmpty(), "Failed to find target entity columns: {}", targetEntity);
        final List<Col> srcExtractCols = Lists.newLinkedList(dstCols);
        srcExtractCols.add(COL_CTYPE);

        final String extractQry = sqlStrategy
                .clogExtractQuery(cclogEntity, srcExtractCols, Lists.newArrayList(colsMapping.src()), tidCount,
                        CTYPES_INS_UPD_DEL);
        final ParamQryFunc extractF = new ParamQryFunc(dstCols, extractQry);
        final TupleFunc transformF = new TupleFuncComposition(new FilterTupleFunc(CTYPE_DEL_FILTER, false),
                new ClogCoalesceTupleFunc(colsMapping.src()));

        final ArrayList<Col> dstColsList = Lists.newArrayList(colsMapping.dst());

        final String updateStatement = sqlStrategy.updateStatement(targetEntity, dstCols, dstColsList);
        final String insertStatement = sqlStrategy.insertStatement(targetEntity, dstCols);

        final RelLoadFunc loadF = new RelLoadFuncComposition(new MetricCounterFunc(RelMetrics.COUNTER_TRANSFORM_ROWS),
                new ParamDmlLoadFunc(new InsertOnFailureTemplateFunc(insertStatement, dstCols,
                        new UpdateParamDmlFunc(updateStatement, dstCols, dstColsList))));

        return new TransformInfo(entityMapping, colsMapping, extractF, transformF, loadF, tidCount);
    }

    private TransformInfo newDeleteTransformInfo(final TransformEntity transformEntity, final int tidCount) {
        final String sourceEntity = transformEntity.getSourceLeadingEntity();
        final String targetEntity = transformEntity.getTargetEntity();

        final String cclogEntity = schemas.src().getClogTableName(sourceEntity);
        assertArg(cclogEntity != null, "Failed to find dst clog table name for: {}", sourceEntity);

        final DirectedEdge<String, String> entityMapping = new DirectedEdge<>(cclogEntity, targetEntity);
        final DirectedEdge<Set<Col>, Set<Col>> colsMapping = getSourceLeadingColsToTargetCols(cclogEntity,
                transformEntity.getSourceLeadingColumns(), targetEntity);

        final List<Col> targetCols = new LinkedList<>(schemas.dst().getTableCols(targetEntity));
        assertArg(!targetCols.isEmpty(), "Failed to find target entity columns: {}", targetEntity);

        final ArrayList<Col> dstColsList = Lists.newArrayList(colsMapping.dst());
        final List<Col> srcExtractCols = Lists.newLinkedList(dstColsList);
        srcExtractCols.add(COL_CTYPE);

        final String extractQry = sqlStrategy
                .clogExtractQuery(cclogEntity, srcExtractCols, Lists.newArrayList(colsMapping.src()), tidCount,
                        CTYPES_INS_UPD_DEL);
        final RelExtractFunc extractF = new ParamQryFunc(targetCols, extractQry);
        final TupleFunc transformF = new TupleFuncComposition(new FilterTupleFunc(CTYPE_DEL_FILTER, true),
                new ClogCoalesceTupleFunc(colsMapping.src()));
        final String deleteStmt = sqlStrategy.deleteStatement(targetEntity, dstColsList);
        final ParamDmlFunc paramDmlFunc = new ParamDmlFunc(deleteStmt, dstColsList);
        final RelLoadFunc loadF = new RelLoadFuncComposition(new MetricCounterFunc(RelMetrics.COUNTER_TRANSFORM_ROWS),
                new ParamDmlLoadFunc(paramDmlFunc));

        return new TransformInfo(entityMapping, colsMapping, extractF, transformF, loadF, tidCount);
    }

    private DirectedEdge<Set<Col>, Set<Col>> getSourceLeadingColsToTargetCols(final String sourceEntity,
            final Set<String> sourceLeadingColumns, final String targetEntity) {
        // TODO this implementation is only for migrate it will not work for transform
        // as the sourceEntity may not contain the sourceLeadingColumns

        final Set<Col> sourceCols = schemas.src().getTableCols(sourceEntity);
        final Set<Col> targetCols = schemas.dst().getTableCols(targetEntity);

        final Set<Col> leadingCols = new LinkedHashSet<>();
        for (String leadingColumn : sourceLeadingColumns) {
            final Col leadingCol = new Col(leadingColumn);
            assertArg(sourceCols.contains(leadingCol),
                    "Failed to find leading column {} in leading source entity {} with columns {}", leadingColumn,
                    sourceEntity, sourceCols);
            assertArg(targetCols.contains(leadingCol),
                    "Failed to find leading column {} for target entity {} with columns {}", leadingColumn,
                    targetEntity, targetCols);
            leadingCols.add(leadingCol);
        }

        return new DirectedEdge<>(leadingCols, leadingCols);
    }

    private static LinkedList<Col> intersectCols(final Set<Col> cols1, final Set<Col> cols2) {
        final LinkedHashSet<Col> clogIntCols = new LinkedHashSet<>(cols1);
        clogIntCols.retainAll(cols2);
        return new LinkedList<>(clogIntCols);
    }

    private static final Col COL_CTYPE = new Col(SchemaInfo.COL_CTYPE);
    private static final SqlFilter<String> CTYPE_DEL_FILTER = new SqlFilter<String>() {

        @Override
        public Col getCol() {
            return COL_CTYPE;
        }

        @Override
        public String getValue() {
            return SchemaInfo.ChangeType.D.toString();
        }
    };
    private static final ParamDmlFunc SILENCE_FUNC = new ParamDmlFunc("do nothing for failed CLOG insert",
            asList(new Col(SchemaInfo.COL_GID))) {
        @Override
        public Integer apply(final DirectedEdge<RelDb, List<?>> input) {
            log.debug("Silence on failed insert of CLOG with params({}), due to probable Migrate job recovery.", input);
            return 0;
        }
    };

    private static final Log4jLogger log = new Log4jLogger(Logger.getLogger(RelTransformMapFactory.class));
    private final DirectedEdge<SchemaInfo, SchemaInfo> schemas;
    private final AgentSqlStrategy sqlStrategy;
    private final Collection<TransformEntity> transformEntities;
    private final int maxTidsCount;
    private final boolean migrateCoalesce;

    private Map<String, TransformInfo> migrateMap;
    private Map<String, TransformInfo> migrateNoCoalesceMap;
    private Map<String, TransformInfo> mergeMap;
    private Map<String, TransformInfo> deleteMap;
}
