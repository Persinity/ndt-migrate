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
package com.persinity.ndt.etlmodule.relational.common;

import static org.easymock.EasyMock.capture;
import static org.easymock.EasyMock.createNiceMock;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.newCapture;
import static org.easymock.EasyMock.replay;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.easymock.Capture;
import org.junit.Assert;
import org.junit.Test;

import com.persinity.common.db.RelDb;
import com.persinity.common.db.metainfo.Col;
import com.persinity.common.db.metainfo.SqlFilter;
import com.persinity.ndt.dbagent.relational.AgentSqlStrategy;
import com.persinity.ndt.dbagent.relational.oracle.DbAgentConfig;
import com.persinity.ndt.dbagent.relational.oracle.OracleAgentSqlStrategy;
import com.persinity.ndt.etlmodule.relational.Partitioner;

/**
 * @author Doichin Yordanov
 */
public class SizePartitionerTest {

    private static final String WIN_FILTER_SQL = "1 = 1";
    private static final int PARTITION_SIZE = 2;
    private static final String TABLE = "clog_emp";
    private static final String KEY_COL_NAME_1 = "empid1";
    private static final String KEY_COL_NAME_2 = "empid2";

    private static final String EXPECTED_SQL_TWO_KEYS =
            "SELECT NVL(MAX(MOD(ORA_HASH(''||NVL(empid1, 0)||NVL(empid2, 0)), 1)), 0) AS " +
                    SizePartitioner.MAX_KEY_ALIAS
                    + ", NVL(MIN(MOD(ORA_HASH(''||NVL(empid1, 0)||NVL(empid2, 0)), 1)), 0) AS "
                    + SizePartitioner.MIN_KEY_ALIAS + " FROM " + TABLE + " WHERE " + WIN_FILTER_SQL;

    private static final String EXPECTED_SQL_ONE_KEY =
            "SELECT NVL(MAX(MOD(ORA_HASH(''||NVL(empid1, 0)), 1)), 0) AS " + SizePartitioner.MAX_KEY_ALIAS +
                    ", NVL(MIN(MOD(ORA_HASH(''||NVL(empid1, 0)), 1)), 0) AS " + SizePartitioner.MIN_KEY_ALIAS + " FROM "
                    + TABLE + " WHERE " + WIN_FILTER_SQL;

    /**
     * Test method for {@link SizePartitioner#partition}
     */
    @Test
    public void testPartition() {
        final DbAgentConfig fileConfig = createNiceMock(DbAgentConfig.class);
        expect(fileConfig.getClogTriggerTemplate()).andStubReturn("some");
        expect(fileConfig.getTrlogTriggerTemplate()).andStubReturn("some");
        replay(fileConfig);
        final SizePartitioner testee = new SizePartitioner(PARTITION_SIZE, new OracleAgentSqlStrategy(fileConfig));
        final SqlFilter<?> filter = stubWindowFilter();
        final RelDb db = createNiceMock(RelDb.class);
        final Capture<String> sqlCapture = newCapture();
        final Iterator<Map<String, Object>> rs = stubRs(); // partitioning logic is tested separately
        final Capture<String> sqlCountCapture = newCapture();
        expect(db.getInt(capture(sqlCountCapture))).andReturn(0);
        expect(db.executeQuery(capture(sqlCapture))).andReturn(rs);
        replay(db);

        final List<Col> twoKeys = Arrays.asList(new Col(KEY_COL_NAME_1), new Col(KEY_COL_NAME_2));

        final Partitioner.PartitionData actual = testee.partition(db, TABLE, twoKeys, filter);
        Assert.assertEquals(1, actual.getPartition().size());
        Assert.assertEquals(EXPECTED_SQL_TWO_KEYS, sqlCapture.getValue());
        Assert.assertEquals(EXPECTED_SQL_TWO_KEYS_COUNT, sqlCountCapture.getValue());
    }

