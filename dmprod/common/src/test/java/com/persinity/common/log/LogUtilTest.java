/**
 * Copyright (c) 2015 Persinity Inc.
 */
package com.persinity.common.log;

import org.junit.Assert;
import org.junit.Test;

import com.persinity.common.logging.LogUtil;

/**
 * @author Doichin Yordanov
 */
public class LogUtilTest {

    /**
     * Test method for {@link LogUtil#formatPackageName(java.lang.String)}.
     */
    @Test
    public void testFormatPackageName() {
        String actual = LogUtil.formatPackageName(null);
        Assert.assertEquals("null", actual);

        actual = LogUtil.formatPackageName("");
        Assert.assertEquals("", actual);

        actual = LogUtil.formatPackageName(" ");
        Assert.assertEquals(" ", actual);

        actual = LogUtil.formatPackageName("com.persinity.ndt");
        Assert.assertEquals("c.p.ndt", actual);
    }

}
