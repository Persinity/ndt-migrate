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
package com.pesinity.ndt.dbagent.testutil;

import static com.persinity.common.StringUtils.format;
import static com.persinity.common.db.metainfo.ProxySchema.newSchema;
import static junit.framework.TestCase.assertTrue;
import static org.hamcrest.CoreMatchers.hasItem;
import static org.hamcrest.CoreMatchers.not;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Set;

import org.junit.Assert;

import com.google.common.base.Function;
import com.google.common.base.Predicates;
import com.google.common.collect.Collections2;
import com.persinity.common.db.RelDb;
import com.persinity.common.db.Trimmer;
import com.persinity.ndt.dbagent.CdcAgent;
import com.persinity.ndt.dbagent.ClogAgent;
import com.persinity.ndt.dbagent.DbAgentFactory;
import com.persinity.ndt.dbagent.relational.SchemaInfo;
import com.persinity.ndt.dbagent.relational.oracle.OracleAgentSqlStrategy;
import com.persinity.ndt.dbagent.relational.oracle.OracleSchemaInfo;
import com.persinity.ndt.transform.SerialPlanProcessor;

/**
 * Utility for testing DB agents such as {@link ClogAgent} and {@link CdcAgent}
 *
 * @author Doichin Yordanov
 */
public class TestDbAgent {

    public static final String LOG_LEVEL_ERROR = "ERROR";
    private final SerialPlanProcessor<RelDb, RelDb> processor;
    private final Trimmer trimmer;

    private static class DmlInfo {
        public int insCnt;
        public int updCnt;
        public int delCnt;
        public int cnt;
        public int transCnt;

        @Override
        public boolean equals(final Object obj) {
            if (this == obj) {
                return true;
            }
            if (!(obj instanceof DmlInfo)) {
                return false;
            }
            final DmlInfo that = (DmlInfo) obj;
            return insCnt == that.insCnt && updCnt == that.updCnt && delCnt == that.delCnt && cnt == that.cnt
                    && transCnt == that.transCnt;
        }

        @Override
        public int hashCode() {
            return Objects.hash(insCnt, updCnt, delCnt, cnt, transCnt);
        }

        @Override
        public String toString() {
            return format("{}(insCnt={}, updCnt={}, delCnt={}, cnt={}, transCnt={})", this.getClass().getSimpleName(),
                    insCnt, updCnt, delCnt, cnt, transCnt);
        }

        public DmlInfo add(final DmlInfo that) {
            final DmlInfo result = new DmlInfo();
            result.cnt = that.cnt + cnt;
            result.delCnt = that.delCnt + delCnt;
            result.insCnt = that.insCnt + insCnt;
            result.transCnt = that.transCnt + transCnt;
            result.updCnt = that.updCnt + updCnt;
            return result;
        }

    }

    public TestDbAgent() {
        processor = new SerialPlanProcessor<RelDb, RelDb>();
        trimmer = new Trimmer();
        sqlStrategy = new OracleAgentSqlStrategy();
    }

    /**
     * Assert that a message is logged in the ndt_log table.
     *
     * @param ndtDb
     * @param message
     */
    public void assertLog(final RelDb ndtDb, final String logLevel, final String message) {
        final int actual = ndtDb
                .getInt("SELECT COUNT(1) FROM ndt_log WHERE log_msg LIKE '%" + message + "%' AND log_level = '"
                        + logLevel + "' AND originator IS NOT NULL AND log_date IS NOT NULL");
        assertTrue(actual > 0);
    }

    /**
     * Checks that the clog tables exist in the specified {@link RelDb}
     *
     * @param ndtDb
     */
    public void verifyClogMounted(final RelDb ndtDb) {
        final SchemaInfo ndtInfo = new OracleSchemaInfo(ndtDb.metaInfo(), trimmer, 25);
        final List<String> expected = Arrays.asList(CLOG_TABLES);
        final Set<String> actual = ndtInfo.getTableNames();
        Assert.assertTrue(actual.containsAll(expected));
    }

    /**
     * Checks that the clog tables does not exist in the specified {@link RelDb}
     *
     * @param ndtDb
     */
    public void verifyClogUnmounted(final RelDb ndtDb) {
        final SchemaInfo ndtInfo = new OracleSchemaInfo(newSchema(ndtDb, sqlStrategy), trimmer, 25);
        final List<String> expected = Arrays.asList(CLOG_TABLES);
        final Set<String> actual = ndtInfo.getTableNames();
        for (final String tab : expected) {
            assertThat(actual, not(hasItem(tab)));
        }
    }

    /**
     * Checks that CDC is disabled by mutating data in the specified application {@link RelDb} and verifying that no
     * corresponding changes were captured in the CLOG of the specified NDT {@link RelDb}
     *
     * @param srcAppDb
     * @param ndtDb
     */
    public void verifyCdcDisabled(final RelDb srcAppDb, final RelDb ndtDb) {
        // Generate an update
        final int clogCountBefore = ndtDb.getInt("SELECT COUNT(1) FROM clog_dept");
        final int trlogCountBefore = ndtDb.getInt("SELECT COUNT(1) FROM trlog");
        srcAppDb.executeDmdl("INSERT INTO dept (id, name) VALUES (320, 'CdcDisabled')");
        srcAppDb.commit();

        // Check that the update has not been captured by the CDC
        final int clogCountAfter = ndtDb.getInt("SELECT COUNT(1) FROM clog_dept");
        final int trlogCountAfter = ndtDb.getInt("SELECT COUNT(1) FROM trlog");
        assertEquals(clogCountBefore, clogCountAfter);
        assertEquals(trlogCountBefore, trlogCountAfter);
    }

