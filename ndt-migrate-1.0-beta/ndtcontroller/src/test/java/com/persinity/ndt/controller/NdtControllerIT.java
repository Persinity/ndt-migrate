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
import static com.persinity.ndt.controller.TestNdtControllerUtil.TEST_NDT_UTIL_ALL;
import static com.persinity.ndt.controller.TestNdtControllerUtil.TEST_NDT_UTIL_SRC_APP_ONLY;
import static com.persinity.ndt.controller.TestNdtControllerUtil.createTestNdtUtil;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Properties;
import java.util.Set;

import org.apache.log4j.Logger;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.persinity.common.collection.DirectedEdge;
import com.persinity.common.db.RelDb;
import com.persinity.common.db.RelDbUtil;
import com.persinity.common.db.metainfo.Schema;
import com.persinity.common.logging.Log4jLogger;
import com.persinity.common.metrics.Metrics;
import com.persinity.ndt.controller.impl.StubNdtViewController;
import com.persinity.ndt.dbagent.relational.AgentSqlStrategy;
import com.persinity.ndt.dbagent.relational.oracle.OracleAgentSqlStrategy;
import com.persinity.ndt.etlmodule.relational.common.RelMetrics;

/**
 * @author Ivan Dachev
 */
public class NdtControllerIT {

    @Before
    public void setUp() {
        final Properties props = loadPropsFrom(NDT_CONTROLLER_IT_PROPS_FILE);
        final NdtControllerConfig config = new NdtControllerConfig(props, NDT_CONTROLLER_IT_PROPS_FILE);

        ndtController = NdtController.createFromConfig(config);

        // use own connections in order to not mismatch the NdtController Schemas caches
        appDbs = RelDbUtil.getBridge(ndtController.getRelDbPoolFactory().appBridge());
        AgentSqlStrategy sqlStrategy = new OracleAgentSqlStrategy();
        initTestDb(appDbs.src(), appDbs.src().metaInfo(), ndtController.getSqlStrategy());
        initTestDb(appDbs.dst(), appDbs.dst().metaInfo(), ndtController.getSqlStrategy());
        appDbs.src().close();
        appDbs.dst().close();

        testNdtUtilA = createTestNdtUtil(ndtController, TEST_NDT_UTIL_ALL);
        testNdtUtilB = createTestNdtUtil(ndtController, TEST_NDT_UTIL_SRC_APP_ONLY);
        testNdtUtilC = createTestNdtUtil(ndtController, TEST_NDT_UTIL_SRC_APP_ONLY);
        testNdtUtilD = createTestNdtUtil(ndtController, TEST_NDT_UTIL_SRC_APP_ONLY);
    }

    @After
    public void tearDown() {
        testNdtUtilA.close();
        testNdtUtilB.close();
        testNdtUtilC.close();
        testNdtUtilD.close();

        if (ndtController != null) {
            RelDbUtil.closeBridge(ndtController.getRelDbPoolFactory().appBridge(), appDbs);
            ndtController.close();
            ndtController = null;
        }
    }

