/*
 * Copyright (c) 2015 Persinity Inc.
 */

package com.persinity.ndt.etlmodule.haka;

import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.expectLastCall;
import static org.easymock.EasyMock.isA;

import java.util.Collections;
import java.util.Iterator;
import java.util.Set;

import org.easymock.EasyMockSupport;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.persinity.common.db.Closeable;
import com.persinity.haka.JobIdentity;
import com.persinity.ndt.etlmodule.WindowGenerator;
import com.persinity.ndt.transform.TransferWindow;

/**
 * @author Ivo Yanakiev
 */
public class TransferWindowJobProducerTest extends EasyMockSupport {

    @Before
    public void setUp() throws Exception {

        job = createMock(WindowGeneratorJob.class);
        windowGenerator = createMock(WindowGenerator.class);
        iter = createMock(Iterator.class);
        transferWindow = createMock(TransferWindow.class);

        testee = new TransferWindowJobProducer<>();
    }

    @Test
    public void testProcess() throws Exception {

        expect(job.getWindowGenerator()).andStubReturn(windowGenerator);
        expect(job.getCachedIter()).andStubReturn(null);
        expect(windowGenerator.iterator()).andStubReturn(iter);
        expect(job.getWindowCheckIntervalSeconds()).andStubReturn(1);

        job.setCachedIter(isA(Iterator.class));
        expectLastCall().times(2);

        expect(iter.hasNext()).andStubReturn(true);
        expect(iter.next()).andStubReturn(transferWindow);
        expect(transferWindow.isEmpty()).andStubReturn(false);
        expect(job.getId()).andStubReturn(new JobIdentity());
        expect(job.getContextName()).andStubReturn(STUB_CONTEXT_NAME);

        replayAll();

        Set<TransferWindowJob<Closeable, Closeable>> actualSet1 = testee.process(job);

        Set<TransferWindowJob<Closeable, Closeable>> actualSet2 = testee.process(job);

        verifyAll();

        assertProcessResult(actualSet1);

        assertProcessResult(actualSet2);

    }

    @Test
    public void testProcessNonNullIter() throws Exception {

        expect(job.getWindowGenerator()).andStubReturn(windowGenerator);
        expect(job.getCachedIter()).andStubReturn(iter);
        expect(iter.hasNext()).andStubReturn(true);
        expect(iter.next()).andStubReturn(transferWindow);
        expect(transferWindow.isEmpty()).andStubReturn(false);
        expect(job.getId()).andStubReturn(new JobIdentity());
        expect(job.getContextName()).andStubReturn(STUB_CONTEXT_NAME);

        replayAll();

        Set<TransferWindowJob<Closeable, Closeable>> actualSet = testee.process(job);

        verifyAll();

        Iterator<TransferWindowJob<Closeable, Closeable>> actualSetIterator = actualSet.iterator();
        Assert.assertTrue(actualSetIterator.hasNext());

        TransferWindowJob<Closeable, Closeable> actualJob = actualSetIterator.next();
        Assert.assertFalse(actualSetIterator.hasNext());
        Assert.assertEquals(actualJob.getContextName(), STUB_CONTEXT_NAME);
        Assert.assertEquals(actualJob.getTransferWindow(), transferWindow);
        Assert.assertEquals(actualJob.getClass(), TransferWindowJob.class);
    }

    @Test
    public void testProcessNonNullIterEmptyTransferWin() throws Exception {

        expect(job.getWindowGenerator()).andStubReturn(windowGenerator);
        expect(job.getCachedIter()).andStubReturn(iter);
        expect(iter.hasNext()).andStubReturn(true);
        expect(iter.next()).andStubReturn(transferWindow);
        expect(transferWindow.isEmpty()).andStubReturn(true);
        expect(job.getId()).andStubReturn(new JobIdentity());
        expect(job.getContextName()).andStubReturn(STUB_CONTEXT_NAME);

        replayAll();

        Set<TransferWindowJob<Closeable, Closeable>> actualSet = testee.process(job);

        verifyAll();

        Iterator<TransferWindowJob<Closeable, Closeable>> actualSetIterator = actualSet.iterator();
        Assert.assertTrue(actualSetIterator.hasNext());

        TransferWindowJob<Closeable, Closeable> actualJob = actualSetIterator.next();
        Assert.assertFalse(actualSetIterator.hasNext());
        Assert.assertEquals(actualJob.getContextName(), STUB_CONTEXT_NAME);
        Assert.assertEquals(actualJob.getTransferWindow(), transferWindow);
        Assert.assertEquals(actualJob.getClass(), TransferWindowIdleJob.class);

    }

    @Test
    public void testProcessEmptyIter() throws Exception {

        expect(job.getWindowGenerator()).andStubReturn(windowGenerator);
        expect(job.getCachedIter()).andStubReturn(iter);
        expect(iter.hasNext()).andStubReturn(false);

        replayAll();

        Set<TransferWindowJob<Closeable, Closeable>> actual = testee.process(job);

        verifyAll();

        Assert.assertEquals(Collections.emptySet(), actual);
    }

    private void assertProcessResult(Set<TransferWindowJob<Closeable, Closeable>> actualSet) {

        Iterator<TransferWindowJob<Closeable, Closeable>> actualSetIterator = actualSet.iterator();
        Assert.assertTrue(actualSetIterator.hasNext());

        TransferWindowJob<Closeable, Closeable> actualJob = actualSetIterator.next();
        Assert.assertFalse(actualSetIterator.hasNext());
        Assert.assertEquals(actualJob.getContextName(), STUB_CONTEXT_NAME);
        Assert.assertEquals(actualJob.getTransferWindow(), transferWindow);
        Assert.assertEquals(actualJob.getClass(), TransferWindowJob.class);
    }

    private static final String STUB_CONTEXT_NAME = "stub context name";

    private TransferWindowJobProducer<Closeable, Closeable> testee;
    private WindowGeneratorJob<Closeable, Closeable> job;
    private WindowGenerator<Closeable, Closeable> windowGenerator;
    private Iterator<TransferWindow<Closeable, Closeable>> iter;
    private TransferWindow<Closeable, Closeable> transferWindow;
}
