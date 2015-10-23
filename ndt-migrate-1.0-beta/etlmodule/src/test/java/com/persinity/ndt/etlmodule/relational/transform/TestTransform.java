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
package com.persinity.ndt.etlmodule.relational.transform;

import static junit.framework.TestCase.assertTrue;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;

import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;
import org.hamcrest.Matchers;

import com.google.common.base.Function;
import com.persinity.common.collection.DirectedEdge;
import com.persinity.common.collection.Pool;
import com.persinity.common.collection.Tree;
import com.persinity.common.db.RelDb;
import com.persinity.common.db.RelDbUtil;
import com.persinity.common.db.Trimmer;
import com.persinity.common.db.metainfo.Schema;
import com.persinity.common.logging.Log4jLogger;
import com.persinity.ndt.db.RelDbPoolFactory;
import com.persinity.ndt.dbagent.ClogAgent;
import com.persinity.ndt.dbagent.DbAgentFactory;
import com.persinity.ndt.dbagent.SchemaAgent;
import com.persinity.ndt.dbagent.relational.AgentSqlStrategy;
import com.persinity.ndt.dbagent.relational.DbAgentTracker;
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
import com.persinity.ndt.etlmodule.relational.migrate.TestMigrate;
import com.persinity.ndt.transform.EntitiesDag;

/**
 * Utils for testing transformation.
 *
 * @author Doichin Yordanov
 */
public class TestTransform {

    public TestTransform(final RelDbPoolFactory relDbPoolFactory, final int windowSize, TestMigrate testMigrate) {
        this.appPoolBridge = relDbPoolFactory.appBridge();
        this.ndtPoolBridge = relDbPoolFactory.ndtBridge();
        this.windowSize = windowSize;

        ndtDbBridge = RelDbUtil.getBridge(ndtPoolBridge);
        appDbBridge = RelDbUtil.getBridge(appPoolBridge);

        sqlStrategy = new OracleAgentSqlStrategy();

        srcAppMetaInfo = new OracleSchemaInfo(appDbBridge.src().metaInfo(), new Trimmer(),
                sqlStrategy.getMaxNameLength());
        final Schema dstAppSchema = appDbBridge.dst().metaInfo();
        dstAppMetaInfo = new OracleSchemaInfo(dstAppSchema, new Trimmer(), sqlStrategy.getMaxNameLength());
        srcNdtMetaInfo = new OracleSchemaInfo(ndtDbBridge.src().metaInfo(), new Trimmer(),
                sqlStrategy.getMaxNameLength());
        dstNdtMetaInfo = new OracleSchemaInfo(ndtDbBridge.dst().metaInfo(), new Trimmer(),
                sqlStrategy.getMaxNameLength());

        final DbAgentTracker dbAgentTracker = new DbAgentTracker();
        final DbAgentFactory<Function<RelDb, RelDb>> dstDrivenDbAgentFactory = new DbAgentFactory<>(dstAppSchema,
                sqlStrategy, dbAgentTracker);
        dstSchemaAgent = dstDrivenDbAgentFactory.dispatchSchemaAgent(ndtDbBridge.dst());
        this.testMigrate = testMigrate;
    }

    public void initTransformEngine(final int etlInstructionSize) {
        final DirectedEdge<Pool<RelDb>, Pool<RelDb>> transformPath = new DirectedEdge<>(ndtPoolBridge.dst(),
                appPoolBridge.dst());

        initTransformWinGen(transformPath);
        log.debug("Using {} over DM path {}", transformWinGen, transformPath);
        final DirectedEdge<SchemaInfo, SchemaInfo> srcAppTodstAppMetaInfo = new DirectedEdge<>(srcAppMetaInfo,
                dstAppMetaInfo);
        final SchemaDiffGenerator sdg = new MigrateSchemaDiffGenerator();
        final Collection<TransformEntity> transformEntities = sdg.generateDiff(srcAppTodstAppMetaInfo, sqlStrategy);
        final DirectedEdge<SchemaInfo, SchemaInfo> dstNdtToDstAppMetaInfo = new DirectedEdge<>(dstNdtMetaInfo,
                dstAppMetaInfo);
        final TransformMapFactory transformMapFactory = new RelTransformMapFactory(dstNdtToDstAppMetaInfo, sqlStrategy,
                transformEntities, windowSize);

        final Map<String, TransformInfo> mergeMap = transformMapFactory.getMergeMap();
        final Map<String, TransformInfo> deleteMap = transformMapFactory.getDeleteMap();
        transformPlanner = getTransformPlanner(mergeMap, deleteMap, etlInstructionSize);
        log.debug("Using {} over transformation merge {} and delete map {}", transformPlanner, mergeMap, deleteMap);
    }

