/**
 * Copyright (c) 2015 Persinity Inc.
 */
/**
 *
 */
package com.persinity.ndt.controller;

import static com.persinity.common.Config.loadPropsFrom;
import static com.persinity.common.StringUtils.format;
import static com.persinity.common.ThreadUtil.sleepSeconds;
import static com.persinity.common.invariant.Invariant.notNull;
import static com.persinity.ndt.controller.NdtEvent.NdtEventType.bootstrapCompleted;
import static com.persinity.ndt.controller.NdtEvent.NdtEventType.deltaTransferCompleted;
import static com.persinity.ndt.controller.NdtEvent.NdtEventType.initialTransferCompleted;
import static com.persinity.ndt.controller.NdtEvent.NdtEventType.setupCompleted;
import static com.persinity.ndt.controller.NdtViewController.PROGRESS_ON;
import static com.persinity.ndt.controller.step.StopWindowGenerator.FEED_EXAUSTED_STOP;
import static com.persinity.ndt.controller.step.StopWindowGenerator.FORCE_STOP;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

import org.apache.log4j.Logger;

import com.persinity.common.BuildInfo;
import com.persinity.common.logging.Log4jLogger;
import com.persinity.ndt.controller.script.Script;
import com.persinity.ndt.controller.script.Step;
import com.persinity.ndt.controller.step.ClogGc;
import com.persinity.ndt.controller.step.MetricsReport;
import com.persinity.ndt.controller.step.Migrate;
import com.persinity.ndt.controller.step.MigrateNoCoalesce;
import com.persinity.ndt.controller.step.NdtDisableAndUninstall;
import com.persinity.ndt.controller.step.NdtInstallAndEnable;
import com.persinity.ndt.controller.step.Pause;
import com.persinity.ndt.controller.step.RollTargetConsistent;
import com.persinity.ndt.controller.step.StopOnFinish;
import com.persinity.ndt.controller.step.StopWindowGenerator;
import com.persinity.ndt.controller.step.Transform;
import com.persinity.ndt.db.RelDbPoolFactory;
import com.persinity.ndt.dbagent.DbAgentExecutor;
import com.persinity.ndt.dbagent.relational.AgentSqlStrategy;
import com.persinity.ndt.dbagent.relational.oracle.OracleAgentSqlStrategy;
import com.persinity.ndt.etlmodule.EtlPlanExecutor;
import com.persinity.ndt.etlmodule.haka.HakaEtlPlanExecutor;
import com.persinity.ndt.etlmodule.serial.SerialEtlPlanExecutor;

/**
 * This is the main class that can be executed.
 * <p/>
 * TODO We could isolate various steps in the NDT controller from agent specifics by a common DbAgentController.
 * The idea is to have the DbAgentController to encapsulate the details of which specific DbAgents are employed for
 * the given DB operation - install, mount, dismount, deninstall, etc.
 * Thus we could change specific agent interface or add/remove agents without affecting various steps.
 *
 * @author Ivan Dachev
 */
public class NdtController {

    public static final String NDT_MIGRATE_VERSION = BuildInfo.getInstance().getProductVersion();

    /**
     * @param args
     */
    public static void main(String[] args) {

        try {
            log.info("{}", BuildInfo.getInstance());
            final NdtController ndtController = createFromConfig(NdtControllerConfig.DEFAULT_CONFIG_FILE_NAME);

            Runtime.getRuntime().addShutdownHook(new Thread() {
                @Override
                public void run() {
                    log.debug("Starting shutdown hook.");
                    ndtController.ndtCleanup();
                    ndtController.close();
                }
            });

            ndtController.run();
            System.exit(0);

        } catch (final RuntimeException e) {

            log.error(e, "NDT Failed!");
            System.err.println(format("NDT Failed!\nCause: {}", e.getMessage()));
            System.exit(1);
        }
    }

    /**
     * @return NdtController created from config file
     */
    public static NdtController createFromConfig(String configFileName) {

        final NdtControllerConfig config = new NdtControllerConfig(loadPropsFrom(configFileName), configFileName);

        return createFromConfig(config);
    }

