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
package com.persinity.ndt.datamutator.reldb;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import org.junit.Before;
import org.junit.Test;

/**
 * @author Ivan Dachev
 */
public class RelDbTypeFactoryTest {
    @Before
    public void setUp() throws Exception {
        testee = new RelDbTypeFactory();
    }

    @Test
    public void testFormatValue_Number() throws Exception {
        assertThat((Integer) testee.formatValue("number", 1001, null), is(0));
        assertThat((Integer) testee.formatValue("number(0)", 1001, null), is(0));
        assertThat((Boolean) testee.formatValue("number(1)", 1001, null), is(false));
        assertThat((Boolean) testee.formatValue("number(1)", 1002, null), is(true));
        assertThat((Integer) testee.formatValue("number(10)", 1001, null), is(1001));
        assertThat((Long) testee.formatValue("number(11)", 1001L, null), is(1001L));
        assertThat((Long) testee.formatValue("number(19,9)", 1001222333333333333L, null), is(1001222333333333333L));
    }

    @Test
    public void testFormatValue_Varchar() throws Exception {
        assertThat((String) testee.formatValue("varchar2", 1001, null), is("1001"));
        assertThat((String) testee.formatValue("varchar2(1 CHAR)", 1001, "test_1001"), is("t"));
        assertThat((String) testee.formatValue("varchar2(10 CHAR)", 1001, "test_2000asdss"), is("test_2000a"));
        assertThat((String) testee.formatValue("varchar2(255 CHAR)", 1001L, "test_long1test_long2test_long"),
                is("test_long1test_long2test_long"));
    }

    @Test
    public void testFormatValue_char() throws Exception {
        assertThat((String) testee.formatValue("char", 1001, null), is("1001"));
        assertThat((String) testee.formatValue("char(1)", 1001, "test_1001"), is("t"));
    }

    @Test
    public void testFormatValue_clob() throws Exception {
        assertThat((String) testee.formatValue("clob", 1001, null), is("1001"));
        assertThat((String) testee.formatValue("clob", 1001, "test_1001"), is("test_1001"));
    }

    private RelDbTypeFactory testee;
}