    public void initPreConsTransformEngine(final int eltInstructionSize) {
        final DirectedEdge<Pool<RelDb>, Pool<RelDb>> transformPath = new DirectedEdge<>(ndtPoolBridge.dst(),
                appPoolBridge.dst());
        initPreConsTransformWinGen(transformPath);
    }

    public void cleanup() {
        transformWinGen = null;
        transformPlanner = null;
    }

    public void transformSerial() {
        executePlan(dstSchemaAgent.breakRefIntegrityCycles());
        testMigrate.getTestDm().serialHop(transformWinGen, transformPlanner);
        executePlan(dstSchemaAgent.renewRefIntegrityCycles());
    }

    public void transformHaka() {
        executePlan(dstSchemaAgent.breakRefIntegrityCycles());
        testMigrate.getTestDm().hakaHop(transformWinGen, transformPlanner);
        executePlan(dstSchemaAgent.renewRefIntegrityCycles());
    }

    public void rollTargetConsistentSerial() {
        executePlan(dstSchemaAgent.disableIntegrity());
        verifyFkConstraintsDisabled(appDbBridge.dst());
        testMigrate.getTestDm().serialHop(preConsTransformWinGen, transformPlanner);
        executePlan(dstSchemaAgent.enableIntegrity());
        verifyFkConstraintsEnabled(appDbBridge.dst());
    }

    public static void verifyFkConstraintsEnabled(RelDb db) {
        final Integer disabledCnt = getFkConstraintsCnt(db, "DISABLED");
        assertEquals(Integer.valueOf(0), disabledCnt);
        final Integer enabledCnt = getFkConstraintsCnt(db, "ENABLED");
        assertThat(enabledCnt, Matchers.greaterThan(0));
    }

    public static void verifyFkConstraintsDisabled(RelDb db) {
        final Integer enabledCnt = getFkConstraintsCnt(db, "ENABLED");
        assertEquals(Integer.valueOf(0), enabledCnt);
        final Integer disabledCnt = getFkConstraintsCnt(db, "DISABLED");
        assertThat(disabledCnt, Matchers.greaterThan(0));
    }

    public void rollTargetConsistentHaka() {
        executePlan(dstSchemaAgent.disableIntegrity());
        verifyFkConstraintsDisabled(appDbBridge.dst());
        testMigrate.getTestDm().hakaHop(preConsTransformWinGen, transformPlanner);
        executePlan(dstSchemaAgent.enableIntegrity());
        verifyFkConstraintsEnabled(appDbBridge.dst());
    }

    public void verifyDestEntityDataAfterDm() {
        final RelDb srcAppDb = appDbBridge.src();
        final RelDb dstAppDb = appDbBridge.dst();
        // Check that the transformed records match the source
        String qry = "SELECT id, name FROM dept ORDER BY id, name";
        TestDm.compareRs(srcAppDb, dstAppDb, qry, qry);
        qry = "SELECT id, name, dept_id FROM emp ORDER BY id, name, dept_id";
        TestDm.compareRs(srcAppDb, dstAppDb, qry, qry);
        qry = "SELECT id, sid, name, emp_id FROM kid ORDER BY id, sid, name, emp_id";
        TestDm.compareRs(srcAppDb, dstAppDb, qry, qry);
    }