    /**
     * Mutating data in the specified application {@link RelDb}
     *
     * @param db
     * @param sqlBatch
     */
    public void mutateData(final RelDb db, final List<String> sqlBatch) {
        for (final String sql : sqlBatch) {
            db.executeDmdl(sql);
        }
        db.commit();
    }

    /**
     * Checks that CDC is enabled by mutating data in the specified application {@link RelDb} and verifying there are
     * relevant captured changes in the CLOG of the specified NDT {@link RelDb}.
     *
     * @param srcAppDb
     * @param ndtDb
     */
    public void mutateDataAndVerifyCdcEnabled(final RelDb srcAppDb, final RelDb ndtDb, final List<String> sqlBatch) {
        final DmlInfo clogEmpInfoPreDml = getDmlInfoForClog(ndtDb, "clog_emp");
        final DmlInfo clogDeptInfoPreDml = getDmlInfoForClog(ndtDb, "clog_dept");
        final DmlInfo clogKidInfoPreDml = getDmlInfoForClog(ndtDb, "clog_kid");

        final DmlInfo sqlEmpInfo = getDmlInfoForSql(sqlBatch, "emp");
        final DmlInfo sqlDeptInfo = getDmlInfoForSql(sqlBatch, "dept");
        final DmlInfo sqlKidInfo = getDmlInfoForSql(sqlBatch, "kid");

        mutateData(srcAppDb, sqlBatch);

        final DmlInfo clogEmpInfoPostDml = getDmlInfoForClog(ndtDb, "clog_emp");
        final DmlInfo clogDeptInfoPostDml = getDmlInfoForClog(ndtDb, "clog_dept");
        final DmlInfo clogKidInfoPostDml = getDmlInfoForClog(ndtDb, "clog_kid");

        assertEquals(clogEmpInfoPreDml.add(sqlEmpInfo), clogEmpInfoPostDml);
        assertEquals(clogDeptInfoPreDml.add(sqlDeptInfo), clogDeptInfoPostDml);
        assertEquals(clogKidInfoPreDml.add(sqlKidInfo), clogKidInfoPostDml);
    }

    public void deleteTrlogs(final DbAgentFactory<Function<RelDb, RelDb>> dbAgentFactory, final RelDb srcAppDb) {
        final ClogAgent<Function<RelDb, RelDb>> clogAgent = dbAgentFactory.dispatchClogAgent(srcAppDb);
        processor.process(clogAgent.trlogCleanup(), srcAppDb);
    }

    public void verifyEmptyTrlog(final RelDb srcNdtDb) {
        int rowsCount = srcNdtDb.getInt("SELECT NVL(COUNT(1),0) FROM " + SchemaInfo.TAB_TRLOG);
        assertEquals(0, rowsCount);
    }

    private DmlInfo getDmlInfoForClog(final RelDb ndtDb, final String clogTable) {
        final DmlInfo clogInfo = new DmlInfo();
        clogInfo.cnt = ndtDb.getInt("SELECT COUNT(1) FROM " + clogTable
                + " WHERE id IS NOT NULL AND tid IS NOT NULL AND gid IS NOT NULL AND ctype IS NOT NULL");
        clogInfo.transCnt = ndtDb
                .getInt("SELECT COUNT(1) FROM trlog WHERE tid IS NOT NULL AND last_gid IS NOT NULL AND tab_name = '"
                        + clogTable + "'");
        clogInfo.insCnt = ndtDb.getInt("SELECT COUNT(1) FROM " + clogTable + " WHERE ctype = 'I'");
        clogInfo.updCnt = ndtDb.getInt("SELECT COUNT(1) FROM " + clogTable + " WHERE ctype = 'U'");
        clogInfo.delCnt = ndtDb.getInt("SELECT COUNT(1) FROM " + clogTable + " WHERE ctype = 'D'");
        return clogInfo;
    }

    private DmlInfo getDmlInfoForSql(final List<String> sqlBatch, final String table) {
        final DmlInfo sqlInfo = new DmlInfo();
        sqlInfo.insCnt = Collections2.filter(sqlBatch, Predicates.containsPattern("INSERT INTO " + table)).size();
        sqlInfo.updCnt = Collections2.filter(sqlBatch, Predicates.containsPattern("UPDATE " + table)).size();
        sqlInfo.delCnt = Collections2.filter(sqlBatch, Predicates.containsPattern("DELETE FROM " + table)).size();
        sqlInfo.cnt = sqlInfo.insCnt + sqlInfo.updCnt + sqlInfo.delCnt;
        sqlInfo.transCnt = sqlInfo.cnt > 0 ? 1 : 0;
        return sqlInfo;
    }

    private static final String[] CLOG_TABLES = { "clog_emp", "clog_dept", "clog_kid" };

    private final OracleAgentSqlStrategy sqlStrategy;
}
