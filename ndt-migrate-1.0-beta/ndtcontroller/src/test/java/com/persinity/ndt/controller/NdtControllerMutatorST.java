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
package com.persinity.ndt.controller;

import static com.persinity.common.Config.loadPropsFrom;
import static com.persinity.common.StringUtils.format;
import static com.persinity.common.ThreadUtil.sleepSeconds;
import static com.persinity.ndt.controller.TestNdtControllerUtil.TEST_NDT_UTIL_ALL;
import static com.persinity.ndt.controller.TestNdtControllerUtil.createTestNdtUtil;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;

import org.apache.log4j.Logger;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.persinity.common.Resource;
import com.persinity.common.db.RelDb;
import com.persinity.common.logging.Log4jLogger;
import com.persinity.common.metrics.Metrics;
import com.persinity.ndt.controller.impl.StubNdtViewController;
import com.persinity.ndt.datamutator.DataMutator;
import com.persinity.ndt.etlmodule.relational.common.RelMetrics;

/**
 * Test for extensive loading of {@link NdtController} using {@link DataMutator}.
 *
 * @author Ivan Dachev
 */
public class NdtControllerMutatorST {

    // set this JVM property in case of cpu/memory profiling,
    // to exclude the DataMutator weight in the reports
    public static final boolean DATA_MUTATOR_IN_SEPARATE_JVM = Boolean
            .getBoolean("persinity.ndtcontrollermutatorst.datamutator.separate.jvm");

    @Before
    public void setUp() {
        final Properties props = loadPropsFrom(NDT_CONTROLLER_MUTATOR_IT_PROPS_FILE);
        final NdtControllerConfig config = new NdtControllerConfig(props, NDT_CONTROLLER_MUTATOR_IT_PROPS_FILE);

        ndtController = NdtController.createFromConfig(config);

        resource.accessAndClose(
                new Resource.Accessor<RelDb, Void>(ndtController.getRelDbPoolFactory().appBridge().src().get(), null) {
                    @Override
                    public Void access(final RelDb resource) throws Exception {
                        resource.executeScript("testapp-deinit.sql");
                        return null;
                    }
                });
        resource.accessAndClose(
                new Resource.Accessor<RelDb, Void>(ndtController.getRelDbPoolFactory().appBridge().dst().get(), null) {
                    @Override
                    public Void access(final RelDb resource) throws Exception {
                        resource.executeScript("testapp-deinit.sql");
                        return null;
                    }
                });

        initMutator();

        testNdtUtil = createTestNdtUtil(ndtController, TEST_NDT_UTIL_ALL);
    }

    @After
    public void tearDown() {
        testNdtUtil.close();

        if (ndtController != null) {
            ndtController.close();
            ndtController = null;
        }
    }

    /**
     * Testing with DataMutator
     */
    @Test
    public void test() {
        Metrics.getMetrics().reset();

        final StubNdtViewController view = ((StubNdtViewController) ndtController.getView());

        final Thread testThread = Thread.currentThread();
        final Thread ndtThread = new Thread() {
            @Override
            public void run() {
                setName("NdtController");
                try {
                    ndtController.run();
                } catch (RuntimeException e) {
                    log.error(e, "NdtController run failed");
                    System.err.println(format("NDT Failed!\nCause: {}", e.getMessage()));
                    testThread.interrupt();
                }
            }
        };

        startDataMutator();

        ndtThread.start();

        view.waitForPauseMsgAndReleaseIt("Confirm to setup NDT.");
        view.waitForPauseMsgAndReleaseIt("Confirm to start NDT Migrate.");

        view.waitForMsg("Confirm when initial transfer is completed.");

        testNdtUtil.verifyEmptyDestEntityData();

        testNdtUtil.doInitialCopy();

        view.waitForPauseMsgAndReleaseIt("Confirm when initial transfer is completed.");

        view.waitForMsg("Confirm when Source Application is stopped.");

        log.info("At rolling consistent done, rows migrated: {}, transformed: {}", RelMetrics.getMigrateRows(),
                RelMetrics.getTransformRows());

        view.waitForMsg("Migrating from Destination Staging");

        waitDataMutatorToFinish();

        final int rowsToTransfer = (int) Math.abs(RelMetrics.getMigrateRows() - RelMetrics.getTransformRows());
        int waitCompleteSeconds = rowsToTransfer * 3;
        if (waitCompleteSeconds < 60) {
            waitCompleteSeconds = 60;
        }
        log.info("Wait {} seconds to complete transfer", waitCompleteSeconds);
        testNdtUtil.waitForEmptyWindows(waitCompleteSeconds);

        view.waitForPauseMsgAndReleaseIt("Confirm when Source Application is stopped.");

        view.waitForMsg("Debug pause to check for consistency before uninstall to cleanup the clogs.");

        log.info("At test end, rows migrated: {}, transformed: {}", RelMetrics.getMigrateRows(),
                RelMetrics.getTransformRows());

        testNdtUtil.verifyDestEntityDataAfterDm();

        view.waitForPauseMsgAndReleaseIt("Debug pause to check for consistency before uninstall to cleanup the clogs.");
        view.waitForMsg("Migration completed");

        ndtController.close();
        ndtController = null;

        assertTrue(RelMetrics.getMigrateRows() >= RelMetrics.getTransformRows());

        assertTrue(RelMetrics.getTransformRows() > 0);
    }

    private void startDataMutator() {
        if (DATA_MUTATOR_IN_SEPARATE_JVM) {
            log.info("Starting DataMutator in separate JVM");
            srcDmProcess = startSecondJvm(DataMutator.class,
                    Arrays.asList("--config=data-mutator-it.properties", "--db-config=data-mutator-src-db.properties",
                            "--quite"));
            sleepSeconds(10);
        } else {
            log.info("Starting DataMutator");
            srcDm.start(false);
        }
    }

    private void waitDataMutatorToFinish() {
        log.info("Wait DataMutator to finish");
        if (srcDmProcess != null) {
            try {
                srcDmProcess.waitFor();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        } else {
            srcDm.waitToFinish();
        }
    }

    public static Process startSecondJvm(final Class mainClass, final List<String> args) {
        final String separator = System.getProperty("file.separator");
        final String classpath = System.getProperty("java.class.path");
        final String path = System.getProperty("java.home") + separator + "bin" + separator + "java";
        final List<String> allArgs = new ArrayList<>();
        allArgs.add(path);
        allArgs.add("-cp");
        allArgs.add(classpath);
        allArgs.add(mainClass.getName());
        allArgs.addAll(args);
        final ProcessBuilder processBuilder = new ProcessBuilder(allArgs);
        final Process process;
        try {
            process = processBuilder.start();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return process;
    }

    private void initMutator() {
        srcDm = new DataMutator("data-mutator-it.properties", "data-mutator-src-db.properties", true);
        final DataMutator dstDm = new DataMutator("data-mutator-it.properties", "data-mutator-dst-db.properties", true);

        srcDm.initSchema();
        srcDm.cleanupSchema();

        dstDm.initSchema();
        dstDm.cleanupSchema();
    }

    public static final String NDT_CONTROLLER_MUTATOR_IT_PROPS_FILE = "ndt-controller-mutator-st.properties";

    private NdtController ndtController;
    private TestNdtUtil testNdtUtil;
    private Process srcDmProcess;
    private DataMutator srcDm;
    private final Resource resource = new Resource();

    private static final Log4jLogger log = new Log4jLogger(Logger.getLogger(NdtControllerMutatorST.class));
}