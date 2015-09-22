/**
 * Copyright (c) 2015 Persinity Inc.
 */

package com.persinity.common.db;

/**
 * In comparison to {@link java.io.Closeable} and {@link AutoCloseable} this interfaces does
 * not impose mandatory handling of checked exceptions.
 *
 * @author dyordanov
 */
public interface Closeable {
    /**
     * Closes the associated resource and frees all related resources.
     *
     * @throws RuntimeException
     *         if the close operation has failed.
     */
    void close() throws RuntimeException;
}
