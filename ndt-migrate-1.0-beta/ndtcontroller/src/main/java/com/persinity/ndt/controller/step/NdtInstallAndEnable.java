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
import static com.persinity.common.db.RelDbUtil.warmUpCache;
import static com.persinity.common.db.metainfo.BufferedSchema.SKIP_WARMUP_FKS;
import static com.persinity.common.db.metainfo.BufferedSchema.WARMUP_FKS;
import static com.persinity.common.fp.FunctionUtil.timeOf;
import static com.persinity.ndt.controller.NdtViewController.PROGRESS_OFF;
import static com.persinity.ndt.controller.NdtViewController.PROGRESS_ON;

import java.util.Map;

import org.apache.log4j.Logger;

import com.google.common.base.Function;
import com.google.common.base.Stopwatch;
import com.persinity.common.collection.DirectedEdge;
import com.persinity.common.db.RelDb;
import com.persinity.common.logging.Log4jLogger;
import com.persinity.ndt.controller.NdtController;
import com.persinity.ndt.controller.NdtViewController;
import com.persinity.ndt.controller.script.BaseStep;
import com.persinity.ndt.controller.script.Step;
import com.persinity.ndt.dbagent.CdcAgent;
import com.persinity.ndt.dbagent.ClogAgent;
import com.persinity.ndt.dbagent.DbAgentExecutor;
import com.persinity.ndt.dbagent.DbAgentFactory;
import com.persinity.ndt.dbagent.SchemaAgent;
import com.persinity.ndt.dbagent.relational.DbAgentTracker;

/**
 * @author Ivan Dachev
 */
public class NdtInstallAndEnable extends BaseStep {
    /**
     * @param prev
     *         Step or {@code null} if the first step
     * @param delaySecs
     *         from previous step
     * @param ctx
     */
    public NdtInstallAndEnable(final Step prev, final int delaySecs, final Map<Object, Object> ctx) {
        super(prev, delaySecs, ctx);

        final NdtController ndtController = getController();

        view = ndtController.getView();

        dbAgentExecutor = ndtController.getDbAgentExecutor();

        // TODO limit the live span of these.
        srcNdtDb = ndtController.getRelDbPoolFactory().ndtBridge().src().get();
        dstNdtDb = ndtController.getRelDbPoolFactory().ndtBridge().dst().get();
        srcAppDb = ndtController.getRelDbPoolFactory().appBridge().src().get();
        dstAppDb = ndtController.getRelDbPoolFactory().appBridge().dst().get();

        final DbAgentTracker dbAgentTracker = new DbAgentTracker();
        srcDrivenDbAgentFactory = new DbAgentFactory<>(srcAppDb.metaInfo(), ndtController.getSqlStrategy(),
                dbAgentTracker);
        dstDrivenDbAgentFactory = new DbAgentFactory<>(dstAppDb.metaInfo(), ndtController.getSqlStrategy(),
                dbAgentTracker);
    }

