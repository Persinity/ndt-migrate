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
package com.persinity.ndt.etlmodule.relational.migrate;

import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.Matchers.hasEntry;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;

import org.junit.Before;
import org.junit.Test;

import com.google.common.collect.ImmutableMap;
import com.persinity.common.db.metainfo.Col;
import com.persinity.ndt.dbagent.relational.SchemaInfo;

/**
 * @author Ivan Dachev
 */
public class ClogCoalesceTupleFuncTest {
    @Before
    public void setUp() {
        Col col = new Col("pid", "NUMBER(1)", false);
        funciton = new ClogCoalesceTupleFunc(Collections.singleton(col));
    }

    @Test
    public void test_SeveralPids() throws Exception {
        List<Map<String, Object>> input = Arrays
                .asList(makeObjectMap("pid", "1", SchemaInfo.COL_CTYPE, "I", "cvalue", "cvalue11"),
                        makeObjectMap("pid", "1", SchemaInfo.COL_CTYPE, "U", "cvalue", "cvalue12"),
                        makeObjectMap("pid", "2", SchemaInfo.COL_CTYPE, "U", "cvalue", "cvalue21"),
                        makeObjectMap("pid", "2", SchemaInfo.COL_CTYPE, "U", "cvalue", "cvalue22"),
                        makeObjectMap("pid", "3", SchemaInfo.COL_CTYPE, "U", "cvalue", "cvalue31"));

        Iterator<Map<String, Object>> coalesced = funciton.apply(input.iterator());
        assertThat(coalesced, notNullValue());

        Map<String, Object> map;

        assertTrue(coalesced.hasNext());
        map = coalesced.next();
        assertThat(map, hasEntry("pid", (Object) "1"));
        assertThat(map, hasEntry(SchemaInfo.COL_CTYPE, (Object) "U"));
        assertThat(map, hasEntry("cvalue", (Object) "cvalue12"));

        assertTrue(coalesced.hasNext());
        map = coalesced.next();
        assertThat(map, hasEntry("pid", (Object) "2"));
        assertThat(map, hasEntry(SchemaInfo.COL_CTYPE, (Object) "U"));
        assertThat(map, hasEntry("cvalue", (Object) "cvalue22"));

        assertTrue(coalesced.hasNext());
        map = coalesced.next();
        assertThat(map, hasEntry("pid", (Object) "3"));
        assertThat(map, hasEntry(SchemaInfo.COL_CTYPE, (Object) "U"));
        assertThat(map, hasEntry("cvalue", (Object) "cvalue31"));

        assertFalse(coalesced.hasNext());
    }

    @Test
    public void test_SeveralPidsWith_IUD_Pid() throws Exception {
        List<Map<String, Object>> input = Arrays
                .asList(makeObjectMap("pid", "1", SchemaInfo.COL_CTYPE, "I", "cvalue", "cvalue11"),
                        makeObjectMap("pid", "1", SchemaInfo.COL_CTYPE, "U", "cvalue", "cvalue12"),
                        makeObjectMap("pid", "2", SchemaInfo.COL_CTYPE, "I", "cvalue", "cvalue21"),
                        makeObjectMap("pid", "2", SchemaInfo.COL_CTYPE, "U", "cvalue", "cvalue22"),
                        makeObjectMap("pid", "2", SchemaInfo.COL_CTYPE, "D", "cvalue", "cvalue23"),
                        makeObjectMap("pid", "3", SchemaInfo.COL_CTYPE, "U", "cvalue", "cvalue31"));

        Iterator<Map<String, Object>> coalesced = funciton.apply(input.iterator());
        assertThat(coalesced, notNullValue());

        Map<String, Object> map;

        assertTrue(coalesced.hasNext());
        map = coalesced.next();
        assertThat(map, hasEntry("pid", (Object) "1"));
        assertThat(map, hasEntry(SchemaInfo.COL_CTYPE, (Object) "U"));
        assertThat(map, hasEntry("cvalue", (Object) "cvalue12"));

        assertTrue(coalesced.hasNext());
        map = coalesced.next();
        assertThat(map, hasEntry("pid", (Object) "3"));
        assertThat(map, hasEntry(SchemaInfo.COL_CTYPE, (Object) "U"));
        assertThat(map, hasEntry("cvalue", (Object) "cvalue31"));

        assertFalse(coalesced.hasNext());
    }

