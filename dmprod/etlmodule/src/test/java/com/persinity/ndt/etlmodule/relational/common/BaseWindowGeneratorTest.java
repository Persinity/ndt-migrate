/**
 * Copyright (c) 2015 Persinity Inc.
 */
package com.persinity.ndt.etlmodule.relational.common;

import static org.easymock.EasyMock.createNiceMock;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Random;
import java.util.Set;

import org.easymock.EasyMock;
import org.junit.Assert;
import org.junit.Test;

import com.persinity.common.collection.DirectedEdge;
import com.persinity.common.collection.Pool;
import com.persinity.common.db.RelDb;
import com.persinity.common.db.metainfo.Col;
import com.persinity.ndt.db.TransactionId;
import com.persinity.ndt.dbagent.relational.AgentSqlStrategy;
import com.persinity.ndt.dbagent.relational.StringTid;
import com.persinity.ndt.transform.EntitiesDag;
import com.persinity.ndt.transform.ParamQryFunc;
import com.persinity.ndt.transform.TransferWindow;

/**
 * @author Doichin Yordanov
 */
public class BaseWindowGeneratorTest {

    private static final int TRANSACTIONS_PER_WINDOW = 3; // Transactions per Window
    private static final int TRANSACTION_SIZE = 2; // DMLs per transaction
    private static final Character TR_STATUS_UNPROCESSED = null;
    private static final List<String> tabNames = Arrays.asList("emp", "dept", "sale");
    private final Random rand = new Random();

    /**
     * Test method for {@link BaseWindowGenerator} constructor with null input.
     */
    @Test(expected = NullPointerException.class)
    public void testBaseWindowGeneratorDirectedEdgeOfDbDbSqlStrategy_1() {
        new BaseWindowGenerator(null, null, null, null, createNiceMock(AgentSqlStrategy.class),
                TRANSACTIONS_PER_WINDOW);
    }

    /**
     * Test method for {@link BaseWindowGenerator} constructor with null input.
     */
    @Test(expected = NullPointerException.class)
    public void testBaseWindowGeneratorDirectedEdgeOfDbDbSqlStrategy_2() {
        new BaseWindowGenerator(null, null, null, createNiceMock(EntityDagFunc.class), null, TRANSACTIONS_PER_WINDOW);
    }

    /**
     * Test method for {@link BaseWindowGenerator} constructor with null input.
     */
    @SuppressWarnings("unchecked")
    @Test(expected = NullPointerException.class)
    public void testBaseWindowGeneratorDirectedEdgeOfDbDbSqlStrategy_3() {
        new BaseWindowGenerator(createNiceMock(DirectedEdge.class), null, null, null, null, TRANSACTIONS_PER_WINDOW);
    }

    /**
     * Test method for {@link BaseWindowGenerator} constructor with null input.
     */
    @SuppressWarnings("unchecked")
    @Test(expected = NullPointerException.class)
    public void testBaseWindowGeneratorDirectedEdgeOfDbDbSqlStrategy_4() {
        new BaseWindowGenerator(null, createNiceMock(ParamQryFunc.class), null, null, null, TRANSACTIONS_PER_WINDOW);
    }

    /**
     * Test method for {@link BaseWindowGenerator} constructor with null input.
     */
    @SuppressWarnings("unchecked")
    @Test(expected = NullPointerException.class)
    public void testBaseWindowGeneratorDirectedEdgeOfDbDbSalStrategy_5() {
        new BaseWindowGenerator(null, null, createNiceMock(TidsLeftCntF.class), null, null, TRANSACTIONS_PER_WINDOW);
    }