    /**
     * @return NdtController created from config
     */
    public static NdtController createFromConfig(final NdtControllerConfig config) {
        log.info("Using {}", config);

        final NdtViewController view;
        try {
            view = (NdtViewController) Class.forName(config.getViewClassname()).newInstance();
        } catch (final Exception e) {
            throw new RuntimeException(e);
        }

        view.logNdtMessage(format("Persinity NDT Migrate v{}", NDT_MIGRATE_VERSION));
        view.logNdtMessage(format("Loading"));
        view.setProgress(PROGRESS_ON);

        final AgentSqlStrategy sqlStrategy = new OracleAgentSqlStrategy();

        final EtlPlanExecutor etlPlanExecutor;
        if (config.getHakaEnable()) {
            final String hakaName = config.getHakaName();
            final String hakaConfigName = config.getHakaConfigName();
            etlPlanExecutor = new HakaEtlPlanExecutor(hakaName, hakaConfigName, config.getHakaTimeoutSeconds(),
                    config.getEtlWindowCheckIntervalSeconds());
        } else {
            etlPlanExecutor = new SerialEtlPlanExecutor(config.getEtlWindowCheckIntervalSeconds());
        }

        RelDbPoolFactory relDbPoolFactory = new RelDbPoolFactory(config.getDbConfigName());
        final NdtController ndtController = new NdtController(relDbPoolFactory, etlPlanExecutor, view, config,
                sqlStrategy);
        return ndtController;
    }

    /**
     * @param relDbPoolFactory
     * @param etlPlanExecutor
     * @param view
     * @param config
     */
    public NdtController(final RelDbPoolFactory relDbPoolFactory, final EtlPlanExecutor etlPlanExecutor,
            final NdtViewController view, final NdtControllerConfig config, final AgentSqlStrategy sqlStrategy) {
        notNull(relDbPoolFactory);
        notNull(etlPlanExecutor);
        notNull(view);
        notNull(config);
        notNull(sqlStrategy);

        this.etlPlanExecutor = etlPlanExecutor;
        this.view = view;
        this.config = config;
        this.relDbPoolFactory = relDbPoolFactory;

        // TODO make this alla spring so it can be configured to use other AgentSqlStrategy/SchemaInfo impls

        this.sqlStrategy = sqlStrategy;

        dbAgentExecutor = new DbAgentExecutor();

        context = Collections.synchronizedMap(new HashMap<>());
        context.put(NdtController.class, this);

        script = new Script(null, Step.NO_DELAY, context);

        defineSteps();
    }

    /**
     * Starts NDT controller.
     */
    public void run() {
        script.run();
    }

    /**
     * This is called when script run fails.
     */
    public void ndtCleanup() {

        if (cleanedUp.getAndSet(true)) {
            return;
        }

        log.info("Cleanup...");

        // first close view to stop user interaction only output
        try {
            view.close();
        } catch (RuntimeException _) {
            // silence
        }

        try {
            script.sigStop();
        } catch (RuntimeException e) {
            log.warn(e, "Error during cleanup!");
        }

        try {
            // give chance steps to react on stop signal
            sleepSeconds(1);
        } catch (RuntimeException _) {
            // silence
        }

        try {
            ndtDisableAndUninstall.forceWork();
        } catch (RuntimeException e) {
            log.warn(e, "Cleanup failed!");
        }

    }

    /**
     * Close NDT controller.
     */
    public void close() {

        if (closed.getAndSet(true)) {
            return;
        }

        log.info("Closing...");
        view.close();
        etlPlanExecutor.close();
        relDbPoolFactory.close();
        log.info("Cleanup DONE");
    }

