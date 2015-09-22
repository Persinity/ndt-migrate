/**
 * Copyright (c) 2015 Persinity Inc.
 */
package com.persinity.ndt.dbdiff.rel;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThat;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Set;

import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.skyscreamer.jsonassert.JSONAssert;

import com.persinity.ndt.dbdiff.TransformEntity;

/**
 * @author Ivan Dachev
 */
public class JsonTransformEntityStoreTest {
    @BeforeClass
    public static void setUpClass() throws IOException {
        tempFolder = new TemporaryFolder();
        tempFolder.create();
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
    public void test_MultipleTransformations() throws Exception {
        Path path = getResourcePath("transform_entity_source_multiple.json");
        JsonTransformEntityStore source = new JsonTransformEntityStore(path.toString());
        Collection<TransformEntity> entities = source.loadTransformEntities();

        assertThat(entities, notNullValue());
        assertThat(entities.size(), is(2));

        Iterator<TransformEntity> iter = entities.iterator();
        TransformEntity entity;

        entity = iter.next();
        final Set<String> expectedCols1 = new LinkedHashSet<>();
        expectedCols1.add("col1");
        expectedCols1.add("col2");
        assertThat(entity.getTargetEntity(), is("entity1"));
        assertThat(entity.getSourceLeadingColumns(), is(expectedCols1));
        assertThat(entity.getTransformStatement(), is("load sql 1"));
        assertThat(entity.getSourceLeadingEntity(), is("sourceLeadingEntity1"));

        entity = iter.next();
        final Set<String> expectedCols2 = new LinkedHashSet<>();
        expectedCols2.add("col3");
        expectedCols2.add("col4");
        assertThat(entity.getTargetEntity(), is("entity2"));
        assertThat(entity.getSourceLeadingColumns(), is(expectedCols2));
        assertThat(entity.getTransformStatement(), is("load sql 2"));
        assertThat(entity.getSourceLeadingEntity(), is("sourceLeadingEntity2"));

        assertFalse(iter.hasNext());

        final JsonTransformEntityStore writer = new JsonTransformEntityStore(tempFile.getAbsolutePath());
        writer.saveTransformEntities(entities);
        String rData = new String(Files.readAllBytes(path));
        String wData = new String(Files.readAllBytes(tempFile.getAbsoluteFile().toPath()));
        JSONAssert.assertEquals(rData, wData, true);
    }

    @Test
    public void test_SingleTransformation() throws Exception {
        Path path = getResourcePath("transform_entity_source_single.json");
        JsonTransformEntityStore source = new JsonTransformEntityStore(path.toString());
        Collection<TransformEntity> entities = source.loadTransformEntities();

        assertThat(entities, notNullValue());
        assertThat(entities.size(), is(1));

        Iterator<TransformEntity> iter = entities.iterator();
        TransformEntity entity;

        entity = iter.next();
        final Set<String> expectedCols1 = new LinkedHashSet<>();
        expectedCols1.add("col1");
        expectedCols1.add("col2");
        assertThat(entity.getTargetEntity(), is("entity1"));
        assertThat(entity.getSourceLeadingColumns(), is(expectedCols1));
        assertThat(entity.getTransformStatement(), is("load sql 1"));
        assertThat(entity.getSourceLeadingEntity(), is("sourceLeadingEntity1"));

        assertFalse(iter.hasNext());

        final JsonTransformEntityStore writer = new JsonTransformEntityStore(tempFile.getAbsolutePath());
        writer.saveTransformEntities(entities);
        String rData = new String(Files.readAllBytes(path));
        String wData = new String(Files.readAllBytes(tempFile.getAbsoluteFile().toPath()));
        JSONAssert.assertEquals(rData, wData, true);
    }

    @Test
    public void test_EmptyTransformation() throws Exception {
        Path path = getResourcePath("transform_entity_source_empty.json");
        JsonTransformEntityStore source = new JsonTransformEntityStore(path.toString());
        Collection<TransformEntity> entities = source.loadTransformEntities();

        assertThat(entities, notNullValue());
        assertThat(entities.size(), is(0));
        Iterator<TransformEntity> iter = entities.iterator();
        assertFalse(iter.hasNext());

        final JsonTransformEntityStore writer = new JsonTransformEntityStore(tempFile.getAbsolutePath());
        writer.saveTransformEntities(entities);
        String rData = new String(Files.readAllBytes(path));
        String wData = new String(Files.readAllBytes(tempFile.getAbsoluteFile().toPath()));
        JSONAssert.assertEquals(rData, wData, true);
    }

    @Test(expected = RuntimeException.class)
    public void test_InvalidEmptyTargetEntity() throws Exception {
        JsonTransformEntityStore source = new JsonTransformEntityStore(
                getResourcePath("transform_entity_source_invalid_1.json").toString());
        source.loadTransformEntities();
    }

    @Test(expected = RuntimeException.class)
    public void test_InvalidEmptyTransformStatement() throws Exception {
        JsonTransformEntityStore source = new JsonTransformEntityStore(
                getResourcePath("transform_entity_source_invalid_2.json").toString());
        source.loadTransformEntities();
    }

    @Test(expected = RuntimeException.class)
    public void test_InvalidEmptySourceLeadingEntity() throws Exception {
        JsonTransformEntityStore source = new JsonTransformEntityStore(
                getResourcePath("transform_entity_source_invalid_3.json").toString());
        source.loadTransformEntities();
    }

    @Test(expected = RuntimeException.class)
    public void test_InvalidEmptySourceLeadingColumns() throws Exception {
        JsonTransformEntityStore source = new JsonTransformEntityStore(
                getResourcePath("transform_entity_source_invalid_4.json").toString());
        source.loadTransformEntities();
    }

    @Test(expected = RuntimeException.class)
    public void test_InvalidNoSuchFile() throws Exception {
        JsonTransformEntityStore source = new JsonTransformEntityStore(getResourcePath("no_such_file.json").toString());
        source.loadTransformEntities();
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