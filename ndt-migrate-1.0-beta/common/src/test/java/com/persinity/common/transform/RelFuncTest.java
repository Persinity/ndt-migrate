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
package com.persinity.common.transform;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.persinity.ndt.transform.RelFunc;

/**
 * @author Doichin Yordanov
 */
public class RelFuncTest {

    private static final String SELECT_2 = "SELECT 2 FROM dual";
    private static final String SELECT_1 = "SELECT 1 FROM dual";

    private RelFunc testee11, testee12, testee20;

    @Before
    public void setUp() {
        testee11 = testee12 = new RelFunc(SELECT_1);
        testee20 = new RelFunc(SELECT_2);
    }

    /**
     * Test method for {@link com.persinity.common.fp.RelFunc#hashCode()}.
     */
    @Test
    public void testHashCode() {
        Assert.assertEquals(testee11.hashCode(), testee12.hashCode());
    }

    /**
     * Test method for {@link com.persinity.common.fp.RelFunc#equals(java.lang.Object)}.
     */
    @Test
    public void testEqualsObject() {
        Assert.assertTrue(testee11.equals(testee12));
        Assert.assertFalse(testee11.equals(null));
        Assert.assertFalse(testee11.equals(testee20));
    }

    /**
     * Test method for {@link com.persinity.common.fp.RelFunc#toString()}.
     */
    @Test
    public void testToString() {
        Assert.assertNotNull(testee11.toString());
    }

}
