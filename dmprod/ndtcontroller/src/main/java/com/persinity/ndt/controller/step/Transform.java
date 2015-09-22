/**
 * Copyright (c) 2015 Persinity Inc.
 */
package com.persinity.ndt.controller.step;

import static com.persinity.common.StringUtils.format;
import static com.persinity.common.fp.FunctionUtil.timeOf;
import static com.persinity.common.invariant.Invariant.notNull;
import static com.persinity.ndt.controller.step.ContextUtil.addWindowGenerator;

import java.util.Collection;
import java.util.Map;

import org.apache.log4j.Logger;

import com.google.common.base.Function;
import com.google.common.base.Stopwatch;
import com.persinity.common.Resource;
import com.persinity.common.collection.DirectedEdge;
import com.persinity.common.collection.Pool;
import com.persinity.common.db.RelDb;
import com.persinity.common.db.RelDbPool;
import com.persinity.common.db.Trimmer;
import com.persinity.common.db.metainfo.Schema;
import com.persinity.common.logging.Log4jLogger;
import com.persinity.ndt.controller.NdtController;
import com.persinity.ndt.controller.NdtViewController;
import com.persinity.ndt.controller.script.BaseStep;
import com.persinity.ndt.controller.script.Step;
import com.persinity.ndt.dbagent.ClogAgent;
import com.persinity.ndt.dbagent.SchemaAgent;
import com.persinity.ndt.dbagent.relational.AgentSqlStrategy;
import com.persinity.ndt.dbagent.relational.SchemaInfo;
import com.persinity.ndt.dbagent.relational.oracle.OracleSchemaInfo;
import com.persinity.ndt.dbdiff.SchemaDiffGenerator;
import com.persinity.ndt.dbdiff.TransformEntity;
import com.persinity.ndt.etlmodule.EtlPlanGenerator;
import com.persinity.ndt.etlmodule.WindowGenerator;
import com.persinity.ndt.etlmodule.relational.TransformInfo;
import com.persinity.ndt.etlmodule.relational.common.RelTransformMapFactory;
import com.persinity.ndt.etlmodule.relational.common.TransformMapFactory;
import com.persinity.ndt.etlmodule.relational.transform.TransformEtlPlanGenerator;
import com.persinity.ndt.etlmodule.relational.transform.TransformWindowGenerator;
import com.persinity.ndt.transform.EntitiesDag;
import com.persinity.ndt.transform.SerialPlanProcessor;

/**
 * Transforms staging area to target entities.
 *
 * @author Ivan Dachev
 */
public class Transform extends BaseStep {

    /**
     * @param prev
     *         Step or {@code null} if the first step
     * @param delaySecs
     *         from previous step
     * @param ctx
     */
    public Transform(final Step prev, final int delaySecs, final Map<Object, Object> ctx) {
        super(prev, delaySecs, ctx);

        final NdtController ndtController = getController();

        view = getController().getView();
        notNull(view);

        srcNdtDbPool = ndtController.getRelDbPoolFactory().ndtBridge().src();
        dstNdtDbPool = ndtController.getRelDbPoolFactory().ndtBridge().dst();
        srcAppDbPool = ndtController.getRelDbPoolFactory().appBridge().src();
        dstAppDbPool = ndtController.getRelDbPoolFactory().appBridge().dst();

        windowSize = ndtController.getConfig().getTransformWindowSize();
        etlInstructionSize = ndtController.getConfig().getEtlInstructionSize();
        sqlStrategy = ndtController.getSqlStrategy();

        // TODO replace the RelDbPool casts with DbPool iface.
        final DirectedEdge<Schema, Schema> appSchemas = new DirectedEdge<>(((RelDbPool) srcAppDbPool).metaInfo(),
                ((RelDbPool) dstAppDbPool).metaInfo());
        final DirectedEdge<Schema, Schema> ndtSchemas = new DirectedEdge<>(((RelDbPool) srcNdtDbPool).metaInfo(),
                ((RelDbPool) dstNdtDbPool).metaInfo());

        final Trimmer trimmer = new Trimmer();
        final int maxNameLength = ndtController.getSqlStrategy().getMaxNameLength();
        final SchemaInfo ndtDstSchemaInfo = new OracleSchemaInfo(ndtSchemas.dst(), trimmer, maxNameLength);
        final SchemaInfo appDstSchemaInfo = new OracleSchemaInfo(appSchemas.dst(), trimmer, maxNameLength);
        final SchemaInfo appSrcSchemaInfo = new OracleSchemaInfo(appSchemas.src(), trimmer, maxNameLength);
        appSchemaInfos = new DirectedEdge<>(appSrcSchemaInfo, appDstSchemaInfo);
        ndtDstToAppDstSchemas = new DirectedEdge<>(ndtDstSchemaInfo, appDstSchemaInfo);

        processor = new SerialPlanProcessor<>();
    }

