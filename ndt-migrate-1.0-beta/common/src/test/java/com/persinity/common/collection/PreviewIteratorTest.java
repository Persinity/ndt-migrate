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

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.NoSuchElementException;

import org.junit.Before;
import org.junit.Test;

/**
 * @author Ivan Dachev
 */
public class PreviewIteratorTest {

    @Before
    public void setUp() {
        iter = Arrays.asList(1, 2, 3).iterator();
    }

    @Test
    public void test() throws Exception {
        PreviewIterator<Integer> previewIter = new PreviewIterator<>(iter);
        int value;

        assertTrue(previewIter.hasNext());
        value = previewIter.preview();
        assertThat(value, is(1));
        value = previewIter.next();
        assertThat(value, is(1));

        assertTrue(previewIter.hasNext());
        value = previewIter.preview();
        assertThat(value, is(2));
        value = previewIter.next();
        assertThat(value, is(2));

        assertTrue(previewIter.hasNext());
        value = previewIter.preview();
        assertThat(value, is(3));
        value = previewIter.next();
        assertThat(value, is(3));

        assertFalse(previewIter.hasNext());
    }

    @Test
    public void testEmptyHasNext() throws Exception {
        PreviewIterator previewIter = new PreviewIterator<>(Collections.emptyList().iterator());
        assertFalse(previewIter.hasNext());
    }

    @Test(expected = UnsupportedOperationException.class)
    public void testRemove() throws Exception {
        PreviewIterator previewIter = new PreviewIterator<>(Collections.emptyList().iterator());
        previewIter.remove();
    }

    @Test(expected = NoSuchElementException.class)
    public void testEmptyNext() throws Exception {
        PreviewIterator previewIter = new PreviewIterator<>(Collections.emptyList().iterator());
        previewIter.next();
    }

    @Test(expected = NoSuchElementException.class)
    public void testEmptyPreview() throws Exception {
        PreviewIterator previewIter = new PreviewIterator<>(Collections.emptyList().iterator());
        previewIter.preview();
    }

    private Iterator<Integer> iter;
}