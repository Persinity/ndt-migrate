/**
 * Copyright (c) 2015 Persinity Inc.
 */
package com.persinity.ndt.etlmodule.haka;

import java.util.concurrent.Future;

import org.apache.log4j.Logger;

import com.persinity.common.db.RelDb;
import com.persinity.common.logging.Log4jLogger;
import com.persinity.haka.HakaExecutor;
import com.persinity.haka.HakaExecutorFactory;
import com.persinity.haka.HakaExecutorFactoryProvider;
import com.persinity.haka.impl.actor.HakaNode;
import com.persinity.ndt.etlmodule.EtlPlanExecutor;
import com.persinity.ndt.etlmodule.EtlPlanGenerator;
import com.persinity.ndt.etlmodule.WindowGenerator;
import com.persinity.ndt.etlmodule.haka.context.ContextFactoryProvider;

/**
 * @author Ivan Dachev
 */
public class HakaEtlPlanExecutor implements EtlPlanExecutor {

    public HakaEtlPlanExecutor(final String hakaNodeName, final String hakaConfig, final int timeoutSeconds,
            final int windowCheckIntervalSeconds) {
        this.timeoutSeconds = timeoutSeconds;
        this.windowCheckIntervalSeconds = windowCheckIntervalSeconds;

        hakaNode = new HakaNode(hakaNodeName, hakaConfig);
        factory = HakaExecutorFactoryProvider.getFactory();
        hakaExecutor = factory.newEmbeddedInstance(hakaNode);
    }

    @Override
    public void execute(final WindowGenerator<RelDb, RelDb> winGen, final EtlPlanGenerator<RelDb, RelDb> etlPlanner,
            final String name) {
        final EtlContext<RelDb, RelDb> context = new EtlContext<>(etlPlanner);

        WindowGeneratorJob job = new WindowGeneratorJob<>(winGen, windowCheckIntervalSeconds);
        ContextFactoryProvider.getFactory().setContext(job.getContextName(), context);

        // TODO the timeout here is for the job result and it should be changed to be
        // the timeout if there is no update response for the execution job
        // which for now is not implemented in the HakaExecutor
        // for now use 21474835s max allowed scala futures to always wait job to finish
        final Future<WindowGeneratorJob> future = hakaExecutor
                .executeJob(job, 21474835 * 1000L/*timeoutSeconds * 1000L*/);
        log.debug("Execute with haka {}", name);
        try {
            job = future.get();
        } catch (final Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void close() {
        hakaNode.shutdown();
    }

    /**
     * @return {@link HakaExecutor}
     */
    public HakaExecutor getHakaExecutor() {
        return hakaExecutor;
    }

    private final int timeoutSeconds;
    private final int windowCheckIntervalSeconds;
    private final HakaNode hakaNode;
    private final HakaExecutorFactory factory;
    private final HakaExecutor hakaExecutor;

    private static final Log4jLogger log = new Log4jLogger(Logger.getLogger(HakaEtlPlanExecutor.class));
}
