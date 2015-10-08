/**
 * Copyright (c) 2015 Persinity Inc.
 */

package com.persinity.common.collection;

import static com.persinity.common.StringUtils.format;
import static com.persinity.common.invariant.Invariant.notNull;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;

import com.google.common.base.Function;
import com.persinity.common.db.Closeable;
import com.persinity.common.db.KVStore;

/**
 * Pool that allocates new resource on each get request and deallocates on remove request.
 * Essentially pool that gives to the user full manual control over resource de/allocation.
 *
 * @author dyordanov
 */
public class ManualPool<T extends Closeable> implements Pool<T> {

    public ManualPool(final Function<Void, T> newEntryF, final Function<T, T> closeEntryF) {
        notNull(newEntryF);
        notNull(closeEntryF);
        this.newEntryF = newEntryF;
        final Function<T, T> repeaterF = new Function<T, T>() {
            @Override
            public T apply(final T input) {
                return input;
            }
        };
        entryStore = new KVStore<>(repeaterF, closeEntryF);
    }

    @Override
    public void close() throws RuntimeException {
        stopRequested.getAndSet(true);
        final Map<T, RuntimeException> closeExceptions = new HashMap<>();
        entryStore.removeAll(entryStore.keySet(), closeExceptions);
        if (!closeExceptions.isEmpty()) {
            final T faultyKey = closeExceptions.keySet().iterator().next();
            final RuntimeException fault = closeExceptions.get(faultyKey);
            throw new RuntimeException(format("Failed to close {}", faultyKey), fault);
        }
    }

    @Override
    public T get() {
        if (stopRequested.get()) {
            throw new IllegalStateException();
        }
        return entryStore.get(newEntryF.apply(null));
    }

    @Override
    public void remove(final T value) {
        entryStore.remove(value);
    }

    @Override
    public Set<T> entries() {
        return entryStore.entries();
    }

    private final AtomicBoolean stopRequested = new AtomicBoolean(false);
    private final KVStore<T, T> entryStore;

    private final Function<Void, T> newEntryF;
}
