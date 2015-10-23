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

import static org.easymock.EasyMock.anyObject;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.expectLastCall;

import java.util.Collections;
import java.util.Set;

import org.easymock.EasyMockSupport;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.persinity.common.collection.DirectedEdge;
import com.persinity.common.collection.Pool;
import com.persinity.common.db.RelDb;
import com.persinity.haka.Job;
import com.persinity.ndt.transform.TransferFunc;

/**
 * @author Ivo Yanakiev
 */
public class LuwJobProcessorTest extends EasyMockSupport {

    @Before
    public void setUp() throws Exception {
        testee = new LuwJobProcessor<>();
        luwJob = createMock(LuwJob.class);
        transferFunc = createMock(TransferFunc.class);
        dataPoolBridge = createMock(DirectedEdge.class);
        pool = createMock(Pool.class);
        relDb = createMock(RelDb.class);

    }

    @Test
    public void testProcess() throws Exception {

        expect(luwJob.getTransferFunc()).andStubReturn(transferFunc);

        expect(luwJob.getDataPoolBridge()).andStubReturn(dataPoolBridge);
        expect(dataPoolBridge.src()).andStubReturn(pool);
        expect(dataPoolBridge.dst()).andStubReturn(pool);
        expect(pool.get()).andReturn(relDb).anyTimes();
        relDb.close();
        expectLastCall().anyTimes();

        expect(transferFunc.apply(anyObject(DirectedEdge.class))).andStubReturn(null);

        replayAll();

        Set<Job> actual = testee.process(luwJob);

        verifyAll();

        Assert.assertEquals(Collections.emptySet(), actual);
    }

    @Test(expected = UnsupportedOperationException.class)
    public void testProcessed1() throws Exception {
        testee.processed(null, null);
    }

    @Test(expected = UnsupportedOperationException.class)
    public void testProcessed2() throws Exception {
        testee.processed(luwJob, null);
    }

    @Test(expected = UnsupportedOperationException.class)
    public void testProcessed3() throws Exception {
        testee.processed(luwJob, luwJob);
    }

    private LuwJobProcessor<RelDb, RelDb> testee;
    private LuwJob<RelDb, RelDb> luwJob;
    private TransferFunc<RelDb, RelDb> transferFunc;
    private DirectedEdge<Pool<RelDb>, Pool<RelDb>> dataPoolBridge;
    private Pool<RelDb> pool;
    private RelDb relDb;
}
