/**
 * Copyright (c) 2015 Persinity Inc.
 */

package com.persinity.common.db;

import static com.persinity.common.invariant.Invariant.notNull;

import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import org.apache.log4j.Logger;

import com.google.common.base.Function;
import com.persinity.common.logging.Log4jLogger;

/**
 * Provides concurrent access to and manages K/V entries. Thread-safe.
 *
 * @author dyordanov
 */
public class KVStore<K, V> {

    public KVStore(final Function<K, V> newEntryF, final Function<V, V> closeEntryF) {
        notNull(newEntryF);
        notNull(closeEntryF);
        this.newEntryF = newEntryF;
        this.closeEntryF = closeEntryF;
        entryRegister = new ConcurrentHashMap<>();
    }

    /**
     * Closes and removes the entry uniquely identified by the supplied ID or null if such not found.
     *
     * @param id
     *         Non-null ID.
     * @return The removed entry
     * @throws RuntimeException
     *         if close of an entry fails.
     */
    public V remove(K id) {
        notNull(id);
        final V entry = entryRegister.remove(id);
        if (entry != null) {
            try {
                closeEntryF.apply(entry);
            } catch (Exception e) {
                log.error("Was not able to close {}: {}", entry, e.getMessage());
                throw new RuntimeException(e);
            }
        }
        return entry;
    }

    /**
     * Gets the entry uniquely identified by the supplied ID. If no such, creates and returns one.
     * @param id
     *         Non-null ID to get entry for
     * @return entry uniquely identified by the supplied ID.
     */
    public V get(final K id) {
        notNull(id);
        V entry = entryRegister.get(id);
        if (entry == null) {
            entry = newEntryF.apply(id);
            entryRegister.putIfAbsent(id, entry);
        }
        return entry;
    }

    /**
     * @return the IDs contained by this instance or empty set if no such.
     */
    public Set<K> keySet() {
        return new HashSet<>(entryRegister.keySet());
    }

    /**
     * Removes all entries identified by the supplied keys.
     *
     * @param ids
     *         Non-null set of IDs.
     * @param exceptions
     *         if non-null map is supplied it is filled with close exceptions if such.
     * @see KVStore#remove(Object)
     */
    public void removeAll(final Set<K> ids, final Map<K, RuntimeException> exceptions) {
        for (final K id : ids) {
            try {
                remove(id);
            } catch (RuntimeException e) {
                if (exceptions == null) {
                    throw e;
                } else {
                    exceptions.put(id, e);
                }
            }
        }
    }

    /**
     * @param id
     *         Non-null ID.
     * @return returns {@code true} if this instance contains entry for the given ID.
     */
    public boolean contains(K id) {
        notNull(id);
        return entryRegister.containsKey(id);
    }

    /**
     * @param <T>
     * @return enumerates the entries contained in this instance.
     */
    public <T extends Closeable> Set<T> entries() {
        final Collection<T> values = (Collection<T>) entryRegister.values();
        return new HashSet<>(values);
    }

    private static final Log4jLogger log = new Log4jLogger(Logger.getLogger(KVStore.class));
    private final ConcurrentMap<K, V> entryRegister;
    private final Function<K, V> newEntryF;
    private final Function<V, V> closeEntryF;
}
