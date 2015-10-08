/*
 * Copyright (c) 2015 Persinity Inc.
 */

package com.persinity.ndt.etlmodule.haka;

import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.expectLastCall;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import org.easymock.EasyMockSupport;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.persinity.common.collection.DirectedEdge;
import com.persinity.common.collection.Pool;
import com.persinity.common.db.Closeable;
import com.persinity.haka.JobIdentity;
import com.persinity.ndt.etlmodule.TransferFunctor;
import com.persinity.ndt.transform.TransferFunc;

/**
 * @author Ivo Yanakiev
 */
public class LuwJobProducerTest extends EasyMockSupport {

    @Before
    public void setUp() throws Exception {
        testee = new LuwJobProducer();
        transferJob = createMock(TransferEntityJob.class);
        directedEdge = createMock(DirectedEdge.class);
        function = createMock(TransferFunctor.class);

        func1 = createMock(TransferFunc.class);
        func2 = createMock(TransferFunc.class);
        func3 = createMock(TransferFunc.class);

        transferFuncSet = new HashSet<TransferFunc<Closeable, Closeable>>() {{
            add(func1);
            add(func2);
            add(func3);
        }};

    }

    @Test
    public void testProcess() throws Exception {

        expect(transferJob.isProcessed()).andStubReturn(false);
        transferJob.setProcessed();
        expectLastCall().andVoid();

        expect(transferJob.getDataPoolBridge()).andStubReturn(directedEdge);
        expect(transferJob.getFunctor()).andStubReturn(function);

        expect(function.apply(null)).andStubReturn(transferFuncSet);

        expect(transferJob.getId()).andStubReturn(jobId1);
        expect(transferJob.getId()).andStubReturn(jobId2);
        expect(transferJob.getId()).andStubReturn(jobId3);

        replayAll();

        Set<LuwJob<Closeable, Closeable>> actual = testee.process(transferJob);

        verifyAll();

        Assert.assertEquals(transferFuncSet.size(), actual.size());
    }

    @Test(expected = IllegalStateException.class)
    public void testProcessNullTransferFuncSet() throws Exception {

        expect(transferJob.isProcessed()).andStubReturn(false);
        transferJob.setProcessed();
        expectLastCall().andVoid();

        expect(transferJob.getDataPoolBridge()).andStubReturn(directedEdge);
        expect(transferJob.getFunctor()).andStubReturn(function);

        expect(function.apply(null)).andStubReturn(null);

        replayAll();

        testee.process(transferJob);
    }

    @Test
    public void testProcessProcessedJob() throws Exception {

        expect(transferJob.isProcessed()).andStubReturn(true);

        replayAll();

        Set<LuwJob<Closeable, Closeable>> actual = testee.process(transferJob);

        verifyAll();

        Assert.assertEquals(Collections.emptySet(), actual);

    }

    private LuwJobProducer testee;
    private TransferEntityJob<Closeable, Closeable> transferJob;
    private DirectedEdge<Pool<Closeable>, Pool<Closeable>> directedEdge;
    private TransferFunctor<Closeable, Closeable> function;
    private Set<TransferFunc<Closeable, Closeable>> transferFuncSet;

    private TransferFunc<Closeable, Closeable> func1;
    private TransferFunc<Closeable, Closeable> func2;
    private TransferFunc<Closeable, Closeable> func3;

    private final JobIdentity jobId1 = new JobIdentity();
    private final JobIdentity jobId2 = new JobIdentity();
    private final JobIdentity jobId3 = new JobIdentity();

}
