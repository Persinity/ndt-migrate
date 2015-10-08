/**
 * Copyright (c) 2015 Persinity Inc.
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