    @Override
    public void sigStop() {
        super.sigStop();
        final WindowGenerator<RelDb, RelDb> transformWinGen_ = transformWinGen;
        if (transformWinGen_ != null) {
            log.debug("On sigStop force stop window generator: {}", transformWinGen_);
            transformWinGen_.forceStop();
        }
    }

    @Override
    protected final void work() {
        log.info("{} {} -> {}", getClass().getSimpleName(), dstNdtDbPool, dstAppDbPool);

        logNdtMessage(getStartMsg());

        log.info("{} init engine...", getClass().getSimpleName());
        final Stopwatch initTime = timeOf(new Function<Void, Void>() {
            @Override
            public Void apply(final Void arg) {
                initTransformEngine();
                return null;
            }
        });
        log.info("{} init engine done for {}", getClass().getSimpleName(), initTime);

        log.info("{} execute...", getClass().getSimpleName());
        final Stopwatch executeTime = timeOf(new Function<Void, Void>() {
            @Override
            public Void apply(final Void arg) {
                execute();
                return null;
            }
        });
        log.info("{} done for {}", getClass().getSimpleName(), executeTime);

        final String msg = getStopMsg();
        if (msg != null) {
            logNdtMessage(msg);
        }
    }

    private void initTransformEngine() {
        AgentContext agentCtx = (AgentContext) getCtx().get(AgentContext.class);
        dstSchemaAgent = agentCtx.getDstSchemaAgent();
        srcClogAgent = agentCtx.getSrcClogAgent();
        dstClogAgent = agentCtx.getDstClogAgent();

        final DirectedEdge<Pool<RelDb>, Pool<RelDb>> transformPath = new DirectedEdge<>(dstNdtDbPool, dstAppDbPool);
        EntitiesDag entityDag = (EntitiesDag) getCtx().get(EntitiesDag.class);
        if (entityDag == null) {
            entityDag = getDstSchemaAgent().getSchema();
            getCtx().put(EntitiesDag.class, entityDag);
        }
        transformWinGen = getTransformWinGen(transformPath, entityDag);

        log.debug("Using {}", transformWinGen);
        final SchemaDiffGenerator sdg = ContextUtil.getSchemaDiffGenerator(getCtx());
        final Collection<TransformEntity> transformEntities = sdg.generateDiff(appSchemaInfos, sqlStrategy);
        final TransformMapFactory transformMapFactory = new RelTransformMapFactory(ndtDstToAppDstSchemas, sqlStrategy,
                transformEntities, windowSize, TransformInfo.MIGRATE_DONT_COALESCE);

        final Map<String, TransformInfo> mergeMap = transformMapFactory.getMergeMap();
        final Map<String, TransformInfo> deleteMap = transformMapFactory.getDeleteMap();

        transformPlanner = getTransformPlanner(mergeMap, deleteMap);

        log.debug("Using {} over transformation merge {} and delete map {}", transformPlanner, mergeMap, deleteMap);
    }

