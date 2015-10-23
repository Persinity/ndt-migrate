/*
 * Copyright 2015 Persinity Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
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
import com.persinity.ndt.etlmodule.relational.migrate.MigrateEtlPlanGenerator;
import com.persinity.ndt.etlmodule.relational.migrate.MigrateWindowGenerator;

/**
 * Migrates deltas to staging area.
 *
 * @author Ivan Dachev
 */
public class Migrate extends BaseStep {
    /**
     * @param prev
     *         Step or {@code null} if the first step
     * @param delaySecs
     *         from previous step
     * @param ctx
     */
    public Migrate(final Step prev, final int delaySecs, final Map<Object, Object> ctx) {
        super(prev, delaySecs, ctx);

        final NdtController ndtController = getController();

        view = ndtController.getView();
        notNull(view);

        srcNdtDbPool = ndtController.getRelDbPoolFactory().ndtBridge().src();
        dstNdtDbPool = ndtController.getRelDbPoolFactory().ndtBridge().dst();
        srcAppDbPool = ndtController.getRelDbPoolFactory().appBridge().src();
        dstAppDbPool = ndtController.getRelDbPoolFactory().appBridge().dst();

        windowSize = ndtController.getConfig().getMigrateWindowSize();
        etlInstructionSize = ndtController.getConfig().getEtlInstructionSize();
        sqlStrategy = ndtController.getSqlStrategy();

        final Trimmer trimmer = new Trimmer();
        final int maxNameLength = ndtController.getSqlStrategy().getMaxNameLength();

        // TODO replace the RelDbPool casts with DbPool iface.
        final DirectedEdge<Schema, Schema> appSchemas = new DirectedEdge<>(((RelDbPool) srcAppDbPool).metaInfo(),
                ((RelDbPool) dstAppDbPool).metaInfo());
        final DirectedEdge<Schema, Schema> ndtSchemas = new DirectedEdge<>(((RelDbPool) srcNdtDbPool).metaInfo(),
                ((RelDbPool) dstNdtDbPool).metaInfo());

        final SchemaInfo ndtSrcSchemaInfo = new OracleSchemaInfo(ndtSchemas.src(), trimmer, maxNameLength);
        final SchemaInfo ndtDstSchemaInfo = new OracleSchemaInfo(ndtSchemas.dst(), trimmer, maxNameLength);
        final SchemaInfo appSrcSchemaInfo = new OracleSchemaInfo(appSchemas.src(), trimmer, maxNameLength);
        final SchemaInfo appDstSchemaInfo = new OracleSchemaInfo(appSchemas.dst(), trimmer, maxNameLength);

        appSchemaInfos = new DirectedEdge<>(appSrcSchemaInfo, appDstSchemaInfo);
        ndtSchemaInfos = new DirectedEdge<>(ndtSrcSchemaInfo, ndtDstSchemaInfo);
    }

    @Override
    public void sigStop() {
        super.sigStop();
        final WindowGenerator<RelDb, RelDb> migrateWinGen_ = migrateWinGen;
        if (migrateWinGen_ != null) {
            log.debug("On sigStop force stop window generator: {}", migrateWinGen_);
            migrateWinGen_.forceStop();
        }
    }

    @Override
    protected void work() {
        log.info("{} {} -> {}", getClass().getSimpleName(), srcNdtDbPool, dstNdtDbPool);

        final String msg = getStartMsg();
        if (msg != null) {
            view.logNdtMessage(msg);
        }

        log.info("{} init engine...", getClass().getSimpleName());
        final Stopwatch initTime = timeOf(new Function<Void, Void>() {
            @Override
            public Void apply(final Void arg) {
                initMigrateEngine();
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
    }

    protected String getStartMsg() {
        return format("Migrating from Source Staging \"{}\" to Destination Staging \"{}\"...",
                ((RelDbPool) srcNdtDbPool).metaInfo().getUserName(),
                ((RelDbPool) dstNdtDbPool).metaInfo().getUserName());
    }

    private void initMigrateEngine() {
        final DirectedEdge<Pool<RelDb>, Pool<RelDb>> migrationPath = new DirectedEdge<>(srcNdtDbPool, dstNdtDbPool);
        migrateWinGen = getMigrationWinGen(migrationPath);
        addWindowGenerator(getClass().getName(), migrateWinGen, getCtx());
        log.debug("Using {} over DM path {}", migrateWinGen, migrationPath);

        final Map<String, TransformInfo> transformInfoMap = getMigrateMap(appSchemaInfos, ndtSchemaInfos, sqlStrategy);
        migratePlanner = getMigrationPlanner(transformInfoMap);
        log.debug("Using {} over DM transformation map {}", migratePlanner, transformInfoMap);
    }

    private void execute() {
        getController().getEtlPlanExecutor().execute(migrateWinGen, migratePlanner, "migrate");
    }

    private WindowGenerator<RelDb, RelDb> getMigrationWinGen(
            final DirectedEdge<Pool<RelDb>, Pool<RelDb>> dataPoolBridge) {
        final WindowGenerator<RelDb, RelDb> result = new MigrateWindowGenerator(dataPoolBridge, sqlStrategy,
                windowSize);
        return result;
    }

    private EtlPlanGenerator<RelDb, RelDb> getMigrationPlanner(final Map<String, TransformInfo> transformMap) {
        final EtlPlanGenerator<RelDb, RelDb> result = new MigrateEtlPlanGenerator(transformMap, etlInstructionSize,
                ndtSchemaInfos, sqlStrategy);
        return result;
    }

    protected Map<String, TransformInfo> getMigrateMap(final DirectedEdge<SchemaInfo, SchemaInfo> appSchemas,
            final DirectedEdge<SchemaInfo, SchemaInfo> ndtSchemas, final AgentSqlStrategy sqlStrategy) {
        final SchemaDiffGenerator sdg = ContextUtil.getSchemaDiffGenerator(getCtx());
        final Collection<TransformEntity> transformEntities = sdg.generateDiff(appSchemas, sqlStrategy);
        final boolean migrateCoalesce = getController().getConfig().getMigrateCoalesce();
        final TransformMapFactory tmf = new RelTransformMapFactory(ndtSchemas, sqlStrategy, transformEntities,
                windowSize, migrateCoalesce);
        final Map<String, TransformInfo> result = tmf.getMigrateMap();
        return result;
    }

    protected int getWindowSize() {
        return windowSize;
    }

    private final NdtViewController view;

    private final Pool<RelDb> srcNdtDbPool;
    private final Pool<RelDb> dstNdtDbPool;
    private final Pool<RelDb> srcAppDbPool;
    private final Pool<RelDb> dstAppDbPool;
    private final int windowSize;
    private final int etlInstructionSize;

    private final AgentSqlStrategy sqlStrategy;
    private final DirectedEdge<SchemaInfo, SchemaInfo> appSchemaInfos;
    private final DirectedEdge<SchemaInfo, SchemaInfo> ndtSchemaInfos;

    private WindowGenerator<RelDb, RelDb> migrateWinGen;
    private EtlPlanGenerator<RelDb, RelDb> migratePlanner;

    private static final Log4jLogger log = new Log4jLogger(Logger.getLogger(Migrate.class));
}
