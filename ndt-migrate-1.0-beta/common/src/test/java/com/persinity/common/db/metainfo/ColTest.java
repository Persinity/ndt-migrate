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

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;

import org.easymock.EasyMock;
import org.junit.Assert;
import org.junit.Test;

/**
 * @author Doichin Yordanov
 */
public class ColTest {

    /**
     * Test method for {@link com.persinity.common.db.metainfo.Col#hashCode()} and
     * {@link com.persinity.common.db.metainfo.Col#equals(java.lang.Object)}
     */
    @Test
    public void testEqualsHashCode() {
        Col col1 = null, col2 = null;

        // Equal objects
        col1 = col2 = new Col("a", "varchar2(20)", false);
        Assert.assertTrue(col1.equals(col2) && col2.equals(col1));
        Assert.assertEquals(col1.hashCode(), col2.hashCode());

        // Quasi-equal objects
        col1 = new Col("a", "varchar2(20)", true);
        col2 = new Col("a", "date", false);
        Assert.assertTrue(col1.equals(col2) && col2.equals(col1));
        Assert.assertEquals(col1.hashCode(), col2.hashCode());

        // Non equal objects
        col1 = new Col("a", "varchar2(20)", false);
        col2 = new Col("b", "varchar2(20)", false);
        Assert.assertTrue(!col1.equals(col2) && !col2.equals(col1));

        // Null vs. non-null object
        col1 = new Col("a", "varchar2(20)", false);
        Assert.assertTrue(!col1.equals(null));

    }

    /**
     * Test method for {@link com.persinity.common.db.metainfo.Col#calcTypeSql(java.sql.ResultSet)}.
     */
    @Test
    public void testCalcTypeSql() {
        checkType("VARCHAR2(20)", "VARCHAR2", Types.VARCHAR, 20, null);
        checkType("NUMBER(9, 2)", "NUMBER", Types.NUMERIC, 9, 2);
        checkType("DATE", "DATE", Types.DATE, null, null);
    }

    private void checkType(String expected, String sqlType, Integer jdbcType, Integer size, Integer decimals) {
        final ResultSet rs = mockColRs(sqlType, jdbcType, size, decimals);
        String actual = null;
        try {
            actual = Col.calcTypeSql(rs);
        } catch (final SQLException e) {
            Assert.fail("Check mocking!");
        }
        Assert.assertEquals(expected, actual);
    }

    private ResultSet mockColRs(String sqlType, Integer jdbcType, Integer size, Integer decimals) {
        final ResultSet rs = EasyMock.createNiceMock(ResultSet.class);
        try {
            EasyMock.expect(rs.getString(Col.IDX_COL_TYPE)).andReturn(sqlType);
            EasyMock.expect(rs.getInt(Col.IDX_COL_JDBCTYPE)).andReturn(jdbcType);
            if (size != null) {
                EasyMock.expect(rs.getInt(Col.IDX_COL_SIZE)).andReturn(size);
            }
            if (decimals != null) {
                EasyMock.expect(rs.getInt(Col.IDX_COL_DECSIZE)).andReturn(decimals);
            }
            EasyMock.replay(rs);
        } catch (final SQLException e) {
        }
        return rs;
    }

}