    /**
     * Testing with two transactions with FK cycle.
     */
    @Test
    public void testTwoTransTransactions() {
        final List<String> sqlBatch1 = new LinkedList<>();
        sqlBatch1.add("INSERT INTO dept (id, name) VALUES (1, 'Eng')");
        sqlBatch1.add("INSERT INTO emp (id, bin_id, name, dept_id) VALUES (1, utl_raw.cast_to_raw('Ivan'), 'Ivan', 1)");
        sqlBatch1
                .add("INSERT INTO emp (id, bin_id, name, dept_id) VALUES (2, utl_raw.cast_to_raw('Doichin'), 'Doichin', 1)");
        sqlBatch1.add("INSERT INTO emp (id, name, dept_id) VALUES (3, 'Rosen', 1)");
        sqlBatch1.add("INSERT INTO emp (id, name, dept_id) VALUES (4, 'Ivo Yanakiev', 1)");
        sqlBatch1.add("UPDATE dept SET mngr_id = 1 WHERE id = 1");
        sqlBatch1.add("INSERT INTO dept (id, name) VALUES (2, 'FreeLancer')");
        sqlBatch1.add("INSERT INTO emp (id, name, dept_id) VALUES (5, 'Bozhidar', 2)");
        sqlBatch1.add("INSERT INTO emp (id, name, dept_id) VALUES (6, 'Ralitza', 2)");
        sqlBatch1.add("UPDATE dept SET mngr_id = 5 WHERE id = 2");
        sqlBatch1.add("INSERT INTO kid (id, sid, name, emp_id) VALUES (1, 's4', 'DoichinKid', 2)");
        sqlBatch1.add("INSERT INTO kid (id, sid, name, emp_id) VALUES (1, 's6', 'IvoKids6', 4)");
        sqlBatch1.add("INSERT INTO kid (id, sid, name, emp_id) VALUES (1, 's3', 'IvoKids2', 4)");

        final List<String> sqlBatch2 = new LinkedList<>();
        sqlBatch2.add("UPDATE dept SET mngr_id = NULL WHERE mngr_id = 5");
        sqlBatch2.add("DELETE FROM emp WHERE id = 5");
        sqlBatch2.add("DELETE FROM emp WHERE id = 6");
        sqlBatch2.add("DELETE FROM dept WHERE id = 2");
        sqlBatch2.add("UPDATE emp SET name = 'Ivan Dachev', bin_id = utl_raw.cast_to_raw('Ivan Dachev') WHERE id = 1");
        sqlBatch2
                .add("UPDATE emp SET name = 'Doichin Yordanov', bin_id = utl_raw.cast_to_raw('Doichin Yordanov') WHERE id = 2");
        sqlBatch2.add("UPDATE kid SET name = 'Mia Yordanova' WHERE id = 1 AND sid = 's4'");
        sqlBatch2.add("UPDATE dept SET mngr_id = 4 WHERE id = 1");
        sqlBatch2.add("DELETE FROM emp WHERE id = 3");
        sqlBatch2.add("INSERT INTO dept (id, name) VALUES (3, 'Sales')");
        sqlBatch2.add("INSERT INTO emp (id, name, dept_id) VALUES (10, 'Vladi Goranov', 3)");
        sqlBatch2.add("INSERT INTO emp (id, name, dept_id) VALUES (11, 'Boyko Asenov', 3)");
        sqlBatch2.add("INSERT INTO kid (id, sid, name, emp_id) VALUES (2, 's54', 'VladiKid', 10)");
        sqlBatch2.add("INSERT INTO kid (id, sid, name, emp_id) VALUES (4, 's54', 'BoykoKid', 11)");
        sqlBatch2.add("DELETE FROM kid WHERE (id = 1) AND (sid = 's6')");

        final List<List<String>> sqlBatches = Arrays.asList(sqlBatch1, sqlBatch2);
        execute(SQL_BATCHES_EMPTY, SQL_BATCHES_EMPTY, SQL_BATCHES_EMPTY, sqlBatches);
    }

    /**
     * Testing with two initial and two ongoing transactions.
     */
    @Test
    public void testTwoInitialTwoOngoingTransactions() {
        final List<String> sqlBatchInit1 = new LinkedList<>();
        sqlBatchInit1.add("INSERT INTO dept (id, name) VALUES (1, 'SW')");
        sqlBatchInit1.add("INSERT INTO emp (id, name, dept_id) VALUES (1, 'Ivan', 1)");
        sqlBatchInit1.add("INSERT INTO emp (id, name, dept_id) VALUES (2, 'Doichin', 1)");
        sqlBatchInit1.add("INSERT INTO emp (id, name, dept_id) VALUES (3, 'Rosen', 1)");

        final List<String> sqlBatchInit2 = new LinkedList<>();
        sqlBatchInit2.add("UPDATE emp SET name = 'Ivan Dachev' WHERE id = 1");
        sqlBatchInit2.add("UPDATE emp SET name = 'Doichin Yordanov' WHERE id = 2");
        sqlBatchInit2.add("DELETE FROM emp WHERE id = 3");

        final List<String> sqlBatchParallel1 = new LinkedList<>();
        sqlBatchParallel1.add("INSERT INTO dept (id, name) VALUES (100, 'SW100')");
        sqlBatchParallel1.add("INSERT INTO emp (id, name, dept_id) VALUES (101, 'Ivan101', 100)");
        sqlBatchParallel1.add("INSERT INTO emp (id, name, dept_id) VALUES (102, 'Doichin102', 1)");
        sqlBatchParallel1.add("DELETE FROM emp WHERE id = 102");
        sqlBatchParallel1.add("INSERT INTO emp (id, name, dept_id) VALUES (103, 'Rosen103', 1)");

        final List<String> sqlBatchParallel2 = new LinkedList<>();
        sqlBatchParallel2.add("UPDATE emp SET name = 'Ivan Dachev' WHERE id = 1");
        sqlBatchParallel2.add("UPDATE emp SET name = 'Doichin Yordanov' WHERE id = 2");
        sqlBatchParallel2.add("DELETE FROM emp WHERE id = 3");

        final List<List<String>> sqlBatchesInitial = Arrays.asList(sqlBatchInit1, sqlBatchInit2);
        final List<List<String>> sqlBatchesParallel = Arrays.asList(sqlBatchParallel1, sqlBatchParallel2);
        execute(sqlBatchesInitial, SQL_BATCHES_EMPTY, SQL_BATCHES_EMPTY, sqlBatchesParallel);
    }

