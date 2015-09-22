/**
 * Copyright (c) 2015 Persinity Inc.
 */

package com.persinity.common.db.metainfo;

import static org.junit.Assert.assertEquals;

import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.Set;

import org.easymock.EasyMock;
import org.junit.Test;

import com.google.common.collect.Sets;
import com.persinity.common.db.metainfo.constraint.FK;
import com.persinity.common.db.metainfo.constraint.PK;

/**
 * Checks that the {@link BufferedSchema} caches correctly the {@link Schema} data after first call.
 *
 * @author dyordanov
 */
public class BufferedSchemaTest {

    @Test
    public void testGetUserName() {
        final Schema schema = EasyMock.createStrictMock(Schema.class);
        EasyMock.expect(schema.getUserName()).andReturn(USER_NAME).once();
        EasyMock.replay(schema);

        final Schema testee = new BufferedSchema(schema);
        assertEquals(USER_NAME, testee.getUserName());
        assertEquals(USER_NAME, testee.getUserName());

        EasyMock.verify(schema);
    }

    @Test
    public void testGetTableNames() {
        final Schema schema = EasyMock.createStrictMock(Schema.class);
        EasyMock.expect(schema.getTableNames()).andReturn(TABLES).once();
        EasyMock.replay(schema);

        final Schema testee = new BufferedSchema(schema);
        assertEquals(TABLES, testee.getTableNames());
        assertEquals(TABLES, testee.getTableNames());

        EasyMock.verify(schema);
    }

    @Test
    public void testGetTableCols() {
        final Schema schema = EasyMock.createStrictMock(Schema.class);
        EasyMock.expect(schema.getTableCols(EasyMock.anyString())).andReturn(TABLE1_COLS).once();
        EasyMock.replay(schema);

        final Schema testee = new BufferedSchema(schema);
        assertEquals(TABLE1_COLS, testee.getTableCols(TABLE1));
        assertEquals(TABLE1_COLS, testee.getTableCols(TABLE1));

        EasyMock.verify(schema);
    }

    @Test
    public void testGetTablePk() {
        final Schema schema = EasyMock.createStrictMock(Schema.class);
        EasyMock.expect(schema.getTablePk(EasyMock.anyString())).andReturn(PRIMARY_KEY).once();
        EasyMock.replay(schema);

        final Schema testee = new BufferedSchema(schema);
        assertEquals(PRIMARY_KEY, testee.getTablePk(TABLE1));
        assertEquals(PRIMARY_KEY, testee.getTablePk(TABLE1));

        EasyMock.verify(schema);
    }

    @Test
    public void testGetTableFks() {
        final Schema schema = EasyMock.createStrictMock(Schema.class);
        EasyMock.expect(schema.getTableFks(EasyMock.anyString())).andReturn(FKS).once();
        EasyMock.replay(schema);

        final Schema testee = new BufferedSchema(schema);
        assertEquals(FKS, testee.getTableFks(TABLE1));
        assertEquals(FKS, testee.getTableFks(TABLE1));

        EasyMock.verify(schema);
    }

    @Test(expected = NullPointerException.class)
    public void testBufferedSchema_InvalidInput() throws Exception {
        new BufferedSchema(null);
    }

    private static final String USER_NAME = "me";
    private static final String TABLE1 = "t1";
    private static final Set<String> TABLES = Sets.newHashSet(TABLE1, "t2");
    private static final Set<Col> TABLE1_COLS = new LinkedHashSet<>(Arrays.asList(new Col("col1"), new Col("col2")));
    private static final PK PRIMARY_KEY = new PK("pk1", TABLE1, TABLE1_COLS);
    private static final FK FK1 = new FK("fk1", TABLE1, TABLE1_COLS, PRIMARY_KEY);
    private static final Set<FK> FKS = Sets.newHashSet(FK1);
}