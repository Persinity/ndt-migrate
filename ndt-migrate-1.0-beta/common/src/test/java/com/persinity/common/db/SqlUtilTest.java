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
package com.persinity.common.db;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import com.persinity.common.db.metainfo.Col;

/**
 * @author Ivan Dachev
 */
public class SqlUtilTest {

    /**
     * Test method for {@link SqlUtil#toEqualParams(List)}
     */
    @Test
    public void testToEqualParams() {
        List<Col> list = new ArrayList<>();

        list.add(new Col("col1"));
        assertEquals("[col1 = ?]", SqlUtil.toEqualParams(list).toString());

        list.add(new Col("col2"));
        assertEquals("[col1 = ?, col2 = ?]", SqlUtil.toEqualParams(list).toString());
    }

    /**
     * Test method for {@link SqlUtil#findColumn(List, String)}
     */
    @Test
    public void testFindColumn() {
        List<Col> list = new ArrayList<>();
        list.add(new Col("col1"));
        list.add(new Col("col2"));
        assertThat(SqlUtil.findColumn(list, "col1"), is(list.get(0)));
        assertThat(SqlUtil.findColumn(list, "col2"), is(list.get(1)));
        assertThat(SqlUtil.findColumn(list, "col3"), nullValue());
    }
}