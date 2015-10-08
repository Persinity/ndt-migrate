/**
 * Copyright (c) 2015 Persinity Inc.
 */

package com.persinity.common.collection;

import java.util.Set;

import com.persinity.common.db.Closeable;

/**
 * Pool for {@link Closeable} resources.
 *
 * @author dyordanov
 */
public interface Pool<T extends Closeable> extends Closeable {
    /**
     * The returned instance is expected to deregister itself from the pool on close.
     * @return Entry from the pool. It is up to pool impl. to decide whether it is cached or new one.
     */
    T get();

    /**
     * If the pool contains the entry, it is removed and all associated resources are released.
     *
     * @param value
     */
    void remove(T value);

    /**
     * @return Enumerates the entries contained by this pool.
     */
    Set<T> entries();

}
