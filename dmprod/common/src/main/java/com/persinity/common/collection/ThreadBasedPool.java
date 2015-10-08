/**
 * Copyright (c) 2015 Persinity Inc.
 */

package com.persinity.common.collection;

import static com.persinity.common.StringUtils.format;
import static com.persinity.common.StringUtils.formatObj;
import static com.persinity.common.invariant.Invariant.assertArg;
import static com.persinity.common.invariant.Invariant.notNull;

import java.lang.ref.SoftReference;
import java.util.Iterator;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;

import org.apache.log4j.Logger;

import com.google.common.base.Function;
import com.persinity.common.ReferentComparableSoftReference;
import com.persinity.common.ThreadUtil;
import com.persinity.common.db.Closeable;
import com.persinity.common.db.KVStore;
import com.persinity.common.logging.Log4jLogger;

/**
 * Generic pool for {@link Closeable} resources (entries), which are <b>maintained per the thread</b> that requested them.
 * Periodically the pool checks for dangling resources, such as resources used in dead threads and closes them (Pool GC).
 * Thread-safe. <b>Do NOT share pooled resources between different threads.</b>
 *
 * @author dyordanov
 */
public class ThreadBasedPool<T extends Closeable> implements Runnable, Pool<T> {

    /**
     * @param newEntryF
     *         Generator function for used to create the new pool entries.
     * @param closeEntryF
     *         Clean up function used during removal of entries.
     * @param poolGcPeriodMs
     *         How often the pool should be checked for dangling resources to be closed
     */
    public ThreadBasedPool(final Function<Long, T> newEntryF, final Function<T, T> closeEntryF,
            final long poolGcPeriodMs) {
        this(newEntryF, closeEntryF, danglingThreadF, poolGcPeriodMs);
    }

    ThreadBasedPool(final Function<Long, T> newEntryF, final Function<T, T> closeEntryF,
            final Function<Thread, Boolean> isDanglingThreadF, final long poolGcPeriodMs) {
        notNull(newEntryF);
        notNull(closeEntryF);
        notNull(isDanglingThreadF);
        assertArg(poolGcPeriodMs > 0);

        entryStore = new KVStore<>(newEntryF, closeEntryF);
        this.poolGcPeriodMs = poolGcPeriodMs;
        this.isDanglingThreadF = isDanglingThreadF;
        final Function<SoftReference<Thread>, Long> newSessionF = new Function<SoftReference<Thread>, Long>() {
            final AtomicLong sessId = new AtomicLong(0L);

            @Override
            public Long apply(final SoftReference<Thread> input) {
                long id = sessId.incrementAndGet();
                entryStore.get(id);
                return id;
            }
        };
        final Function<Long, Long> closeSessionF = new Function<Long, Long>() {
            @Override
            public Long apply(final Long input) {
                entryStore.remove(input);
                return input;
            }
        };
        threadRegister = new KVStore<>(newSessionF, closeSessionF);
    }

    @Override
    public void run() {
        while (!stopRequested.get()) {
            ThreadUtil.sleep(poolGcPeriodMs);
            prune(isDanglingThreadF, threadRegister);
        }
    }

    @Override
    public void close() {
        stopRequested.getAndSet(true);
        prune(allThreadsF, threadRegister);
        assert threadRegister.keySet().isEmpty() && entryStore.keySet().isEmpty();
    }

    /**
     * Returns the cached resource for the current thread. If no such, creates one, caches it and returns it.<BR>
     * Do NOT share the returned resources among different threads.
     *
     * @return cached resource
     */
    @Override
    public T get() {
        if (stopRequested.get()) {
            throw new IllegalStateException();
        }
        final Thread thread = Thread.currentThread();
        final Long sessId = threadRegister.get(new ReferentComparableSoftReference<Thread>(thread));
        return entryStore.get(sessId);
    }

    /**
     * Removes the entry for the current thread, if such entry is found.
     */
    @Override
    public void remove(final T value) {
        final Thread thread = Thread.currentThread();
        threadRegister.remove(new ReferentComparableSoftReference<>(thread));
    }

    @Override
    public Set<T> entries() {
        return entryStore.entries();
    }

    @Override
    public String toString() {
        if (toString == null) {
            toString = format("{}({}, GC frequency {})", formatObj(this), stopRequested.get() ? "Stopped" : "Running",
                    poolGcPeriodMs);
        }
        return toString;
    }

    /**
     * Prunes resources for dangling threads (those GCed by JVM, dead or have no corresponding entry).
     *
     * @param isDanglingThreadF
     * @param threadRegister
     */
    static void prune(final Function<Thread, Boolean> isDanglingThreadF,
            final KVStore<SoftReference<Thread>, Long> threadRegister) {

        final Iterator<SoftReference<Thread>> it = threadRegister.keySet().iterator();
        while (it.hasNext()) {
            SoftReference<Thread> threadRef = it.next();
            final Thread thread = threadRef.get();
            if (isDanglingThreadF.apply(thread)) {
                log.debug("Pruning of {} dangling thread reference.", threadRef);
                threadRegister.remove(threadRef);
                threadRef.clear();
            }
        }

    }

    /**
     * Scans the thread register and determines which of the registered threads have no or should have no resources associated to them.
     * Takes into account that:<BR>
     * - the thread could've died
     * - the thread could've died and collected by the system GC, hence the soft reference could point to "null thread";<BR>
     *
     * @param <T>
     */
    private static final Function<Thread, Boolean> danglingThreadF = new Function<Thread, Boolean>() {
        @Override
        public Boolean apply(final Thread input) {
            return input == null || !input.isAlive();
        }
    };

    private static final Function<Thread, Boolean> allThreadsF = new Function<Thread, Boolean>() {
        @Override
        public Boolean apply(final Thread input) {
            return true;
        }
    };

    private static final Log4jLogger log = new Log4jLogger(Logger.getLogger(Pool.class));

    private final KVStore<SoftReference<Thread>, Long> threadRegister;
    private final AtomicBoolean stopRequested = new AtomicBoolean(false);
    private final long poolGcPeriodMs;
    private final KVStore<Long, T> entryStore;
    private final Function<Thread, Boolean> isDanglingThreadF;
    private String toString;
}
