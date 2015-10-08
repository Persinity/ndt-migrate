/**
 * Copyright (c) 2015 Persinity Inc.
 */
package com.persinity.ndt.etlmodule.relational.migrate;

import java.util.List;

import org.junit.Test;

/**
 * Haka migrate integration test.
 * <p/>
 * By using haka verifies that {@link MigrateWindowGenerator} and {@link MigrateEtlPlanGenerator}
 * migrate mutating source data to a target DB CCLOG.
 * <p/>
 * Before use: Configure your own source and destination DBs in etl-it.properties and
 * run dst/src-admin.sql to create the test users.
 *
 * @author Ivan Dachev
 */
public class HakaMigrateIT extends BaseMigrateIT {

    @Test
    @Override
    public void testOnceWithTwoTrans() {
        super.testOnceWithTwoTrans();
    }

    @Test
    @Override
    public void testOnceWithTwoTransBigEtlInstr() {
        super.testOnceWithTwoTransBigEtlInstr();
    }

    @Test
    @Override
    public void testOnceWithSeveralWindowsBigEtlInstr() {
        super.testOnceWithSeveralWindowsBigEtlInstr();
    }

    @Test
    @Override
    public void testOnceWithZeroTrans() {
        super.testOnceWithZeroTrans();
    }

    /**
     * Installs NDT, mutates some data,then does DM through haka and verifies the result, finally deinstalls NDT
     */
    @Override
    public void execute(final List<List<String>> srcPopulateSqlBatches, final List<List<String>> initialSqlBatches,
            final List<List<String>> preConsistentSqlBatches, final List<List<String>> deltaSqlBatches,
            final int etlInstructionSize) {
        getTestDm().cleanup();
        getTestMigrate().cleanup();

        getTestDm().installAndEnableNDT();

        getTestDm().mutateSrcDataVerify(deltaSqlBatches);

        getTestMigrate().initMigrateEngine(etlInstructionSize, TestMigrate.COALESCE);

        getTestMigrate().verifyTrlogDataBeforeMigrate();

        getTestMigrate().migrateHaka();

        getTestMigrate().verifyCclogDataAfterMigrate();
        getTestMigrate().verifyTrlogDataAfterMigrate();

        getTestDm().disableAndDeinstallNdt();
        getTestDm().metricsReport();
    }

}
