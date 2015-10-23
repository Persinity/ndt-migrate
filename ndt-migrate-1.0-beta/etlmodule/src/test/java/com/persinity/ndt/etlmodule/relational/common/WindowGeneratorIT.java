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

import static com.persinity.common.Config.loadPropsFrom;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.Collections;
import java.util.Iterator;
import java.util.Set;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import com.google.common.base.Function;
import com.google.common.collect.Sets;
import com.persinity.common.Resource;
import com.persinity.common.collection.DirectedEdge;
import com.persinity.common.collection.Pool;
import com.persinity.common.db.DbConfig;
import com.persinity.common.db.RelDb;
import com.persinity.common.db.SimpleRelDb;
import com.persinity.ndt.db.TransactionId;
import com.persinity.ndt.dbagent.DbAgentFactory;
import com.persinity.ndt.dbagent.relational.AgentSqlStrategy;
import com.persinity.ndt.dbagent.relational.DbAgentTracker;
import com.persinity.ndt.dbagent.relational.StringTid;
import com.persinity.ndt.dbagent.relational.oracle.OracleAgentSqlStrategy;
import com.persinity.ndt.etlmodule.WindowGenerator;
import com.persinity.ndt.etlmodule.relational.migrate.MigrateWindowGenerator;
import com.persinity.ndt.etlmodule.relational.transform.TransformWindowGenerator;
import com.persinity.ndt.transform.EntitiesDag;
import com.persinity.ndt.transform.TransferWindow;

/**
 * Tests that the various window generators return valid data:<BR>
 * - The count query which is used to check in hasNext (window)
 * - The query which extracts window data on next
 *
 * @author dyordanov
 */
@Ignore("TODO: ticket#198")
public class WindowGeneratorIT {

    @Before
    public void setUp() {

        final DbConfig dbConfig = new DbConfig(loadPropsFrom(DB_PROPS_FILE), DB_PROPS_FILE, "src.ndt.");
        final RelDb dbSrc = new SimpleRelDb(dbConfig);
        final RelDb dbDst = new SimpleRelDb(dbConfig);

        dataPoolBridge = new DirectedEdge<Pool<RelDb>, Pool<RelDb>>(new OneInstancePool(dbSrc),
                new OneInstancePool(dbDst));

        sqlStrategy = new OracleAgentSqlStrategy();

        RelDb src = dataPoolBridge.src().get();

        // Re/create TRLOG
        final DbAgentTracker dbAgentTracker = new DbAgentTracker();
        final DbAgentFactory<Function<RelDb, RelDb>> dbAgentFactory = new DbAgentFactory<>(src.metaInfo(), sqlStrategy,
                dbAgentTracker);
        dbAgentFactory.dispatchClogAgent(src);

        // Populate TRLOG
        dbSrc.executeScript("wingen-it.sql");
        dbSrc.executeSp("gen_test_trlog(?)", TRLOG_CNT);
    }

    @Test
    public void testMigrateWindowGenerator() {
        final WindowGenerator<RelDb, RelDb> testee = new MigrateWindowGenerator(dataPoolBridge, sqlStrategy, WIN_SIZE);
        testee.stopWhenFeedExhausted();
        final Iterator<TransferWindow<RelDb, RelDb>> it = testee.iterator();

        verify(it, WIN1_TABS, WIN2_TABS);
    }

    @Test
    public void testTransformWindowGenerator() {
        final EntitiesDag dag = new EntitiesDag(WIN1_TABS);
        final WindowGenerator<RelDb, RelDb> testee = new TransformWindowGenerator(dataPoolBridge, dag, sqlStrategy,
                WIN_SIZE);
        testee.stopWhenFeedExhausted();
        final Iterator<TransferWindow<RelDb, RelDb>> it = testee.iterator();

        verify(it, WIN1_TABS, WIN1_TABS);
    }

    private void verify(final Iterator<TransferWindow<RelDb, RelDb>> it, final Set<String> win1Tabs,
            final Set<String> win2Tabs) {
        assertNotNull(it);

        verifyNextWin(it, WIN1_TIDS, win1Tabs);

        resource.accessAndClose(new Resource.Accessor<RelDb, Void>(dataPoolBridge.src().get(), null) {
            @Override
            public Void access(final RelDb resource) throws Exception {
                resource.executeDmdl("DELETE FROM trlog WHERE last_gid < 21");
                resource.commit();

                verifyNextWin(it, WIN2_TIDS, win2Tabs);
                resource.executeDmdl("DELETE FROM trlog WHERE last_gid < 41");
                resource.commit();
                return null;
            }
        });

        assertFalse(it.hasNext());
    }

    private void verifyNextWin(final Iterator<TransferWindow<RelDb, RelDb>> it, Set<? extends TransactionId> tids,
            Set<String> entities) {
        assertTrue(it.hasNext());
        final TransferWindow<RelDb, RelDb> win = it.next();
        assertEquals(entities, win.getDstEntitiesDag().vertexSet());
        assertEquals(tids, Sets.newHashSet(win.getSrcTids()));
    }

    private static class OneInstancePool implements Pool<RelDb> {

        public OneInstancePool(final RelDb instance) {
            this.instance = instance;
        }

        @Override
        public RelDb get() {
            return instance;
        }

        @Override
        public void remove(final RelDb value) {

        }

        @Override
        public Set<RelDb> entries() {
            return Collections.singleton(instance);
        }

        @Override
        public void close() throws RuntimeException {
            instance.close();
        }

        private final RelDb instance;
    }

    private static final String DB_PROPS_FILE = "etl-it.properties";
    private static final int TRLOG_CNT = 40;
    private static final Set<String> WIN1_TABS = Sets
            .newHashSet("tab1", "tab3", "tab5", "tab7", "tab9", "tab11", "tab13", "tab15", "tab17", "tab19");
    private static final Set<String> WIN2_TABS = Sets
            .newHashSet("tab21", "tab23", "tab25", "tab27", "tab29", "tab31", "tab33", "tab35", "tab37", "tab39");
    private static final int WIN_SIZE = 10;
    private static final Set<? extends TransactionId> WIN1_TIDS = Sets
            .newHashSet(new StringTid("T1"), new StringTid("T3"), new StringTid("T5"), new StringTid("T7"),
                    new StringTid("T9"), new StringTid("T11"), new StringTid("T13"), new StringTid("T15"),
                    new StringTid("T17"), new StringTid("T19"));
    private static final Set<? extends TransactionId> WIN2_TIDS = Sets
            .newHashSet(new StringTid("T21"), new StringTid("T23"), new StringTid("T25"), new StringTid("T27"),
                    new StringTid("T29"), new StringTid("T31"), new StringTid("T33"), new StringTid("T35"),
                    new StringTid("T37"), new StringTid("T39"));
    private DirectedEdge<Pool<RelDb>, Pool<RelDb>> dataPoolBridge;
    private AgentSqlStrategy sqlStrategy;
    private final Resource resource = new Resource();
}