    /**
     * Tests multiple transactions spanning several ETL windows that are executed in parallel.
     */
    @Test
    public void testMultipleTransactions() {
        final List<String> sqlBatch1 = new LinkedList<>();
        sqlBatch1.add("INSERT INTO dept (id, name) VALUES (1, 'SW')");
        sqlBatch1.add("INSERT INTO emp (id, name, dept_id) VALUES (1, 'Ivan', 1)");
        sqlBatch1.add("INSERT INTO emp (id, name, dept_id) VALUES (2, 'Doichin', 1)");
        sqlBatch1.add("INSERT INTO emp (id, name, dept_id) VALUES (3, 'Rosen', 1)");

        final List<String> sqlBatch2 = new LinkedList<>();
        sqlBatch2.add("UPDATE emp SET name = 'Ivan Dachev' WHERE id = 1");
        sqlBatch2.add("UPDATE emp SET name = 'Doichin Yordanov' WHERE id = 2");
        sqlBatch2.add("DELETE FROM emp WHERE id = 3");
        sqlBatch2.add("INSERT INTO kid (id, sid, name, emp_id) VALUES (1, 's7', 'IvanKid', 1)");

        ArrayList<List<String>> sqlBatchesInitial = new ArrayList<>();
        sqlBatchesInitial.add(sqlBatch1);
        sqlBatchesInitial.add(sqlBatch2);

        final int batchesCount = ndtController.getConfig().getMigrateWindowSize() * 2;

        ArrayList<List<String>> sqlBatchesParallelPreNdtSetup = createSqlBatches(batchesCount, 1000);

        ArrayList<List<String>> sqlBatchesParallelPostNdtSetup = createSqlBatches(batchesCount, 2000);

        ArrayList<List<String>> sqlBatchesParallelPostTransformBegin = createSqlBatches(batchesCount, 3000);

        execute(sqlBatchesInitial, sqlBatchesParallelPreNdtSetup, sqlBatchesParallelPostNdtSetup,
                sqlBatchesParallelPostTransformBegin);
    }

    private ArrayList<List<String>> createSqlBatches(final int batchesCount, final int beginId) {
        final ArrayList<List<String>> sqlBatches = new ArrayList<>();
        int j = 0;
        int insDelId = beginId + 10000;
        for (int i = 0; i < batchesCount; i++) {
            final List<String> sqlBatch = new LinkedList<>();

            if (j == 1) {
                sqlBatch.add(format("INSERT INTO emp (id, name, dept_id) VALUES ({}, 'Petkan', 1)", insDelId));
                sqlBatch.add(format("INSERT INTO kid (id, sid, name, emp_id) VALUES ({}, 's7', 'IvanKid', 1)",
                        insDelId + 1));
            } else if (j == 2) {
                sqlBatch.add(format("DELETE FROM emp WHERE id = {}", insDelId));
                sqlBatch.add(format("DELETE FROM kid WHERE (id = {}) AND (sid = 's7')", insDelId + 1));
            } else if (j == 3) {
                j = 0;
                insDelId += 10;
            }
            j++;
            sqlBatch.add(format("UPDATE emp SET name = 'Ivan {}' WHERE id = 1", beginId + i));
            sqlBatch.add(format("UPDATE emp SET name = 'Doichin {}' WHERE id = 2", beginId + i));
            sqlBatch.add(format("UPDATE kid SET name = 'IvanKid {}' WHERE (id = 1) AND (sid = 's7')", beginId + i));
            sqlBatches.add(sqlBatch);
        }

        return sqlBatches;
    }

    /**
     * Tests one ETL transfer over no accumulated data
     */
    @Test
    public void testOnceWithZeroTrans() {
        execute(SQL_BATCHES_EMPTY, SQL_BATCHES_EMPTY, SQL_BATCHES_EMPTY, SQL_BATCHES_EMPTY);
    }

