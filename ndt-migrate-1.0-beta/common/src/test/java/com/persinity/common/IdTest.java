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

import static com.persinity.test.TestUtil.serDeser;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

/**
 * @author Ivan Dachev
 */
public class IdTest {

    @Test
    public void testNextValue() throws Exception {
        final Id id1 = Id.nextValue();
        assertThat(id1, notNullValue());

        final Id id2 = Id.nextValue();
        assertThat(id2, notNullValue());

        final String id1Str = id1.toString();
        final String id2Str = id2.toString();
        assertThat(id1Str.substring(0, id1Str.lastIndexOf('-')), is(id2Str.substring(0, id2Str.lastIndexOf('-'))));
    }

    @Test
    public void testEquals() throws Exception {
        Id id1 = Id.nextValue();

        assertTrue(id1.equals(id1));

        assertFalse(id1.equals(null));
        assertFalse(id1.equals(new Object()));
        assertFalse(id1.equals(Id.nextValue()));

        Id id2 = Id.nextValue();
        assertFalse(id2.equals(id1));
        assertFalse(id1.equals(id2));

        Id idDeser = serDeser(id1);
        assertTrue(id1 != idDeser);
        assertTrue(idDeser.equals(id1));
        assertTrue(id1.equals(idDeser));
    }

    @Test
    public void testHashCode() throws Exception {
        Id id1 = Id.nextValue();
        assertThat(id1.hashCode(), is(id1.toString().hashCode()));

        Id id2 = Id.nextValue();
        assertThat(id1.hashCode(), not(id2.hashCode()));
    }

    @Test
    public void testToString() throws Exception {
        Id id = Id.nextValue();
        assertThat(id.toString(), notNullValue());
        assertThat(id.toString().length(), not(0));
    }

    @Test
    public void testToStringShort() throws Exception {
        Id id = Id.nextValue();
        String shortId = id.toStringShort();
        assertThat(shortId, notNullValue());
        assertThat(shortId.length(), not(0));
        assertTrue(shortId.length() < id.toString().length());
    }
}