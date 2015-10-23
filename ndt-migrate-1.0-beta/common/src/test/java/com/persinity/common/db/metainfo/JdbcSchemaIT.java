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

import java.util.Properties;

import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.persinity.common.Config;
import com.persinity.common.db.DbConfig;
import com.persinity.common.db.RelDb;
import com.persinity.common.db.SimpleRelDb;

/**
 * @author dyordanov
 */
public class JdbcSchemaIT {

    @BeforeClass
    public static void setUpClass() throws Exception {
        final String id = "testapp.";
        final Properties props = Config.loadPropsFrom("ndt-integrationtest.properties");
        final DbConfig dbConfig = new DbConfig(props, "ndt-integrationtest.properties", id);
        db = new SimpleRelDb(dbConfig);
        // Create test bed schemas
        db.executeScript("testapp-init.sql");
    }

    @AfterClass
    public static void tearDownClass() throws Exception {
        db.close();
    }

    @Before
    public void setUp() throws Exception {
        Schema testee = new JdbcSchema(((SimpleRelDb) db).getConnection());
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
        testSchema.testGetTableFks(TestSchema.UQ_DEPT);
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

    private static RelDb db;
    private TestSchema testSchema;
}