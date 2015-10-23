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

import org.junit.Assert;
import org.junit.Test;

import com.persinity.common.db.metainfo.Params.ParameterCount;

/**
 * @author Doichin Yordanov
 */
public class ParamsTest {

    /**
     * Test method for {@link com.persinity.common.db.metainfo.Params#Params(int)} with illegal arguments.
     */
    @Test(expected = IllegalArgumentException.class)
    public void testParamsIllegal() {
        new Params(0, ParameterCount.EXACT);
    }

    /**
     * Test method for {@link com.persinity.common.db.metainfo.Params#getSize()} with value of "1".
     */
    @Test
    public void testGetSize1() {
        Assert.assertEquals(1, new Params(1, ParameterCount.ROUNDED_BY_PWR_OF_TWO).getSize());
        Assert.assertEquals(1, new Params(1, ParameterCount.EXACT).getSize());
    }

    /**
     * Test method for {@link com.persinity.common.db.metainfo.Params#getSize()} with value of "2".
     */
    @Test
    public void testGetSize2() {
        Assert.assertEquals(2, new Params(2, ParameterCount.ROUNDED_BY_PWR_OF_TWO).getSize());
        Assert.assertEquals(2, new Params(2, ParameterCount.EXACT).getSize());
    }

    /**
     * Test method for {@link com.persinity.common.db.metainfo.Params#getSize()} with value of "99".
     */
    @Test
    public void testGetSize99() {
        Assert.assertEquals(128, new Params(99, ParameterCount.ROUNDED_BY_PWR_OF_TWO).getSize());
        Assert.assertEquals(99, new Params(99, ParameterCount.EXACT).getSize());
    }

    /**
     * Test method for {@link com.persinity.common.db.metainfo.Params#toString()} with one parameter.
     */
    @Test
    public void testToString1() {
        Assert.assertEquals("?", new Params(1, ParameterCount.ROUNDED_BY_PWR_OF_TWO).toString());
        Assert.assertEquals("?", new Params(1, ParameterCount.EXACT).toString());
    }

    /**
     * Test method for {@link com.persinity.common.db.metainfo.Params#toString()} with two parameters.
     */
    @Test
    public void testToString2() {
        Assert.assertEquals("?, ?", new Params(2, ParameterCount.ROUNDED_BY_PWR_OF_TWO).toString());
        Assert.assertEquals("?, ?", new Params(2, ParameterCount.EXACT).toString());
    }

    /**
     * Test method for {@link com.persinity.common.db.metainfo.Params#toString()} with three parameters.
     */
    @Test
    public void testToString3() {
        Assert.assertEquals("?, ?, ?, ?", new Params(3, ParameterCount.ROUNDED_BY_PWR_OF_TWO).toString());
        Assert.assertEquals("?, ?, ?", new Params(3, ParameterCount.EXACT).toString());
    }

}
