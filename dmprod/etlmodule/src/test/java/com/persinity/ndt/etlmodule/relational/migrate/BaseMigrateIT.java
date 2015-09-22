/**
 * Copyright (c) 2015 Persinity Inc.
 */
package com.persinity.ndt.etlmodule.relational.migrate;

import static com.persinity.common.StringUtils.format;
import static com.persinity.common.invariant.Invariant.assertArg;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import org.junit.After;
import org.junit.Before;

import com.persinity.common.Config;
import com.persinity.common.Resource;
import com.persinity.common.db.RelDb;
import com.persinity.ndt.db.RelDbPoolFactory;
import com.persinity.ndt.etlmodule.relational.common.TestDm;

/**
 * Base migrate integration test.
 *
 * @author Ivan Dachev
 */
public abstract class BaseMigrateIT {

    @Before
    public void setUp() {
        relDbPoolFactory = new RelDbPoolFactory(ETL_IT_PROPERTIES);

        // init test bed schemas
        resource.accessAndClose(new Resource.Accessor<RelDb, Void>(relDbPoolFactory.appBridge().src().get(), null) {
            @Override
            public Void access(final RelDb resource) throws Exception {
                resource.executeScript("testapp-init.sql");
                return null;
            }
        });
        resource.accessAndClose(new Resource.Accessor<RelDb, Void>(relDbPoolFactory.appBridge().dst().get(), null) {
            @Override
            public Void access(final RelDb resource) throws Exception {
                resource.executeScript("testapp-init.sql");
                return null;
            }
        });

        windowSize = Integer.parseInt(Config.loadPropsFrom(ETL_IT_PROPERTIES).getProperty("window.size"));
        assertArg(windowSize > 0);

        testMigrate = new TestMigrate(relDbPoolFactory, windowSize);
    }

    @After
    public void tearDown() {
        testMigrate.close();
        relDbPoolFactory.close();
    }

    /**
     * Tests one ETL transfer over accumulated data of two transactions, using small set of parallel ETL Instructions
     */
    public void testOnceWithTwoTrans() {

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

        execute(getSrcPopulateSqlBatches(), getInitialTransferSqlBatches(), getPreConsSqlBatches(),
                Arrays.asList(sqlBatch1, sqlBatch2), 2);
    }

    /**
     * Tests one ETL transfer over accumulated data of two transactions, using bulk ETL Instruction to transfer changes
     * for all records at once.
     */
    public void testOnceWithTwoTransBigEtlInstr() {
        final List<String> sqlBatch1 = new LinkedList<>();
        sqlBatch1.add("INSERT INTO dept (id, name) VALUES (1, 'SW')");
        sqlBatch1.add("INSERT INTO emp (id, name, dept_id) VALUES (1, 'Ivan', 1)");
        sqlBatch1.add("INSERT INTO emp (id, name, dept_id) VALUES (2, 'Doichin', 1)");
        sqlBatch1.add("INSERT INTO emp (id, name, dept_id) VALUES (3, 'Rosen', 1)");

        final List<String> sqlBatch2 = new LinkedList<>();
        sqlBatch2.add("UPDATE emp SET name = 'Ivan Dachev' WHERE id = 1");
        sqlBatch2.add("UPDATE emp SET name = 'Doichin Yordanov' WHERE id = 2");
        sqlBatch2.add("DELETE FROM emp WHERE id = 3");

        execute(getSrcPopulateSqlBatches(), getInitialTransferSqlBatches(), getPreConsSqlBatches(),
                Arrays.asList(sqlBatch1, sqlBatch2), 10);
    }

