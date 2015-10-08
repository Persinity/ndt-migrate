/**
 * Copyright (c) 2015 Persinity Inc.
 */
package com.persinity.ndt.etlmodule.relational.common;

import static com.persinity.common.ThreadUtil.sleepSeconds;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Future;

import org.apache.log4j.Logger;
import org.junit.Assert;

import com.google.common.base.Function;
import com.persinity.common.collection.DirectedEdge;
import com.persinity.common.collection.Pool;
import com.persinity.common.db.RelDb;
import com.persinity.common.db.RelDbUtil;
import com.persinity.common.metrics.Metrics;
import com.persinity.haka.Job;
import com.persinity.haka.JobIdentity;
import com.persinity.ndt.dbagent.CdcAgent;
import com.persinity.ndt.dbagent.ClogAgent;
import com.persinity.ndt.dbagent.DbAgentExecutor;
import com.persinity.ndt.dbagent.DbAgentFactory;
import com.persinity.ndt.dbagent.relational.AgentSqlStrategy;
import com.persinity.ndt.dbagent.relational.DbAgentTracker;
import com.persinity.ndt.dbagent.relational.oracle.OracleAgentSqlStrategy;
import com.persinity.ndt.etlmodule.EtlPlanGenerator;
import com.persinity.ndt.etlmodule.WindowGenerator;
import com.persinity.ndt.etlmodule.haka.GcJob;
import com.persinity.ndt.etlmodule.haka.HakaEtlPlanExecutor;
import com.persinity.ndt.etlmodule.serial.SerialEtlPlanExecutor;
import com.pesinity.ndt.dbagent.testutil.TestDbAgent;

/**
 * Utils for testing Data Motion
 *
 * @author Doichin Yordanov
 */
public class TestDm {

    public static final int HAKA_EXECUTION_TIMEOUT_S = 60;
    public static final int WINDOW_CHECK_INTERVAL_S = 2;

    public TestDm(final DirectedEdge<Pool<RelDb>, Pool<RelDb>> appPoolBridge,
            final DirectedEdge<Pool<RelDb>, Pool<RelDb>> ndtPoolBridge) {
        this.appPoolBridge = appPoolBridge;
        this.ndtPoolBridge = ndtPoolBridge;
        ndtBridge = RelDbUtil.getBridge(ndtPoolBridge);
        appBridge = RelDbUtil.getBridge(appPoolBridge);
        dbAgentExecutor = new DbAgentExecutor();
        agentUtils = new TestDbAgent();
        dbAgentTracker = new DbAgentTracker();
        final AgentSqlStrategy sqlStrategy = new OracleAgentSqlStrategy();
        srcDrivenDbAgentFactory = new DbAgentFactory<>(appBridge.src().metaInfo(), sqlStrategy, dbAgentTracker);

        hakaEtlPlanExecutor = new HakaEtlPlanExecutor("haka-etl-it", "haka-etl-it.conf", HAKA_EXECUTION_TIMEOUT_S,
                WINDOW_CHECK_INTERVAL_S);
        serialEtlPlanExecutor = new SerialEtlPlanExecutor(WINDOW_CHECK_INTERVAL_S);
    }

    public void installAndEnableNDT() {
        final RelDb srcNdtDb = ndtBridge.src();
        final RelDb dstNdtDb = ndtBridge.dst();
        final RelDb srcAppDb = appBridge.src();

        log.debug("Installing NDT at " + srcAppDb + ", " + ndtBridge.src() + ", " + ndtBridge.dst());
        srcClogAgent = dbAgentExecutor.clogAgentInstallMount(srcDrivenDbAgentFactory, srcNdtDb);
        agentUtils.verifyClogMounted(srcNdtDb);
        dstClogAgent = dbAgentExecutor.clogAgentInstallMount(srcDrivenDbAgentFactory, dstNdtDb);
        agentUtils.verifyClogMounted(dstNdtDb);
        cdcAgent = dbAgentExecutor.cdcAgentInstallMount(srcDrivenDbAgentFactory, srcAppDb, srcNdtDb);
        agentUtils.deleteTrlogs(srcDrivenDbAgentFactory, srcNdtDb);
        agentUtils.verifyEmptyTrlog(srcNdtDb);
    }

