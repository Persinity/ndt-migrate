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