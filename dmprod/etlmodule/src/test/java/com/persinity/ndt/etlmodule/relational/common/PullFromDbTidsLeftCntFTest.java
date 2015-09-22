/**
 * Copyright (c) 2015 Persinity Inc.
 */

package com.persinity.ndt.etlmodule.relational.common;

import static org.easymock.EasyMock.createStrictMock;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.expectLastCall;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.verify;
import static org.junit.Assert.assertEquals;

import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.junit.Test;

import com.persinity.common.collection.DirectedEdge;
import com.persinity.common.collection.Pool;
import com.persinity.common.db.RelDb;
import com.persinity.ndt.dbagent.relational.AgentSqlStrategy;
import com.persinity.ndt.dbagent.relational.oracle.OracleAgentSqlStrategy;

/**
 * @author dyordanov
 */
public class PullFromDbTidsLeftCntFTest {

    @Test(expected = NullPointerException.class)
    public void testPullFromDbTidsLeftCntFInvalidInput_1() throws Exception {
        new PullFromDbTidsLeftCntF(null);
    }

    @Test
    public void testApply() throws Exception {
        final AgentSqlStrategy sqlStrategy = new OracleAgentSqlStrategy();
        final BaseWindowGenerator winGen = createStrictMock(BaseWindowGenerator.class);
        final DirectedEdge<Pool<RelDb>, Pool<RelDb>> dataPoolBridge = createStrictMock(DirectedEdge.class);
        final Pool<RelDb> pool = createStrictMock(Pool.class);
        final RelDb db = createStrictMock(RelDb.class);
        final Iterator<Map<String, Object>> qryRes = stubRes().iterator();
        expect(winGen.getDataPoolBridge()).andReturn(dataPoolBridge);
        expect(dataPoolBridge.src()).andReturn(pool);
        expect(pool.get()).andReturn(db);
        expect(db.executeQuery(sqlStrategy.countUnprocessedTids())).andReturn(qryRes);
        db.close();
        expectLastCall();

        replay(db, dataPoolBridge, winGen, pool);

        final PullFromDbTidsLeftCntF testee = new PullFromDbTidsLeftCntF(sqlStrategy);
        final Long actual = testee.apply(winGen);

        verify(db, dataPoolBridge, winGen, pool);
        assertEquals(CNT_VAL, actual.longValue());
    }

    private List<Map<String, Object>> stubRes() {
        final List<Map<String, Object>> res = new LinkedList<>();
        final Map<String, Object> row = new HashMap<>();
        row.put(PullFromDbTidsLeftCntF.CNT, 9);
        res.add(row);
        return res;
    }

    private static final long CNT_VAL = 9L;
}
