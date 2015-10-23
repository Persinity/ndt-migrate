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

import static org.easymock.EasyMock.createNiceMock;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.verify;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;

import java.util.Arrays;
import java.util.Properties;

import org.easymock.EasyMock;
import org.junit.Before;
import org.junit.Test;

/**
 * @author dyordanov
 */
public class ConfigTest {

    @Before
    public void setUp() {
        propsStub = new Properties();
        propsStub.setProperty(KEY1_STRING, VAL1);
        propsStub.setProperty(KEY2_STRING, VAL2);
        propsStub.setProperty(KEY3_INTEGER, VAL3.toString());
        propsStub.setProperty(KEY4_INTEGER, VAL4.toString());
        propsStub.setProperty(KEY5_NEGATIVE_INTEGER, VAL5.toString());
        propsStub.setProperty(KEY6_BOOL_FALSE, VAL6.toString());
        propsStub.setProperty(KEY7_BOOL_TRUE, VAL7.toString());
        propsStub.setProperty(KEY8_TEXT, VAL8);
        propsStub.setProperty(KEY9_LONG, VAL9.toString());
        propsStub.setProperty(KEY10_NEGATIVE_LONG, VAL10.toString());
    }

    @Test(expected = NullPointerException.class)
    public void testConfig_InvalidInput10() {
        new Config(null, PROPS_SOURCE);
    }

