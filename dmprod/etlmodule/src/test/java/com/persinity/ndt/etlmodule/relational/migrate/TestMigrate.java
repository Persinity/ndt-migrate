/**
 * Copyright (c) 2015 Persinity Inc.
 */
package com.persinity.ndt.etlmodule.relational.migrate;

import static com.persinity.common.StringUtils.format;
import static org.hamcrest.CoreMatchers.hasItem;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.apache.log4j.Logger;

import com.persinity.common.collection.DirectedEdge;
import com.persinity.common.collection.Pool;
import com.persinity.common.db.RelDb;
import com.persinity.common.db.RelDbUtil;
import com.persinity.common.db.Trimmer;
import com.persinity.ndt.db.RelDbPoolFactory;
import com.persinity.ndt.dbagent.relational.AgentSqlStrategy;
import com.persinity.ndt.dbagent.relational.SchemaInfo;
import com.persinity.ndt.dbagent.relational.oracle.OracleAgentSqlStrategy;
import com.persinity.ndt.dbagent.relational.oracle.OracleSchemaInfo;
import com.persinity.ndt.dbdiff.MigrateSchemaDiffGenerator;
import com.persinity.ndt.dbdiff.SchemaDiffGenerator;
import com.persinity.ndt.dbdiff.TransformEntity;
import com.persinity.ndt.etlmodule.EtlPlanGenerator;
import com.persinity.ndt.etlmodule.WindowGenerator;
import com.persinity.ndt.etlmodule.relational.TransformInfo;
import com.persinity.ndt.etlmodule.relational.common.RelTransformMapFactory;
import com.persinity.ndt.etlmodule.relational.common.TestDm;
import com.persinity.ndt.etlmodule.relational.common.TransformMapFactory;

/**
 * Utils for testing migration.
 *
 * @author Doichin Yordanov
 */
public class TestMigrate {

    private final DirectedEdge<SchemaInfo, SchemaInfo> appSchemas;

    private final TestDm testDm;
    private final DirectedEdge<Pool<RelDb>, Pool<RelDb>> appPoolBridge;
    private final DirectedEdge<Pool<RelDb>, Pool<RelDb>> ndtPoolBridge;
    private WindowGenerator<RelDb, RelDb> migrateWinGen;
    private EtlPlanGenerator<RelDb, RelDb> migratePlanner;
    private Map<BigDecimal, Map<String, Object>> trlogCheckData;
    private final AgentSqlStrategy sqlStrategy;
    private final DirectedEdge<RelDb, RelDb> appBridge;
    private final DirectedEdge<RelDb, RelDb> ndtBridge;
    private final DirectedEdge<SchemaInfo, SchemaInfo> ndtSchemas;
    private final int windowSize;

    private static final Logger log = Logger.getLogger(TestMigrate.class);

    public TestMigrate(final RelDbPoolFactory relDbPoolFactory, final int windowSize) {
        this.appPoolBridge = relDbPoolFactory.appBridge();
        this.ndtPoolBridge = relDbPoolFactory.ndtBridge();
        appBridge = RelDbUtil.getBridge(appPoolBridge);
        ndtBridge = RelDbUtil.getBridge(ndtPoolBridge);
        this.windowSize = windowSize;

        sqlStrategy = new OracleAgentSqlStrategy();
        final SchemaInfo srcNdtMetaInfo = new OracleSchemaInfo(ndtBridge.src().metaInfo(), new Trimmer(),
                sqlStrategy.getMaxNameLength());
        final SchemaInfo dstNdtMetaInfo = new OracleSchemaInfo(ndtBridge.dst().metaInfo(), new Trimmer(),
                sqlStrategy.getMaxNameLength());
        ndtSchemas = new DirectedEdge<>(srcNdtMetaInfo, dstNdtMetaInfo);

        final SchemaInfo srcAppMetaInfo = new OracleSchemaInfo(appBridge.src().metaInfo(), new Trimmer(),
                sqlStrategy.getMaxNameLength());
        final SchemaInfo dstAppMetaInfo = new OracleSchemaInfo(appBridge.dst().metaInfo(), new Trimmer(),
                sqlStrategy.getMaxNameLength());
        appSchemas = new DirectedEdge<>(srcAppMetaInfo, dstAppMetaInfo);
        testDm = new TestDm(appPoolBridge, ndtPoolBridge);
    }

