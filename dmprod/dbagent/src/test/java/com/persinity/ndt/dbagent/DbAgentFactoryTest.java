/**
 * Copyright (c) 2015 Persinity Inc.
 */

package com.persinity.ndt.dbagent;

import static org.easymock.EasyMock.createMock;
import static org.easymock.EasyMock.createNiceMock;
import static org.easymock.EasyMock.createStrictMock;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.expectLastCall;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.verify;

import org.junit.Assert;
import org.junit.Test;

import com.google.common.base.Function;
import com.persinity.common.db.RelDb;
import com.persinity.common.db.metainfo.Schema;
import com.persinity.ndt.dbagent.relational.AgentSqlStrategy;
import com.persinity.ndt.dbagent.relational.DbAgentTracker;
import com.persinity.ndt.dbagent.relational.RelClogAgent;

/**
 * @author dyordanov
 */
public class DbAgentFactoryTest {
    @Test
    public void testDispatchAgent() throws Exception {
        final Schema schema = createMock(Schema.class);
        final AgentSqlStrategy sqlStrategy = createNiceMock(AgentSqlStrategy.class);
        final DbAgentTracker dbAgentTracker = createStrictMock(DbAgentTracker.class);

        expect(schema.getUserName()).andStubReturn("testapp");
        replay(schema, sqlStrategy);
        final DbAgentFactory<Function<RelDb, RelDb>> testee = new DbAgentFactory<>(schema, sqlStrategy, dbAgentTracker);

        final RelDb ndtDb = createMock(RelDb.class);
        final RelClogAgent agent = createMock(RelClogAgent.class);

        expect(ndtDb.getUserName()).andStubReturn("ndt");

        final Function<RelDb, RelClogAgent> dispatchF = createStrictMock(Function.class);
        expect(dbAgentTracker.isAgentDispatched(ndtDb, RelClogAgent.class)).andReturn(false);
        expect(dispatchF.apply(ndtDb)).andReturn(agent);
        dbAgentTracker.agentDispatched(ndtDb, agent);
        expectLastCall();
        expect(dbAgentTracker.isAgentDispatched(ndtDb, RelClogAgent.class)).andReturn(true);
        expect(dbAgentTracker.getDispatchedAgent(ndtDb, RelClogAgent.class)).andReturn(agent);

        replay(dbAgentTracker, ndtDb, agent, dispatchF);

        RelClogAgent actualAgent = testee.dispatchAgent(ndtDb, RelClogAgent.class, dispatchF);
        Assert.assertEquals(agent, actualAgent);
        actualAgent = testee.dispatchAgent(ndtDb, RelClogAgent.class, dispatchF);
        Assert.assertEquals(agent, actualAgent);

        verify(dbAgentTracker, dispatchF);
    }
}