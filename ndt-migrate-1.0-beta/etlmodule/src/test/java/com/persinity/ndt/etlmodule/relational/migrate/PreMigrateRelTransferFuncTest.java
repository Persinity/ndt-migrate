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

package com.persinity.ndt.etlmodule.relational.migrate;

import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.expectLastCall;
import static org.hamcrest.CoreMatchers.endsWith;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.CoreMatchers.startsWith;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertThat;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.easymock.EasyMockSupport;
import org.easymock.IAnswer;
import org.junit.Before;
import org.junit.Test;

import com.persinity.common.collection.DirectedEdge;
import com.persinity.common.db.OracleSqlStrategy;
import com.persinity.common.db.RelDb;
import com.persinity.common.db.metainfo.Col;
import com.persinity.common.db.metainfo.constraint.PK;
import com.persinity.ndt.db.TransactionId;
import com.persinity.ndt.dbagent.relational.AgentSqlStrategy;
import com.persinity.ndt.dbagent.relational.SchemaInfo;
import com.persinity.ndt.dbagent.relational.StringTid;

/**
 * @author Ivo Yanakiev
 */
public class PreMigrateRelTransferFuncTest extends EasyMockSupport {

    @SuppressWarnings("unchecked")
    @Before
    public void setUp() {
        final List<TransactionId> tids1 = new LinkedList<>();
        tids1.add(new StringTid("t1"));
        tids1.add(new StringTid("t2"));

        final List<TransactionId> tids2 = new LinkedList<>();
        tids2.add(new StringTid("t3"));

        final SchemaInfo srcSchema = createMock(SchemaInfo.class);
        final SchemaInfo dstSchema = createMock(SchemaInfo.class);
        final DirectedEdge<SchemaInfo, SchemaInfo> schemas = new DirectedEdge<>(srcSchema, dstSchema);
        final AgentSqlStrategy sqlStrategy = createMock(AgentSqlStrategy.class);

        final Set<Col> cols = new LinkedHashSet<>();
        cols.add(new Col("id"));
        cols.add(new Col("name"));

        testee = new PreMigrateRelTransferFunc(tids1, schemas, sqlStrategy);
        same = new PreMigrateRelTransferFunc(tids1, schemas, sqlStrategy);
        other = new PreMigrateRelTransferFunc(tids2, schemas, sqlStrategy);
        diffTids = new PreMigrateRelTransferFunc(tids2, schemas, sqlStrategy);

        final RelDb srcDb = createMock(RelDb.class);
        final RelDb dstDb = createMock(RelDb.class);
        dbBridge = new DirectedEdge<>(srcDb, dstDb);

        srcDb.commit();
        expectLastCall();
        dstDb.commit();
        expectLastCall();

        expect(srcSchema.getTableCols("trlog")).andStubReturn(cols);
        expect(dstSchema.getTableCols("trlog")).andStubReturn(cols);
        expect(srcDb.getSqlStrategy()).andStubReturn(new OracleSqlStrategy());
        expect(dstDb.getSqlStrategy()).andStubReturn(new OracleSqlStrategy());

        final Set<Col> pkCols = new LinkedHashSet<>();
        pkCols.add(new Col("id"));
        final PK pk = new PK("trlog", pkCols);
        expect(dstSchema.getTablePk("trlog")).andStubReturn(pk);

        expect(sqlStrategy.trlogExtractQuery("trlog", new ArrayList<>(cols), 2)).andReturn("extract trlog");
        expect(sqlStrategy.updateStatement("trlog", new ArrayList<>(cols), new ArrayList<>(pkCols)))
                .andReturn("update trlog");
        expect(sqlStrategy.insertStatement("trlog", new ArrayList<>(cols))).andReturn("insert trlog");
        expect(sqlStrategy.trlogUpdateStatus("trlog", SchemaInfo.TrlogStatusType.P, 2))
                .andReturn("update trlog status to P");

        final List<Map<String, Object>> extractData = new ArrayList<>();
        extractData.add(new HashMap<String, Object>());
        extractData.add(new HashMap<String, Object>());

        extractData.get(0).put("id", 1);
        extractData.get(0).put("name", "tdata1");
        extractData.get(1).put("id", 2);
        extractData.get(1).put("name", "tdata2");

        expect(srcDb.executePreparedQuery("extract trlog", Arrays.asList("t1", "t2")))
                .andReturn(extractData.iterator());

        expect(dstDb.executePreparedDml("insert trlog", Arrays.asList(1, "tdata1"))).andAnswer(new IAnswer<Integer>() {
            @Override
            public Integer answer() throws Throwable {
                throw new RuntimeException("Already exists", new SQLException("SQL Already exists", "2300"));
            }
        });
        expect(dstDb.executePreparedDml("update trlog", Arrays.asList(1, "tdata1", 1))).andReturn(1);
        expect(dstDb.executePreparedDml("insert trlog", Arrays.asList(2, "tdata2"))).andReturn(1);
        expect(srcDb.executePreparedDml("update trlog status to P", Arrays.asList("t1", "t2"))).andReturn(1);
    }

    @SuppressWarnings("ConstantConditions")
    @Test
    public void testApply() throws Exception {
        replayAll();

        final int res = testee.apply(dbBridge);

        verifyAll();

        assertThat(res, is(3));
    }

    @Test
    public void testHashCode() throws Exception {
        assertThat(testee.hashCode(), is(not(0)));

        assertEquals(testee.hashCode(), same.hashCode());
    }

    @Test
    public void testEquals() throws Exception {
        assertEquals(testee, testee);
        assertEquals(testee, same);

        assertNotEquals(testee, null);
        assertNotEquals(testee, diffTids);
    }

    @Test
    public void testToString() throws Exception {
        assertThat(testee.toString(), startsWith("PreMigrateRelTransferFunc@"));
        assertThat(testee.toString(), endsWith("([t1, t2])"));

        assertThat(other.toString(), startsWith("PreMigrateRelTransferFunc@"));
        assertThat(other.toString(), endsWith("([t3])"));
    }

    private PreMigrateRelTransferFunc testee;
    private PreMigrateRelTransferFunc same;
    private PreMigrateRelTransferFunc other;
    private PreMigrateRelTransferFunc diffTids;
    private DirectedEdge<RelDb, RelDb> dbBridge;
}
