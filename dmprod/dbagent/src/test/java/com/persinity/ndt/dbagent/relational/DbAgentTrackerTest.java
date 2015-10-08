/**
 * Copyright (c) 2015 Persinity Inc.
 */

package com.persinity.ndt.dbagent.relational;

import static org.easymock.EasyMock.createMock;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.replay;
import static org.junit.Assert.assertEquals;

import org.easymock.EasyMock;
import org.junit.Test;

import com.persinity.common.db.RelDb;

/**
 * @author dyordanov
 */
public class DbAgentTrackerTest {
    @Test
    public void test() throws Exception {
        final DbAgentTracker testee = new DbAgentTracker();

        final RelDb ndtDb1 = createMock(RelDb.class);
        expect(ndtDb1.getUserName()).andStubReturn("ndtDb1");
        final RelDb ndtDb2 = createMock(RelDb.class);
        expect(ndtDb2.getUserName()).andStubReturn("ndtDb2");

        replay(ndtDb1, ndtDb2);

        boolean actual = testee.isAgentDispatched(ndtDb1, RelClogAgent.class);
        assertEquals(false, actual);

        final RelClogAgent agent = EasyMock.createNiceMock(RelClogAgent.class);
        testee.agentDispatched(ndtDb1, agent);
        actual = testee.isAgentDispatched(ndtDb1, agent.getClass());
        assertEquals(true, actual);
        actual = testee.isAgentDispatched(ndtDb2, agent.getClass());
        assertEquals(false, actual);
        actual = testee.isAgentDispatched(ndtDb1, RelCdcAgent.class);
        assertEquals(false, actual);

        final RelClogAgent actualAgent = testee.getDispatchedAgent(ndtDb1, agent.getClass());
        assertEquals(agent, actualAgent);
    }
}