    /**
     * Test method for {@link BaseWindowGenerator#iterator()}.
     */
    @SuppressWarnings("unchecked")
    @Test
    public void testIterator() {
        final DirectedEdge<Pool<RelDb>, Pool<RelDb>> dataPoolBridge = createNiceMock(DirectedEdge.class);
        final Pool<RelDb> pool = createNiceMock(Pool.class);
        final RelDb relDb = createNiceMock(RelDb.class);
        final AgentSqlStrategy sqlStrategy = createNiceMock(AgentSqlStrategy.class);
        final List<TransactionId> tids = new LinkedList<>();
        mockTids(sqlStrategy, tids);
        final EntityDagFunc entityDagF = createNiceMock(EntityDagFunc.class);
        final ParamQryFunc windowF = stubWindowF();
        final TidsLeftCntF unprocessedCntF = stubUnprocessedTidsF();
        final Set<String> anyEntities = EasyMock.anyObject();
        final EntitiesDag entityDag = createNiceMock(EntitiesDag.class);
        EasyMock.expect(entityDagF.apply(anyEntities)).andReturn(entityDag);
        EasyMock.expect(dataPoolBridge.src()).andStubReturn(pool);
        EasyMock.expect(pool.get()).andStubReturn(relDb);
        EasyMock.replay(dataPoolBridge, sqlStrategy, entityDagF, pool, relDb);

        final BaseWindowGenerator testee = new BaseWindowGenerator(dataPoolBridge, windowF, unprocessedCntF, entityDagF,
                sqlStrategy, 2);
        final Iterator<TransferWindow<RelDb, RelDb>> winIt = testee.iterator();

        Assert.assertTrue(winIt.hasNext());
        final TransferWindow<RelDb, RelDb> win = winIt.next();

        verityDataPoolBridge(dataPoolBridge, win);
        verifyDstEntities(entityDag, win);
        verifyTids(tids, win);

        testee.stopWhenFeedExhausted();
        Assert.assertFalse(winIt.hasNext());
        boolean exceptionCaught = false;
        try {
            winIt.next();
        } catch (final NoSuchElementException e) {
            exceptionCaught = true;
        }
        Assert.assertTrue(exceptionCaught);
    }

    /**
     * @return
     */
    private TidsLeftCntF stubUnprocessedTidsF() {
        final TidsLeftCntF result = new TidsLeftCntF() {
            @Override
            public Long apply(final BaseWindowGenerator input) {
                return 0L;
            }
        };
        return result;
    }

    private void mockTids(final AgentSqlStrategy sqlStrategy, final List<TransactionId> tids) {
        for (int i = 0; i < TRANSACTIONS_PER_WINDOW; i++) {
            final String tidValue = "T" + (i + 1);
            final TransactionId tid = new StringTid(tidValue);
            tids.add(tid);
            EasyMock.expect(sqlStrategy.newTransactionId(tidValue)).andStubReturn(tid);
        }
    }

    private void verifyTids(final List<TransactionId> tids, final TransferWindow<RelDb, RelDb> win) {
        Assert.assertEquals(tids, win.getSrcTids());
    }

    private void verifyDstEntities(final EntitiesDag entitiesDag, final TransferWindow<RelDb, RelDb> win) {
        Assert.assertEquals(entitiesDag, win.getDstEntitiesDag());
    }

    private void verityDataPoolBridge(final DirectedEdge<Pool<RelDb>, Pool<RelDb>> dataPoolBridge,
            final TransferWindow<RelDb, RelDb> win) {
        Assert.assertEquals(dataPoolBridge, win.getDataPoolBridge());
    }

    private ParamQryFunc stubWindowF() {
        final List<Map<String, Object>> trLogRecs = stubTrLogRecordSet();
        final List<Col> someParams = BaseWindowGenerator.TRLOG_COLS;
        final ParamQryFunc stub = new ParamQryFunc(someParams, "dummySql") {
            @Override
            public Iterator<Map<String, Object>> apply(final DirectedEdge<RelDb, List<?>> input) {
                return trLogRecs.iterator();
            }
        };
        return stub;
    }

    /**
     * @return Recordset that simulates TRLOG one.
     */
    private List<Map<String, Object>> stubTrLogRecordSet() {
        final List<Map<String, Object>> trLogRecs = new LinkedList<>();
        for (int i = 0; i < TRANSACTIONS_PER_WINDOW; i++) {
            for (int j = 0; j < TRANSACTION_SIZE; j++) {
                final Map<String, Object> rec = new HashMap<>();
                rec.put("tid", "T" + (i + 1));
                rec.put("last_gid", (i + 1) * (j + 1));
                rec.put("tab_name", getRandomTable());
                trLogRecs.add(rec);
            }
        }
        return trLogRecs;
    }

    private String getRandomTable() {
        final int i = Math.abs(rand.nextInt() % tabNames.size());
        final String tabName = tabNames.get(i);
        return tabName;
    }

}