    /**
     * Test method for {@link SizePartitioner#partition}
     */
    @Test
    public void testPartition_OneKeyCol() {
        final DbAgentConfig fileConfig = createNiceMock(DbAgentConfig.class);
        expect(fileConfig.getClogTriggerTemplate()).andStubReturn("some");
        expect(fileConfig.getTrlogTriggerTemplate()).andStubReturn("some");
        replay(fileConfig);
        final SizePartitioner testee = new SizePartitioner(PARTITION_SIZE, new OracleAgentSqlStrategy(fileConfig));
        final SqlFilter<?> filter = stubWindowFilter();
        final RelDb db = createNiceMock(RelDb.class);
        final Capture<String> sqlCapture = newCapture();
        final Iterator<Map<String, Object>> rs = stubRs(); // partitioning logic is tested separately
        final Capture<String> sqlCountCapture = newCapture();
        expect(db.getInt(capture(sqlCountCapture))).andReturn(0);
        expect(db.executeQuery(capture(sqlCapture))).andReturn(rs);
        replay(db);

        final List<Col> oneKeys = Collections.singletonList(new Col(KEY_COL_NAME_1));

        final Partitioner.PartitionData actual = testee.partition(db, TABLE, oneKeys, filter);
        Assert.assertEquals(1, actual.getPartition().size());
        Assert.assertEquals(EXPECTED_SQL_ONE_KEY, sqlCapture.getValue());
        Assert.assertEquals(EXPECTED_SQL_ONE_KEY_COUNT, sqlCountCapture.getValue());
    }

    @Test
    public void testEqualsHashCode() {
        final int size1 = 1;
        final AgentSqlStrategy sqlStrategy1 = createNiceMock(AgentSqlStrategy.class);
        final int size2 = 2;
        final AgentSqlStrategy sqlStrategy2 = createNiceMock(AgentSqlStrategy.class);
        replay(sqlStrategy1, sqlStrategy2);

        final SizePartitioner o11 = new SizePartitioner(size1, sqlStrategy1);
        final SizePartitioner o12 = new SizePartitioner(size1, sqlStrategy1);
        final SizePartitioner o22 = new SizePartitioner(size2, sqlStrategy2);
        final SizePartitioner o21 = new SizePartitioner(size2, sqlStrategy1);

        Assert.assertEquals(o11, o11);
        Assert.assertEquals(o11.hashCode(), o11.hashCode());
        Assert.assertEquals(o11, o12);
        Assert.assertEquals(o11.hashCode(), o12.hashCode());
        Assert.assertEquals(o12, o11);

        Assert.assertNotEquals(o11, null);
        Assert.assertNotEquals(o11, o22);
        Assert.assertNotEquals(o11, o21);
    }

    private SqlFilter<?> stubWindowFilter() {
        final SqlFilter<?> filter = new SqlFilter<Integer>() {

            @Override
            public Col getCol() {
                return new Col("1");
            }

            @Override
            public Integer getValue() {
                return 1;
            }

            @Override
            public String toString() {
                return WIN_FILTER_SQL;
            }
        };
        return filter;
    }

    private Iterator<Map<String, Object>> stubRs() {
        final List<Map<String, Object>> list = new ArrayList<>(1);
        final Map<String, Object> row = new HashMap<String, Object>();
        row.put(SizePartitioner.MAX_KEY_ALIAS, 0);
        row.put(SizePartitioner.MIN_KEY_ALIAS, 0);
        list.add(row);

        return list.iterator();
    }

    private static final String EXPECTED_SQL_ONE_KEY_COUNT =
            "" + "SELECT COUNT(DISTINCT ORA_HASH(''||NVL(empid1, 0))) FROM clog_emp WHERE 1 = 1";
    private static final String EXPECTED_SQL_TWO_KEYS_COUNT =
            "" + "SELECT COUNT(DISTINCT ORA_HASH(''||NVL(empid1, 0)||NVL(empid2, 0))) FROM clog_emp WHERE 1 = 1";
}