    @Test
    public void test_SeveralPids_SamePidInsertedTwice() throws Exception {
        List<Map<String, Object>> input = Arrays
                .asList(makeObjectMap("pid", "1", SchemaInfo.COL_CTYPE, "I", "cvalue", "cvalue11"),
                        makeObjectMap("pid", "1", SchemaInfo.COL_CTYPE, "U", "cvalue", "cvalue12"),
                        makeObjectMap("pid", "1", SchemaInfo.COL_CTYPE, "D", "cvalue", "cvalue13"),
                        makeObjectMap("pid", "1", SchemaInfo.COL_CTYPE, "I", "cvalue", "cvalue14"),
                        makeObjectMap("pid", "1", SchemaInfo.COL_CTYPE, "U", "cvalue", "cvalue15"),
                        makeObjectMap("pid", "2", SchemaInfo.COL_CTYPE, "D", "cvalue", "cvalue21"),
                        makeObjectMap("pid", "3", SchemaInfo.COL_CTYPE, "U", "cvalue", "cvalue31"));

        Iterator<Map<String, Object>> coalesced = funciton.apply(input.iterator());
        assertThat(coalesced, notNullValue());

        Map<String, Object> map;

        assertTrue(coalesced.hasNext());
        map = coalesced.next();
        assertThat(map, hasEntry("pid", (Object) "1"));
        assertThat(map, hasEntry(SchemaInfo.COL_CTYPE, (Object) "U"));
        assertThat(map, hasEntry("cvalue", (Object) "cvalue15"));

        assertTrue(coalesced.hasNext());
        map = coalesced.next();
        assertThat(map, hasEntry("pid", (Object) "2"));
        assertThat(map, hasEntry(SchemaInfo.COL_CTYPE, (Object) "D"));
        assertThat(map, hasEntry("cvalue", (Object) "cvalue21"));

        assertTrue(coalesced.hasNext());
        map = coalesced.next();
        assertThat(map, hasEntry("pid", (Object) "3"));
        assertThat(map, hasEntry(SchemaInfo.COL_CTYPE, (Object) "U"));
        assertThat(map, hasEntry("cvalue", (Object) "cvalue31"));

        assertFalse(coalesced.hasNext());
    }

    @Test
    public void test_SingleInsert() throws Exception {
        List<Map<String, Object>> input = Collections
                .singletonList(makeObjectMap("pid", "1", SchemaInfo.COL_CTYPE, "I", "cvalue", "cvalue1"));

        Iterator<Map<String, Object>> coalesced = funciton.apply(input.iterator());
        assertThat(coalesced, notNullValue());

        assertTrue(coalesced.hasNext());

        Map<String, Object> map = coalesced.next();
        assertThat(map, hasEntry("pid", (Object) "1"));
        assertThat(map, hasEntry(SchemaInfo.COL_CTYPE, (Object) "I"));
        assertThat(map, hasEntry("cvalue", (Object) "cvalue1"));

        assertFalse(coalesced.hasNext());
    }

    @Test
    public void test_SingleUpdate() throws Exception {
        List<Map<String, Object>> input = Collections
                .singletonList(makeObjectMap("pid", "1", SchemaInfo.COL_CTYPE, "U", "cvalue", "cvalue1"));

        Iterator<Map<String, Object>> coalesced = funciton.apply(input.iterator());
        assertThat(coalesced, notNullValue());

        assertTrue(coalesced.hasNext());

        Map<String, Object> map = coalesced.next();
        assertThat(map, hasEntry("pid", (Object) "1"));
        assertThat(map, hasEntry(SchemaInfo.COL_CTYPE, (Object) "U"));
        assertThat(map, hasEntry("cvalue", (Object) "cvalue1"));

        assertFalse(coalesced.hasNext());
    }

    @Test
    public void test_SingleDelete() throws Exception {
        List<Map<String, Object>> input = Collections
                .singletonList(makeObjectMap("pid", "1", SchemaInfo.COL_CTYPE, "D", "cvalue", "cvalue1"));

        Iterator<Map<String, Object>> coalesced = funciton.apply(input.iterator());
        assertThat(coalesced, notNullValue());

        assertTrue(coalesced.hasNext());

        Map<String, Object> map = coalesced.next();
        assertThat(map, hasEntry("pid", (Object) "1"));
        assertThat(map, hasEntry(SchemaInfo.COL_CTYPE, (Object) "D"));
        assertThat(map, hasEntry("cvalue", (Object) "cvalue1"));

        assertFalse(coalesced.hasNext());
    }

