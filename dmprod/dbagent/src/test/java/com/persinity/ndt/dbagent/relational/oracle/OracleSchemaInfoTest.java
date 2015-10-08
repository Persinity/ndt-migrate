/**
 * Copyright (c) 2015 Persinity Inc.
 */
package com.persinity.ndt.dbagent.relational.oracle;

import static com.persinity.common.StringUtils.format;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import org.easymock.EasyMock;
import org.junit.Test;

import com.persinity.common.StringUtils;
import com.persinity.common.db.Trimmer;
import com.persinity.common.db.metainfo.Col;
import com.persinity.common.db.metainfo.Schema;
import com.persinity.common.db.metainfo.constraint.PK;
import com.persinity.ndt.dbagent.relational.SchemaInfo;

/**
 * @author Doichin Yordanov
 */
public class OracleSchemaInfoTest {

    /**
     * Test method for {@link com.persinity.ndt.dbagent.relational.oracle.OracleSchemaInfo#getTableNames()}.
     */
    @Test
    public void testGetTableNames() {
        final Schema schema = mockSchema();
        final OracleSchemaInfo schemaInfo = new OracleSchemaInfo(schema, new Trimmer(), MAX_NAME_LENGTH);

        Set<String> actualTableNames = schemaInfo.getTableNames();
        assertThat(actualTableNames, equalTo(EXPECTED_TABLE_NAMES));

        // Check cache
        actualTableNames = schemaInfo.getTableNames();
        assertThat(actualTableNames, equalTo(EXPECTED_TABLE_NAMES));
    }

    /**
     * Test method for
     * {@link com.persinity.ndt.dbagent.relational.oracle.OracleSchemaInfo#newName(java.lang.String, java.lang.String)}.
     */
    @Test
    public void testNewName() {
        final Schema schema = mockSchema();
        final OracleSchemaInfo schemaInfo = new OracleSchemaInfo(schema, new Trimmer(), MAX_NAME_LENGTH);

        String actual = schemaInfo.newName("short", "name");
        String expected = "shortname";
        assertThat(actual, equalTo(expected));

        actual = schemaInfo.newName("short", "name");
        expected = "shortname";
        assertThat(actual, equalTo(expected));

        actual = schemaInfo.newName("verylong", "naame");
        expected = "v" + StringUtils.hashString("verylongnaame");
        assertThat(actual, equalTo(expected));

    }

    @Test(expected = NullPointerException.class)
    public void testNewNameNull() {
        final Schema schema = mockSchema();
        final OracleSchemaInfo schemaInfo = new OracleSchemaInfo(schema, new Trimmer(), MAX_NAME_LENGTH);

        schemaInfo.newName(null, "name");
    }

    /**
     * Test method for
     * {@link com.persinity.ndt.dbagent.relational.oracle.OracleSchemaInfo#getClogTableName(java.lang.String)}.
     */
    @Test
    public void testGetClogTableName() {
        final Schema schema = mockSchema();
        final OracleSchemaInfo schemaInfo = new OracleSchemaInfo(schema, new Trimmer(), MAX_NAME_LENGTH);

        String actual = schemaInfo.getClogTableName(TABLE_NAME_EMP);
        String expected = OracleSchemaInfo.CLOG_PREFIX + TABLE_NAME_EMP;
        assertThat(actual, equalTo(expected));

        // Check cache
        actual = schemaInfo.getClogTableName(TABLE_NAME_EMP);
        expected = OracleSchemaInfo.CLOG_PREFIX + TABLE_NAME_EMP;
        assertThat(actual, equalTo(expected));
    }

    /**
     * Test method for
     * {@link com.persinity.ndt.dbagent.relational.oracle.OracleSchemaInfo#getClogTriggerName(java.lang.String)}.
     */
    @Test
    public void testGetClogTriggerName() {
        final Schema schema = mockSchema();
        final OracleSchemaInfo schemaInfo = new OracleSchemaInfo(schema, new Trimmer(), MAX_NAME_LENGTH + 10);

        String actual = schemaInfo.getClogTriggerName(TABLE_NAME_EMP);
        String expected = OracleSchemaInfo.TRG_CLOG_PREFIX + TABLE_NAME_EMP;
        assertThat(actual, equalTo(expected));

        // Check cache
        actual = schemaInfo.getClogTriggerName(TABLE_NAME_EMP);
        expected = OracleSchemaInfo.TRG_CLOG_PREFIX + TABLE_NAME_EMP;
        assertThat(actual, equalTo(expected));

    }

    /**
     * Test method for
     * {@link com.persinity.ndt.dbagent.relational.oracle.OracleSchemaInfo#getTableCols(java.lang.String)}.
     */
    @Test
    public void testGetTableCols() {
        final Schema schema = mockSchema();
        final OracleSchemaInfo schemaInfo = new OracleSchemaInfo(schema, new Trimmer(), MAX_NAME_LENGTH);

        Set<Col> actual = schemaInfo.getTableCols(TABLE_NAME_EMP);
        assertThat(actual, equalTo(EXPECTED_TABLE_COLS));

        // Check cache
        actual = schemaInfo.getTableCols(TABLE_NAME_EMP);
        assertThat(actual, equalTo(EXPECTED_TABLE_COLS));
    }