    /**
     * Executes the NDT migrate and loads source DB data in 4 different phases:
     * <ul>
     * <li>Loaded as initial DB data - transactions are waited to be populated before test continues.</li>
     * <li>Started pre NDT setup - transactions are executed in parallel.</li>
     * <li>Started post NDT setup - transactions are executed in parallel.</li>
     * <li>Started post NDT transform begin - transactions are executed in parallel.</li>
     * </ul>
     *
     * @param sqlBatchesInitial
     * @param sqlBatchesParallelPreNdtSetup
     * @param sqlBatchesParallelPostNdtSetup
     * @param sqlBatchesParallelPostTransformBegin
     */
    public void execute(final List<List<String>> sqlBatchesInitial,
            final List<List<String>> sqlBatchesParallelPreNdtSetup,
            final List<List<String>> sqlBatchesParallelPostNdtSetup,
            final List<List<String>> sqlBatchesParallelPostTransformBegin) {

        Metrics.getMetrics().reset();

        final StubNdtViewController view = ((StubNdtViewController) ndtController.getView());

        final Thread testThread = Thread.currentThread();
        final Thread runTh = new Thread() {
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

        if (sqlBatchesInitial.size() > 0) {
            testNdtUtilA.mutateSrcData(sqlBatchesInitial);
        }

        final TestMutateInParallel thFromStart = new TestMutateInParallel(testNdtUtilB, sqlBatchesParallelPreNdtSetup);
        final TestMutateInParallel thFromTriggers = new TestMutateInParallel(testNdtUtilC,
                sqlBatchesParallelPostNdtSetup);
        final TestMutateInParallel thFromTransform = new TestMutateInParallel(testNdtUtilD,
                sqlBatchesParallelPostTransformBegin);

        thFromStart.start();

        runTh.start();

        view.waitForPauseMsgAndReleaseIt("Confirm to setup NDT.");
        view.waitForPauseMsgAndReleaseIt("Confirm to start NDT Migrate.");

        thFromTriggers.start();

        view.waitForMsg("Confirm when initial transfer is completed.");

        testNdtUtilA.verifyEmptyDestEntityData();

        testNdtUtilA.doInitialCopy();

        view.waitForPauseMsgAndReleaseIt("Confirm when initial transfer is completed.");

        view.waitForMsg("Confirm when Source Application is stopped.");

        log.info("At rolling consistent done, rows migrated: {}, transformed: {}", RelMetrics.getMigrateRows(),
                RelMetrics.getTransformRows());

        view.waitForMsg("Migrating from Destination Staging");

        thFromTransform.start();

        thFromStart.waitDone();
        thFromTriggers.waitDone();
        thFromTransform.waitDone();

        testNdtUtilA.waitForEmptyWindows(ndtController.getConfig().getEtlWindowCheckIntervalSeconds() * 10);

        view.waitForPauseMsgAndReleaseIt("Confirm when Source Application is stopped.");

        view.waitForMsg("Debug pause to check for consistency before uninstall to cleanup the clogs.");

        log.info("At test end, rows migrated: {}, transformed: {}", RelMetrics.getMigrateRows(),
                RelMetrics.getTransformRows());

        testNdtUtilA.verifyDestEntityDataAfterDm();

        view.waitForPauseMsgAndReleaseIt("Debug pause to check for consistency before uninstall to cleanup the clogs.");
        view.waitForMsg("Migration completed");

        ndtController.close();
        ndtController = null;

        assertTrue(RelMetrics.getMigrateRows() >= RelMetrics.getTransformRows());

        if (sqlBatchesParallelPostNdtSetup.size() > 0 || sqlBatchesParallelPostTransformBegin.size() > 0) {
            // if there are sql batches after our triggers are up and running
            // then the transform rows should be > 0
            assertTrue(RelMetrics.getTransformRows() > 0);
        }
    }

    private void initTestDb(final RelDb db, final Schema schema, final AgentSqlStrategy sqlStrategy) {
        db.executeScript("testapp-init.sql");

        // cleanup DataMutator tables brute force
        final Set<String> tableNames = schema.getTableNames();
        for (int i = 0; i < tableNames.size(); i++) {
            for (final String tableName : tableNames) {
                if (tableName.toLowerCase().startsWith("dm_")) {
                    try {
                        db.executeDmdl(sqlStrategy.dropTable(tableName));
                    } catch (RuntimeException e) {
                        // silent
                    }
                }
            }
        }

        db.commit();
    }

    public static final String NDT_CONTROLLER_IT_PROPS_FILE = "ndt-controller-it.properties";

    private NdtController ndtController;
    private TestNdtUtil testNdtUtilA;
    private TestNdtUtil testNdtUtilB;
    private TestNdtUtil testNdtUtilC;
    private TestNdtUtil testNdtUtilD;

    private static final List<List<String>> SQL_BATCHES_EMPTY = Collections.emptyList();

    private static final Log4jLogger log = new Log4jLogger(Logger.getLogger(NdtControllerMutatorST.class));
    private DirectedEdge<RelDb, RelDb> appDbs;
}