    public void mutateSrcDataVerify(final List<List<String>> sqlBatches) {
        final RelDb srcNdtDb = ndtBridge.src();
        final RelDb srcAppDb = appBridge.src();

        final int maxItemToVerifyCdc = 10;
        int i = 0;
        for (final List<String> batch : sqlBatches) {
            i++;
            if (i < maxItemToVerifyCdc) {
                agentUtils.mutateDataAndVerifyCdcEnabled(srcAppDb, srcNdtDb, batch);
            } else {
                agentUtils.mutateData(srcAppDb, batch);
            }
        }
    }

    public void mutateSrcData(final List<List<String>> sqlBatches) {
        final RelDb dstAppDb = appBridge.src();
        mutateData(sqlBatches, dstAppDb);
    }

    public void mutateDstData(final List<List<String>> sqlBatches) {
        final RelDb dstAppDb = appBridge.dst();
        mutateData(sqlBatches, dstAppDb);
    }

    public void executeClogGcHaka() {
        executeClogGcHakaOn(ndtPoolBridge.src(), srcClogAgent);
        executeClogGcHakaOn(ndtPoolBridge.dst(), dstClogAgent);
    }

    public void executeClogGcSerial() {
        final RelDb srcNdtDb = ndtBridge.src();
        log.debug("Execute clog GC on " + srcNdtDb);
        dbAgentExecutor.clogAgentGc(srcClogAgent, srcNdtDb);
        final RelDb dstNdtDb = ndtBridge.dst();
        log.debug("Execute clog GC on " + dstNdtDb);
        dbAgentExecutor.clogAgentGc(dstClogAgent, dstNdtDb);
    }

    public void disableAndDeinstallNdt() {
        final RelDb srcNdtDb = ndtBridge.src();
        final RelDb dstNdtDb = ndtBridge.dst();
        final RelDb srcAppDb = appBridge.src();

        log.debug("Deinstalling NDT from " + srcAppDb + ", " + srcNdtDb + ", " + dstNdtDb);

        dbAgentExecutor.cdcAgentUnmount(cdcAgent, srcAppDb);
        agentUtils.verifyCdcDisabled(srcAppDb, srcNdtDb);
        dbAgentExecutor.clogAgentUnmount(srcClogAgent, srcNdtDb);
        agentUtils.verifyClogUnmounted(srcNdtDb);
        dbAgentExecutor.clogAgentUnmount(dstClogAgent, dstNdtDb);
        agentUtils.verifyClogUnmounted(dstNdtDb);
    }

    public static void compareRs(final RelDb db1, final RelDb db2, final String srcQry, final String dstQry) {
        final Iterator<Map<String, Object>> srcRecs = db1.executeQuery(srcQry);
        final Iterator<Map<String, Object>> dstRecs = db2.executeQuery(dstQry);
        while (srcRecs.hasNext()) {
            final Map<String, Object> srcRec = srcRecs.next();
            assertTrue(dstRecs.hasNext());
            final Map<String, Object> dstRec = dstRecs.next();
            Assert.assertEquals(srcRec, dstRec);
        }
        assertFalse(dstRecs.hasNext());
    }

    public static void assertEmptyRs(final RelDb db, final String qry) {
        final Iterator<Map<String, Object>> recs = db.executeQuery(qry);
        assertFalse(recs.hasNext());
    }

    public static void assertNotEmptyRs(final RelDb db, final String qry) {
        final Iterator<Map<String, Object>> recs = db.executeQuery(qry);
        assertTrue(recs.hasNext());
    }