    /**
     * Test method for
     * {@link com.persinity.ndt.dbagent.relational.oracle.OracleSchemaInfo#getTablePk(java.lang.String)}.
     */
    @Test
    public void testGetTablePk() {
        final Schema schema = mockSchema();
        final OracleSchemaInfo schemaInfo = new OracleSchemaInfo(schema, new Trimmer(), MAX_NAME_LENGTH);

        PK actual = schemaInfo.getTablePk(TABLE_NAME_EMP);
        assertThat(actual, equalTo(EXPECTED_TABLE_PK));

        // Check cache
        actual = schemaInfo.getTablePk(TABLE_NAME_EMP);
        assertThat(actual, equalTo(EXPECTED_TABLE_PK));
    }

    /**
     * Test method for
     * {@link com.persinity.ndt.dbagent.relational.oracle.OracleSchemaInfo#getClogTableCols(java.lang.String)}.
     */
    @Test
    public void testGetClogTableCols() {
        final Schema schema = mockSchema();
        final OracleSchemaInfo schemaInfo = new OracleSchemaInfo(schema, new Trimmer(), MAX_NAME_LENGTH);

        Set<Col> actual = schemaInfo.getClogTableCols(TABLE_NAME_EMP);
        assertThat(actual, equalTo(EXPECTED_CLOG_TABLE_COLS));

        // Check cache
        actual = schemaInfo.getClogTableCols(TABLE_NAME_EMP);
        assertThat(actual, equalTo(EXPECTED_CLOG_TABLE_COLS));
    }

    /**
     * Test method for
     * {@link com.persinity.ndt.dbagent.relational.oracle.OracleSchemaInfo#getTrlogTriggerName(java.lang.String)}.
     */
    @Test
    public void testGetTrlogTriggerName() {
        final Schema schema = mockSchema();
        final OracleSchemaInfo schemaInfo = new OracleSchemaInfo(schema, new Trimmer(), MAX_NAME_LENGTH + 10);

        final String actual = schemaInfo.getTrlogTriggerName(TABLE_NAME_EMP);
        assertThat(actual, equalTo(OracleSchemaInfo.TRG_TRLOG_PREFIX + TABLE_NAME_EMP));
    }

    /**
     * Test method for
     * {@link OracleSchemaInfo#toString()}
     */
    @Test
    public void testToString() {
        final Schema schema = mockSchema();
        final OracleSchemaInfo schemaInfo = new OracleSchemaInfo(schema, new Trimmer(), MAX_NAME_LENGTH);
        assertThat(schemaInfo.toString(),
                is(format("OracleSchemaInfo@{}(EasyMock for interface com.persinity.common.db.metainfo.Schema)",
                        Integer.toHexString(schemaInfo.hashCode()))));
    }

    private Schema mockSchema() {
        final Schema schema = EasyMock.createNiceMock(Schema.class);
        EasyMock.expect(schema.getTableNames()).andStubReturn(EXPECTED_TABLE_NAMES);
        EasyMock.expect(schema.getTableCols(EasyMock.anyString())).andStubReturn(EXPECTED_TABLE_COLS);
        EasyMock.expect(schema.getTablePk(TABLE_NAME_EMP)).andStubReturn(EXPECTED_TABLE_PK);
        EasyMock.replay(schema);
        return schema;
    }

    private static final int MAX_NAME_LENGTH = 11;
    private static final String TABLE_NAME_EMP = "emp";
    private static final Set<String> EXPECTED_TABLE_NAMES = new HashSet<>(
            Arrays.asList(new String[] { TABLE_NAME_EMP }));
    private static final Set<Col> EXPECTED_TABLE_COLS = new HashSet<>();
    private static final Set<Col> EXPECTED_CLOG_TABLE_COLS = new HashSet<>();
    private static final PK EXPECTED_TABLE_PK;

    static {
        final Col colId = new Col("id", "NUMBER(9, 0)", false);
        final Col colName = new Col("name", "VARCHAR2(20)", true);

        EXPECTED_TABLE_COLS.add(colId);
        EXPECTED_TABLE_COLS.add(colName);

        EXPECTED_CLOG_TABLE_COLS.add(new Col(SchemaInfo.COL_GID, OracleSchemaInfo.TYPE_GID, false));
        EXPECTED_CLOG_TABLE_COLS.add(new Col(SchemaInfo.COL_TID, OracleSchemaInfo.TYPE_TID, false));
        EXPECTED_CLOG_TABLE_COLS.add(new Col(SchemaInfo.COL_CTYPE, "CHAR(1)", false));
        EXPECTED_CLOG_TABLE_COLS.addAll(EXPECTED_TABLE_COLS);

        EXPECTED_TABLE_PK = new PK(TABLE_NAME_EMP, Collections.singleton(colId));
    }
}
