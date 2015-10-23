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

package com.persinity.ndt.etlmodule.haka;

import static java.util.Arrays.asList;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.expectLastCall;
import static org.easymock.EasyMock.isA;

import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import org.easymock.EasyMockSupport;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.persinity.common.collection.DirectedEdge;
import com.persinity.common.collection.Pool;
import com.persinity.common.db.RelDb;
import com.persinity.common.db.SqlStrategy;
import com.persinity.haka.JobIdentity;
import com.persinity.ndt.dbagent.relational.SchemaInfo;
import com.persinity.ndt.etlmodule.EtlPlanDag;
import com.persinity.ndt.etlmodule.EtlPlanGenerator;
import com.persinity.ndt.etlmodule.TransferFunctor;
import com.persinity.ndt.etlmodule.haka.context.ContextFactory;
import com.persinity.ndt.etlmodule.haka.context.ContextFactoryProvider;
import com.persinity.ndt.etlmodule.relational.common.BaseEtlPlanGenerator;
import com.persinity.ndt.etlmodule.relational.common.NoOpsRelTransferFunctor;
import com.persinity.ndt.etlmodule.relational.common.RelTransferFunctor;
import com.persinity.ndt.transform.TransferWindow;

/**
 * @author Ivo Yanakiev
 */
public class TransferEntityJobProducerTest extends EasyMockSupport {

    @Before
    public void setUp() throws Exception {

        job = createMock(TransferWindowJob.class);
        transferWindow = createMock(TransferWindow.class);
        iter = createMock(Iterator.class);
        relTransferFunctor = createMock(RelTransferFunctor.class);
        otherRelTransferFunctor = createMock(RelTransferFunctor.class);
        noOpFunctor = createMock(NoOpsRelTransferFunctor.class);
        otherNoOpFunctor = createMock(NoOpsRelTransferFunctor.class);

        dataPoolBridge = createMock(DirectedEdge.class);
        pool = createMock(Pool.class);
        relDb = createMock(RelDb.class);
        factory = ContextFactoryProvider.getFactory();
        // create partial mock
        etlPlanGenerator = createMockBuilder(BaseEtlPlanGenerator.class).createMock();
        etlPlanDag = new EtlPlanDag<>();
        factory.setContext(STUB_CONTEXT_NAME, new EtlContext<>(etlPlanGenerator));

        tWindow = createMock(TransferWindow.class);
        schemas = createMock(DirectedEdge.class);
        sqlStrategy = createMock(SqlStrategy.class);

        testee = new TransferEntityJobProducer<>();
    }

    @Test
    public void testProcessOneFunc() throws Exception {

        expect(job.getTransferWindow()).andStubReturn(transferWindow);
        expect(transferWindow.isEmpty()).andStubReturn(false);
        expect(job.getCachedIter()).andStubReturn(null);
        expect(job.getContextName()).andStubReturn(STUB_CONTEXT_NAME);

        // no-op func, no-op func, rel transfer func
        etlPlanDag.addVertex(noOpFunctor);
        etlPlanDag.addVertex(otherNoOpFunctor);
        etlPlanDag.addVertex(relTransferFunctor);

        expect(job.getId()).andStubReturn(new JobIdentity());
        expect(transferWindow.getDataPoolBridge()).andStubReturn(dataPoolBridge);
        expect(dataPoolBridge.src()).andStubReturn(pool);
        expect(dataPoolBridge.dst()).andStubReturn(pool);
        expect(pool.get()).andStubReturn(relDb);
        expect(etlPlanGenerator.newEtlPlan(isA(TransferWindow.class))).andStubReturn(etlPlanDag);

        job.setCachedIter(isA(Iterator.class));
        expectLastCall().times(1);

        replayAll();

        Set<TransferEntityJob<RelDb, RelDb>> actualSet1 = testee.process(job);

        verifyAll();

        assertSingleFunctor(actualSet1, relTransferFunctor);
    }

