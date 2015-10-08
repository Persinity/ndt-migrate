/**
 * Copyright (c) 2015 Persinity Inc.
 */

package com.persinity.common.collection;

import static org.easymock.EasyMock.createNiceMock;
import static org.easymock.EasyMock.expect;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import java.lang.ref.SoftReference;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

import org.apache.log4j.Logger;
import org.easymock.EasyMock;
import org.junit.Test;

import com.google.common.base.Function;
import com.persinity.common.ReferentComparableSoftReference;
import com.persinity.common.db.KVStore;
import com.persinity.common.db.KVStoreTest.LongCloseable;
import com.persinity.common.logging.Log4jLogger;

/**
 * @author dyordanov
 */
public class ThreadBasedPoolTest {

    @Test
    public void testClose() throws Exception {
        final Pool<LongCloseable> testee = new ThreadBasedPool<>(stubCreateF(), stubCloseF(), GC_CONTINUOUS);
        testee.close();
        assertEquals(Collections.emptySet(), testee.entries());
    }

    @Test
    public void testGetRemoveEntries() throws Exception {
        final Pool<LongCloseable> testee = new ThreadBasedPool<>(stubCreateF(), stubCloseF(), GC_CONTINUOUS);

        final Set<LongCloseable> createdEntries = concurrentTest(testee, 20, 100);
        testee.close();
        checkCreatedEntriesAreClosed(createdEntries);
        assertEquals(Collections.emptySet(), testee.entries());
    }

    public static Function<Long, LongCloseable> stubCreateF() {
        return new Function<Long, LongCloseable>() {
            @Override
            public LongCloseable apply(final Long input) {
                final LongCloseable result = new LongCloseable(input.intValue());
                log.debug("Created {} in {}", result, Thread.currentThread());
                return result;
            }
        };
    }

    public static Function<LongCloseable, LongCloseable> stubCloseF() {
        return new Function<LongCloseable, LongCloseable>() {
            @Override
            public LongCloseable apply(final LongCloseable input) {
                log.debug("Closing {} in {}", input, Thread.currentThread());
                input.close();
                return input;
            }
        };
    }

    @Test
    public void testPrune() {
        final Thread t1 = createNiceMock(Thread.class);
        final Thread t2 = createNiceMock(Thread.class);
        final Thread t3 = createNiceMock(Thread.class);

        final KVStore<SoftReference<Thread>, Long> threadRegister = createNiceMock(KVStore.class);
        final SoftReference<Thread> r1 = new ReferentComparableSoftReference<>(t1);
        final SoftReference<Thread> r2 = new ReferentComparableSoftReference<>(t2);
        final SoftReference<Thread> r3 = new ReferentComparableSoftReference<>(t3);
        expect(threadRegister.keySet()).andReturn(new HashSet<>(Arrays.asList(r1, r2, r3)));

        final Function<Thread, Boolean> isDanglingTreadF = createNiceMock(Function.class);
        expect(isDanglingTreadF.apply(t1)).andReturn(false);
        expect(isDanglingTreadF.apply(t2)).andReturn(true);
        expect(isDanglingTreadF.apply(t3)).andReturn(true);

        EasyMock.replay(isDanglingTreadF, threadRegister);

        ThreadBasedPool.prune(isDanglingTreadF, threadRegister);

        EasyMock.verify(isDanglingTreadF);
        assertNotNull(r1.get());
        assertNull(r2.get());
        assertNull(r3.get());
    }

    public static void checkCreatedEntriesAreClosed(final Set<LongCloseable> createdEntries) {
        LongCloseable closedEntry = new LongCloseable(1);
        closedEntry.close();
        for (LongCloseable createdEntry : createdEntries) {
            assertEquals(closedEntry, createdEntry);
        }
    }

    public static Set<LongCloseable> concurrentTest(final Pool<LongCloseable> testee, final int threadNumber,
            final int maxOpsPerThread) throws Exception {

        final Set<Thread> threads = new HashSet<>();
        final Set<LongCloseable> createdEntries = new HashSet<>();
        final Random rand = new Random();
        final AtomicInteger ops = new AtomicInteger();
        final AtomicReference<Exception> re = new AtomicReference<>();

        long startTime = System.currentTimeMillis();

        for (int i = 0; i < threadNumber; i++) {
            threads.add(new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        log.debug("Starting workload for thread {}", Thread.currentThread());
                        final int opsNum = rand.nextInt(maxOpsPerThread) + 1;
                        for (int i = 0; i < opsNum; i++) {
                            final LongCloseable actual = testee.get();
                            ops.incrementAndGet();
                            createdEntries.add(actual);
                            assertEquals(testee.get(), actual);
                            if (i % (rand.nextInt(i > 0 ? i : 1) + 1) == 0) {
                                testee.remove(actual);
                                ops.incrementAndGet();
                                assertEquals(0, actual.value());
                            }
                        }
                        log.debug("Finished workload for thread {}", Thread.currentThread());
                    } catch (final Exception e) {
                        log.error(e.getMessage());
                        re.set(e);
                        throw e;
                    }
                }
            }));
        }

        for (final Thread thread : threads) {
            thread.start();
        }

        while (!threads.isEmpty()) {
            final Iterator<Thread> it = threads.iterator();
            while (it.hasNext()) {
                final Thread thread = it.next();
                thread.join(1000L);
                if (re.get() != null) {
                    throw re.get();
                }
                if (!thread.isAlive()) {
                    it.remove();
                }
            }
        }

        log.debug("Finished concurrent test over {} : {} ops in {} ms", testee, ops.get(),
                System.currentTimeMillis() - startTime);
        return createdEntries;
    }

    public static final long GC_CONTINUOUS = 1L;
    private static final Log4jLogger log = new Log4jLogger(Logger.getLogger(ThreadBasedPoolTest.class));
    private static final long GC_ABSENT = Long.MAX_VALUE;
}