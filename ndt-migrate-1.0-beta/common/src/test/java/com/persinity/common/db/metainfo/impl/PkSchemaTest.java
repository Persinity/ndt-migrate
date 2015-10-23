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

package com.persinity.common.db.metainfo.impl;

import static org.easymock.EasyMock.createNiceMock;
import static org.easymock.EasyMock.createStrictMock;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.verify;
import static org.junit.Assert.assertEquals;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;

import com.persinity.common.collection.DirectedEdge;
import com.persinity.common.db.RelDb;
import com.persinity.common.db.SqlStrategy;
import com.persinity.common.db.metainfo.BufferedSchema;
import com.persinity.common.db.metainfo.Col;
import com.persinity.common.db.metainfo.constraint.PK;
import com.persinity.ndt.transform.ParamQryFunc;

/**
 * @author dyordanov
 */
public class PkSchemaTest {

    @Before
    public void setUp() throws Exception {
        tabConsFParams = Arrays.asList(TABLE_NAME.toUpperCase(), PkSchema.CONS_TYPE_PK);
        db = createNiceMock(RelDb.class);
        tabNameForPkNameNoCallF = createStrictMock(ParamQryFunc.class);
        tabConsNoCallF = createStrictMock(ParamQryFunc.class);
        tableSchema = createStrictMock(BufferedSchema.class);
        expect(tableSchema.getTableCols(TABLE_NAME)).andReturn(TABLE_COLS);
        tableSchemaEmptyRs = createStrictMock(BufferedSchema.class);
        expect(tableSchemaEmptyRs.getTableCols(TABLE_NAME)).andReturn(Collections.<Col>emptySet());
        replay(db, tableSchema, tableSchemaEmptyRs, tabNameForPkNameNoCallF, tabConsNoCallF);
    }

    @Test(expected = UnsupportedOperationException.class)
    public void testGetTableCols() throws Exception {
        final PkSchema testee = new PkSchema(db, tableSchema, tabConsNoCallF, tabNameForPkNameNoCallF);
        testee.getTableCols(TABLE_NAME);
    }

    @Test(expected = UnsupportedOperationException.class)
    public void testGetTableFks() throws Exception {
        final PkSchema testee = new PkSchema(db, tableSchema, tabConsNoCallF, tabNameForPkNameNoCallF);
        testee.getTableFks(TABLE_NAME);
    }

    @Test(expected = UnsupportedOperationException.class)
    public void testGetTableNames() throws Exception {
        final PkSchema testee = new PkSchema(db, tableSchema, tabConsNoCallF, tabNameForPkNameNoCallF);
        testee.getTableNames();
    }

    @Test
    public void testGetTablePk_EmptyPkRs() throws Exception {
        final ParamQryFunc tabConsFEmptyRs = createStrictMock(ParamQryFunc.class);
        expect(tabConsFEmptyRs.apply(new DirectedEdge<RelDb, List<?>>(db, tabConsFParams)))
                .andReturn(Collections.<Map<String, Object>>emptyIterator());
        replay(tabConsFEmptyRs);

        final PkSchema testee = new PkSchema(db, tableSchema, tabConsFEmptyRs, tabNameForPkNameNoCallF);
        final PK actual = testee.getTablePk(TABLE_NAME);

        assertEquals(null, actual);
        verify(tabConsFEmptyRs, tabNameForPkNameNoCallF);
    }

    @Test
    public void testGetTablePk_EmptyColsRs() throws Exception {
        final ParamQryFunc tabConsFEmptyRs = createStrictMock(ParamQryFunc.class);
        expect(tabConsFEmptyRs.apply(new DirectedEdge<RelDb, List<?>>(db, tabConsFParams)))
                .andReturn(Collections.<Map<String, Object>>emptyIterator());
        replay(tabConsFEmptyRs);

        final PkSchema testee = new PkSchema(db, tableSchemaEmptyRs, tabConsFEmptyRs, tabNameForPkNameNoCallF);
        final PK actual = testee.getTablePk(TABLE_NAME);

        assertEquals(null, actual);
        verify(tabConsFEmptyRs, tabNameForPkNameNoCallF);
    }