    public TestDm getTestDm() {
        return testDm;
    }

    public void initMigrateEngine(final int etlInstructionSize, boolean doCoalesce) {
        if (migrateWinGen != null) {
            migrateWinGen.forceStop();
        }

        // TODO: when the connections must be closed?
        final Pool<RelDb> srcNdtDb = ndtPoolBridge.src();
        final Pool<RelDb> dstNdtDb = ndtPoolBridge.dst();

        final DirectedEdge<Pool<RelDb>, Pool<RelDb>> migrationPath = new DirectedEdge<>(srcNdtDb, dstNdtDb);
        migrateWinGen = getMigrationWinGen(migrationPath);
        log.debug("Using " + migrateWinGen + " over DM path " + migrationPath);
        Map<String, TransformInfo> transformInfoMap = null;
        transformInfoMap = getMigrateMap(appSchemas, ndtSchemas, sqlStrategy, doCoalesce);
        migratePlanner = getMigrationPlanner(transformInfoMap, etlInstructionSize);
        log.debug("Using " + migratePlanner + " over transformation map " + transformInfoMap);
    }

    public void verifyTrlogDataBeforeMigrate() {
        final String srcQry = format("SELECT * FROM {}", SchemaInfo.TAB_TRLOG);
        final String dstQry = format("SELECT * FROM {}", SchemaInfo.TAB_TRLOG);
        final RelDb srcNdtDb = ndtBridge.src();
        final RelDb dstNdtDb = ndtBridge.dst();
        final Iterator<Map<String, Object>> srcTrlog = srcNdtDb.executeQuery(srcQry);
        trlogCheckData = new HashMap<>();
        if (srcTrlog.hasNext()) {
            while (srcTrlog.hasNext()) {
                final Map<String, Object> res = srcTrlog.next();
                trlogCheckData.put((BigDecimal) res.get(SchemaInfo.COL_LAST_GID), res);
            }
            assertTrue(trlogCheckData.size() > 0);
        }
        final Iterator<Map<String, Object>> dstTrlog = dstNdtDb.executeQuery(dstQry);
        assertFalse(dstTrlog.hasNext());
    }

    public void cleanup() {
        migrateWinGen = null;
        migratePlanner = null;
        trlogCheckData = null;
    }

    public void close() {
        testDm.close();
        RelDbUtil.closeBridge(ndtPoolBridge, ndtBridge);
        RelDbUtil.closeBridge(appPoolBridge, appBridge);
    }

    public void migrateSerial() {
        testDm.serialHop(migrateWinGen, migratePlanner);
    }

    public void migrateHaka() {
        testDm.hakaHop(migrateWinGen, migratePlanner);
    }

    public void verifyCclogDataAfterMigrate() {
        final RelDb srcAppDb = appBridge.src();
        final RelDb dstNdtDb = ndtBridge.dst();
        // Check that the migrated records match the source
        String srcQry = "SELECT id, name FROM dept ORDER BY id, name";
        String dstQry =
                "SELECT DISTINCT id, name FROM (SELECT * FROM clog_dept WHERE ctype IN ('U', 'I') ORDER BY gid DESC) "
                        + "WHERE ROWNUM <= (SELECT COUNT(DISTINCT id) FROM clog_dept WHERE id NOT IN (SELECT id FROM clog_dept WHERE ctype = 'D')) ORDER BY id, name";
        TestDm.compareRs(srcAppDb, dstNdtDb, srcQry, dstQry);

        // the magic here is to select from clog only the latest updated values and compare them to the source entity
        srcQry = "SELECT id, name, dept_id FROM emp ORDER BY id, name, dept_id";
        dstQry =
                "SELECT DISTINCT id, name, dept_id FROM (SELECT * FROM clog_emp WHERE ctype IN ('U', 'I') ORDER BY gid DESC) "
                        + "WHERE ROWNUM <= (SELECT COUNT(DISTINCT id) FROM clog_emp WHERE id NOT IN (SELECT id FROM clog_emp WHERE ctype = 'D')) ORDER BY id, name, dept_id";

        TestDm.compareRs(srcAppDb, dstNdtDb, srcQry, dstQry);

        srcQry = "SELECT id, sid, name, emp_id FROM kid ORDER BY id, sid, name, emp_id";
        dstQry =
                "SELECT DISTINCT id, sid, name, emp_id FROM (SELECT * FROM clog_kid WHERE ctype IN ('U', 'I') ORDER BY gid DESC) "
                        + "WHERE ROWNUM <= (SELECT COUNT(*) FROM (SELECT DISTINCT id, sid FROM clog_kid WHERE id NOT IN (SELECT id FROM clog_kid WHERE ctype = 'D'))) ORDER BY id, sid, name, emp_id";

        TestDm.compareRs(srcAppDb, dstNdtDb, srcQry, dstQry);
    }

