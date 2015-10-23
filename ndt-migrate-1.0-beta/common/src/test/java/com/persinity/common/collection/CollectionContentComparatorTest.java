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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;

import org.junit.Test;

/**
 * @author dyordanov
 */
public class CollectionContentComparatorTest {
    @Test
    public void testCompare() throws Exception {
        final Collection<Integer> emptySet = Collections.emptySet();
        final CollectionContentComparator<Integer> testee = new CollectionContentComparator<>(new IntegerComparator());

        assertEquals(testee.compare(Arrays.asList(1, 2, 3), Arrays.asList(1, 2, 3)), 0);
        assertEquals(testee.compare(Arrays.asList(1, 2, 3), Arrays.asList(3, 1, 2)), 0);
        assertEquals(testee.compare(Arrays.asList(1), Arrays.asList(1)), 0);
        assertEquals(testee.compare(null, null), 0);
        assertEquals(testee.compare(emptySet, emptySet), 0);

        assertNotEquals(testee.compare(Arrays.asList(1, 2, 3), Arrays.asList(1)), 0);
        assertNotEquals(testee.compare(Arrays.asList(1), Arrays.asList(1, 2, 3)), 0);
        assertNotEquals(testee.compare(Arrays.asList(1), null), 0);
        assertNotEquals(testee.compare(null, Arrays.asList(1)), 0);
        assertNotEquals(testee.compare(emptySet, Arrays.asList(1)), 0);
    }

    private class IntegerComparator implements java.util.Comparator<Integer> {
        @Override
        public int compare(final Integer o1, final Integer o2) {
            return o1 - o2;
        }
    }
}