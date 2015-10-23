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

import static com.persinity.common.ThreadUtil.sleep;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import com.google.common.base.Function;

/**
 * @author Ivan Dachev
 */
public class DelayedIteratorTest {
    @Before
    public void setUp() throws Exception {
        data = Arrays.asList("V1", "M", "V2", "M", "M", "V3");
        final Function<String, Boolean> markerValueF = new Function<String, Boolean>() {
            @Override
            public Boolean apply(final String s) {
                return s.equals("M");
            }
        };
        testee = new DelayedIterator<>(data.iterator(), markerValueF, 200);
    }

    @Test
    public void test() throws Exception {
        assertTrue(testee.hasNext());
        assertThat(testee.next(), is("V1"));

        assertTrue(testee.hasNext());
        assertThat(testee.next(), is("M"));
        assertTrue(testee.hasNext());
        assertThat(testee.next(), is("M"));
        sleep(200);

        assertTrue(testee.hasNext());
        assertThat(testee.next(), is("V2"));

        assertTrue(testee.hasNext());
        assertThat(testee.next(), is("M"));
        assertTrue(testee.hasNext());
        assertThat(testee.next(), is("M"));
        sleep(200);

        assertTrue(testee.hasNext());
        assertThat(testee.next(), is("M"));
        assertTrue(testee.hasNext());
        assertThat(testee.next(), is("M"));
        sleep(200);

        assertTrue(testee.hasNext());
        assertThat(testee.next(), is("V3"));

        assertFalse(testee.hasNext());
    }

    @Test(expected = UnsupportedOperationException.class)
    public void testRemove() throws Exception {
        testee.remove();
    }

    private List<String> data;
    private DelayedIterator<String> testee;
}