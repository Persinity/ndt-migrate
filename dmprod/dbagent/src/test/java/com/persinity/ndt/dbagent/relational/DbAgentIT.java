/**
 * Copyright (c) 2015 Persinity Inc.
 */
package com.persinity.ndt.dbagent.relational;

import static com.persinity.common.Config.loadPropsFrom;

import java.util.LinkedList;
import java.util.List;
import java.util.Properties;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.google.common.base.Function;
import com.persinity.common.db.DbConfig;
import com.persinity.common.db.RelDb;
import com.persinity.common.db.SimpleRelDb;
import com.persinity.ndt.dbagent.CdcAgent;
import com.persinity.ndt.dbagent.ClogAgent;
import com.persinity.ndt.dbagent.DbAgentExecutor;
import com.persinity.ndt.dbagent.DbAgentFactory;
import com.persinity.ndt.dbagent.relational.oracle.OracleAgentSqlStrategy;
import com.pesinity.ndt.dbagent.testutil.TestDbAgent;

/**
 * Integration test for database agents.<BR>
 * Before use: Configure your own DB in ndt-integrationtest.properties and run admin.sql to create the test users.<BR>
 *
 * @author Doichin Yordanov
 */
public class DbAgentIT {

    @Before
    public void setUp() {
        final Properties props = loadPropsFrom(NDT_IT_PROPERTIES_FILE);
        ndtDb = new SimpleRelDb(new DbConfig(props, NDT_IT_PROPERTIES_FILE, "ndt."));
        testAppDb = new SimpleRelDb(new DbConfig(props, NDT_IT_PROPERTIES_FILE, "testapp."));

        // Create test bed schemas
        testAppDb.executeScript("testapp-init.sql");

        dbAgentTracker = new DbAgentTracker();
        final AgentSqlStrategy sqlStrategy = new OracleAgentSqlStrategy();
        dbAgentFactory = new DbAgentFactory<>(testAppDb.metaInfo(), new OracleAgentSqlStrategy(),
                dbAgentTracker);
        dbAgentExecutor = new DbAgentExecutor();
        agentUtils = new TestDbAgent();

        sqlBatch1.add("INSERT INTO dept (id, name) VALUES (1, 'SW')");
        sqlBatch1.add("INSERT INTO emp (id, name, dept_id) VALUES (1, 'Ivan', 1)");
        sqlBatch1.add("INSERT INTO emp (id, name, dept_id) VALUES (2, 'Doichin', 1)");
        sqlBatch1.add("INSERT INTO emp (id, name, dept_id) VALUES (3, 'Rosen', 1)");

        sqlBatch2.add("UPDATE emp SET name = 'Ivan Dachev' WHERE id = 1");
        sqlBatch2.add("UPDATE emp SET name = 'Doichin Yordanov' WHERE id = 2");
        sqlBatch2.add("DELETE FROM emp WHERE id = 3");
    }

    @After
    public void tearDown() {
        ndtDb.executeDmdl("DROP TABLE ndt_log");
        ndtDb.close();
        testAppDb.close();
    }

    /**
     * Test mount and then unmount of the agent.
     */
    @Test
    public void testMountUnmount() {
        final ClogAgent<Function<RelDb, RelDb>> clogAgent = dbAgentExecutor
                .clogAgentInstallMount(dbAgentFactory, ndtDb, false);
        agentUtils.verifyClogMounted(ndtDb);

        final CdcAgent<Function<RelDb, RelDb>> cdcAgent = dbAgentExecutor
                .cdcAgentInstallMount(dbAgentFactory, testAppDb, ndtDb);
        agentUtils.mutateDataAndVerifyCdcEnabled(testAppDb, ndtDb, sqlBatch1);
        agentUtils.mutateDataAndVerifyCdcEnabled(testAppDb, ndtDb, sqlBatch2);

        dbAgentExecutor.cdcAgentUnmount(cdcAgent, testAppDb);
        agentUtils.verifyCdcDisabled(testAppDb, ndtDb);

        dbAgentExecutor.clogAgentUnmount(clogAgent, ndtDb);
        agentUtils.verifyClogUnmounted(ndtDb);
    }

    @Test
    public void testFailSafeCdc() {
        final ClogAgent<Function<RelDb, RelDb>> clogAgent = dbAgentExecutor
                .clogAgentInstallMount(dbAgentFactory, ndtDb, false);
        agentUtils.verifyClogMounted(ndtDb);

        final CdcAgent<Function<RelDb, RelDb>> cdcAgent = dbAgentExecutor
                .cdcAgentInstallMount(dbAgentFactory, testAppDb, ndtDb);
        agentUtils.mutateDataAndVerifyCdcEnabled(testAppDb, ndtDb, sqlBatch1);

        ndtDb.executeDmdl(
                "CREATE OR REPLACE TRIGGER trg_clog_emp_fail_dml " + "AFTER INSERT OR UPDATE OR DELETE ON clog_emp "
                        + "BEGIN " + "  raise_application_error(-20101, 'Assert caught exception!');" + "END;");
        agentUtils.mutateData(testAppDb, sqlBatch2); // should not fail although the broken CDC.
        agentUtils.assertLog(ndtDb, TestDbAgent.LOG_LEVEL_ERROR, "Assert caught exception!");

        dbAgentExecutor.cdcAgentUnmount(cdcAgent, testAppDb);
        agentUtils.verifyCdcDisabled(testAppDb, ndtDb);

        dbAgentExecutor.clogAgentUnmount(clogAgent, ndtDb);
        agentUtils.verifyClogUnmounted(ndtDb);
    }

    public static final String NDT_IT_PROPERTIES_FILE = "ndt-integrationtest.properties";
    private RelDb ndtDb, testAppDb;
    private DbAgentTracker dbAgentTracker;
    private DbAgentFactory<Function<RelDb, RelDb>> dbAgentFactory;
    private DbAgentExecutor dbAgentExecutor;
    private TestDbAgent agentUtils;
    private final List<String> sqlBatch1 = new LinkedList<>();
    private final List<String> sqlBatch2 = new LinkedList<>();
}
