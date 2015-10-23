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
package com.persinity.ndt.dbagent.relational;

import org.junit.Assert;

import com.persinity.common.StringUtils;
import com.persinity.common.db.Trimmer;
import junit.framework.TestCase;

/**
 * @author Doichin Yordanov
 */
public class TrimmerTest extends TestCase {

    /**
     * Test method for {@link Trimmer#trim(java.lang.String, int)}.
     */
    public void testTrim() {
        final Trimmer trimmer = new Trimmer();

        // Repetition and uniqueness
        String actual = trimmer.trim(VAL, 20);
        String expected = TRIMMED_VAL;
        Assert.assertEquals(expected, actual);

        actual = trimmer.trim(VAL, 20);
        Assert.assertEquals(expected, actual);

        // No trimming
        actual = trimmer.trim(VAL, 21);
        expected = VAL;
        Assert.assertEquals(expected, actual);

        // Impossible trimming
        boolean catched = false;
        try {
            actual = trimmer.trim(VAL, 1);
        } catch (final IllegalArgumentException e) {
            catched = true;
        }
        if (!catched) {
            Assert.fail("Impossible trimming exception not caught!");
        }

    }

    public static final String VAL = "123456789012345678901";
    public static final String TRIMMED_VAL = "1234567890" + StringUtils.hashString(VAL);
}