    @Override
    protected void work() {
        log.info("Installing NDT at {}, {}, {}, {}", srcAppDb, srcNdtDb, dstNdtDb, dstAppDb);
        final Stopwatch stTotal = Stopwatch.createStarted();

        view.logNdtMessage(
                format("Installing NDT at Source DB \"{}\", Source Staging \"{}\",\nDestination Staging \"{}\" and Destination DB \"{}\"",
                        srcAppDb.getUserName(), srcNdtDb.getUserName(), dstNdtDb.getUserName(),
                        dstAppDb.getUserName()));
        view.setProgress(PROGRESS_ON);

        // TODO define in parallel steps to speed up setup.

        log.info("Mounting clog agent on {}...", srcNdtDb);
        final DirectedEdge<ClogAgent<Function<RelDb, RelDb>>, Stopwatch> srcClogAgentRes = timeOf(
                new Function<Void, ClogAgent<Function<RelDb, RelDb>>>() {
                    @Override
                    public ClogAgent<Function<RelDb, RelDb>> apply(final Void arg) {
                        final ClogAgent<Function<RelDb, RelDb>> srcClogAgent = dbAgentExecutor
                                .clogAgentInstallMount(srcDrivenDbAgentFactory, srcNdtDb);
                        srcNdtDb.commit();
                        return srcClogAgent;
                    }
                }, null);
        log.info("Mounted clog agent for {}", srcClogAgentRes.dst());

        log.info("Mounting clog agent on {}...", dstNdtDb);
        final DirectedEdge<ClogAgent<Function<RelDb, RelDb>>, Stopwatch> dstClogAgentRes = timeOf(
                new Function<Void, ClogAgent<Function<RelDb, RelDb>>>() {
                    @Override
                    public ClogAgent<Function<RelDb, RelDb>> apply(final Void arg) {
                        final ClogAgent<Function<RelDb, RelDb>> dstClogAgent = dbAgentExecutor
                                .clogAgentInstallMount(srcDrivenDbAgentFactory, dstNdtDb);
                        dstNdtDb.commit();
                        return dstClogAgent;
                    }
                }, null);
        log.info("Mounted clog agent for {}", dstClogAgentRes.dst());

        log.info("Mounting cdc agent on {} -> {}...", srcAppDb, srcNdtDb);
        final DirectedEdge<CdcAgent<Function<RelDb, RelDb>>, Stopwatch> cdcAgentRes = timeOf(
                new Function<Void, CdcAgent<Function<RelDb, RelDb>>>() {
                    @Override
                    public CdcAgent<Function<RelDb, RelDb>> apply(final Void arg) {
                        final CdcAgent<Function<RelDb, RelDb>> cdcAgent = dbAgentExecutor
                                .cdcAgentInstallMount(srcDrivenDbAgentFactory, srcAppDb, srcNdtDb);
                        srcAppDb.commit();
                        srcNdtDb.commit();
                        return cdcAgent;
                    }
                }, null);
        log.info("Mounted cdc agent for {}", cdcAgentRes.dst());

        log.info("Mounting schema agent on {} -> {}...", dstNdtDb, dstAppDb);
        final DirectedEdge<SchemaAgent<Function<RelDb, RelDb>>, Stopwatch> dstSchemaAgentRes = timeOf(
                new Function<Void, SchemaAgent<Function<RelDb, RelDb>>>() {
                    @Override
                    public SchemaAgent<Function<RelDb, RelDb>> apply(final Void arg) {
                        final SchemaAgent<Function<RelDb, RelDb>> dstSchemaAgent = dbAgentExecutor
                                .schemaAgentInstallMount(dstDrivenDbAgentFactory, dstNdtDb, dstAppDb);
                        dstNdtDb.commit();
                        dstAppDb.commit();
                        return dstSchemaAgent;
                    }
                }, null);
        log.info("Mounted schema agent for {}", dstSchemaAgentRes.dst());

        getCtx().put(AgentContext.class,
                new AgentContext(srcClogAgentRes.src(), dstClogAgentRes.src(), cdcAgentRes.src(),
                        dstSchemaAgentRes.src()));

        dbAgentExecutor.trlogCleanup(srcClogAgentRes.src(), srcNdtDb);
        srcNdtDb.commit();

        log.info("Warming up schemas...");
        final Stopwatch schemasTime = timeOf(new Function<Void, Void>() {
            @Override
            public Void apply(final Void arg) {
                warmUpCache(srcAppDb.metaInfo(), SKIP_WARMUP_FKS);
                warmUpCache(dstAppDb.metaInfo(), WARMUP_FKS);
                warmUpCache(srcNdtDb.metaInfo(), SKIP_WARMUP_FKS);
                warmUpCache(dstNdtDb.metaInfo(), SKIP_WARMUP_FKS);
                return null;
            }
        });
        log.info("Warm up schemas for {}", schemasTime);

        stTotal.stop();
        log.info("Installing NDT done for {}", stTotal);
        view.setProgress(PROGRESS_OFF);
    }

    private final NdtViewController view;
    private final DbAgentExecutor dbAgentExecutor;
    private final RelDb srcNdtDb;
    private final RelDb dstNdtDb;
    private final RelDb srcAppDb;
    private final RelDb dstAppDb;
    private final DbAgentFactory<Function<RelDb, RelDb>> srcDrivenDbAgentFactory;
    private final DbAgentFactory<Function<RelDb, RelDb>> dstDrivenDbAgentFactory;

    private static final Log4jLogger log = new Log4jLogger(Logger.getLogger(NdtInstallAndEnable.class));
}
