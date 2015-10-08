/**
 * Copyright (c) 2015 Persinity Inc.
 */
package com.persinity.ndt.etlmodule.relational.common;

import static org.hamcrest.CoreMatchers.endsWith;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.CoreMatchers.startsWith;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertThat;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import org.easymock.EasyMock;
import org.easymock.EasyMockSupport;
import org.hamcrest.CoreMatchers;
import org.junit.Before;
import org.junit.Test;

import com.persinity.common.collection.DirectedEdge;
import com.persinity.common.db.RelDb;
import com.persinity.ndt.db.TransactionId;
import com.persinity.ndt.dbagent.relational.AgentSqlStrategy;
import com.persinity.ndt.dbagent.relational.SchemaInfo;
import com.persinity.ndt.dbagent.relational.StringTid;

/**
 * @author Ivan Dachev
 */
public class NoOpsRelTransferFuncTest extends EasyMockSupport {

    @Before
    public void setUp() {
        List<TransactionId> tids1 = new LinkedList<>();
        tids1.add(new StringTid("t1"));
        tids1.add(new StringTid("t2"));

        List<TransactionId> tids2 = new LinkedList<>();
        tids2.add(new StringTid("t3"));
        tids2.add(new StringTid("t4"));

        @SuppressWarnings("unchecked")
        DirectedEdge<SchemaInfo, SchemaInfo> schemas = EasyMock.createNiceMock(DirectedEdge.class);
        AgentSqlStrategy sqlStrategy = EasyMock.createNiceMock(AgentSqlStrategy.class);

        testee = new NoOpsRelTransferFunc("testee", tids1, schemas, sqlStrategy);
        same = new NoOpsRelTransferFunc("same", tids1, schemas, sqlStrategy);
        other = new NoOpsRelTransferFunc("other", tids2, schemas, sqlStrategy);
    }

    @SuppressWarnings({ "unchecked", "ConstantConditions" })
    @Test
    public void testApply() throws Exception {
        final DirectedEdge<SchemaInfo, SchemaInfo> schemas = createMock(DirectedEdge.class);
        final AgentSqlStrategy sqlStrategy = createMock(AgentSqlStrategy.class);
        final List<StringTid> tids = Collections.singletonList(new StringTid("t"));
        final DirectedEdge<RelDb, RelDb> dataBridge = createMock(DirectedEdge.class);

        replayAll();

        final NoOpsRelTransferFunc noOpsRelTransferFunc = new NoOpsRelTransferFunc("test", tids, schemas, sqlStrategy);

        final int res = noOpsRelTransferFunc.apply(dataBridge);

        verifyAll();

        assertThat(res, is(0));
    }

    @Test
    public void testHashCode() {
        assertThat(testee.hashCode(), CoreMatchers.is(not(0)));
    }

    @Test
    public void testEquals() {
        assertEquals(testee, testee);
        assertNotEquals(testee, same);
        assertNotEquals(same, testee);

        assertNotEquals(testee, null);
    }

    @Test
    public void testToString() throws Exception {
        assertThat(testee.toString(), startsWith("NoOpsRelTransferFunc@"));
        assertThat(testee.toString(), endsWith("([t1, t2])(testee)"));

        assertThat(other.toString(), startsWith("NoOpsRelTransferFunc@"));
        assertThat(other.toString(), endsWith("([t3, t4])(other)"));
    }

    private Object testee;
    private Object same;
    private Object other;
}