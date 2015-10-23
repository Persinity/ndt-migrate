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

/**
 * @author Doichin Yordanov
 */
public class SqlFormatterTest {

    @Test(expected = IllegalArgumentException.class)
    public void testSubstringInvalidInput() {
        SqlFormatter.substring(null, "1", "2");
    }

    @Test
    public void testSubstring() {
        String actual = SqlFormatter.substring("12345", null, null);
        String expected = "12345";
        Assert.assertEquals(expected, actual);

        actual = SqlFormatter.substring("12345", "1", "6");
        expected = "";
        Assert.assertEquals(expected, actual);

        actual = SqlFormatter.substring("12345", "0", "5");
        expected = "";
        Assert.assertEquals(expected, actual);

        actual = SqlFormatter.substring("12345", null, "0");
        expected = "";
        Assert.assertEquals(expected, actual);

        actual = SqlFormatter.substring("12345", "0", null);
        expected = "";
        Assert.assertEquals(expected, actual);

        actual = SqlFormatter.substring("12345", "3", "2");
        expected = "";
        Assert.assertEquals(expected, actual);

        actual = SqlFormatter.substring("12345", "1", "5");
        expected = "234";
        Assert.assertEquals(expected, actual);

        actual = SqlFormatter.substring("12345", "2", "4");
        expected = "3";
        Assert.assertEquals(expected, actual);

        actual = SqlFormatter.substring("12345", "2", "3");
        expected = "";
        Assert.assertEquals(expected, actual);

        actual = SqlFormatter.substring("12345", "2", "2");
        expected = "";
        Assert.assertEquals(expected, actual);

        actual = SqlFormatter.substring("123452", "2", "2");
        expected = "345";
        Assert.assertEquals(expected, actual);

    }

    @Test(expected = NullPointerException.class)
    public void testGetTableClauseInvalidInput() {
        SqlFormatter.getTableClause(null);
    }

    @Test
    public void testGetTableClause() {
        String actual = SqlFormatter
                .getTableClause("SELECT 1, 2, 3 FROM dual WHERE 1 = 1 OR 2 = 2 GROUP BY 1 ORDER BY 1");
        String expected = "dual";
        Assert.assertEquals(expected, actual);

        actual = SqlFormatter.getTableClause("SELECT 1, 2, 3 FROM dual");
        expected = "dual";
        Assert.assertEquals(expected, actual);

        actual = SqlFormatter.getTableClause("SELECT 1, 2, 3 FROM dual WHERE 1 = 1");
        expected = "dual";
        Assert.assertEquals(expected, actual);

        actual = SqlFormatter.getTableClause("SELECT 1, 2, 3 FROM dual WHERE 1 = 1 GROUP BY 1");
        expected = "dual";
        Assert.assertEquals(expected, actual);

        actual = SqlFormatter.getTableClause("SELECT 1, 2, 3 FROM dual WHERE 1 = 1 ORDER BY 1");
        expected = "dual";
        Assert.assertEquals(expected, actual);

        actual = SqlFormatter.getTableClause("SELECT 1, 2, 3 FROM dual ORDER BY 1");
        expected = "dual";
        Assert.assertEquals(expected, actual);

        actual = SqlFormatter
                .getTableClause("SELECT * FROM tab1 a INNER JOIN tab2 b ON (a.id = b.id) WHERE 1 = 1 GROUP BY 1");
        expected = "tab1 a INNER JOIN tab2 b ON (a.id = b.id)";
        Assert.assertEquals(expected, actual);

        actual = SqlFormatter.getTableClause(
                "SELECT * FROM (SELECT * FROM tab1 a INNER JOIN tab2 b ON (a.id = b.id)) WHERE 1 = 1 GROUP BY 1");
        expected = "tab1 a INNER JOIN tab2 b ON (a.id = b.id)";
        Assert.assertEquals(expected, actual);

        actual = SqlFormatter.getTableClause(
                "SELECT * FROM (SELECT * FROM (SELECT * FROM tab1 a INNER JOIN tab2 b ON (a.id = b.id))) WHERE 1 = 1 GROUP BY 1");
        expected = "tab1 a INNER JOIN tab2 b ON (a.id = b.id)";
        Assert.assertEquals(expected, actual);

        actual = SqlFormatter.getTableClause("INSERT INTO tab (col1, col2, col3) VALUES (1, 2, 3)");
        expected = "tab";
        Assert.assertEquals(expected, actual);

        actual = SqlFormatter.getTableClause("INSERT INTO tab (col1, col2, col3) AS SELECT * FROM tab1");
        expected = "tab";
        Assert.assertEquals(expected, actual);

        actual = SqlFormatter.getTableClause("UPDATE tab SET col1 = 1, col2 = 2 WHERE col3 = 3");
        expected = "tab";
        Assert.assertEquals(expected, actual);

        actual = SqlFormatter.getTableClause("DELETE FROM tab WHERE col1 = 1");
        expected = "tab";
        Assert.assertEquals(expected, actual);

        actual = SqlFormatter.getTableClause("DELETE FROM tab");
        expected = "tab";
        Assert.assertEquals(expected, actual);

    }

}