    private void execute() {
        preExecute();
        getController().getEtlPlanExecutor().execute(transformWinGen, transformPlanner, "transform");
        postExecute();
    }

    protected void preExecute() {
        resource.accessAndClose(new Resource.Accessor<RelDb, Void>(dstAppDbPool.get(), null) {
            @Override
            public Void access(final RelDb resource) throws Exception {
                processor.process(dstSchemaAgent.breakRefIntegrityCycles(), resource);
                return null;
            }
        });
    }

    protected void postExecute() {
        resource.accessAndClose(new Resource.Accessor<RelDb, Void>(dstAppDbPool.get(), null) {
            @Override
            public Void access(final RelDb resource) throws Exception {
                processor.process(dstSchemaAgent.renewRefIntegrityCycles(), resource);
                return null;
            }
        });
    }

    protected WindowGenerator<RelDb, RelDb> getTransformWinGen(
            final DirectedEdge<Pool<RelDb>, Pool<RelDb>> dataPoolBridge,
            EntitiesDag entityDag) {
        final WindowGenerator<RelDb, RelDb> result = new TransformWindowGenerator(dataPoolBridge, entityDag,
                sqlStrategy,
                windowSize);
        addWindowGenerator(Transform.class.getName(), result, getCtx());
        return result;
    }

    private EtlPlanGenerator<RelDb, RelDb> getTransformPlanner(final Map<String, TransformInfo> mergeMap,
            final Map<String, TransformInfo> deleteMap) {
        final EtlPlanGenerator<RelDb, RelDb> result = new TransformEtlPlanGenerator(mergeMap, deleteMap,
                etlInstructionSize, ndtDstToAppDstSchemas, sqlStrategy);
        return result;
    }

    protected String getStartMsg() {
        return format("Migrating from Destination Staging \"{}\" to Destination DB \"{}\"...",
                ((RelDbPool) dstNdtDbPool).metaInfo().getUserName(),
                ((RelDbPool) dstAppDbPool).metaInfo().getUserName());
    }

    protected String getStopMsg() {
        return "Completed";
    }

    protected int getWindowSize() {
        return windowSize;
    }

    protected AgentSqlStrategy getSqlStrategy() {
        return sqlStrategy;
    }

    protected SchemaAgent<Function<RelDb, RelDb>> getDstSchemaAgent() {
        return dstSchemaAgent;
    }

    protected SerialPlanProcessor<RelDb, RelDb> getProcessor() {
        return processor;
    }

    protected ClogAgent<Function<RelDb, RelDb>> getSrcClogAgent() {
        return srcClogAgent;
    }

    protected ClogAgent<Function<RelDb, RelDb>> getDstClogAgent() {
        return dstClogAgent;
    }

    protected Pool<RelDb> getDstAppDbPool() {
        return dstAppDbPool;
    }

    private final NdtViewController view;

    private final Pool<RelDb> dstNdtDbPool;
    private final Pool<RelDb> dstAppDbPool;
    private final Pool<RelDb> srcNdtDbPool;
    private final Pool<RelDb> srcAppDbPool;
    private final int windowSize;
    private final int etlInstructionSize;

    private final AgentSqlStrategy sqlStrategy;
    private final DirectedEdge<SchemaInfo, SchemaInfo> ndtDstToAppDstSchemas;
    private DirectedEdge<SchemaInfo, SchemaInfo> appSchemaInfos;

    private SchemaAgent<Function<RelDb, RelDb>> dstSchemaAgent;
    private ClogAgent<Function<RelDb, RelDb>> srcClogAgent;
    private ClogAgent<Function<RelDb, RelDb>> dstClogAgent;
    private WindowGenerator<RelDb, RelDb> transformWinGen;
    private EtlPlanGenerator<RelDb, RelDb> transformPlanner;
    private SerialPlanProcessor<RelDb, RelDb> processor;
    private final Resource resource = new Resource();

    private static final Log4jLogger log = new Log4jLogger(Logger.getLogger(Transform.class));
}
