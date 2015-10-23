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
import java.util.HashSet;
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
import com.persinity.common.db.metainfo.constraint.FK;
import com.persinity.common.db.metainfo.constraint.PK;
import com.persinity.ndt.transform.ParamQryFunc;

/**
 * @author dyordanov
 */
public class FkSchemaTest {

    @Before
    public void setUp() throws Exception {
        db = createNiceMock(RelDb.class);
        tableSchema = createStrictMock(BufferedSchema.class);
        expect(tableSchema.getTableCols(TAB_EMP_NAME))
                .andReturn(new LinkedHashSet<Col>(Arrays.asList(COL_DEPT_ID, COL_GEO_ID)));
        pkSchema = createStrictMock(BufferedSchema.class);
        expect(pkSchema.getTableName(PK_DEPT_NAME)).andReturn(TAB_DEPT_NAME);
        expect(pkSchema.getTablePk(TAB_DEPT_NAME)).andReturn(PK_DEPT);
        expect(pkSchema.getTableName(PK_GEO_NAME)).andReturn(TAB_GEO_NAME);
        expect(pkSchema.getTablePk(TAB_GEO_NAME)).andReturn(PK_GEO);
        tabConsNoCallF = createStrictMock(ParamQryFunc.class);
        replay(db, tableSchema, pkSchema, tabConsNoCallF);
    }

    @Test(expected = UnsupportedOperationException.class)
    public void testGetTableCols() throws Exception {
        final FkSchema testee = new FkSchema(db, tableSchema, pkSchema, tabConsNoCallF);
        testee.getTableCols(TAB_EMP_NAME);
    }

    @Test
    public void testGetTableFks_EmptyRs() throws Exception {
        final ParamQryFunc tabConsFRsEmpty = createStrictMock(ParamQryFunc.class);
        final List<?> params = Arrays.asList(TAB_EMP_NAME.toUpperCase(), FkSchema.CONS_TYPE_FK);
        expect(tabConsFRsEmpty.apply(new DirectedEdge<RelDb, List<?>>(db, params)))
                .andReturn(Collections.<Map<String, Object>>emptyIterator());

        replay(tabConsFRsEmpty);
        final FkSchema testee = new FkSchema(db, tableSchema, pkSchema, tabConsFRsEmpty);
        final Set<FK> actual = testee.getTableFks(TAB_EMP_NAME);

        assertEquals(Collections.emptySet(), actual);
        verify(tabConsFRsEmpty);
    }

    @Test
    public void testGetTableFks() {
        final ParamQryFunc tabConsF = createStrictMock(ParamQryFunc.class);
        final List<?> params = Arrays.asList(TAB_EMP_NAME.toUpperCase(), FkSchema.CONS_TYPE_FK);
        final List<Map<String, Object>> rs = new LinkedList<>();
        addRsRow(rs, FK_EMP2DEPT_NAME, PK_DEPT_NAME, COL_DEPT_ID.getName());
        addRsRow(rs, FK_EMP2GEO_NAME, PK_GEO_NAME, COL_GEO_ID.getName());
        expect(tabConsF.apply(new DirectedEdge<RelDb, List<?>>(db, params))).andReturn(rs.iterator());

        replay(tabConsF);
        final FkSchema testee = new FkSchema(db, tableSchema, pkSchema, tabConsF);
        final Set<FK> actual = testee.getTableFks(TAB_EMP_NAME);

        assertEquals(new HashSet<FK>(Arrays.asList(FK_EMP2DEPT, FK_EMP2GEO)), actual);
        verify(tableSchema, pkSchema, tabConsF);
    }

    private void addRsRow(final List<Map<String, Object>> rs, final String fkName, final String pkName,
            final String colName) {
        final Map<String, Object> row = new HashMap<>();
        row.put(SqlStrategy.COL_CONSTRAINT_NAME, fkName);
        row.put(SqlStrategy.COL_REF_CONSTRAINT_NAME, pkName);
        row.put(SqlStrategy.COL_COLUMN_NAME, colName);
        rs.add(row);
    }

    @Test(expected = UnsupportedOperationException.class)
    public void testGetTableNames() throws Exception {
        final FkSchema testee = new FkSchema(db, tableSchema, pkSchema, tabConsNoCallF);
        testee.getTableNames();
    }

    @Test(expected = UnsupportedOperationException.class)
    public void testGetTablePk() throws Exception {
        final FkSchema testee = new FkSchema(db, tableSchema, pkSchema, tabConsNoCallF);
        testee.getTablePk(TAB_EMP_NAME);
    }

    @Test(expected = UnsupportedOperationException.class)
    public void testGetTableName() throws Exception {
        final FkSchema testee = new FkSchema(db, tableSchema, pkSchema, tabConsNoCallF);
        testee.getTableName(PK_EMP_NAME);
    }

    @Test(expected = UnsupportedOperationException.class)
    public void testGetUserName() throws Exception {
        final FkSchema testee = new FkSchema(db, tableSchema, pkSchema, tabConsNoCallF);
        testee.getUserName();
    }

    private static final String TAB_EMP_NAME = "emp";
    private static final String TAB_DEPT_NAME = "dept";
    private static final String TAB_GEO_NAME = "geo";
    private static final String PK_EMP_NAME = "pk_emp";
    private static final String FK_EMP2GEO_NAME = "fk_emp2dept";
    private static final String FK_EMP2DEPT_NAME = "fk_emp2geo";
    private static final Col COL_DEPT_ID = new Col("dept_id");
    private static final Col COL_GEO_ID = new Col("geo_id");
    private static Set<Col> FK_EMP2DEPT_COLS = new HashSet<>(Arrays.asList(COL_DEPT_ID));
    private static Set<Col> FK_EMP2GEO_COLS = new HashSet<>(Arrays.asList(COL_GEO_ID));
    private static final String PK_DEPT_NAME = "pk_dept";
    private static final String PK_GEO_NAME = "pk_geo";
    private static final Set<Col> PK_DEPT_COLS = new HashSet<>(Arrays.asList(COL_DEPT_ID));
    private static final Set<Col> PK_GEO_COLS = new HashSet<>(Arrays.asList(COL_GEO_ID));
    private static final PK PK_DEPT = new PK(PK_DEPT_NAME, TAB_DEPT_NAME, PK_DEPT_COLS);
    private static final PK PK_GEO = new PK(PK_GEO_NAME, TAB_GEO_NAME, PK_GEO_COLS);
    private static final FK FK_EMP2DEPT = new FK(FK_EMP2DEPT_NAME, TAB_EMP_NAME, FK_EMP2DEPT_COLS, PK_DEPT);
    private static final FK FK_EMP2GEO = new FK(FK_EMP2GEO_NAME, TAB_EMP_NAME, FK_EMP2GEO_COLS, PK_GEO);

    private RelDb db;
    private BufferedSchema tableSchema;
    private BufferedSchema pkSchema;
    private ParamQryFunc tabConsNoCallF;
}