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
package com.persinity.ndt.controller.step;

import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.expectLastCall;

import org.junit.Test;

import com.google.common.base.Function;
import com.persinity.common.ThreadUtil;
import com.persinity.common.collection.DirectedEdge;
import com.persinity.common.collection.Pool;
import com.persinity.common.db.RelDb;
import com.persinity.ndt.controller.script.Step;
import com.persinity.ndt.db.RelDbPoolFactory;
import com.persinity.ndt.dbagent.ClogAgent;
import com.persinity.ndt.dbagent.DbAgentExecutor;

/**
 * @author Ivan Dachev
 */
public class ClogGcTest extends NdtStepBaseTest {

    @SuppressWarnings("unchecked")
    @Test
    public void testWork() throws Exception {
        expect(ndtControllerConfig.getDbAgentClogGcIntervalSeconds()).andStubReturn(1);

        final DbAgentExecutor dbAgentExecutor = createMock(DbAgentExecutor.class);
        expect(ndtController.getDbAgentExecutor()).andReturn(dbAgentExecutor);

        final AgentContext agentContext = createMock(AgentContext.class);

        final ClogAgent<Function<RelDb, RelDb>> srcClogAgent = createMock(ClogAgent.class);
        expect(agentContext.getSrcClogAgent()).andReturn(srcClogAgent).times(2);

        final ClogAgent<Function<RelDb, RelDb>> dstClogAgent = createMock(ClogAgent.class);
        expect(agentContext.getDstClogAgent()).andReturn(dstClogAgent).times(2);

        ctx.put(AgentContext.class, agentContext);

        final RelDbPoolFactory relDbPoolFactory = createMock(RelDbPoolFactory.class);
        final Pool<RelDb> srcDbPool = createMock(Pool.class);
        expect(ndtController.getRelDbPoolFactory()).andStubReturn(relDbPoolFactory);
        final DirectedEdge<Pool<RelDb>, Pool<RelDb>> ndtBridge = createMock(DirectedEdge.class);
        expect(relDbPoolFactory.ndtBridge()).andReturn(ndtBridge).times(REPETITIONS);
        expect(ndtBridge.src()).andReturn(srcDbPool).times(REPETITIONS);
        final RelDb srcDb = createMock(RelDb.class);
        expect(srcDbPool.get()).andReturn(srcDb).times(REPETITIONS);

        final Pool<RelDb> dstDbPool = createMock(Pool.class);
        expect(ndtController.getRelDbPoolFactory()).andStubReturn(relDbPoolFactory);
        expect(relDbPoolFactory.ndtBridge()).andReturn(ndtBridge).times(REPETITIONS);
        expect(ndtBridge.dst()).andReturn(dstDbPool).times(REPETITIONS);
        final RelDb dstDb = createMock(RelDb.class);
        expect(dstDbPool.get()).andReturn(dstDb).times(REPETITIONS);

        dbAgentExecutor.clogAgentGc(srcClogAgent, srcDb);
        expectLastCall().times(REPETITIONS);

        dbAgentExecutor.clogAgentGc(dstClogAgent, dstDb);
        expectLastCall().times(REPETITIONS);

        srcDb.close();
        expectLastCall().times(REPETITIONS);
        dstDb.close();
        expectLastCall().times(REPETITIONS);

        replayAll();

        final ClogGc testee = new ClogGc(null, Step.NO_DELAY, ctx);

        final Thread th = new Thread() {
            @Override
            public void run() {
                ThreadUtil.sleep(2500);
                testee.sigStop();
            }
        };

        th.start();
        testee.work();

        verifyAll();
    }

    public static final int REPETITIONS = 2;
}