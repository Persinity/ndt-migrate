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
     *
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
