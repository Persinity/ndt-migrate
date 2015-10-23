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
package com.persinity.common.db.metainfo;

import java.util.Arrays;
import java.util.Collections;

import org.junit.Assert;
import org.junit.Test;

/**
 * @author Doichin Yordanov
 */
public class InTest {

    /**
     * Test method for
     * {@link com.persinity.common.db.metainfo.In#In(com.persinity.common.db.metainfo.Col, java.util.List)} with
     * invalid input.
     */
    @Test(expected = IllegalArgumentException.class)
    public void testInColListOfAllInvalid() {
        new In<Object>(null, Collections.emptyList());
    }

    /**
     * Test method for
     * {@link com.persinity.common.db.metainfo.In#In(com.persinity.common.db.metainfo.Col, java.util.List)} with
     * invalid input.
     */
    @Test(expected = IllegalArgumentException.class)
    public void testInColListOfColInvalid() {
        new In<Integer>(null, Arrays.asList(1));
    }

    /**
     * Test method for
     * {@link com.persinity.common.db.metainfo.In#In(com.persinity.common.db.metainfo.Col, java.util.List)} with
     * invalid input.
     */
    @Test(expected = IllegalArgumentException.class)
    public void testInColListOfCollectionInvalidEmpty() {
        new In<Object>(new Col("col"), Collections.emptyList());
    }

    /**
     * Test method for {@link com.persinity.common.db.metainfo.In#getCol()}.
     */
    @Test
    public void testGetCol() {
        final Col col = new Col("col");
        Assert.assertEquals(col, new In<Integer>(col, Arrays.asList(1)).getCol());
    }

    /**
     * Test method for {@link com.persinity.common.db.metainfo.In#getValue()}.
     */
    @Test
    public void testGetValue() {
        final Col col = new Col("col");
        Assert.assertEquals(Arrays.asList(1), new In<Integer>(col, Arrays.asList(1)).getValue());
        Assert.assertEquals(Arrays.asList(1, 2), new In<Integer>(col, Arrays.asList(1, 2)).getValue());
    }

    /**
     * Test method for {@link com.persinity.common.db.metainfo.In#toString()}.
     */
    @Test
    public void testToString() {
        final Col col = new Col("col");
        Assert.assertEquals("col IN (1, 2)", new In<Integer>(col, Arrays.asList(1, 2)).toString());
        Assert.assertEquals("col IN ('1', '')", new In<String>(col, Arrays.asList("1", "", null)).toString());
    }
}
