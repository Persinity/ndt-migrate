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

package com.persinity.ndt.datamutator;

import static com.persinity.common.StringUtils.format;
import static com.persinity.common.ThreadUtil.sleepSeconds;

import java.io.Reader;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.apache.commons.lang3.time.DurationFormatUtils;
import org.apache.log4j.Logger;

import com.google.common.base.Stopwatch;
import com.persinity.common.BuildInfo;
import com.persinity.common.Config;
import com.persinity.common.logging.Log4jLogger;
import com.persinity.common.metrics.Metrics;
import com.persinity.ndt.datamutator.load.EntityFactory;
import com.persinity.ndt.datamutator.load.EntityPool;
import com.persinity.ndt.datamutator.load.EntityPoolUtil;
import com.persinity.ndt.datamutator.load.LoadBase;
import com.persinity.ndt.datamutator.load.LoadParameters;
import com.persinity.ndt.datamutator.load.TimeLoad;
import com.persinity.ndt.datamutator.load.TransactionLoad;
import com.persinity.ndt.datamutator.reldb.RelDbTypeFactory;
import net.sourceforge.argparse4j.ArgumentParsers;
import net.sourceforge.argparse4j.impl.action.StoreTrueArgumentAction;
import net.sourceforge.argparse4j.inf.ArgumentParser;
import net.sourceforge.argparse4j.inf.ArgumentParserException;
import net.sourceforge.argparse4j.inf.Namespace;

public class DataMutator implements LoadParameters {
    public static final String NAME_VERSION =
            "Persinity NDT DataMutator v" + BuildInfo.getInstance().getProductVersion();

    /**
     * @param args
     */
    public static void main(String[] args) {
        final ArgumentParser parser = ArgumentParsers.newArgumentParser(NAME_VERSION).defaultHelp(true)
                .description("DB data mutator tool.");
        parser.addArgument("--config").setDefault(DEFAULT_PROPERTIES_NAME).help("Config file");
        parser.addArgument("--db-config").setDefault(DEFAULT_DB_PROPERTIES_NAME).help("DB config file");
        parser.addArgument("--init-schema").action(new StoreTrueArgumentAction()).help("Init the DB schema and exit");
        parser.addArgument("--drop-schema").action(new StoreTrueArgumentAction()).help("Drop the DB schema and exit");
        parser.addArgument("--cleanup-schema").action(new StoreTrueArgumentAction())
                .help("Cleanup all records from DB schema and exit");
        parser.addArgument("--auto-resume").action(new StoreTrueArgumentAction())
                .help("Automatically resume connections on start");
        parser.addArgument("--quite").action(new StoreTrueArgumentAction()).help("Do not dmp on console");

        Namespace ns = null;
        try {
            ns = parser.parseArgs(args);
        } catch (final ArgumentParserException e) {
            parser.handleError(e);
            System.exit(1);
        }

        final String config = ns.getString("config");
        final String dbConfig = ns.getString("db_config");
        final boolean initSchema = ns.getBoolean("init_schema");
        final boolean dropSchema = ns.getBoolean("drop_schema");
        final boolean cleanupSchema = ns.getBoolean("cleanup_schema");
        final boolean autoResume = ns.getBoolean("auto_resume");
        final boolean quite = ns.getBoolean("quite");

        final DataMutator dm = new DataMutator(config, dbConfig, quite);
        dm.loadSchema();
        if (initSchema) {
            dm.initSchema();
        } else if (dropSchema) {
            dm.dropSchema();
        } else if (cleanupSchema) {
            dm.cleanupSchema();
        } else {
            dm.start(!autoResume);
            dm.waitToFinish();
        }
    }

    public DataMutator() {
        this(DEFAULT_PROPERTIES_NAME, DEFAULT_DB_PROPERTIES_NAME, false);
    }