    @Test
    public void test_InsertUpdate() throws Exception {
        List<Map<String, Object>> input = Arrays
                .asList(makeObjectMap("pid", "1", SchemaInfo.COL_CTYPE, "I", "cvalue", "cvalue1"),
                        makeObjectMap("pid", "1", SchemaInfo.COL_CTYPE, "U", "cvalue", "cvalue2"));

        Iterator<Map<String, Object>> coalesced = funciton.apply(input.iterator());
        assertThat(coalesced, notNullValue());

        assertTrue(coalesced.hasNext());

        Map<String, Object> map = coalesced.next();
        assertThat(map, hasEntry("pid", (Object) "1"));
        assertThat(map, hasEntry(SchemaInfo.COL_CTYPE, (Object) "U"));
        assertThat(map, hasEntry("cvalue", (Object) "cvalue2"));

        assertFalse(coalesced.hasNext());
    }

    @Test
    public void test_InsertUpdateDelete() throws Exception {
        List<Map<String, Object>> input = Arrays
                .asList(makeObjectMap("pid", "1", SchemaInfo.COL_CTYPE, "I", "cvalue", "cvalue1"),
                        makeObjectMap("pid", "1", SchemaInfo.COL_CTYPE, "U", "cvalue", "cvalue2"),
                        makeObjectMap("pid", "1", SchemaInfo.COL_CTYPE, "D", "cvalue", "cvalue3"));

        Iterator<Map<String, Object>> coalesced = funciton.apply(input.iterator());
        assertThat(coalesced, notNullValue());

        assertFalse(coalesced.hasNext());
    }

    @Test
    public void test_UpdateDelete() throws Exception {
        List<Map<String, Object>> input = Arrays
                .asList(makeObjectMap("pid", "1", SchemaInfo.COL_CTYPE, "U", "cvalue", "cvalue1"),
                        makeObjectMap("pid", "1", SchemaInfo.COL_CTYPE, "D", "cvalue", "cvalue2"));

        Iterator<Map<String, Object>> coalesced = funciton.apply(input.iterator());
        assertThat(coalesced, notNullValue());

        assertTrue(coalesced.hasNext());

        Map<String, Object> map = coalesced.next();
        assertThat(map, hasEntry("pid", (Object) "1"));
        assertThat(map, hasEntry(SchemaInfo.COL_CTYPE, (Object) "D"));
        assertThat(map, hasEntry("cvalue", (Object) "cvalue2"));

        assertFalse(coalesced.hasNext());
    }

    @Test
    public void test_ManyInsertsForOnePid() throws Exception {
        List<Map<String, Object>> input = Arrays
                .asList(makeObjectMap("pid", "1", SchemaInfo.COL_CTYPE, "I", "cvalue", "cvalue1"),
                        makeObjectMap("pid", "1", SchemaInfo.COL_CTYPE, "I", "cvalue", "cvalue2"),
                        makeObjectMap("pid", "1", SchemaInfo.COL_CTYPE, "I", "cvalue", "cvalue3"));

        Iterator<Map<String, Object>> coalesced = funciton.apply(input.iterator());
        assertThat(coalesced, notNullValue());

        assertTrue(coalesced.hasNext());

        Map<String, Object> map = coalesced.next();
        assertThat(map, hasEntry("pid", (Object) "1"));
        assertThat(map, hasEntry(SchemaInfo.COL_CTYPE, (Object) "I"));
        assertThat(map, hasEntry("cvalue", (Object) "cvalue3"));

        assertFalse(coalesced.hasNext());
    }

    @Test
    public void test_ManyUpdatesForOnePid() throws Exception {
        List<Map<String, Object>> input = Arrays
                .asList(makeObjectMap("pid", "1", SchemaInfo.COL_CTYPE, "U", "cvalue", "cvalue1"),
                        makeObjectMap("pid", "1", SchemaInfo.COL_CTYPE, "U", "cvalue", "cvalue2"),
                        makeObjectMap("pid", "1", SchemaInfo.COL_CTYPE, "U", "cvalue", "cvalue3"));

        Iterator<Map<String, Object>> coalesced = funciton.apply(input.iterator());
        assertThat(coalesced, notNullValue());

        assertTrue(coalesced.hasNext());

        Map<String, Object> map = coalesced.next();
        assertThat(map, hasEntry("pid", (Object) "1"));
        assertThat(map, hasEntry(SchemaInfo.COL_CTYPE, (Object) "U"));
        assertThat(map, hasEntry("cvalue", (Object) "cvalue3"));

        assertFalse(coalesced.hasNext());
    }

