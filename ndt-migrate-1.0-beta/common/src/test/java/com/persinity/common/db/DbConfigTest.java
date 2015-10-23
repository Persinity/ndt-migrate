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

package com.persinity.common.db;

import java.util.Arrays;
import java.util.Properties;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 * @author dyordanov
 */
public class DbConfigTest {

    @Before
    public void setUp() {
        propsBareStub = new Properties();
        propsBareStub.setProperty(DbConfig.DB_URL_KEY, DB_URL);
        propsBareStub.setProperty(DbConfig.DB_USER_KEY, DB_USER);
        propsBareStub.setProperty(DbConfig.DB_PASS_KEY, DB_PASS);

        propsExtendedStub = new Properties();
        propsExtendedStub.putAll(propsBareStub);
        propsExtendedStub.setProperty(DbConfig.DB_ENABLE_OUTPUT_KEY, DB_ENABLE_OUTPUT);
        propsExtendedStub.setProperty(DbConfig.DB_TABLES_SKIPLIST_KEY, DB_TABLES_SKIP_LIST);
        propsExtendedStub.setProperty(DbConfig.DB_CACHE_SQL_KEY, DB_CACHE_SQL);
        propsExtendedStub.setProperty(DbConfig.DB_SQL_STRATEGY_KEY, DB_SQL_STRATEGY);
    }

    @Test(expected = NullPointerException.class)
    public void testDbConfig_InvalidInput() {
        new DbConfig(propsBareStub, PROPS_BARE_STUB, null);
    }

    @Test
    public void testGetDbUrl() throws Exception {
        final DbConfig testee = new DbConfig(propsBareStub, PROPS_BARE_STUB, "");
        Assert.assertEquals(DB_URL, testee.getDbUrl());
    }

    @Test
    public void testGetDbUser() throws Exception {
        final DbConfig testee = new DbConfig(propsBareStub, PROPS_BARE_STUB, "");
        Assert.assertEquals(DB_USER, testee.getDbUser());
    }

    @Test
    public void testGetDbPass() throws Exception {
        final DbConfig testee = new DbConfig(propsBareStub, PROPS_BARE_STUB, "");
        Assert.assertEquals(DB_PASS, testee.getDbPass());
    }

    @Test
    public void testGetDbEnableOutput() throws Exception {
        final DbConfig testeeBare = new DbConfig(propsBareStub, PROPS_BARE_STUB, "");
        Assert.assertEquals(false, testeeBare.getDbEnableOutput());
        final DbConfig testeeExtended = new DbConfig(propsExtendedStub, PROPS_EXTENDED_STUB, "");
        Assert.assertEquals(true, testeeExtended.getDbEnableOutput());
    }

    @Test
    public void testGetCacheSql() throws Exception {
        final DbConfig testeeBare = new DbConfig(propsBareStub, PROPS_BARE_STUB, "");
        Assert.assertEquals(false, testeeBare.getCacheSql());
        final DbConfig testeeExtended = new DbConfig(propsExtendedStub, PROPS_EXTENDED_STUB, "");
        Assert.assertEquals(true, testeeExtended.getCacheSql());
    }

    @Test
    public void testGetSkipTables() throws Exception {
        final DbConfig testeeBare = new DbConfig(propsBareStub, PROPS_BARE_STUB, "");
        Assert.assertEquals(0, testeeBare.getSkipTables().size());
        final DbConfig testeeExtended = new DbConfig(propsExtendedStub, PROPS_EXTENDED_STUB, "");
        Assert.assertEquals(Arrays.asList("tab1", "tab2"), testeeExtended.getSkipTables());
    }

    @Test
    public void testGetSqlStrategy() throws Exception {
        final DbConfig testeeBare = new DbConfig(propsBareStub, PROPS_BARE_STUB, "");
        final String defaultSqlStrategy = OracleSqlStrategy.class.getCanonicalName();
        Assert.assertEquals(defaultSqlStrategy, testeeBare.getSqlStrategy());
        final DbConfig testeeExtended = new DbConfig(propsExtendedStub, PROPS_EXTENDED_STUB, "");
        Assert.assertEquals(DB_SQL_STRATEGY, testeeExtended.getSqlStrategy());
    }

    private static final String DB_URL = "url";
    private static final String DB_USER = "user";
    private static final String DB_PASS = "pass";
    private static final String DB_ENABLE_OUTPUT = "true";
    private static final String DB_TABLES_SKIP_LIST = "tab1, tab2";
    private static final String PROPS_BARE_STUB = "Bare props";
    private static final String PROPS_EXTENDED_STUB = "Extended props";
    private static final String DB_CACHE_SQL = "true";
    private static final String DB_SQL_STRATEGY = "com.persinity.dbagent.MsSqlTestStrategy";

    private Properties propsBareStub;
    private Properties propsExtendedStub;

}