    /**
     * @param configFile
     * @param dbConfigFile
     * @param quite
     */
    public DataMutator(final String configFile, final String dbConfigFile, final boolean quite) {
        this(Config.loadPropsFrom(configFile), configFile, Config.loadPropsFrom(dbConfigFile), dbConfigFile, quite);
    }

    /**
     * @param configProps
     * @param configSource
     * @param dbConfigProps
     * @param dbConfigSource
     * @param quite
     */
    public DataMutator(final Properties configProps, final String configSource, final Properties dbConfigProps,
            final String dbConfigSource, final boolean quite) {

        config = new DataMutatorConfig(configProps, configSource);
        log.info(config.toString());

        try {
            entityFactory = (EntityFactory) config.getEntityFactoryClass().newInstance();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        entityPoolUtil = new EntityPoolUtil(new EntityPool(this.config.getRatio()), new RelDbTypeFactory());

        entityFactory.init(dbConfigProps, dbConfigSource, entityPoolUtil);

        loadExecutors = new ArrayList<>();
        threads = new ArrayList<>();

        Reader consoleReader = null;
        if (!quite && this.config.getLoadType() == LoadType.TIME && this.config.getLoadQuantity() == -1) {
            consoleReader = ConsoleView.openConsoleReader();
        }

        transactionDelayInMs = this.config.getTransactionDelayInMs();
        dmlsPerTransaction = this.config.getDmlsPerTransaction();

        scheduler = Executors.newSingleThreadScheduledExecutor();
        runningTime = Stopwatch.createUnstarted();
        logStatusTime = Stopwatch.createStarted();

        this.quite = quite;
        view = new ConsoleView(this, consoleReader, quite);

        view.logMsg(NAME_VERSION);
        view.logMsg(entityFactory.getConnectionInfo());

        log.info("{}", BuildInfo.getInstance());
    }

    /**
     * Load schema info with progress.
     */
    public void loadSchema() {
        view.logMsg("Loading");
        view.setProgress(true);

        final String schemaInfo = entityFactory.getSchemaInfo();
        entityFactory.readSchema(config.getInitialTableEntitiesRead());

        view.setProgress(false);
        view.logMsg(format("Loaded {}", schemaInfo));
    }

    /**
     * Init schema.
     */
    public void initSchema() {
        view.logMsg(format("Recreating {}", entityFactory.getSchemaInfo()));
        view.setProgress(true);

        entityFactory.initSchema();

        view.setProgress(false);
        view.logMsg(format("Recreated {}", entityFactory.getSchemaInfo()));
    }

    /**
     * Cleanup schema.
     */
    public void cleanupSchema() {
        view.logMsg(format("Cleanup {}", entityFactory.getSchemaInfo()));
        view.setProgress(true);

        entityFactory.cleanupSchema();

        view.setProgress(false);
        view.logMsg("Schema is clean");
    }

    /**
     * Drop schema.
     */
    public void dropSchema() {
        view.logMsg(format("Dropping {}", entityFactory.getSchemaInfo()));
        view.setProgress(true);

        entityFactory.dropSchema();

        view.setProgress(false);
        view.logMsg("Schema dropped");
    }

    /**
     * Start initial loaders form config.
     *
     * @param startPaused
     */
    public void start(final boolean startPaused) {
        view.logMsg(format("Starting {} connections{}", config.getParallelConnections(), startPaused ? " paused" : ""));
        view.setProgress(true);

        if (!startPaused) {
            runningTime.start();
        }

        for (int i = 0; i < config.getParallelConnections(); i++) {
            startNewLoader(startPaused);
        }

        while (true) {
            boolean allRunning = true;
            synchronized (lock) {
                for (final LoadBase loadExecutor : loadExecutors) {
                    if (!loadExecutor.isRunning()) {
                        allRunning = false;
                        break;
                    }
                }
            }

            if (allRunning) {
                break;
            }

            sleepSeconds(1);
        }

        view.setProgress(false);
        view.logMsg("Load started.");
    }

    /**
     * Will block calling thread until all loaders are done.
     */
    public void waitToFinish() {
        log.info("Waiting to finish {} threads...", threads.size());

        view.start();
        startStatusDumpTask();

        while (true) {
            Thread aliveTh = null;
            synchronized (lock) {
                for (Thread th : threads) {
                    if (th.isAlive()) {
                        aliveTh = th;
                        break;
                    }
                }
            }

            if (aliveTh == null) {
                break;
            }

            try {
                aliveTh.join();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }

        scheduler.shutdown();
        entityFactory.close();

        log.info("All threads DONE.");
        view.logMsg("Load done");
    }

    /**
     * Pause all loaders.
     */
    public void pause() {
        synchronized (lock) {
            view.logMsg(format("Pausing {} connections...", loadExecutors.size()));
            for (final LoadBase loadExecutor : loadExecutors) {
                loadExecutor.requestPause();
            }
            if (runningTime.isRunning()) {
                runningTime.stop();
            }
        }
    }

    /**
     * Resume all loaders.
     */
    public void resume() {
        synchronized (lock) {
            view.logMsg(format("Resuming {} connections...", loadExecutors.size()));
            for (final LoadBase loadExecutor : loadExecutors) {
                loadExecutor.requestResume();
            }
            if (!runningTime.isRunning()) {
                runningTime.start();
            }
        }
    }

    /**
     * Cleanup all records in tables.
     */
    public void cleanup() {
        pauseStatusDumpTask();
        if (view.confirm("Confirm cleanup of all records in tables.")) {
            final boolean paused = areAllPaused();
            if (!paused) {
                pause();
            }
            waitAllPaused();
            cleanupSchema();
            resetStates();
            if (!paused) {
                resume();
            }
        }
        resumeStatusDumpTask();
    }

    /**
     * Reset all schemas.
     */
    public void reset() {
        pauseStatusDumpTask();
        if (view.confirm("Confirm reset of all tables.")) {
            final boolean paused = areAllPaused();
            if (!paused) {
                pause();
            }
            waitAllPaused();
            initSchema();
            resetStates();
            if (!paused) {
                resume();
            }
        }
        resumeStatusDumpTask();
    }

    /**
     * Stop all loaders.
     */
    public boolean stop() {
        boolean stopped = false;
        pauseStatusDumpTask();
        if (view.confirm("Confirm stop.")) {
            synchronized (lock) {
                view.logMsg(format("Stopping {} connections...", loadExecutors.size()));
                for (final LoadBase loadExecutor : loadExecutors) {
                    loadExecutor.requestStop();
                }
                loadExecutors.clear();
            }
            stopped = true;
        } else {
            resumeStatusDumpTask();
        }
        return stopped;
    }

    /**
     * Increase loaders/connections by one.
     */
    public void connectionsUp() {
        synchronized (lock) {
            startNewLoader(areAllPaused());
            view.logMsg(format("Increased connections: {}", loadExecutors.size()));
        }
    }

    /**
     * Decrease loaders/connections by one.
     */
    public void connectionsDown() {
        synchronized (lock) {
            if (loadExecutors.size() > 1) {
                final LoadBase loadExecutor = loadExecutors.remove(0);
                loadExecutor.requestStop();
            }
            view.logMsg(format("Decreased connections: {}", loadExecutors.size()));
        }
    }

    /**
     * Increase transactions delay.
     */
    public void transactionDelayUp() {
        synchronized (lock) {
            transactionDelayInMs += TRANSACTION_DELAY_INCREMENT_MS;
            view.logMsg(format("Increased transaction delay: {} ms", transactionDelayInMs));
        }
    }

    /**
     * Decrease transactions delay.
     */
    public void transactionDelayDown() {
        synchronized (lock) {
            transactionDelayInMs -= TRANSACTION_DELAY_INCREMENT_MS;
            if (transactionDelayInMs < MINIMUM_TRANSACTION_DELAY_MS) {
                transactionDelayInMs = MINIMUM_TRANSACTION_DELAY_MS;
            }
            view.logMsg(format("Decreased transaction delay: {} ms", transactionDelayInMs));
        }
    }

    /**
     * Increase records per transactions.
     */
    public void transactionRecordsUp() {
        synchronized (lock) {
            dmlsPerTransaction++;
            view.logMsg(format("Increased DMLs per transaction: {}", dmlsPerTransaction));
        }
    }

    /**
     * Decrease records per transactions.
     */
    public void transactionRecordsDown() {
        synchronized (lock) {
            dmlsPerTransaction--;
            if (dmlsPerTransaction < MINIMUM_DMLS_PER_TRANSACTION) {
                dmlsPerTransaction = MINIMUM_DMLS_PER_TRANSACTION;
            }
            view.logMsg(format("Decreased DMLs per transaction: {}", dmlsPerTransaction));
        }
    }

    @Override
    public long getTransactionDelayInMs() {
        synchronized (lock) {
            return transactionDelayInMs;
        }
    }

    @Override
    public int getDmlsPerTransaction() {
        synchronized (lock) {
            return dmlsPerTransaction;
        }
    }

    private void resetStates() {
        entityPoolUtil.getEntityPool().reset();
        runningTime.reset();
        Metrics.getMetrics().reset();
    }

    private void waitAllPaused() {
        synchronized (lock) {
            boolean notPaused = true;
            while (notPaused) {
                notPaused = false;
                for (final LoadBase loadExecutor : loadExecutors) {
                    if (!loadExecutor.isPaused()) {
                        notPaused = true;
                        break;
                    }
                }

                if (notPaused) {
                    try {
                        lock.wait(500);
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                }
            }
        }
    }

    private boolean areAllPaused() {
        boolean allPaused;
        synchronized (lock) {
            allPaused = loadExecutors.size() > 0;
            for (final LoadBase loadExecutor : loadExecutors) {
                if (!loadExecutor.isRequestPause()) {
                    allPaused = false;
                    break;
                }
            }
        }
        return allPaused;
    }

    private void startStatusDumpTask() {
        if (!quite) {
            log.info("Starting dump status task");
            synchronized (lock) {
                dumpStatusPaused = false;
            }
            scheduler.scheduleWithFixedDelay(new Runnable() {
                @Override
                public void run() {
                    try {
                        dumpStatus();
                    } catch (RuntimeException re) {
                        log.error(re, "");
                    }
                }
            }, 0, DUMP_STATUS_PERIOD_SECONDS, TimeUnit.SECONDS);
        }
    }

    private void pauseStatusDumpTask() {
        synchronized (lock) {
            dumpStatusPaused = true;
        }
    }

    private void resumeStatusDumpTask() {
        synchronized (lock) {
            dumpStatusPaused = false;
        }
    }

    private void dumpStatus() {
        final String config;
        final String status;
        final String schemaInfo = entityFactory.getSchemaInfo();
        synchronized (lock) {
            if (dumpStatusPaused) {
                return;
            }

            config = format("Config:\n\tConnections : {}\n\tTransaction Delay: {} ms\n\tDMLs Per Transaction: {}",
                    loadExecutors.size(), transactionDelayInMs, dmlsPerTransaction);

            final boolean allPaused = areAllPaused();
            final String pausedMsg = allPaused ? (pausedBlink ? "<      > " : "<PAUSED> ") : "";
            pausedBlink = !pausedBlink;

            final EntityPool entityPool = entityPoolUtil.getEntityPool();

            final String formattedRunningTime = formatRunningTime();
            status = format("{}l:{}/a:{}/d:{}, dmls: {}/{}dps, ctrs: {}/{}tps, rtrs: {}, {}", pausedMsg,
                    entityPool.getLive(), entityPool.getAdded(), entityPool.getDeleted(), DataMutatorMetrics.getDmls(),
                    (long) DataMutatorMetrics.getDps(), DataMutatorMetrics.getTransactionCommits(),
                    (long) DataMutatorMetrics.getTps(), DataMutatorMetrics.getTransactionRollbacks(),
                    formattedRunningTime);
            if (!view.isLastMsgWasStatus()) {
                view.logMsg(schemaInfo);
                view.logMsg(config);
                view.logMsg("Legend:\nEntities l: {Live} / a: {Added} / d: {Deleted}, dmls: {DMLs},\n"
                        + "ctrs: {Committed Transactions}, rtrs: {Failed Transactions}, {Running Time}\n");
            }
            view.logStatus(status);
            if (logStatusTime.elapsed(TimeUnit.SECONDS) >= LOG_STATUS_PERIOD_SECONDS) {
                log.info("Status: {}", status);
                logStatusTime.reset();
            }
        }
    }

    private String formatRunningTime() {
        final long elapsedMs = runningTime.elapsed(TimeUnit.MILLISECONDS);
        final String res;
        if (runningTime.elapsed(TimeUnit.DAYS) > 0) {
            res = DurationFormatUtils.formatDuration(elapsedMs, "dd:HH:mm:ss");
        } else if (runningTime.elapsed(TimeUnit.HOURS) > 0) {
            res = DurationFormatUtils.formatDuration(elapsedMs, "HH:mm:ss");
        } else {
            res = DurationFormatUtils.formatDuration(elapsedMs, "mm:ss");
        }
        return res;
    }

    private void startNewLoader(final boolean startPaused) {
        final LoadBase loadExecutor = buildTestLoadExecutor(config.getLoadType(), entityFactory, entityPoolUtil);
        if (startPaused) {
            loadExecutor.requestPause();
        }
        int indx;
        synchronized (lock) {
            loadExecutors.add(loadExecutor);
            indx = loadExecutors.size();
        }
        final Thread th = new Thread(loadExecutor);
        th.setName("DataMutator-" + indx);
        th.start();
        synchronized (lock) {
            threads.add(th);
        }
    }

    private LoadBase buildTestLoadExecutor(final LoadType type, final EntityFactory entityFactory,
            final EntityPoolUtil entityPoolUtil) {

        int loadQuantity = config.getLoadQuantity();

        switch (type) {
        case TIME:
            return new TimeLoad(loadQuantity, this, entityFactory, entityPoolUtil);
        case TRANSACTIONS:
            return new TransactionLoad(loadQuantity, this, entityFactory, entityPoolUtil);
        default:
            throw new RuntimeException("Unsupported load type: " + type.toString());
        }
    }

    private static final String DEFAULT_PROPERTIES_NAME = "data-mutator.properties";
    private static final String DEFAULT_DB_PROPERTIES_NAME = "data-mutator-db.properties";

    private static final long TRANSACTION_DELAY_INCREMENT_MS = 50;
    private static final long MINIMUM_TRANSACTION_DELAY_MS = 100;
    private static final int MINIMUM_DMLS_PER_TRANSACTION = 3;
    private static final long DUMP_STATUS_PERIOD_SECONDS = 1;
    private static final long LOG_STATUS_PERIOD_SECONDS = 60;

    private final DataMutatorConfig config;
    private final EntityFactory entityFactory;
    private final EntityPoolUtil entityPoolUtil;
    private final ConsoleView view;
    private final List<Thread> threads;
    private final List<LoadBase> loadExecutors;
    private final boolean quite;
    private final ScheduledExecutorService scheduler;
    private final Stopwatch runningTime;
    private final Stopwatch logStatusTime;

    private boolean dumpStatusPaused;
    private long transactionDelayInMs;
    private int dmlsPerTransaction;

    private boolean pausedBlink;

    private final Object lock = new Object();

    private static final Log4jLogger log = new Log4jLogger(Logger.getLogger(DataMutator.class));
}
