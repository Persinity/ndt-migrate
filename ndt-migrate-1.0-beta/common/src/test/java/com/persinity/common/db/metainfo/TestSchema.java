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

import static com.persinity.common.invariant.Invariant.notNull;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import com.persinity.common.db.metainfo.constraint.FK;
import com.persinity.common.db.metainfo.constraint.PK;
import com.persinity.common.db.metainfo.constraint.Unique;

/**
 * Test utils for {@link Schema} integration testing
 *
 * @author dyordanov
 */
public class TestSchema {

    public TestSchema(final Schema testee) {
        notNull(testee);
        this.testee = testee;
    }

    public void testGetTableCols() throws Exception {
        final Set<Col> cols = testee.getTableCols(EMP);
        final Set<Col> expectedTableCols = new HashSet<>(Arrays.asList(ID, BIN_ID, NAME, DEPT_ID));
        assertEquals(expectedTableCols, cols);

        final Map<String, Col> colsMap = Col.toColsMap(cols);
        assertTrue(colsMap.get(NAME.getName()).isNullAllowed());
        assertFalse(colsMap.get(ID.getName()).isNullAllowed());
    }

    public void testGetTableColsInvalidInput() throws Exception {
        testee.getTableCols("");
    }

    public void testToColsMap() {
        final Set<Col> cols = new HashSet<>(Arrays.asList(ID, NAME));
        final Map<String, Col> colsMap = Col.toColsMap(cols);
        final Map<String, Col> expectedColsMap = new HashMap<>();
        expectedColsMap.put(ID.getName(), ID);
        expectedColsMap.put(NAME.getName(), NAME);
        assertEquals(expectedColsMap, colsMap);

        assertEquals(Collections.emptyMap(), Col.toColsMap(Collections.<Col>emptySet()));
    }

    public void testGetTableFks(Unique uq) throws Exception {
        final Set<FK> fks = testee.getTableFks(EMP);
        final Set<FK> expectedFks = new HashSet<>();
        expectedFks.add(new FK("fk_emp2dept", EMP, Collections.singleton(DEPT_ID), uq));
        assertEquals(expectedFks, fks);
    }

    public void testGetTableFksInvalidInput() throws Exception {
        testee.getTableFks("");
    }

    public void testGetTableNames() throws Exception {
        final Set<String> tableNames = testee.getTableNames();
        final Set<String> expectedTableNames = new HashSet<>(Arrays.asList(EMP, DEPT));
        assertTrue(tableNames.containsAll(expectedTableNames));
    }

    public void testGetTablePk() throws Exception {
        final PK pk = testee.getTablePk(DEPT);
        assertEquals(PK_DEPT, pk);
    }

    public void testGetTablePkInvalidInput() throws Exception {
        testee.getTablePk("");
    }

    public void testGetTablePk_NoConstraints() {
        final PK actual = testee.getTablePk("dual");
        assertNull(actual);
    }

    public void testGetTableFks_NoConstraints() {
        final Set<FK> fks = testee.getTableFks("dual");
        assertNotNull(fks);
        assertTrue(fks.isEmpty());
    }

    public void testGetTableName() {
        String actual = testee.getTableName(PK_DEPT_NAME);
        assertEquals(DEPT, actual);
        actual = testee.getTableName(PK_DEPT_NAME + "1");
        assertNull(actual);
    }

    public void testGetTableNameInvalidInputEmpty() throws Exception {
        testee.getTableName("");
    }

    public void testGetTableNameInvalidInputNull() throws Exception {
        testee.getTableName(null);
    }

    public void testGetUserName(final String expected) {
        assertEquals(expected, testee.getUserName());
    }

    public static final Col ID = new Col("id");
    public static final Col NAME = new Col("name");
    public static final Col DEPT_ID = new Col("dept_id", "NUMBER(9)", false);
    public static final Col BIN_ID = new Col("bin_id");
    public static final String EMP = "emp";
    public static final String DEPT = "dept";
    public static final String PK_DEPT_NAME = "pk_dept";
    public static final PK PK_DEPT = new PK(PK_DEPT_NAME, DEPT, Collections.singleton(ID));
    public static final Unique UQ_DEPT = new Unique("Unique", DEPT, Collections.singleton(ID));

    private final Schema testee;
}