    public void verifyEmptyClogs() {
        final RelDb srcNdtDb = ndtDbBridge.src();
        log.debug("Verify empty clog tables on {}", srcNdtDb);
        final Set<String> srcTableNames = srcAppMetaInfo.getTableNames();
        assertTrue(srcTableNames.size() > 0);
        for (final String tableName : srcTableNames) {
            final String clogTableName = srcNdtMetaInfo.getClogTableName(tableName);
            log.debug("Verify empty clog table {}", clogTableName);
            TestDm.assertEmptyRs(srcNdtDb, sqlStrategy.selectAllStatement(clogTableName));
        }

        final RelDb dstNdtDb = ndtDbBridge.dst();
        log.debug("Verify empty clog tables on {}", dstNdtDb);
        final Set<String> dstTableNames = dstAppMetaInfo.getTableNames();
        assertTrue(dstTableNames.size() > 0);
        for (final String tableName : dstTableNames) {
            final String clogTableName = dstNdtMetaInfo.getClogTableName(tableName);
            log.debug("Verify empty clog table {}", clogTableName);
            TestDm.assertEmptyRs(dstNdtDb, sqlStrategy.selectAllStatement(clogTableName));
        }
    }

    public void close() {
        RelDbUtil.closeBridge(ndtPoolBridge, ndtDbBridge);
        RelDbUtil.closeBridge(appPoolBridge, appDbBridge);
    }

    private static Integer getFkConstraintsCnt(final RelDb db, final String status) {
        return db.getInt("SELECT COUNT(1) AS cnt " +
                "FROM user_constraints " +
                "WHERE status = '" + status + "' AND constraint_type = 'R'");
    }

    private void initTransformWinGen(final DirectedEdge<Pool<RelDb>, Pool<RelDb>> transformPath) {
        entityDag = dstSchemaAgent.getSchema();
        transformWinGen = new TransformWindowGenerator(transformPath, entityDag, sqlStrategy, windowSize);
    }

    private void initPreConsTransformWinGen(final DirectedEdge<Pool<RelDb>, Pool<RelDb>> transformPath) {
        final ClogAgent<Function<RelDb, RelDb>> srcClogAgent = testMigrate.getTestDm().getSrcClogAgent();
        final ClogAgent<Function<RelDb, RelDb>> dstClogAgent = testMigrate.getTestDm().getDstClogAgent();
        preConsTransformWinGen = InConsistentTransformWindowGenerator
                .newInstance(srcClogAgent, dstClogAgent, transformPath, entityDag, sqlStrategy, windowSize);
        preConsTransformWinGen.stopWhenFeedExhausted();
    }

    private EtlPlanGenerator<RelDb, RelDb> getTransformPlanner(final Map<String, TransformInfo> mergeMap,
            final Map<String, TransformInfo> deleteMap, final int etlInstuctionSize) {

        final AgentSqlStrategy sqlStrategy = new OracleAgentSqlStrategy();
        final DirectedEdge<SchemaInfo, SchemaInfo> schemas = new DirectedEdge<>(dstNdtMetaInfo, dstAppMetaInfo);
        final EtlPlanGenerator<RelDb, RelDb> result = new TransformEtlPlanGenerator(mergeMap, deleteMap,
                etlInstuctionSize, schemas, sqlStrategy);
        return result;

    }

    private void executePlan(final Tree<Function<RelDb, RelDb>> plan) {
        if (plan.getRoot() != null) {
            final Iterator<Function<RelDb, RelDb>> it = plan.breadthFirstTraversal(plan.getRoot()).iterator();
            while (it.hasNext()) {
                it.next().apply(appDbBridge.dst());
            }
        }
    }

    private final AgentSqlStrategy sqlStrategy;
    private final DirectedEdge<Pool<RelDb>, Pool<RelDb>> appPoolBridge;
    private final DirectedEdge<Pool<RelDb>, Pool<RelDb>> ndtPoolBridge;
    private final SchemaInfo dstAppMetaInfo;
    private final SchemaInfo srcNdtMetaInfo;
    private final SchemaInfo dstNdtMetaInfo;
    private final SchemaInfo srcAppMetaInfo;
    private final int windowSize;
    private final SchemaAgent<Function<RelDb, RelDb>> dstSchemaAgent;
    private final DirectedEdge<RelDb, RelDb> ndtDbBridge;
    private final DirectedEdge<RelDb, RelDb> appDbBridge;

    private WindowGenerator<RelDb, RelDb> preConsTransformWinGen;
    private EntitiesDag entityDag;
    private final TestMigrate testMigrate;
    private WindowGenerator<RelDb, RelDb> transformWinGen;
    private EtlPlanGenerator<RelDb, RelDb> transformPlanner;

    private static final Log4jLogger log = new Log4jLogger(Logger.getLogger(TestTransform.class));
}