    public void verifyTrlogDataAfterMigrate() {
        final RelDb srcNdtDb = ndtBridge.src();
        final RelDb dstNdtDb = ndtBridge.dst();
        final String srcQry = format("SELECT * FROM {}", SchemaInfo.TAB_TRLOG);
        final String dstQry = format("SELECT * FROM {}", SchemaInfo.TAB_TRLOG);
        final Iterator<Map<String, Object>> srcTrlog = srcNdtDb.executeQuery(srcQry);
        assertFalse(srcTrlog.hasNext());
        final Iterator<Map<String, Object>> dstTrlog = dstNdtDb.executeQuery(dstQry);
        if (trlogCheckData.size() > 0) {
            assertTrue(dstTrlog.hasNext());
            int i = 0;
            while (dstTrlog.hasNext()) {
                i += 1;
                final Map<String, Object> res = dstTrlog.next();
                final BigDecimal last_gid = (BigDecimal) res.get(SchemaInfo.COL_LAST_GID);
                assertThat(trlogCheckData.keySet(), hasItem(last_gid));
                final Map<String, Object> expected = trlogCheckData.get(last_gid);
                expected.put(SchemaInfo.COL_STATUS, SchemaInfo.TrlogStatusType.R.toString());
                assertThat(expected, is(res));
            }
            assertEquals(i, trlogCheckData.size());
        } else {
            assertFalse(dstTrlog.hasNext());
        }
    }

    private WindowGenerator<RelDb, RelDb> getMigrationWinGen(
            final DirectedEdge<Pool<RelDb>, Pool<RelDb>> migrationPath) {
        final AgentSqlStrategy sqlStrategy = new OracleAgentSqlStrategy();
        final WindowGenerator<RelDb, RelDb> result = new MigrateWindowGenerator(migrationPath, sqlStrategy, windowSize);
        return result;
    }

    private EtlPlanGenerator<RelDb, RelDb> getMigrationPlanner(final Map<String, TransformInfo> transformMap,
            final int etlInstuctionSize) {

        final AgentSqlStrategy sqlStrategy = new OracleAgentSqlStrategy();
        final EtlPlanGenerator<RelDb, RelDb> result = new MigrateEtlPlanGenerator(transformMap, etlInstuctionSize,
                ndtSchemas, sqlStrategy);
        return result;

    }

    private Map<String, TransformInfo> getMigrateMap(final DirectedEdge<SchemaInfo, SchemaInfo> appSchemas,
            final DirectedEdge<SchemaInfo, SchemaInfo> ndtSchemas, final AgentSqlStrategy sqlStrategy,
            final boolean doCoalesce) {
        final SchemaDiffGenerator sdg = new MigrateSchemaDiffGenerator();
        final Collection<TransformEntity> transformEntities = sdg.generateDiff(appSchemas, sqlStrategy);

        final TransformMapFactory tmf = new RelTransformMapFactory(ndtSchemas, sqlStrategy, transformEntities,
                windowSize);
        Map<String, TransformInfo> result = null;
        if (doCoalesce) {
            result = tmf.getMigrateMap();
        } else {
            result = tmf.getMigrateNoCoalesceMap();
        }

        return result;
    }

    public static final boolean NOCOALESCE = false;
    public static final boolean COALESCE = true;

}
