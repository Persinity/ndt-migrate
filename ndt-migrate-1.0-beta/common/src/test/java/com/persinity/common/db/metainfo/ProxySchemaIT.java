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

import static com.persinity.common.StringUtils.format;
import static com.persinity.common.db.metainfo.ProxySchema.newSchema;
import static org.junit.Assert.assertTrue;

import java.util.Properties;

import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.persinity.common.Config;
import com.persinity.common.db.DbConfig;
import com.persinity.common.db.RelDb;
import com.persinity.common.db.SimpleRelDb;
import com.persinity.common.db.SqlStrategy;

/**
 * @author dyordanov
 */
public class ProxySchemaIT {

    @BeforeClass
    public static void setUpClass() throws Exception {
        final String id = "testapp.";
        final Properties props = Config.loadPropsFrom("ndt-integrationtest.properties");
        final DbConfig dbConfig = new DbConfig(props, "ndt-integrationtest.properties", id);
        db = new SimpleRelDb(dbConfig);
        // Create test bed schemas
        db.executeScript("testapp-init.sql");

        sqlStrategy = db.getSqlStrategy();
    }

    @AfterClass
    public static void tearDownClass() throws Exception {
        db.close();
    }

    @Before
    public void setUp() throws Exception {
        Schema testee = newSchema(db, sqlStrategy);
        testSchema = new TestSchema(testee);
    }

    @Test
    public void testGetTableCols() throws Exception {
        testSchema.testGetTableCols();
    }

    @Test(expected = IllegalArgumentException.class)
    public void testGetTableColsInvalidInput() throws Exception {
        testSchema.testGetTableColsInvalidInput();
    }

    @Test
    public void testToColsMap() {
        testSchema.testToColsMap();
    }

    @Test
    public void testGetTableFks() throws Exception {
        testSchema.testGetTableFks(TestSchema.PK_DEPT);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testGetTableFksInvalidInput() throws Exception {
        testSchema.testGetTableFksInvalidInput();
    }

    @Test
    public void testGetTableNames() throws Exception {
        testSchema.testGetTableNames();
    }

    @Test
    public void testGetTablePk() throws Exception {
        testSchema.testGetTablePk();
    }

    @Test(expected = IllegalArgumentException.class)
    public void testGetTablePkInvalidInput() throws Exception {
        testSchema.testGetTablePkInvalidInput();
    }

    @Test
    public void testGetTablePk_NoConstraints() {
        testSchema.testGetTablePk_NoConstraints();
    }

    @Test
    public void testGetTableFks_NoConstraints() {
        testSchema.testGetTableFks_NoConstraints();
    }

    @Test
    public void schemaPerfComparison() {
        final long jdbcSchemaTime = compositeTest(new JdbcSchema(((SimpleRelDb) db).getConnection()), REPEAT);
        final long oracleSchemaTime = compositeTest(newSchema(db, sqlStrategy), REPEAT);
        System.out.println(format("Average Times: {} {} ms. , {} {} ms.", JdbcSchema.class.getSimpleName(),
                jdbcSchemaTime / REPEAT, ProxySchema.class.getSimpleName(), oracleSchemaTime / REPEAT));
        assertTrue(oracleSchemaTime < jdbcSchemaTime);
    }

    @Test
    public void testGetTableName() throws Exception {
        testSchema.testGetTableName();
    }

    @Test(expected = IllegalArgumentException.class)
    public void testGetTableNameInvalidInputEmpty() throws Exception {
        testSchema.testGetTableNameInvalidInputEmpty();
    }

    @Test(expected = NullPointerException.class)
    public void testGetTableNameInvalidInputNull() throws Exception {
        testSchema.testGetTableNameInvalidInputNull();
    }

    @Test
    public void testGetUserName() {
        testSchema.testGetUserName(db.getUserName());
    }

    private long compositeTest(final Schema schema, int repeat) {
        long startTime = System.currentTimeMillis();
        for (int i = 0; i < repeat; i++) {
            for (String table : schema.getTableNames()) {
                schema.getTableCols(table);
                schema.getTablePk(table);
                schema.getTableFks(table);
            }
        }
        return System.currentTimeMillis() - startTime;
    }

    public static final int REPEAT = 3;
    private static RelDb db;
    private static SqlStrategy sqlStrategy;
    private TestSchema testSchema;
}