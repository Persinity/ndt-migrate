/**
 * Copyright (c) 2015 Persinity Inc.
 */
package com.persinity.ndt.dbdiff;

import static com.persinity.common.Config.loadPropsFrom;
import static com.persinity.common.db.DbConfig.NO_KEY_PREFIX;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Set;

import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.skyscreamer.jsonassert.JSONAssert;

import com.persinity.common.db.DbConfig;
import com.persinity.common.db.RelDb;
import com.persinity.common.db.SimpleRelDb;
import com.persinity.common.db.metainfo.Schema;
import com.persinity.ndt.dbagent.relational.oracle.OracleAgentSqlStrategy;

/**
 * Before use: Configure your own DB in ndtdst/src.properties and run admin.sql to create the test users.
 *
 * @author Ivan Dachev
 */
public class DbDiffIT {

    @BeforeClass
    public static void setUpClass() throws IOException {
        tempFolder = new TemporaryFolder();
        tempFolder.create();

        initDb(new DbConfig(loadPropsFrom(DbDiff.NDT_SRC_PROPS_FILE), DbDiff.NDT_SRC_PROPS_FILE, NO_KEY_PREFIX));
        initDb(new DbConfig(loadPropsFrom(DbDiff.NDT_DST_PROPS_FILE), DbDiff.NDT_DST_PROPS_FILE, NO_KEY_PREFIX));
    }

    private static void initDb(DbConfig dbConfig) {
        final RelDb db = new SimpleRelDb(dbConfig);
        final Schema schema = db.metaInfo();
        final OracleAgentSqlStrategy sqlStrategy = new OracleAgentSqlStrategy();
        db.executeScript("testapp-init.sql");

        // cleanup DataMutator tables brute force
        final Set<String> tableNames = schema.getTableNames();
        for (int i = 0; i < tableNames.size(); i++) {
            for (final String tableName : tableNames) {
                if (tableName.toLowerCase().startsWith("dm_")) {
                    try {
                        db.executeDmdl(sqlStrategy.dropTable(tableName));
                    } catch (RuntimeException e) {
                        // silent
                    }
                }
            }
        }

        db.commit();
        db.close();
    }

    @Before
    public void setUp() throws IOException {
        tempFile = tempFolder.newFile();
    }

    @AfterClass
    public static void tearDownClass() throws IOException {
        tempFolder.delete();
    }

    @Test
    public void test() throws Exception {
        DbDiff.main(tempFile.getAbsolutePath());
        String wData = new String(Files.readAllBytes(tempFile.toPath()));
        String rData = new String(Files.readAllBytes(getResourcePath("testapp_transformations.json")));
        JSONAssert.assertEquals(rData, wData, true);
    }

    private Path getResourcePath(String resource) throws URISyntaxException {
        URL resourceUrl = getClass().getResource(resource);
        if (resourceUrl == null) {
            resourceUrl = getClass().getResource("/" + resource);
        }
        if (resourceUrl == null) {
            return Paths.get(resource);
        }
        return Paths.get(resourceUrl.toURI());
    }

    private static TemporaryFolder tempFolder;
    private File tempFile;
}