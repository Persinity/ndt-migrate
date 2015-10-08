/**
 * Copyright (c) 2015 Persinity Inc.
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
