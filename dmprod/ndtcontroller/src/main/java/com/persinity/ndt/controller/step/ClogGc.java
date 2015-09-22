/**
 * Copyright (c) 2015 Persinity Inc.
 */
package com.persinity.ndt.controller.step;

import static com.persinity.common.ThreadUtil.waitForCondition;
import static com.persinity.common.invariant.Invariant.notNull;

import java.util.Map;

import org.apache.log4j.Logger;

import com.google.common.base.Function;
import com.persinity.common.IoUtils;
import com.persinity.common.db.RelDb;
import com.persinity.common.logging.Log4jLogger;
import com.persinity.ndt.controller.script.BaseStep;
import com.persinity.ndt.controller.script.Step;
import com.persinity.ndt.dbagent.DbAgentExecutor;

/**
 * @author Ivan Dachev
 */
public class ClogGc extends BaseStep {
    /**
     * @param prev
     *         Step or {@code null} if the first step
     * @param delaySecs
     *         from previous step
     * @param ctx
     */
    public ClogGc(final Step prev, final int delaySecs, final Map<Object, Object> ctx) {
        super(prev, delaySecs, ctx);

        dbAgentExecutor = getController().getDbAgentExecutor();
        clogGcIntervalSeconds = getController().getConfig().getDbAgentClogGcIntervalSeconds();
    }

    @Override
    protected void work() {
        while (waitForNextInterval()) {
            RelDb ownSrcNdtDb = null;
            RelDb ownDstNdtDb = null;
            try {
                ownSrcNdtDb = getController().getRelDbPoolFactory().ndtBridge().src().get();
                ownDstNdtDb = getController().getRelDbPoolFactory().ndtBridge().dst().get();
                final AgentContext agentContext = (AgentContext) getCtx().get(AgentContext.class);
                notNull(agentContext);

                log.debug("Execute clog GC at {}", ownSrcNdtDb);
                dbAgentExecutor.clogAgentGc(agentContext.getSrcClogAgent(), ownSrcNdtDb);

                log.debug("Execute clog GC at {}", ownDstNdtDb);
                dbAgentExecutor.clogAgentGc(agentContext.getDstClogAgent(), ownDstNdtDb);
            } finally {
                IoUtils.silentClose(ownSrcNdtDb, ownDstNdtDb);
            }
        }
    }

    private boolean waitForNextInterval() {
        final Function<Void, Boolean> condition = new Function<Void, Boolean>() {
            @Override
            public Boolean apply(final Void aVoid) {
                return isStopRequested();
            }
        };
        return !waitForCondition(condition, clogGcIntervalSeconds * 1000L);
    }

    private static final Log4jLogger log = new Log4jLogger(Logger.getLogger(ClogGc.class));
    private final DbAgentExecutor dbAgentExecutor;
    private final int clogGcIntervalSeconds;

}
