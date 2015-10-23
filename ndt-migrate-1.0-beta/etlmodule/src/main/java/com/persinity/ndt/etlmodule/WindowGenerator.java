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
package com.persinity.ndt.etlmodule;

import java.util.Iterator;

import com.persinity.common.db.Closeable;
import com.persinity.ndt.transform.TransferWindow;

/**
 * Returns {@link Iterator} of {@link TransferWindow}s for transfer of delta state between Source to Destination nodes.<BR>
 * The returned iterator is read only and does not implement the {@link Iterator#remove()} method.
 *
 * @author Doichin Yordanov
 */
public interface WindowGenerator<S extends Closeable, D extends Closeable> extends Iterable<TransferWindow<S, D>> {
    /**
     * Signals the {@link Iterator}s of {@code this} {@link Iterable} to exit and release resource when the source feed
     * is exhausted. Once the current series of data is exhausted, calls to {@link Iterator#hasNext()} must return
     * {@code false}.<BR>
     * Note that event driven sources, such as data streams, may only seem exhausted and still have some more data in
     * future point of time. This method is called when the caller is not interested in these future points of time and
     * just wants the current series of data to be iterated. <BR>
     */
    void stopWhenFeedExhausted();

    /**
     * Signals the {@link Iterator}s of {@code this} {@link Iterable} to exit and release resource immediately even
     * if there is more source feed.
     */
    void forceStop();
}