    @Test
    public void test_ManyDeletesForOnePid() throws Exception {
        List<Map<String, Object>> input = Arrays
                .asList(makeObjectMap("pid", "1", SchemaInfo.COL_CTYPE, "D", "cvalue", "cvalue1"),
                        makeObjectMap("pid", "1", SchemaInfo.COL_CTYPE, "D", "cvalue", "cvalue2"),
                        makeObjectMap("pid", "1", SchemaInfo.COL_CTYPE, "D", "cvalue", "cvalue3"));

        Iterator<Map<String, Object>> coalesced = funciton.apply(input.iterator());
        assertThat(coalesced, notNullValue());

        assertTrue(coalesced.hasNext());

        Map<String, Object> map = coalesced.next();
        assertThat(map, hasEntry("pid", (Object) "1"));
        assertThat(map, hasEntry(SchemaInfo.COL_CTYPE, (Object) "D"));
        assertThat(map, hasEntry("cvalue", (Object) "cvalue3"));

        assertFalse(coalesced.hasNext());
    }

    @Test(expected = NoSuchElementException.class)
    public void test_Empty() throws Exception {
        List<Map<String, Object>> input = Collections.emptyList();

        Iterator<Map<String, Object>> coalesced = funciton.apply(input.iterator());
        assertThat(coalesced, notNullValue());
        assertFalse(coalesced.hasNext());
        assertThat(coalesced.next(), nullValue());
    }

    @Test(expected = UnsupportedOperationException.class)
    public void test_UnsupportedRemoveCall() throws Exception {
        List<Map<String, Object>> input = Arrays
                .asList(makeObjectMap("pid", "1", SchemaInfo.COL_CTYPE, "I", "cvalue", "cvalue1"),
                        makeObjectMap("pid", "1", SchemaInfo.COL_CTYPE, "U", "cvalue", "cvalue2"),
                        makeObjectMap("pid", "1", SchemaInfo.COL_CTYPE, "D", "cvalue", "cvalue3"));

        Iterator<Map<String, Object>> coalesced = funciton.apply(input.iterator());
        assertThat(coalesced, notNullValue());
        coalesced.remove();
    }

    @Test(expected = IllegalArgumentException.class)
    public void test_NoPkValue() throws Exception {
        List<Map<String, Object>> input = Collections
                .singletonList(makeObjectMap("sid", "1", SchemaInfo.COL_CTYPE, "I", "cvalue", "cvalue1"));

        Iterator<Map<String, Object>> coalesced = funciton.apply(input.iterator());
        assertThat(coalesced, notNullValue());
        coalesced.next();
    }

    @Test(expected = IllegalArgumentException.class)
    public void test_InvalidCtype() throws Exception {
        List<Map<String, Object>> input = Arrays
                .asList(makeObjectMap("pid", "1", SchemaInfo.COL_CTYPE, "I", "cvalue", "cvalue1"),
                        makeObjectMap("pid", "1", SchemaInfo.COL_CTYPE, "U", "cvalue", "cvalue2"),
                        makeObjectMap("pid", "1", SchemaInfo.COL_CTYPE, "Z", "cvalue", "cvalue3"));

        Iterator<Map<String, Object>> coalesced = funciton.apply(input.iterator());
        assertThat(coalesced, notNullValue());
        coalesced.next();
    }

    @Test(expected = IllegalArgumentException.class)
    public void test_NoCtype() throws Exception {
        List<Map<String, Object>> input = Arrays
                .asList(makeObjectMap("pid", "1", SchemaInfo.COL_CTYPE + "X", "I", "cvalue", "cvalue1"),
                        makeObjectMap("pid", "1", SchemaInfo.COL_CTYPE, "U", "cvalue", "cvalue2"),
                        makeObjectMap("pid", "1", SchemaInfo.COL_CTYPE, "U", "cvalue", "cvalue3"));

        Iterator<Map<String, Object>> coalesced = funciton.apply(input.iterator());
        assertThat(coalesced, notNullValue());
        coalesced.next();
    }

    private Map<String, Object> makeObjectMap(String k1, String v1, String k2, String v2, String k3, String v3) {
        return new HashMap<String, Object>(ImmutableMap.of(k1, v1, k2, v2, k3, v3));
    }

    private ClogCoalesceTupleFunc funciton;
}