    /**
     * Tests one ETL transfer over accumulated data of several windows.
     */
    public void testOnceWithSeveralWindowsBigEtlInstr() {
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

        ArrayList<List<String>> sqlBatches = new ArrayList<>();
        sqlBatches.add(sqlBatch1);
        sqlBatches.add(sqlBatch2);

        int j = 0;
        for (int i = 0; i < windowSize * 3; i++) {
            final List<String> sqlBatch = new LinkedList<>();
            if (j == 1) {
                sqlBatch.add("INSERT INTO emp (id, name, dept_id) VALUES (4, 'Petkan', 1)");
                sqlBatch.add("INSERT INTO kid (id, sid, name, emp_id) VALUES (2, 's7', 'IvanKid', 1)");
            } else if (j == 2) {
                sqlBatch.add("DELETE FROM emp WHERE id = 4");
                sqlBatch.add("DELETE FROM kid WHERE (id = 2) AND (sid = 's7')");
            } else if (j == 3) {
                j = 0;
            }
            j++;
            sqlBatch.add(format("UPDATE emp SET name = 'Ivan Dachev {}' WHERE id = 1", i));
            sqlBatch.add(format("UPDATE emp SET name = 'Doichin Yordanov {}' WHERE id = 2", i));
            sqlBatch.add(format("UPDATE kid SET name = 'IvanKid {}' WHERE (id = 1) AND (sid = 's7')", i));
            sqlBatches.add(sqlBatch);
        }

        execute(getSrcPopulateSqlBatches(), getInitialTransferSqlBatches(), getPreConsSqlBatches(), sqlBatches, 10);
    }

    /**
     * Tests one ETL transfer over no accumulated data
     */
    public void testOnceWithZeroTrans() {
        final List<String> sqlEmptyBatch = Collections.emptyList();

        execute(getSrcPopulateSqlBatches(), getSrcPopulateSqlBatches(), Collections.singletonList(sqlEmptyBatch),
                Collections.singletonList(sqlEmptyBatch), 2);
    }

    /**
     * @param srcPopulateSqlBatches
     * @param initialSqlBatches
     *         for initial transfer
     * @param preConsistentSqlBatches
     *         changes accumulated during initial transfer
     * @param deltaSqlBatches
     *         deltas
     * @param etlInstructionSize
     *         ETL instruction size to be used
     */
    public abstract void execute(final List<List<String>> srcPopulateSqlBatches,
            final List<List<String>> initialSqlBatches, final List<List<String>> preConsistentSqlBatches,
            final List<List<String>> deltaSqlBatches, final int etlInstructionSize);

    public RelDbPoolFactory getRelDbPoolFactory() {
        return relDbPoolFactory;
    }

    public TestMigrate getTestMigrate() {
        return testMigrate;
    }

    public TestDm getTestDm() {
        return testMigrate.getTestDm();
    }

    public int getWindowSize() {
        return windowSize;
    }

    private List<List<String>> getSrcPopulateSqlBatches() {
        final String srcPopulateSql1 = "INSERT INTO dept (id, name) VALUES (100, 'Initial Dpt')";
        final List<String> srcPopulateSqlBatch = Arrays.asList(srcPopulateSql1);
        final List<List<String>> srcPopulateSqlBatches = Arrays.asList(srcPopulateSqlBatch);
        return srcPopulateSqlBatches;
    }

    /**
     * @return Inconsistent batch changes recorded during initial transfer that
     * contradict the end state of the initial transfer when applied after it
     */
    private List<List<String>> getPreConsSqlBatches() {
        // Note that the initial sql batch ends with no dept 100, hence the contradiction
        final String preConsSql1 = "INSERT INTO emp (id, name, dept_id) VALUES (101, 'Initial emp1', 100)";
        final String preConsSql2 = "INSERT INTO emp (id, name, dept_id) VALUES (102, 'Initial emp2', 100)";
        final List<String> preConsSqlBatch1 = Arrays.asList(preConsSql1, preConsSql2);
        final String preConsSql3 = "DELETE FROM emp WHERE id = 101";
        final String preConsSql4 = "DELETE FROM emp WHERE id = 102";
        final String preConsSql5 = "DELETE FROM dept WHERE id = 100";
        final List<String> preConsSqlBatch2 = Arrays.asList(preConsSql3, preConsSql4, preConsSql5);
        final List<List<String>> preConsSqlBatches = Arrays.asList(preConsSqlBatch1, preConsSqlBatch2);
        return preConsSqlBatches;
    }

    /**
     * @return Batch of consistent changes exported during initial transfer
     */
    private List<List<String>> getInitialTransferSqlBatches() {
        final List<List<String>> result = new LinkedList<>(getSrcPopulateSqlBatches());
        result.addAll(getPreConsSqlBatches());
        return result;
    }

    public static final String ETL_IT_PROPERTIES = "etl-it.properties";

    protected TestMigrate testMigrate;
    private int windowSize;
    private RelDbPoolFactory relDbPoolFactory;
    private final Resource resource = new Resource();
}
