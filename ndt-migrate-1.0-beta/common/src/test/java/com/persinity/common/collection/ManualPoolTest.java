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

import static org.easymock.EasyMock.createNiceMock;
import static org.easymock.EasyMock.createStrictMock;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.verify;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import org.junit.Assert;
import org.junit.Test;

import com.google.common.base.Function;
import com.persinity.common.db.KVStoreTest.LongCloseable;

/**
 * @author dyordanov
 */
public class ManualPoolTest {

    @Test
    public void testClose() throws Exception {
        final Function<Void, LongCloseable> newEntryF = createStrictMock(Function.class);
        final LongCloseable value1 = createNiceMock(LongCloseable.class);
        expect(newEntryF.apply(null)).andReturn(value1);
        final LongCloseable value2 = createNiceMock(LongCloseable.class);
        expect(newEntryF.apply(null)).andReturn(value2);
        final Function<LongCloseable, LongCloseable> closeEntryF = createNiceMock(Function.class);
        expect(closeEntryF.apply(value1)).andReturn(value1).atLeastOnce();
        expect(closeEntryF.apply(value2)).andReturn(value2).atLeastOnce();
        replay(newEntryF, closeEntryF);

        final ManualPool<LongCloseable> testee = new ManualPool(newEntryF, closeEntryF);
        testee.get();
        testee.get();
        final Set<LongCloseable> actualEntries = testee.entries();

        final Set<LongCloseable> expectedEntries = new HashSet<>(Arrays.asList(value1, value2));
        Assert.assertEquals(expectedEntries, actualEntries);

        testee.close();
        verify(newEntryF, closeEntryF);

        assertTrue(testee.entries().isEmpty());
    }

    @Test
    public void testGet() throws Exception {
        final Function<Void, LongCloseable> newEntryF = createStrictMock(Function.class);
        final Function<LongCloseable, LongCloseable> closeEntryF = createStrictMock(Function.class);
        final LongCloseable expected = createNiceMock(LongCloseable.class);
        expect(newEntryF.apply(null)).andReturn(expected);
        replay(newEntryF, closeEntryF);

        final ManualPool<LongCloseable> testee = new ManualPool(newEntryF, closeEntryF);
        final LongCloseable actual = testee.get();

        verify(newEntryF, closeEntryF);
        Assert.assertEquals(expected, actual);
    }

    @Test
    public void testRemove() throws Exception {
        final Function<Void, LongCloseable> newEntryF = createStrictMock(Function.class);
        final Function<LongCloseable, LongCloseable> closeEntryF = createStrictMock(Function.class);
        final LongCloseable value = createNiceMock(LongCloseable.class);
        expect(newEntryF.apply(null)).andReturn(value);
        expect(closeEntryF.apply(value)).andReturn(value);
        replay(newEntryF, closeEntryF);

        final ManualPool<LongCloseable> testee = new ManualPool(newEntryF, closeEntryF);
        testee.remove(testee.get());
        testee.remove(createNiceMock(LongCloseable.class));

        verify(newEntryF, closeEntryF);
    }

    @Test
    public void testEntries() throws Exception {
        final Function<LongCloseable, LongCloseable> closeEntryF = createStrictMock(Function.class);
        final Function<Void, LongCloseable> newEntryF = createStrictMock(Function.class);
        final LongCloseable value1 = createNiceMock(LongCloseable.class);
        expect(newEntryF.apply(null)).andReturn(value1);
        final LongCloseable value2 = createNiceMock(LongCloseable.class);
        expect(newEntryF.apply(null)).andReturn(value2);
        replay(newEntryF, closeEntryF);

        final ManualPool<LongCloseable> testee = new ManualPool(newEntryF, closeEntryF);
        testee.get();
        testee.get();
        final Set<LongCloseable> actualEntries = testee.entries();

        verify(newEntryF, closeEntryF);
        final Set<LongCloseable> expectedEntries = new HashSet<>(Arrays.asList(value1, value2));
        Assert.assertEquals(expectedEntries, actualEntries);
    }

}