    @Test
    public void testProcessTwoFunc() throws Exception {
        TransferWindowJob<RelDb, RelDb> job = createMock(TransferWindowJob.class);
        expect(job.getTransferWindow()).andStubReturn(transferWindow);
        expect(transferWindow.isEmpty()).andStubReturn(false);
        final Iterator<Set<TransferFunctor<RelDb, RelDb>>> it = createStrictMock(Iterator.class);
        expect(job.getCachedIter()).andStubReturn(it);
        expect(job.getContextName()).andStubReturn(STUB_CONTEXT_NAME);

        expect(it.hasNext()).andReturn(true);
        expect(it.next()).andReturn(new HashSet<TransferFunctor<RelDb, RelDb>>(asList(relTransferFunctor)));
        expect(it.hasNext()).andReturn(true);
        expect(it.next()).andReturn(new HashSet<TransferFunctor<RelDb, RelDb>>(asList(noOpFunctor)));
        expect(it.hasNext()).andReturn(true);
        expect(it.next()).andReturn(new HashSet<TransferFunctor<RelDb, RelDb>>(asList(otherRelTransferFunctor)));
        expect(it.hasNext()).andReturn(false);

        expect(job.getId()).andStubReturn(new JobIdentity());
        expect(transferWindow.getDataPoolBridge()).andStubReturn(dataPoolBridge);
        expect(dataPoolBridge.src()).andStubReturn(pool);
        expect(dataPoolBridge.dst()).andStubReturn(pool);
        expect(pool.get()).andStubReturn(relDb);

        replayAll();

        Set<TransferEntityJob<RelDb, RelDb>> actualSet1 = testee.process(job);
        Set<TransferEntityJob<RelDb, RelDb>> actualSet2 = testee.process(job);
        Set<TransferEntityJob<RelDb, RelDb>> actualSet3 = testee.process(job);

        verifyAll();

        assertSingleFunctor(actualSet1, relTransferFunctor);
        assertSingleFunctor(actualSet2, otherRelTransferFunctor);
        Assert.assertEquals(actualSet3, Collections.emptySet());
    }

    @Test
    public void testProcessNoOps() throws Exception {

        expect(job.getTransferWindow()).andStubReturn(transferWindow);
        expect(transferWindow.isEmpty()).andStubReturn(false);
        expect(job.getCachedIter()).andStubReturn(iter);
        expect(job.getContextName()).andStubReturn(STUB_CONTEXT_NAME);

        expect(iter.hasNext()).andReturn(true);
        expect(iter.next()).andReturn(Collections.<TransferFunctor<RelDb, RelDb>>singleton(noOpFunctor));
        expect(iter.hasNext()).andReturn(true);
        expect(iter.next()).andReturn(Collections.<TransferFunctor<RelDb, RelDb>>singleton(noOpFunctor));
        expect(iter.hasNext()).andReturn(false);

        replayAll();

        final Set<TransferEntityJob<RelDb, RelDb>> actualSet = testee.process(job);
        Assert.assertEquals(Collections.emptySet(), actualSet);

        verifyAll();

    }

    @Test
    public void testProcessEmptyTransferWin() throws Exception {

        expect(job.getTransferWindow()).andStubReturn(transferWindow);
        expect(transferWindow.isEmpty()).andStubReturn(true);

        replayAll();

        Set<TransferEntityJob<RelDb, RelDb>> actual = testee.process(job);

        verifyAll();

        Assert.assertEquals(Collections.emptySet(), actual);
    }

    private void assertSingleFunctor(final Set<TransferEntityJob<RelDb, RelDb>> actualSet1,
            final RelTransferFunctor relTransferFunctor) {
        Assert.assertNotNull(actualSet1);
        Assert.assertEquals(1, actualSet1.size());
        Assert.assertEquals(actualSet1.iterator().next().getFunctor(), relTransferFunctor);
    }

    private static final String STUB_CONTEXT_NAME = "stub context name";

    private TransferEntityJobProducer<RelDb, RelDb> testee;
    private TransferWindowJob<RelDb, RelDb> job;
    private TransferWindow<RelDb, RelDb> transferWindow;
    private Iterator<Set<TransferFunctor<RelDb, RelDb>>> iter;
    private RelTransferFunctor relTransferFunctor;
    private RelTransferFunctor otherRelTransferFunctor;
    private NoOpsRelTransferFunctor noOpFunctor;
    private NoOpsRelTransferFunctor otherNoOpFunctor;
    private ContextFactory factory;
    private EtlPlanGenerator<RelDb, RelDb> etlPlanGenerator;
    private EtlPlanDag<RelDb, RelDb> etlPlanDag;
    private DirectedEdge<Pool<RelDb>, Pool<RelDb>> dataPoolBridge;
    private Pool<RelDb> pool;
    private RelDb relDb;

    private TransferWindow<RelDb, RelDb> tWindow;
    private DirectedEdge<SchemaInfo, SchemaInfo> schemas;
    private SqlStrategy sqlStrategy;
}
