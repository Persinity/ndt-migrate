/**
 * Copyright (c) 2015 Persinity Inc.
 */

package com.persinity.common.db.metainfo;

import static org.easymock.EasyMock.createNiceMock;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.replay;
import static org.junit.Assert.assertEquals;

import java.util.Collection;
import java.util.Collections;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;

import com.google.common.collect.Sets;
import com.persinity.common.db.metainfo.constraint.FK;
import com.persinity.common.db.metainfo.constraint.PK;

/**
 * @author dyordanov
 */
public class SkipTablesSchemaTest {

    @Before
    public void setUp() throws Exception {
        schemaMock = createNiceMock(Schema.class);
        expect(schemaMock.getTableNames()).andStubReturn(TABS);
        expect(schemaMock.getUserName()).andStubReturn(USER_NAME);
        expect(schemaMock.getTableCols(TAB1)).andStubReturn(TAB1_COLS);
        expect(schemaMock.getTablePk(TAB1)).andStubReturn(TAB1_PK);
        expect(schemaMock.getTableFks(TAB1)).andStubReturn(TAB1_FKS);
        replay(schemaMock);
        testee = new SkipTablesSchema(SKIP_TABS, schemaMock);
    }

    @Test(expected = NullPointerException.class)
    public void testSkipTablesSchema_InvalidInput10() {
        new SkipTablesSchema(null, schemaMock);
    }

    @Test(expected = NullPointerException.class)
    public void testSkipTablesSchema_InvalidInput01() {
        new SkipTablesSchema(SKIP_TABS, null);
    }

    @Test
    public void testGetTableCols() throws Exception {
        assertEquals(TAB1_COLS, testee.getTableCols(TAB1));
    }

    @Test
    public void testGetTableFks() throws Exception {
        assertEquals(TAB1_FKS, testee.getTableFks(TAB1));
    }

    @Test
    public void testGetTableNames() throws Exception {
        SkipTablesSchema testee = new SkipTablesSchema(SKIP_TABS, schemaMock);
        Set<String> actual = testee.getTableNames();
        Set<String> expected = OK_TABS;
        assertEquals(expected, actual);

        testee = new SkipTablesSchema(TABS, schemaMock);
        actual = testee.getTableNames();
        expected = Collections.emptySet();
        assertEquals(expected, actual);

        testee = new SkipTablesSchema(Collections.<String>emptySet(), schemaMock);
        actual = testee.getTableNames();
        expected = TABS;
        assertEquals(expected, actual);
    }

    @Test
    public void testGetTablePk() throws Exception {
        assertEquals(TAB1_PK, testee.getTablePk(TAB1));
    }

    @Test
    public void testGetUserName() throws Exception {
        assertEquals(USER_NAME, testee.getUserName());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testGetTableCols_InvalidInput() {
        testee.getTableCols(TAB4);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testGetTablePk_InvalidInput() {
        testee.getTablePk(TAB2);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testGetTableFks_InvalidInput() {
        testee.getTableFks(SKIP_TABS.iterator().next());
    }

    private static final String TAB1 = "tab1";
    private static final String TAB2 = "tab2";
    private static final String TAB3 = "tab3";
    private static final String TAB4 = "tab4";
    private static final Set<String> TABS = Sets.newHashSet(TAB1, TAB2, TAB3, TAB4);
    private static final Collection<String> SKIP_TABS = Sets.newHashSet(TAB2, TAB4);
    private static final Set<String> OK_TABS = Sets.newHashSet(TAB1, TAB3);
    private static final String USER_NAME = "user";
    private static final Set<Col> TAB1_COLS = Sets.newHashSet(new Col("col1"), new Col("col2"));
    private static final PK TAB1_PK = createNiceMock(PK.class);
    private static final FK FK1 = createNiceMock(FK.class);
    private static final FK FK2 = createNiceMock(FK.class);
    private static final Set<FK> TAB1_FKS = Sets.newHashSet(FK1, FK2);

    private Schema schemaMock;
    private SkipTablesSchema testee;
}