    @Test
    public void testGetTablePk() throws Exception {
        final ParamQryFunc tabConsF = createStrictMock(ParamQryFunc.class);
        final List<Map<String, Object>> tabConsFRes = new LinkedList<>();
        addPkRsRow(tabConsFRes, VAL_PK_COL1_NAME);
        addPkRsRow(tabConsFRes, VAL_PK_COL2_NAME);
        expect(tabConsF.apply(new DirectedEdge<RelDb, List<?>>(db, tabConsFParams))).andReturn(tabConsFRes.iterator());
        replay(tabConsF);

        final PkSchema testee = new PkSchema(db, tableSchema, tabConsF, tabNameForPkNameNoCallF);
        final PK actual = testee.getTablePk(TABLE_NAME);
        final PK expected = new PK(VAL_CONSTRAINT_NAME, TABLE_NAME, TABLE_COLS);

        assertEquals(expected, actual);
        verify(tabConsF, tableSchema, tabNameForPkNameNoCallF);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testGetTablePk_IllegalInput() throws Exception {
        final PkSchema testee = new PkSchema(db, tableSchema, tabConsNoCallF, tabNameForPkNameNoCallF);
        testee.getTablePk(ILLEGAL_INPUT);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testGetTableName_IllegalInput() throws Exception {
        final PkSchema testee = new PkSchema(db, tableSchema, tabConsNoCallF, tabNameForPkNameNoCallF);
        testee.getTableName(ILLEGAL_INPUT);
    }

    @Test
    public void testGetTableName_EmptyTabRs() throws Exception {
        final ParamQryFunc tabNameForPkNameF = createStrictMock(ParamQryFunc.class);
        final List<?> params = Collections.singletonList(VAL_CONSTRAINT_NAME.toUpperCase());
        expect(tabNameForPkNameF.apply(new DirectedEdge<RelDb, List<?>>(db, params)))
                .andReturn(Collections.<Map<String, Object>>emptyIterator());
        replay(tabNameForPkNameF);

        final PkSchema testee = new PkSchema(db, tableSchema, tabConsNoCallF, tabNameForPkNameF);
        final String actual = testee.getTableName(VAL_CONSTRAINT_NAME);

        assertEquals(null, actual);
        verify(tabNameForPkNameF, tabConsNoCallF);
    }

    @Test
    public void testGetTableName() throws Exception {
        final ParamQryFunc tabNameForPkNameF = createStrictMock(ParamQryFunc.class);
        final List<?> params = Collections.singletonList(VAL_CONSTRAINT_NAME.toUpperCase());
        final List<Map<String, Object>> tabNameForPkNameRs = new LinkedList<>();
        final Map<String, Object> tabNameForPkNameRow = new HashMap<>();
        tabNameForPkNameRow.put("bla", TABLE_NAME.toUpperCase());
        tabNameForPkNameRs.add(tabNameForPkNameRow);
        expect(tabNameForPkNameF.apply(new DirectedEdge<RelDb, List<?>>(db, params)))
                .andReturn(tabNameForPkNameRs.iterator());
        replay(tabNameForPkNameF);

        final PkSchema testee = new PkSchema(db, tableSchema, tabConsNoCallF, tabNameForPkNameF);
        final String actual = testee.getTableName(VAL_CONSTRAINT_NAME);

        assertEquals(TABLE_NAME, actual);
        verify(tabNameForPkNameF, tabConsNoCallF);
    }

    @Test(expected = UnsupportedOperationException.class)
    public void testGetUserName() throws Exception {
        final PkSchema testee = new PkSchema(db, tableSchema, tabConsNoCallF, tabNameForPkNameNoCallF);
        testee.getUserName();
    }

    private void addPkRsRow(final List<Map<String, Object>> tabConsFRes, final String pkColName) {
        final Map<String, Object> tabConsFResMap = new HashMap<>();
        tabConsFResMap.put(SqlStrategy.COL_CONSTRAINT_NAME, VAL_CONSTRAINT_NAME);
        tabConsFResMap.put(SqlStrategy.COL_COLUMN_NAME, pkColName);
        tabConsFRes.add(tabConsFResMap);
    }

    private static final String TABLE_NAME = "emp";
    private static final String VAL_CONSTRAINT_NAME = "pk_emp";
    private static final String VAL_PK_COL1_NAME = "emp_id1";
    private static final String VAL_PK_COL2_NAME = "emp_id2";
    private static final Set<Col> TABLE_COLS = new LinkedHashSet<Col>(
            Arrays.asList(new Col(VAL_PK_COL1_NAME), new Col(VAL_PK_COL2_NAME)));
    public static final String ILLEGAL_INPUT = "";

    private RelDb db;
    private List<String> tabConsFParams;
    private ParamQryFunc tabNameForPkNameNoCallF;
    private BufferedSchema tableSchema;
    private BufferedSchema tableSchemaEmptyRs;
    private ParamQryFunc tabConsNoCallF;
}