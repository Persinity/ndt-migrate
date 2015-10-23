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