    private void defineSteps() {

        final Step pauseStartDemo = new Pause(null, Step.NO_DELAY,
                new NdtEvent(bootstrapCompleted, "Confirm to setup NDT."), context);
        final Step ndtInstallAndEnable = new NdtInstallAndEnable(pauseStartDemo, Step.NO_DELAY, context);

        final Step pauseMigrate = new Pause(ndtInstallAndEnable, Step.NO_DELAY,
                new NdtEvent(setupCompleted, "Confirm to start NDT Migrate."), context);
        final Step migrateNoCoalesce = new MigrateNoCoalesce(pauseMigrate, Step.NO_DELAY, context);

        final Step pauseTransform = new Pause(pauseMigrate, Step.NO_DELAY, new NdtEvent(initialTransferCompleted,
                "You may start with initial transfer using your favorite backup/recovery or export/import tool.\n"
                        + "Confirm when initial transfer is completed."), context);
        final Step rollTargetConsistent = new RollTargetConsistent(pauseTransform, Step.NO_DELAY, context);
        final Step transform = new Transform(rollTargetConsistent, Step.NO_DELAY, context);

        final Step stopMigrateNoCoalesceWindowGenerator = new StopWindowGenerator(rollTargetConsistent, Step.NO_DELAY,
                context, MigrateNoCoalesce.class.getName(), FORCE_STOP, null);

        final Step migrate = new Migrate(migrateNoCoalesce, Step.NO_DELAY, context);

        final Step pauseRequestStop = new Pause(migrateNoCoalesce, Step.NO_DELAY, new NdtEvent(bootstrapCompleted,
                "Now you can stop Source Application.\nConfirm when Source Application is stopped."), context);
        final Step stopMigrateWindowGenerator = new StopWindowGenerator(pauseRequestStop, Step.NO_DELAY, context,
                Migrate.class.getName(), FEED_EXAUSTED_STOP, "Waiting migration to complete...");
        final Step stopTransformWindowGenerator = new StopWindowGenerator(migrate, Step.NO_DELAY, context,
                Transform.class.getName(), FEED_EXAUSTED_STOP, null);

        final Step clogGc = new ClogGc(ndtInstallAndEnable, Step.NO_DELAY, context);
        final Step clogGcStopOnFinish = new StopOnFinish(transform, Step.NO_DELAY, clogGc, context);

        final Step metricsReport = new MetricsReport(ndtInstallAndEnable, Step.NO_DELAY, context);
        final Step metricsReportStopOnFinish = new StopOnFinish(transform, Step.NO_DELAY, metricsReport, context);

        final Step pauseBeforeUninstallStep;
        // TODO implement other NdtController/Script setup to be used in debug mode
        if (config.isDebugModeOn()) {
            log.info("Debug mode is ON add pause before uninstall step.");
            pauseBeforeUninstallStep = new Pause(transform, Step.NO_DELAY, new NdtEvent(deltaTransferCompleted,
                    "Debug pause to check for consistency before uninstall to cleanup the clogs."), context);
            ndtDisableAndUninstall = new NdtDisableAndUninstall(pauseBeforeUninstallStep, Step.NO_DELAY, context);
        } else {
            pauseBeforeUninstallStep = null;
            ndtDisableAndUninstall = new NdtDisableAndUninstall(clogGc, Step.NO_DELAY, context);
        }

        script.addStep(pauseStartDemo);
        script.addStep(ndtInstallAndEnable);

        script.addStep(pauseMigrate);
        script.addStep(migrateNoCoalesce);

        script.addStep(pauseTransform);
        script.addStep(rollTargetConsistent);
        script.addStep(transform);

        script.addStep(stopMigrateNoCoalesceWindowGenerator);
        script.addStep(migrate);

        script.addStep(pauseRequestStop);
        script.addStep(stopMigrateWindowGenerator);
        script.addStep(stopTransformWindowGenerator);

        script.addStep(clogGc);
        script.addStep(clogGcStopOnFinish);

        script.addStep(metricsReport);
        script.addStep(metricsReportStopOnFinish);

        if (pauseBeforeUninstallStep != null) {
            script.addStep(pauseBeforeUninstallStep);
        }

    }

    // TODO move all of getters here into a type safe context

    /**
     * @return AgentSqlStrategy
     */
    public AgentSqlStrategy getSqlStrategy() {
        return sqlStrategy;
    }

    /**
     * @return EtlPlanExecutor
     */
    public EtlPlanExecutor getEtlPlanExecutor() {
        return etlPlanExecutor;
    }

    /**
     * @return NdtViewController
     */
    public NdtViewController getView() {
        return view;
    }

    /**
     * @return NdtControllerConfig
     */
    public NdtControllerConfig getConfig() {
        return config;
    }

    /**
     * @return DbAgentExecutor
     */
    public DbAgentExecutor getDbAgentExecutor() {
        return dbAgentExecutor;
    }

    /**
     * @return RelDbPoolFactory
     */
    public RelDbPoolFactory getRelDbPoolFactory() {
        return relDbPoolFactory;
    }

    private final AgentSqlStrategy sqlStrategy;
    private final EtlPlanExecutor etlPlanExecutor;
    private final NdtViewController view;
    private final NdtControllerConfig config;
    private final Map<Object, Object> context;
    private final DbAgentExecutor dbAgentExecutor;
    private final Script script;
    private final RelDbPoolFactory relDbPoolFactory;

    private AtomicBoolean closed = new AtomicBoolean(false);
    private AtomicBoolean cleanedUp = new AtomicBoolean(false);
    private NdtDisableAndUninstall ndtDisableAndUninstall;

    private static final Log4jLogger log = new Log4jLogger(Logger.getLogger(NdtController.class));
}