    @Test(expected = NullPointerException.class)
    public void testConfig_InvalidInput01() {
        new Config(createNiceMock(Properties.class), null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testConfig_InvalidInput01_1() {
        new Config(createNiceMock(Properties.class), "");
    }

    @Test(expected = NullPointerException.class)
    public void testGetString_InvalidInput1() {
        final Properties props = EasyMock.createStrictMock(Properties.class);
        replay(props);
        new Config(props, PROPS_SOURCE).getString(null);
        verify(props);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testGetString_InvalidInput2() {
        final Properties props = EasyMock.createStrictMock(Properties.class);
        replay(props);
        new Config(props, PROPS_SOURCE).getString("");
        verify(props);
    }

    @Test
    public void testGetString() {
        final Config testee = new Config(propsStub, PROPS_SOURCE);
        assertEquals(VAL1, testee.getString(KEY1_STRING));
        assertEquals(VAL2, testee.getString(KEY2_STRING));
        assertNotEquals(VAL1, testee.getString(KEY2_STRING));
        assertEquals(VAL3.toString(), testee.getString(KEY3_INTEGER));
        assertEquals(VAL5.toString(), testee.getString(KEY5_NEGATIVE_INTEGER));
        assertEquals(VAL7.toString(), testee.getString(KEY7_BOOL_TRUE));
    }

    @Test
    public void testGetInt() {
        final Config testee = new Config(propsStub, PROPS_SOURCE);
        assertEquals(VAL3.intValue(), testee.getInt(KEY3_INTEGER));
        assertNotEquals(VAL3.intValue(), testee.getInt(KEY4_INTEGER));
        assertEquals(VAL4.intValue(), testee.getInt(KEY4_INTEGER));
    }

    @Test
    public void testGetInt_InvalidInput() {
        try {
            new Config(propsStub, PROPS_SOURCE).getInt(KEY1_STRING);
        } catch (IllegalArgumentException e) {
            assertEquals(
                    "Expected an integer for property \"" + KEY1_STRING + "\" in \"" + PROPS_SOURCE + "\", found \""
                            + VAL1 + "\"", e.getMessage());
            return;
        }
        fail();
    }

    @Test
    public void testGetLong() {
        final Config testee = new Config(propsStub, PROPS_SOURCE);
        assertEquals(VAL9.longValue(), testee.getLong(KEY9_LONG));
        assertNotEquals(VAL10.longValue(), testee.getLong(KEY9_LONG));
        assertEquals(VAL10.longValue(), testee.getLong(KEY10_NEGATIVE_LONG));
    }

    @Test
    public void testGetLong_InvalidInput() {
        try {
            new Config(propsStub, PROPS_SOURCE).getLong(KEY1_STRING);
            fail();
        } catch (IllegalArgumentException e) {
            assertEquals(
                    "Expected a long integer for property \"" + KEY1_STRING + "\" in \"" + PROPS_SOURCE + "\", found \""
                            + VAL1 + "\"", e.getMessage());
        }
    }

    @Test
    public void testGetPositiveLong() {
        final Config testee = new Config(propsStub, PROPS_SOURCE);
        assertEquals(VAL9.longValue(), testee.getPositiveLong(KEY9_LONG));
    }

    @Test
    public void testGetPositiveLong_Invalid() {
        final Config testee = new Config(propsStub, PROPS_SOURCE);
        try {
            testee.getPositiveLong(KEY10_NEGATIVE_LONG);
            fail();
        } catch (IllegalArgumentException e) {
            assertEquals(
                    "Expected positive long integer for property \"" + KEY10_NEGATIVE_LONG + "\" in \"" + PROPS_SOURCE
                            + "\", found \"" + VAL10 + "\"", e.getMessage());
        }
    }

    @Test
    public void testGetBoolean() {
        final Config testee = new Config(propsStub, PROPS_SOURCE);
        assertEquals(VAL7.booleanValue(), testee.getBoolean(KEY7_BOOL_TRUE));
        assertNotEquals(VAL7.booleanValue(), testee.getBoolean(KEY6_BOOL_FALSE));
        assertEquals(VAL7.booleanValue(), testee.getBoolean(KEY7_BOOL_TRUE));
        assertEquals(false, testee.getBoolean(KEY8_TEXT));
        assertEquals(false, testee.getBoolean(KEY3_INTEGER));
    }

    @Test
    public void testGetBooleanDefault() {
        final Config testee = new Config(propsStub, PROPS_SOURCE);
        assertEquals(false, testee.getBooleanDefault("nana", false));
    }

    @Test
    public void testDumpProperties() throws Exception {
        final Config testee = new Config(propsStub, PROPS_SOURCE);

        String res = testee.dumpProperties();
        assertThat(res, is("boolean.key.6 = \"false\", boolean.key.7 = \"true\", boolean.key.8 = \"Yes\", "
                + "integer.key.3 = \"3\", integer.key.4 = \"4\", integer.key.5 = \"-1\", "
                + "long.key.9 = \"9000000000000000000\", negativelong.key.10 = \"-9000000000000000000\", "
                + "string.key.1 = \"string1\", string.key.2 = \"string2\""));

        res = testee.dumpProperties(Arrays.asList(KEY1_STRING));
        assertThat(res, is("string.key.1 = \"string1\""));

        res = testee.dumpProperties(Arrays.asList(KEY1_STRING, KEY4_INTEGER, "invalid"));
        assertThat(res, is("string.key.1 = \"string1\", integer.key.4 = \"4\", invalid = null"));
    }

    @Test
    public void testGetStringDefault() {
        final Config testee = new Config(propsStub, PROPS_SOURCE);
        assertEquals("default", testee.getStringDefault("nana", "default"));
    }

    private static final String PROPS_SOURCE = "property_source";
    private static final String KEY1_STRING = "string.key.1";
    private static final String VAL1 = "string1";
    private static final String KEY2_STRING = "string.key.2";
    private static final String VAL2 = "string2";
    private static final String KEY3_INTEGER = "integer.key.3";
    private static final Integer VAL3 = 3;
    private static final String KEY4_INTEGER = "integer.key.4";
    private static final Integer VAL4 = 4;
    private static final String KEY5_NEGATIVE_INTEGER = "integer.key.5";
    private static final Integer VAL5 = -1;
    private static final String KEY6_BOOL_FALSE = "boolean.key.6";
    private static final Boolean VAL6 = false;
    private static final String KEY7_BOOL_TRUE = "boolean.key.7";
    private static final Boolean VAL7 = true;
    private static final String KEY8_TEXT = "boolean.key.8";
    private static final String VAL8 = "Yes";
    private static final String KEY9_LONG = "long.key.9";
    private static final Long VAL9 = 9000000000000000000L;
    private static final String KEY10_NEGATIVE_LONG = "negativelong.key.10";
    private static final Long VAL10 = -9000000000000000000L;

    private Properties propsStub;
}