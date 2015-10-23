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

import static com.persinity.common.collection.ThreadBasedPoolTest.checkCreatedEntriesAreClosed;
import static com.persinity.common.collection.ThreadBasedPoolTest.concurrentTest;
import static com.persinity.common.collection.ThreadBasedPoolTest.stubCloseF;
import static com.persinity.common.collection.ThreadBasedPoolTest.stubCreateF;
import static org.junit.Assert.assertEquals;

import java.util.Collections;
import java.util.Set;

import org.junit.Test;

import com.persinity.common.db.KVStoreTest;

/**
 * @author dyordanov
 */
public class ThreadBasedPoolIT {
    @Test
    public void testStressGetRemoveEntriesWithGc() throws Exception {
        final ThreadBasedPool<KVStoreTest.LongCloseable> testee = new ThreadBasedPool<>(stubCreateF(), stubCloseF(), 1);
        Thread t = new Thread(testee);
        t.start();
        final Set<KVStoreTest.LongCloseable> createdEntries = concurrentTest(testee, 100, 1000);
        testee.close();
        try {
            testee.get();
        } catch (IllegalStateException e) {
            // It is illegal to call get on closed pool.
        }
        assertEquals(Collections.emptySet(), testee.entries());
        checkCreatedEntriesAreClosed(createdEntries);
        t.join(STOP_TIMEOUT_MS);
        assertEquals(Collections.emptySet(), testee.entries());
    }

    private static final long STOP_TIMEOUT_MS = 5000L;
}
