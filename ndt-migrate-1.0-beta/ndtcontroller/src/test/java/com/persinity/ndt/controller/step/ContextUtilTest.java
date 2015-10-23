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
package com.persinity.ndt.controller.step;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

import java.util.HashMap;

import org.easymock.EasyMock;
import org.junit.Before;
import org.junit.Test;

import com.persinity.common.db.RelDb;
import com.persinity.ndt.dbdiff.BufferedSchemaDiffGenerator;
import com.persinity.ndt.dbdiff.SchemaDiffGenerator;
import com.persinity.ndt.etlmodule.WindowGenerator;

/**
 * @author Ivan Dachev
 */
public class ContextUtilTest {

    @SuppressWarnings("unchecked")
    @Before
    public void setUp() throws Exception {
        ctx = new HashMap<>();
        winGen = EasyMock.createNiceMock(WindowGenerator.class);
    }

    @SuppressWarnings("unchecked")
    @Test
    public void testWindowGenerator() throws Exception {
        assertThat(ctx.get("test"), nullValue());
        ContextUtil.addWindowGenerator("test", winGen, ctx);
        assertThat(ContextUtil.getWindowGenerator("test", ctx), is(winGen));
    }

    @Test(expected = NullPointerException.class)
    public void testGetSchemaDiffGenerator_InvalidInput() {
        ContextUtil.getSchemaDiffGenerator(null);
    }

    @Test
    public void testGetSchemaDiffGenerator() {
        final SchemaDiffGenerator actual = ContextUtil.getSchemaDiffGenerator(ctx);
        assertTrue(actual instanceof BufferedSchemaDiffGenerator);
        final Object expectedObject = ctx.get(SchemaDiffGenerator.class);
        assertTrue(expectedObject instanceof BufferedSchemaDiffGenerator);
        final SchemaDiffGenerator expected = (SchemaDiffGenerator) expectedObject;
        assertEquals(expected, actual);

        final SchemaDiffGenerator actual1 = ContextUtil.getSchemaDiffGenerator(ctx);
        assertTrue(actual1 instanceof BufferedSchemaDiffGenerator);
        final Object expectedObject1 = ctx.get(SchemaDiffGenerator.class);
        assertTrue(expectedObject1 instanceof BufferedSchemaDiffGenerator);
        final SchemaDiffGenerator expected1 = (SchemaDiffGenerator) expectedObject1;
        assertEquals(expected1, actual1);

        assertEquals(expected1, expected);
        assertEquals(actual1, actual);
    }

    private HashMap<Object, Object> ctx;
    private WindowGenerator<RelDb, RelDb> winGen;
}