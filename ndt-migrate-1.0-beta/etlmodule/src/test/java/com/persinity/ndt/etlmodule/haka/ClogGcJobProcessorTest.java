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

import static org.easymock.EasyMock.createNiceMock;
import static org.easymock.EasyMock.createStrictMock;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.expectLastCall;
import static org.easymock.EasyMock.replay;

import org.easymock.EasyMock;
import org.junit.Test;

import com.google.common.base.Function;
import com.persinity.common.collection.Pool;
import com.persinity.common.db.RelDb;
import com.persinity.haka.Job;

/**
 * @author dyordanov
 */
public class ClogGcJobProcessorTest {
    @Test
    public void testProcess() throws Exception {
        final ClogGcJobProcessor testee = new ClogGcJobProcessor();
        final ClogGcJob clogGcJob = createStrictMock(ClogGcJob.class);
        final Function<RelDb, RelDb> gcF = createStrictMock(Function.class);
        final Pool<RelDb> dbPool = createStrictMock(Pool.class);
        final RelDb db = createNiceMock(RelDb.class);
        expect(clogGcJob.getDbPool()).andStubReturn(dbPool);
        expect(dbPool.get()).andReturn(db);
        expect(clogGcJob.getGcF()).andReturn(gcF);
        expect(gcF.apply(db)).andReturn(db);
        db.close();
        expectLastCall();
        replay(db, clogGcJob, dbPool, gcF);

        testee.process(clogGcJob);

        EasyMock.verify(db, dbPool, clogGcJob, gcF);
    }

    @Test(expected = UnsupportedOperationException.class)
    public void testProcessed() throws Exception {
        final ClogGcJobProcessor testee = new ClogGcJobProcessor();
        final Job childJob = createNiceMock(Job.class);
        final ClogGcJob parentJob = createNiceMock(ClogGcJob.class);
        testee.processed(parentJob, childJob);
    }
}