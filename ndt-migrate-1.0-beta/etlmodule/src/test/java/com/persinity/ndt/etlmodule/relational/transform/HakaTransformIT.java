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

import java.util.List;

import org.junit.Test;

import com.persinity.ndt.etlmodule.relational.migrate.TestMigrate;

/**
 * Haka transform integration test.
 * <p/>
 * Verifies that {@link TransformWindowGenerator} and {@link TransformEtlPlanGenerator} transform
 * mutating source data to a target entities.
 * <p/>
 * Before use: Configure your own source and destination DBs in etl-it.properties and
 * run dst/src-admin.sql to create the test users.
 *
 * @author Ivan Dachev
 */
public class HakaTransformIT extends BaseTransformIT {

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
     * Installs NDT, mutates some data,then does DM and verifies the result, finally deinstalls NDT
     */
    @Override
    public void execute(final List<List<String>> srcPopulateSqlBatches,
            final List<List<String>> initialTransferSqlBatches, final List<List<String>> preConsistentSqlBatches,
            final List<List<String>> deltaSqlBatches, final int etlInstructionSize) {
        getTestDm().cleanup();
        getTestMigrate().cleanup();
        getTestTransform().cleanup();
        getTestDm().mutateSrcData(srcPopulateSqlBatches);

        getTestDm().installAndEnableNDT();
        getTestMigrate().initMigrateEngine(etlInstructionSize, TestMigrate.NOCOALESCE);
        getTestTransform().initTransformEngine(etlInstructionSize);

        // simulate initial transfer
        getTestDm().mutateDstData(initialTransferSqlBatches);

        // inconsistent deltas captured during initial transfer
        getTestDm().mutateSrcDataVerify(preConsistentSqlBatches);
        getTestMigrate().verifyTrlogDataBeforeMigrate();
        getTestMigrate().migrateHaka();
        if (!preConsistentSqlBatches.isEmpty() && !preConsistentSqlBatches.get(0).isEmpty()) {
            getTestMigrate().verifyCclogDataAfterMigrate();
        }
        getTestMigrate().verifyTrlogDataAfterMigrate();
        getTestTransform().initPreConsTransformEngine(etlInstructionSize);
        getTestTransform().rollTargetConsistentHaka();
        getTestTransform().verifyDestEntityDataAfterDm();
        getTestDm().executeClogGcHaka();
        getTestTransform().verifyEmptyClogs();

        // delta transfer
        getTestMigrate().initMigrateEngine(etlInstructionSize, TestMigrate.COALESCE);
        getTestDm().mutateSrcDataVerify(deltaSqlBatches);
        getTestMigrate().verifyTrlogDataBeforeMigrate();
        getTestMigrate().migrateHaka();
        if (!deltaSqlBatches.isEmpty() && !deltaSqlBatches.get(0).isEmpty()) {
            getTestMigrate().verifyCclogDataAfterMigrate();
        }
        getTestMigrate().verifyTrlogDataAfterMigrate();
        getTestTransform().transformHaka();
        getTestTransform().verifyDestEntityDataAfterDm();
        getTestDm().executeClogGcHaka();
        getTestTransform().verifyEmptyClogs();

        getTestDm().disableAndDeinstallNdt();
        getTestDm().metricsReport();
    }
}
