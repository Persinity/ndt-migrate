/**
 * Copyright (c) 2015 Persinity Inc.
 */
package com.persinity.ndt.controller.step;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import org.easymock.EasyMock;
import org.junit.Before;
import org.junit.Test;

import com.google.common.base.Function;
import com.persinity.common.db.RelDb;
import com.persinity.ndt.dbagent.CdcAgent;
import com.persinity.ndt.dbagent.ClogAgent;
import com.persinity.ndt.dbagent.SchemaAgent;

/**
 * @author Ivan Dachev
 */
public class AgentContextTest {

    @SuppressWarnings("unchecked")
    @Before
    public void setUp() throws Exception {
        srcClogAgent = EasyMock.createNiceMock(ClogAgent.class);
        dstClogAgent = EasyMock.createNiceMock(ClogAgent.class);
        cdcAgent = EasyMock.createNiceMock(CdcAgent.class);
        dstSchemaAgent = EasyMock.createNiceMock(SchemaAgent.class);
        testee = new AgentContext(srcClogAgent, dstClogAgent, cdcAgent, dstSchemaAgent);
    }

    @Test
    public void testGetSrcClogAgent() throws Exception {
        assertThat(testee.getSrcClogAgent(), is(srcClogAgent));
    }

    @Test
    public void testGetDstClogAgent() throws Exception {
        assertThat(testee.getDstClogAgent(), is(dstClogAgent));
    }

    @Test
    public void testGetCdcAgent() throws Exception {
        assertThat(testee.getCdcAgent(), is(cdcAgent));
    }

    @Test
    public void testGetDstSchemaAgent() throws Exception {
        assertThat(testee.getDstSchemaAgent(), is(dstSchemaAgent));
    }

    private AgentContext testee;
    private ClogAgent<Function<RelDb, RelDb>> srcClogAgent;
    private ClogAgent<Function<RelDb, RelDb>> dstClogAgent;
    private CdcAgent<Function<RelDb, RelDb>> cdcAgent;
    private SchemaAgent<Function<RelDb, RelDb>> dstSchemaAgent;
}