/**
 * Copyright (c) 2015 Persinity Inc.
 */
package com.persinity.ndt.controller.step;

import static com.persinity.common.fp.FunctionUtil.timeOf;
import static com.persinity.common.invariant.Invariant.notNull;
import static com.persinity.ndt.controller.NdtViewController.PROGRESS_OFF;
import static com.persinity.ndt.controller.NdtViewController.PROGRESS_ON;

import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

import org.apache.log4j.Logger;

import com.google.common.base.Function;
import com.google.common.base.Stopwatch;
import com.persinity.common.IoUtils;
import com.persinity.common.collection.DirectedEdge;
import com.persinity.common.db.RelDb;
import com.persinity.common.logging.Log4jLogger;
import com.persinity.ndt.controller.NdtController;
import com.persinity.ndt.controller.NdtViewController;
import com.persinity.ndt.controller.script.BaseStep;
import com.persinity.ndt.controller.script.Step;
import com.persinity.ndt.dbagent.DbAgentExecutor;

/**
 * @author Ivan Dachev
 */
public class NdtDisableAndUninstall extends BaseStep {
    /**
     * @param prev
     *         Step or {@code null} if the first step
     * @param delaySecs
     *         from previous step
     * @param ctx
     */
    public NdtDisableAndUninstall(final Step prev, final int delaySecs, final Map<Object, Object> ctx) {
        super(prev, delaySecs, ctx);

        final NdtController ndtController = getController();

        view = ndtController.getView();
        notNull(view);

        dbAgentExecutor = ndtController.getDbAgentExecutor();
        notNull(dbAgentExecutor);

        workInProgress = new AtomicBoolean(false);
    }

    @Override
    protected void work() {
        if (workInProgress.getAndSet(true)) {
            return;
        }

        final AgentContext agentContext = (AgentContext) getCtx().get(AgentContext.class);
        if (agentContext == null) {
            return;
        }

        RelDb srcNdtDb = null;
        RelDb dstNdtDb = null;
        RelDb srcAppDb = null;
        try {
            srcNdtDb = getController().getRelDbPoolFactory().ndtBridge().src().get();
            dstNdtDb = getController().getRelDbPoolFactory().ndtBridge().dst().get();
            srcAppDb = getController().getRelDbPoolFactory().appBridge().src().get();
            final RelDb srcNdtDbRef = srcNdtDb;
            final RelDb dstNdtDbRef = dstNdtDb;
            final RelDb srcAppDbRef = srcAppDb;

            log.info("Uninstalling NDT at {}, {}, {}", srcAppDb, srcNdtDb, dstNdtDb);
            final Stopwatch stTotal = Stopwatch.createStarted();

            logNdtMessage("Uninstalling NDT");
            if (!disableProgress) {
                view.setProgress(PROGRESS_ON);
            }

            log.info("Unmounting cdc agent on {}...", srcAppDb);
            final DirectedEdge<RuntimeException, Stopwatch> cdcAgentUnmountRes = timeOf(
                    new Function<Void, RuntimeException>() {
                        @Override
                        public RuntimeException apply(final Void arg) {
                            try {
                                dbAgentExecutor.cdcAgentUnmount(agentContext.getCdcAgent(), srcAppDbRef);
                                srcAppDbRef.commit();
                                return null;
                            } catch (RuntimeException e) {
                                log.error(e, "Source CDC agent unmount failed");
                                return e;
                            }
                        }
                    }, null);
            log.info("Unmounted cdc agent for {}", cdcAgentUnmountRes.dst());

            log.info("Unmounting clog agent on {}...", srcNdtDb);
            final DirectedEdge<RuntimeException, Stopwatch> clogAgentUnmountRes = timeOf(
                    new Function<Void, RuntimeException>() {
                        @Override
                        public RuntimeException apply(final Void arg) {
                            try {
                                dbAgentExecutor.clogAgentUnmount(agentContext.getSrcClogAgent(), srcNdtDbRef);
                                srcNdtDbRef.commit();
                                return null;
                            } catch (RuntimeException e) {
                                log.error(e, "Source CLOG agent unmount failed");
                                return e;
                            }
                        }
                    }, null);
            log.info("Unmounted clog agent for {}", clogAgentUnmountRes.dst());

            log.info("Unmounting clog/schema agents on {}...", dstNdtDb);
            final DirectedEdge<RuntimeException, Stopwatch> dstNdtUnmountRes = timeOf(
                    new Function<Void, RuntimeException>() {
                        @Override
                        public RuntimeException apply(final Void arg) {
                            try {
                                dbAgentExecutor.clogAgentUnmount(agentContext.getDstClogAgent(), dstNdtDbRef);
                                dbAgentExecutor.schemaAgentUnmount(agentContext.getDstSchemaAgent(), dstNdtDbRef);
                                dstNdtDbRef.commit();
                                return null;
                            } catch (RuntimeException e) {
                                log.error(e, "Destination agents unmount failed");
                                return e;
                            }
                        }
                    }, null);
            log.info("Unmounted clog/schema agents for {}", dstNdtUnmountRes.dst());

            if (cdcAgentUnmountRes.src() != null) {
                throw cdcAgentUnmountRes.src();
            }

            if (clogAgentUnmountRes.src() != null) {
                throw clogAgentUnmountRes.src();
            }

            if (dstNdtUnmountRes.src() != null) {
                throw dstNdtUnmountRes.src();
            }

            stTotal.stop();
            log.info("Uninstalling NDT done for {}", stTotal);
        } finally {
            IoUtils.silentClose(srcNdtDb, dstNdtDb, srcAppDb);
        }

        view.setProgress(PROGRESS_OFF);
        logNdtMessage("Cleanup completed");
    }

    protected void logNdtMessage(String msg) {
        final NdtController controller = getController();
        final NdtViewController ndtViewController = controller.getView();
        ndtViewController.logNdtMessage(msg);
    }

    /**
     * Force call work().
     */
    public void forceWork() {
        disableProgress = true;
        work();
    }

    private final NdtViewController view;
    private final DbAgentExecutor dbAgentExecutor;

    private boolean disableProgress;
    private AtomicBoolean workInProgress;

    private static final Log4jLogger log = new Log4jLogger(Logger.getLogger(NdtDisableAndUninstall.class));
}