    public void cleanup() {
        metricsReset();
        srcClogAgent = null;
        dstClogAgent = null;
        cdcAgent = null;
    }

    public void metricsReset() {
        Metrics.getMetrics().reset();
    }

    public void metricsReport() {
        Metrics.getMetrics().report();
    }

    public void close() {
        hakaEtlPlanExecutor.close();
        serialEtlPlanExecutor.close();
        RelDbUtil.closeBridge(appPoolBridge, appBridge);
        RelDbUtil.closeBridge(ndtPoolBridge, ndtBridge);
    }

    public void serialHop(final WindowGenerator<RelDb, RelDb> winGen, final EtlPlanGenerator<RelDb, RelDb> etlPlanner) {
        scheduleWindowGenStop(winGen);
        serialEtlPlanExecutor.execute(winGen, etlPlanner, "serial");
    }

    public void hakaHop(final WindowGenerator<RelDb, RelDb> winGen, final EtlPlanGenerator<RelDb, RelDb> etlPlanner) {
        scheduleWindowGenStop(winGen);
        hakaEtlPlanExecutor.execute(winGen, etlPlanner, "haka");
    }

    public ClogAgent<Function<RelDb, RelDb>> getSrcClogAgent() {
        return srcClogAgent;
    }

    public ClogAgent<Function<RelDb, RelDb>> getDstClogAgent() {
        return dstClogAgent;
    }

    private void scheduleWindowGenStop(final WindowGenerator<RelDb, RelDb> winGen) {
        // simulate user stop after a while
        final Thread stopThread = new Thread() {
            @Override
            public void run() {
                sleepSeconds(5);
                winGen.stopWhenFeedExhausted();
            }
        };
        stopThread.start();
    }

    private void executeHakaJob(Job job, final long timeoutMs) {
        final Future<Job> future = hakaEtlPlanExecutor.getHakaExecutor().executeJob(job, timeoutMs);
        log.debug("Executing haka job " + job);
        try {
            job = future.get();
        } catch (final Exception e) {
            throw new RuntimeException(e);
        }
    }

    private void executeClogGcHakaOn(final Pool<RelDb> dbPool, final ClogAgent<Function<RelDb, RelDb>> agent) {
        log.debug("Execute clog GC on " + dbPool);
        final GcJob gcJob = new GcJob(new JobIdentity(), agent.clogGc(), dbPool, GC_CLOG_BULK_SIZE);
        executeHakaJob(gcJob, HAKA_EXECUTION_TIMEOUT_S * 1000);
    }

    private void mutateData(final List<List<String>> sqlBatches, final RelDb dstAppDb) {
        for (List<String> batch : sqlBatches) {
            agentUtils.mutateData(dstAppDb, batch);
        }
    }

    private static final int GC_CLOG_BULK_SIZE = 2;
    private static final Logger log = Logger.getLogger(TestDm.class);

    private final HakaEtlPlanExecutor hakaEtlPlanExecutor;
    private final SerialEtlPlanExecutor serialEtlPlanExecutor;
    private final DbAgentFactory<Function<RelDb, RelDb>> srcDrivenDbAgentFactory;
    private final DbAgentTracker dbAgentTracker;
    private final DirectedEdge<Pool<RelDb>, Pool<RelDb>> appPoolBridge;
    private ClogAgent<Function<RelDb, RelDb>> srcClogAgent;
    private ClogAgent<Function<RelDb, RelDb>> dstClogAgent;
    private final DbAgentExecutor dbAgentExecutor;
    private final TestDbAgent agentUtils;
    private final DirectedEdge<RelDb, RelDb> ndtBridge;
    private final DirectedEdge<Pool<RelDb>, Pool<RelDb>> ndtPoolBridge;
    private CdcAgent<Function<RelDb, RelDb>> cdcAgent;
    private final DirectedEdge<RelDb, RelDb> appBridge;
}
