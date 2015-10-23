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

package com.persinity.common;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;

import java.lang.ref.SoftReference;

import org.junit.Test;

/**
 * @author dyordanov
 */
public class ReferentComparableSoftReferenceTest {

    @Test
    public void testEqualsHashCode() {
        final SoftReference<Integer> testee11 = new ReferentComparableSoftReference(1);
        final SoftReference<Integer> testee12 = new ReferentComparableSoftReference(1);
        final SoftReference<Integer> testee2 = new ReferentComparableSoftReference(2);

        assertEquals(testee11, testee11);
        assertEquals(testee11.hashCode(), testee11.hashCode());
        assertEquals(testee11, testee12);
        assertEquals(testee11.hashCode(), testee12.hashCode());
        assertNotEquals(testee11, testee2);
        assertFalse(testee11.equals(null));
    }

}