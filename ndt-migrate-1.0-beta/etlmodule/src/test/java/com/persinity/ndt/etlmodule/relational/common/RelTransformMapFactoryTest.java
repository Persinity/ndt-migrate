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
package com.persinity.ndt.etlmodule.relational.common;

import static com.persinity.ndt.dbagent.relational.AgentSqlStrategy.CTYPES_DEL;
import static com.persinity.ndt.dbagent.relational.AgentSqlStrategy.CTYPES_INS_UPD;
import static com.persinity.ndt.dbagent.relational.AgentSqlStrategy.CTYPES_INS_UPD_DEL;
import static org.easymock.EasyMock.expect;
import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.hasItem;
import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.easymock.EasyMockSupport;
import org.junit.Before;
import org.junit.Test;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.persinity.common.collection.DirectedEdge;
import com.persinity.common.db.metainfo.Col;
import com.persinity.common.db.metainfo.constraint.PK;
import com.persinity.ndt.dbagent.relational.AgentSqlStrategy;
import com.persinity.ndt.dbagent.relational.SchemaInfo;
import com.persinity.ndt.dbdiff.TransformEntity;
import com.persinity.ndt.etlmodule.relational.TransformInfo;
import com.persinity.ndt.etlmodule.relational.migrate.ClogCoalesceTupleFunc;
import com.persinity.ndt.transform.ParamDmlLoadFunc;
import com.persinity.ndt.transform.ParamQryFunc;
import com.persinity.ndt.transform.RelLoadFuncComposition;
import com.persinity.ndt.transform.RepeaterTupleFunc;

/**
 * @author Ivan Dachev
 */
public class RelTransformMapFactoryTest extends EasyMockSupport {
    @Before
    public void setUp() {
        final SchemaInfo srcSchemaInfo = createMock(SchemaInfo.class);
        final SchemaInfo dstSchemaInfo = createMock(SchemaInfo.class);
        schemas = new DirectedEdge<>(srcSchemaInfo, dstSchemaInfo);
        migrateSqlStrategy = createMock(AgentSqlStrategy.class);
        transformSqlStrategy = createMock(AgentSqlStrategy.class);

        // source schema
        expect(srcSchemaInfo.getClogTableName("leadingEntity1")).andStubReturn("clog_leadingEntity1");
        final Col leadingCol1 = new Col("leadingCol1");
        final Set<Col> leadingEntity1Cols = Collections.singleton(leadingCol1);
        expect(srcSchemaInfo.getTableCols("leadingEntity1")).andStubReturn(leadingEntity1Cols);
        expect(srcSchemaInfo.getTableCols("clog_leadingEntity1")).andStubReturn(leadingEntity1Cols);

        expect(srcSchemaInfo.getClogTableName("leadingEntity2")).andStubReturn("clog_leadingEntity2");
        final Col leadingCol2 = new Col("leadingCol2");
        final Set<Col> leadingEntity2Cols = Collections.singleton(leadingCol2);
        expect(srcSchemaInfo.getTableCols("leadingEntity2")).andStubReturn(leadingEntity2Cols);
        expect(srcSchemaInfo.getTableCols("clog_leadingEntity2")).andStubReturn(leadingEntity2Cols);

        expect(srcSchemaInfo.getClogTableName("leadingEntityNotExisting")).andStubReturn(null);

        final List<Col> leadingColList1 = Collections.singletonList(leadingCol1);
        final List<Col> tidsList = Collections.singletonList(new Col("tid"));

        expect(srcSchemaInfo.getTablePk("clog_leadingEntity1"))
                .andStubReturn(new PK("clog_leadingEntity1", leadingEntity1Cols));
        expect(srcSchemaInfo.getTablePk("clog_leadingEntity2"))
                .andStubReturn(new PK("clog_leadingEntity2", leadingEntity2Cols));

        // destination schema
        expect(dstSchemaInfo.getClogTableName("leadingEntity1")).andStubReturn("clog_leadingEntity1");
        expect(dstSchemaInfo.getTableCols("clog_leadingEntity1")).andStubReturn(leadingEntity1Cols);

        expect(dstSchemaInfo.getClogTableName("leadingEntity2")).andStubReturn("clog_leadingEntity2");
        expect(dstSchemaInfo.getTableCols("clog_leadingEntity2")).andStubReturn(leadingEntity2Cols);

        final Set<Col> entity1Cols = Collections.singleton(leadingCol1);
        expect(dstSchemaInfo.getTableCols("entity1")).andStubReturn(entity1Cols);

        final Set<Col> entity2Cols = Collections.singleton(leadingCol2);
        expect(dstSchemaInfo.getTableCols("entity2")).andStubReturn(entity2Cols);

        expect(dstSchemaInfo.getTableCols("entity4NoCols")).andStubReturn(Collections.<Col>emptySet());

        // migrate sql strategy
        expect(migrateSqlStrategy
                .clogExtractQuery("clog_leadingEntity1", leadingColList1, leadingColList1, TEST_WINDOW_SIZE,
                        CTYPES_INS_UPD)).andStubReturn("select leadingCol1 from clog_leadingEntity1 filter 8 ins_upd");
        expect(migrateSqlStrategy
                .clogExtractQuery("clog_leadingEntity1", leadingColList1, leadingColList1, TEST_WINDOW_SIZE,
                        CTYPES_DEL)).andStubReturn("select leadingCol1 from clog_leadingEntity1 filter 8 del");
        expect(migrateSqlStrategy
                .clogExtractQuery("clog_leadingEntity1", leadingColList1, leadingColList1, TEST_WINDOW_SIZE,
                        CTYPES_INS_UPD_DEL))
                .andStubReturn("select leadingCol1 from clog_leadingEntity1 filter 8 ins_upd_del");
        expect(migrateSqlStrategy
                .clogExtractQuery("clog_leadingEntity1", leadingColList1, tidsList, TEST_WINDOW_SIZE, CTYPES_INS_UPD))
                .andStubReturn("select leadingCol1 from clog_leadingEntity1 filter tid ins_upd");

        expect(migrateSqlStrategy.updateStatement("entity1", leadingColList1, leadingColList1))
                .andStubReturn("update leadingCol1 into entity1");
        expect(migrateSqlStrategy.insertStatement("entity1", leadingColList1))
                .andStubReturn("insert leadingCol1 into entity1");

        expect(migrateSqlStrategy.insertStatement("clog_leadingEntity1", leadingColList1))
                .andStubReturn("insert leadingCol1 into clog_leadingEntity1");

        expect(migrateSqlStrategy
                .clogExtractQuery("entity1", leadingColList1, tidsList, TEST_WINDOW_SIZE, CTYPES_INS_UPD_DEL))
                .andStubReturn("select leadingCol1 from entity1");
        expect(migrateSqlStrategy.deleteStatement("entity1", leadingColList1))
                .andStubReturn("delete leadingCol1 from entity1");

        final List<Col> leadingColList2 = Collections.singletonList(leadingCol2);
        expect(migrateSqlStrategy
                .clogExtractQuery("clog_leadingEntity2", leadingColList2, leadingColList2, TEST_WINDOW_SIZE,
                        CTYPES_INS_UPD_DEL))
                .andStubReturn("select leadingCol2 from clog_leadingEntity2 filter 8 ins_upd_del");
        expect(migrateSqlStrategy
                .clogExtractQuery("clog_leadingEntity2", leadingColList2, leadingColList2, TEST_WINDOW_SIZE,
                        CTYPES_DEL)).andStubReturn("select leadingCol2 from clog_leadingEntity2 filter 8 del");
        expect(migrateSqlStrategy
                .clogExtractQuery("clog_leadingEntity2", leadingColList2, leadingColList2, TEST_WINDOW_SIZE,
                        CTYPES_INS_UPD)).andStubReturn("select leadingCol2 from clog_leadingEntity2 filter 8 ins_upd");
        expect(migrateSqlStrategy.clogExtractQuery("clog_leadingEntity2", leadingColList2, tidsList, TEST_WINDOW_SIZE,
                CTYPES_INS_UPD_DEL)).andStubReturn("select leadingCol2 from clog_leadingEntity2 filter tid");

        expect(migrateSqlStrategy.updateStatement("entity2", leadingColList2, leadingColList2))
                .andStubReturn("update leadingCol2 into entity2");
        expect(migrateSqlStrategy.insertStatement("entity2", leadingColList2))
                .andStubReturn("insert leadingCol2 into entity2");

        expect(migrateSqlStrategy.insertStatement("clog_leadingEntity2", leadingColList2))
                .andStubReturn("insert leadingCol2 into clog_leadingEntity2");

        expect(migrateSqlStrategy
                .clogExtractQuery("entity2", leadingColList2, tidsList, TEST_WINDOW_SIZE, CTYPES_INS_UPD_DEL))
                .andStubReturn("select leadingCol2 from entity2");
        expect(migrateSqlStrategy.deleteStatement("entity2", leadingColList2))
                .andStubReturn("delete leadingCol2 from entity2");

        // transform sql strategy
        final List<Col> extractColList1 = Lists.newLinkedList(leadingColList1);
        extractColList1.add(new Col(SchemaInfo.COL_CTYPE));
        expect(transformSqlStrategy
                .clogExtractQuery("clog_leadingEntity1", extractColList1, leadingColList1, TEST_WINDOW_SIZE,
                        CTYPES_INS_UPD))
                .andStubReturn("select leadingCol1 from clog_leadingEntity1 filter 8 ins_upd_del");
        expect(transformSqlStrategy
                .clogExtractQuery("clog_leadingEntity1", extractColList1, leadingColList1, TEST_WINDOW_SIZE,
                        CTYPES_DEL)).andStubReturn("select leadingCol1 from clog_leadingEntity1 filter 8 ins_upd_del");
        expect(transformSqlStrategy
                .clogExtractQuery("clog_leadingEntity1", extractColList1, leadingColList1, TEST_WINDOW_SIZE,
                        CTYPES_INS_UPD_DEL))
                .andStubReturn("select leadingCol1 from clog_leadingEntity1 filter 8 ins_upd_del");
        expect(transformSqlStrategy
                .clogExtractQuery("clog_leadingEntity1", extractColList1, tidsList, TEST_WINDOW_SIZE, CTYPES_INS_UPD))
                .andStubReturn("select leadingCol1 from clog_leadingEntity1 filter tid ins_upd_del");

        expect(transformSqlStrategy.updateStatement("entity1", leadingColList1, leadingColList1))
                .andStubReturn("update leadingCol1 into entity1");
        expect(transformSqlStrategy.insertStatement("entity1", leadingColList1))
                .andStubReturn("insert leadingCol1 into entity1");

        expect(transformSqlStrategy.insertStatement("clog_leadingEntity1", leadingColList1))
                .andStubReturn("insert leadingCol1 into clog_leadingEntity1");

        expect(transformSqlStrategy
                .clogExtractQuery("entity1", extractColList1, tidsList, TEST_WINDOW_SIZE, CTYPES_INS_UPD_DEL))
                .andStubReturn("select leadingCol1 from entity1");
        expect(transformSqlStrategy.deleteStatement("entity1", leadingColList1))
                .andStubReturn("delete leadingCol1 from entity1");

        final List<Col> extractColList2 = Lists.newLinkedList(leadingColList2);
        extractColList2.add(new Col(SchemaInfo.COL_CTYPE));
        expect(transformSqlStrategy
                .clogExtractQuery("clog_leadingEntity2", extractColList2, leadingColList2, TEST_WINDOW_SIZE,
                        CTYPES_INS_UPD_DEL))
                .andStubReturn("select leadingCol2 from clog_leadingEntity2 filter 8 ins_upd_del");
        expect(transformSqlStrategy
                .clogExtractQuery("clog_leadingEntity2", extractColList2, leadingColList2, TEST_WINDOW_SIZE,
                        CTYPES_INS_UPD_DEL))
                .andStubReturn("select leadingCol2 from clog_leadingEntity2 filter 8 ins_upd_del");
        expect(transformSqlStrategy
                .clogExtractQuery("clog_leadingEntity2", extractColList2, leadingColList2, TEST_WINDOW_SIZE,
                        CTYPES_INS_UPD))
                .andStubReturn("select leadingCol2 from clog_leadingEntity2 filter 8 ins_upd_del");
        expect(transformSqlStrategy.clogExtractQuery("clog_leadingEntity2", extractColList2, tidsList, TEST_WINDOW_SIZE,
                CTYPES_INS_UPD_DEL)).andStubReturn("select leadingCol2 from clog_leadingEntity2 filter tid");

        expect(transformSqlStrategy.updateStatement("entity2", leadingColList2, leadingColList2))
                .andStubReturn("update leadingCol2 into entity2");
        expect(transformSqlStrategy.insertStatement("entity2", leadingColList2))
                .andStubReturn("insert leadingCol2 into entity2");

        expect(transformSqlStrategy.insertStatement("clog_leadingEntity2", leadingColList2))
                .andStubReturn("insert leadingCol2 into clog_leadingEntity2");

        expect(transformSqlStrategy
                .clogExtractQuery("entity2", extractColList2, tidsList, TEST_WINDOW_SIZE, CTYPES_INS_UPD_DEL))
                .andStubReturn("select leadingCol2 from entity2");
        expect(transformSqlStrategy.deleteStatement("entity2", leadingColList2))
                .andStubReturn("delete leadingCol2 from entity2");

        entity1 = new TransformEntity("entity1", "transform statement 1", "leadingEntity1",
                Sets.newHashSet("leadingCol1"));
        entity2 = new TransformEntity("entity2", "transform statement 2", "leadingEntity2",
                Sets.newHashSet("leadingCol2"));
        twoEntities = Arrays.asList(entity1, entity2);
        oneEntity = Collections.singletonList(entity1);
    }

    /**
     * Test method for {@link RelTransformMapFactory#getMigrateMap()}
     */
    @Test
    public void testGetMigrateMap() {
        replayAll();

        final RelTransformMapFactory relTransformMap = new RelTransformMapFactory(schemas, migrateSqlStrategy,
                twoEntities, TEST_WINDOW_SIZE);
        final Map<String, TransformInfo> loadMap = relTransformMap.getMigrateMap();

        verifyMigrateMap(loadMap, ClogCoalesceTupleFunc.class);
    }

    /**
     * Test method for {@link RelTransformMapFactory#getMigrateMap()}
     */
    @Test
    public void testGetMigrateMapNoCoalesce() {
        replayAll();

        final RelTransformMapFactory relTransformMap = new RelTransformMapFactory(schemas, migrateSqlStrategy,
                twoEntities, TEST_WINDOW_SIZE, TransformInfo.MIGRATE_DONT_COALESCE);
        final Map<String, TransformInfo> loadMap = relTransformMap.getMigrateMap();

        verifyMigrateMap(loadMap, RepeaterTupleFunc.class);
    }

    /**
     * Test method for {@link RelTransformMapFactory#getMigrateNoCoalesceMap()}
     */
    @Test
    public void testGetMigrateNoCoalesceMap() {
        replayAll();

        final RelTransformMapFactory relTransformMap = new RelTransformMapFactory(schemas, migrateSqlStrategy,
                twoEntities, TEST_WINDOW_SIZE);
        final Map<String, TransformInfo> loadMap = relTransformMap.getMigrateNoCoalesceMap();

        verifyAll();

        assertThat(loadMap, notNullValue());
        assertThat(loadMap.size(), is(2));
        assertThat(loadMap.keySet(), hasItem("clog_leadingEntity1"));
        assertThat(loadMap.keySet(), hasItem("clog_leadingEntity2"));

        final TransformInfo info1 = loadMap.get("clog_leadingEntity1");
        assertThat(info1.getEntityMapping().src(), is("clog_leadingEntity1"));
        assertThat(info1.getEntityMapping().dst(), is("clog_leadingEntity1"));
        assertThat(info1.getColumnsMapping().src().iterator().next().getName(), is("leadingCol1"));
        assertThat(info1.getColumnsMapping().dst().iterator().next().getName(), is("leadingCol1"));
        assertThat(((ParamQryFunc) info1.getExtractFunc()).getSql(),
                is("select leadingCol1 from clog_leadingEntity1 filter 8 ins_upd_del"));
        assertThat(
                ((ParamDmlLoadFunc) ((RelLoadFuncComposition) info1.getLoadFunc()).getFb()).getParamDmlFunc().getSql(),
                is("insert leadingCol1 into clog_leadingEntity1"));
        assertThat(info1.getTransformFunc(), instanceOf(RepeaterTupleFunc.class));

        final TransformInfo info2 = loadMap.get("clog_leadingEntity2");
        assertThat(info2.getEntityMapping().src(), is("clog_leadingEntity2"));
        assertThat(info2.getEntityMapping().dst(), is("clog_leadingEntity2"));
        assertThat(info2.getColumnsMapping().src().iterator().next().getName(), is("leadingCol2"));
        assertThat(info2.getColumnsMapping().dst().iterator().next().getName(), is("leadingCol2"));
        assertThat(((ParamQryFunc) info2.getExtractFunc()).getSql(),
                is("select leadingCol2 from clog_leadingEntity2 filter 8 ins_upd_del"));
        assertThat(
                ((ParamDmlLoadFunc) ((RelLoadFuncComposition) info2.getLoadFunc()).getFb()).getParamDmlFunc().getSql(),
                is("insert leadingCol2 into clog_leadingEntity2"));
        assertThat(info2.getTransformFunc(), instanceOf(RepeaterTupleFunc.class));
    }

    /**
     * Test method for {@link RelTransformMapFactory#getMergeMap()}
     */
    @Test
    public void testGetMergeMap() {
        replayAll();

        final RelTransformMapFactory relTransformMapFactory = new RelTransformMapFactory(schemas, transformSqlStrategy,
                twoEntities, TEST_WINDOW_SIZE);
        final Map<String, TransformInfo> mergeMap = relTransformMapFactory.getMergeMap();

        verifyAll();

        assertThat(mergeMap, notNullValue());
        assertThat(mergeMap.size(), is(2));
        assertThat(mergeMap.keySet(), hasItem("entity1"));
        assertThat(mergeMap.keySet(), hasItem("entity2"));

        final TransformInfo info1 = mergeMap.get("entity1");
        assertThat(info1.getEntityMapping().src(), is("clog_leadingEntity1"));
        assertThat(info1.getEntityMapping().dst(), is("entity1"));
        assertThat(info1.getColumnsMapping().src().iterator().next().getName(), is("leadingCol1"));
        assertThat(info1.getColumnsMapping().dst().iterator().next().getName(), is("leadingCol1"));
        assertThat(((ParamQryFunc) info1.getExtractFunc()).getSql(),
                is("select leadingCol1 from clog_leadingEntity1 filter 8 ins_upd_del"));
        assertThat(
                ((ParamDmlLoadFunc) ((RelLoadFuncComposition) info1.getLoadFunc()).getFb()).getParamDmlFunc().getSql(),
                is("insert leadingCol1 into entity1 ON FAILURE update leadingCol1 into entity1"));

        final TransformInfo info2 = mergeMap.get("entity2");
        assertThat(info2.getEntityMapping().src(), is("clog_leadingEntity2"));
        assertThat(info2.getEntityMapping().dst(), is("entity2"));
        assertThat(info2.getColumnsMapping().src().iterator().next().getName(), is("leadingCol2"));
        assertThat(info2.getColumnsMapping().dst().iterator().next().getName(), is("leadingCol2"));
        assertThat(((ParamQryFunc) info2.getExtractFunc()).getSql(),
                is("select leadingCol2 from clog_leadingEntity2 filter 8 ins_upd_del"));
        assertThat(
                ((ParamDmlLoadFunc) ((RelLoadFuncComposition) info2.getLoadFunc()).getFb()).getParamDmlFunc().getSql(),
                is("insert leadingCol2 into entity2 ON FAILURE update leadingCol2 into entity2"));
    }

    /**
     * Test method for {@link RelTransformMapFactory#getDeleteMap()}
     */
    @Test
    public void testGetDeleteMap() {
        replayAll();

        final RelTransformMapFactory relTransformMapFactory = new RelTransformMapFactory(schemas, transformSqlStrategy,
                twoEntities, TEST_WINDOW_SIZE);
        final Map<String, TransformInfo> deleteMap = relTransformMapFactory.getDeleteMap();

        verifyAll();

        assertThat(deleteMap, notNullValue());
        assertThat(deleteMap.size(), is(2));
        assertThat(deleteMap.keySet(), hasItem("entity1"));
        assertThat(deleteMap.keySet(), hasItem("entity2"));

        final TransformInfo info1 = deleteMap.get("entity1");
        assertThat(info1.getEntityMapping().src(), is("clog_leadingEntity1"));
        assertThat(info1.getEntityMapping().dst(), is("entity1"));
        assertThat(info1.getColumnsMapping().src().iterator().next().getName(), is("leadingCol1"));
        assertThat(info1.getColumnsMapping().dst().iterator().next().getName(), is("leadingCol1"));
        assertThat(((ParamQryFunc) info1.getExtractFunc()).getSql(),
                is("select leadingCol1 from clog_leadingEntity1 filter 8 ins_upd_del"));
        assertThat(
                ((ParamDmlLoadFunc) ((RelLoadFuncComposition) info1.getLoadFunc()).getFb()).getParamDmlFunc().getSql(),
                is("delete leadingCol1 from entity1"));
        assertThat(
                ((ParamDmlLoadFunc) ((RelLoadFuncComposition) info1.getLoadFunc()).getFb()).getParamDmlFunc().getCols(),
                is((List<Col>) Lists.newArrayList(new Col("leadingCol1"))));

        final TransformInfo info2 = deleteMap.get("entity2");
        assertThat(info2.getEntityMapping().src(), is("clog_leadingEntity2"));
        assertThat(info2.getEntityMapping().dst(), is("entity2"));
        assertThat(info2.getColumnsMapping().src().iterator().next().getName(), is("leadingCol2"));
        assertThat(info2.getColumnsMapping().dst().iterator().next().getName(), is("leadingCol2"));
        assertThat(((ParamQryFunc) info2.getExtractFunc()).getSql(),
                is("select leadingCol2 from clog_leadingEntity2 filter 8 ins_upd_del"));
        assertThat(
                ((ParamDmlLoadFunc) ((RelLoadFuncComposition) info2.getLoadFunc()).getFb()).getParamDmlFunc().getSql(),
                is("delete leadingCol2 from entity2"));
        assertThat(
                ((ParamDmlLoadFunc) ((RelLoadFuncComposition) info2.getLoadFunc()).getFb()).getParamDmlFunc().getCols(),
                is((List<Col>) Lists.newArrayList(new Col("leadingCol2"))));
    }

    /**
     * Test method for {@link RelTransformMapFactory#getMigrateMap()}
     */
    @Test
    public void testGetMigrateMap_SingleEntity() {
        replayAll();

        final RelTransformMapFactory relTransformMap = new RelTransformMapFactory(schemas, migrateSqlStrategy,
                oneEntity, TEST_WINDOW_SIZE);
        final Map<String, TransformInfo> loadMap = relTransformMap.getMigrateMap();

        verifyAll();

        assertThat(loadMap, notNullValue());
        assertThat(loadMap.size(), is(1));
        assertThat(loadMap.keySet(), hasItem("clog_leadingEntity1"));

        final TransformInfo info1 = loadMap.get("clog_leadingEntity1");
        assertThat(info1.getEntityMapping().src(), is("clog_leadingEntity1"));
        assertThat(info1.getEntityMapping().dst(), is("clog_leadingEntity1"));
        assertThat(info1.getColumnsMapping().src().iterator().next().getName(), is("leadingCol1"));
        assertThat(info1.getColumnsMapping().dst().iterator().next().getName(), is("leadingCol1"));
        assertThat(((ParamQryFunc) info1.getExtractFunc()).getSql(),
                is("select leadingCol1 from clog_leadingEntity1 filter 8 ins_upd_del"));
        assertThat(
                ((ParamDmlLoadFunc) ((RelLoadFuncComposition) info1.getLoadFunc()).getFb()).getParamDmlFunc().getSql(),
                is("insert leadingCol1 into clog_leadingEntity1"));
    }

    /**
     * Test method for {@link RelTransformMapFactory#getMergeMap()}
     */
    @Test
    public void testGetMergeMap_SingleEntity() {
        replayAll();

        final RelTransformMapFactory relTransformMap = new RelTransformMapFactory(schemas, transformSqlStrategy,
                oneEntity, TEST_WINDOW_SIZE);
        final Map<String, TransformInfo> transformMap = relTransformMap.getMergeMap();

        verifyAll();

        assertThat(transformMap, notNullValue());
        assertThat(transformMap.size(), is(1));
        assertThat(transformMap.keySet(), hasItem("entity1"));

        final TransformInfo info1 = transformMap.get("entity1");
        assertThat(info1.getEntityMapping().src(), is("clog_leadingEntity1"));
        assertThat(info1.getEntityMapping().dst(), is("entity1"));
        assertThat(info1.getColumnsMapping().src().iterator().next().getName(), is("leadingCol1"));
        assertThat(info1.getColumnsMapping().dst().iterator().next().getName(), is("leadingCol1"));
        assertThat(((ParamQryFunc) info1.getExtractFunc()).getSql(),
                is("select leadingCol1 from clog_leadingEntity1 filter 8 ins_upd_del"));
        assertThat(
                ((ParamDmlLoadFunc) ((RelLoadFuncComposition) info1.getLoadFunc()).getFb()).getParamDmlFunc().getSql(),
                is("insert leadingCol1 into entity1 ON FAILURE update leadingCol1 into entity1"));
    }

    /**
     * Test method for {@link RelTransformMapFactory#getDeleteMap()}
     */
    @Test
    public void testGetDeleteMap_SingleEntity() {
        replayAll();

        final RelTransformMapFactory relTransformMap = new RelTransformMapFactory(schemas, transformSqlStrategy,
                oneEntity, TEST_WINDOW_SIZE);
        final Map<String, TransformInfo> transformMap = relTransformMap.getDeleteMap();

        verifyAll();

        assertThat(transformMap, notNullValue());
        assertThat(transformMap.size(), is(1));
        assertThat(transformMap.keySet(), hasItem("entity1"));

        final TransformInfo info1 = transformMap.get("entity1");
        assertThat(info1.getEntityMapping().src(), is("clog_leadingEntity1"));
        assertThat(info1.getEntityMapping().dst(), is("entity1"));
        assertThat(info1.getColumnsMapping().src().iterator().next().getName(), is("leadingCol1"));
        assertThat(info1.getColumnsMapping().dst().iterator().next().getName(), is("leadingCol1"));
        assertThat(((ParamQryFunc) info1.getExtractFunc()).getSql(),
                is("select leadingCol1 from clog_leadingEntity1 filter 8 ins_upd_del"));
        assertThat(
                ((ParamDmlLoadFunc) ((RelLoadFuncComposition) info1.getLoadFunc()).getFb()).getParamDmlFunc().getSql(),
                is("delete leadingCol1 from entity1"));
        assertThat(
                ((ParamDmlLoadFunc) ((RelLoadFuncComposition) info1.getLoadFunc()).getFb()).getParamDmlFunc().getCols(),
                is((List<Col>) Lists.newArrayList(new Col("leadingCol1"))));
    }

    /**
     * Test method for {@link RelTransformMapFactory#getMigrateMap()}
     */
    @Test
    public void testGetMigrateMap_NoEntities() {
        final Collection<TransformEntity> entities = Collections.emptyList();

        replayAll();

        final RelTransformMapFactory relTransformMap = new RelTransformMapFactory(schemas, migrateSqlStrategy, entities,
                TEST_WINDOW_SIZE);
        final Map<String, TransformInfo> transformMap = relTransformMap.getMigrateMap();

        verifyAll();

        assertThat(transformMap, notNullValue());
        assertThat(transformMap.size(), is(0));
    }

    /**
     * Test method for {@link RelTransformMapFactory#getMergeMap()}
     */
    @Test
    public void testGetMergeMap_NoEntities() {
        final Collection<TransformEntity> entities = Collections.emptyList();

        replayAll();

        final RelTransformMapFactory relTransformMap = new RelTransformMapFactory(schemas, transformSqlStrategy,
                entities, TEST_WINDOW_SIZE);
        final Map<String, TransformInfo> transformMap = relTransformMap.getMergeMap();

        verifyAll();

        assertThat(transformMap, notNullValue());
        assertThat(transformMap.size(), is(0));
    }

    /**
     * Test method for {@link RelTransformMapFactory#getDeleteMap()}
     */
    @Test
    public void testGetDeleteMap_NoEntities() {
        final Collection<TransformEntity> entities = Collections.emptyList();

        replayAll();

        final RelTransformMapFactory relTransformMap = new RelTransformMapFactory(schemas, transformSqlStrategy,
                entities, TEST_WINDOW_SIZE);
        final Map<String, TransformInfo> transformMap = relTransformMap.getDeleteMap();

        verifyAll();

        assertThat(transformMap, notNullValue());
        assertThat(transformMap.size(), is(0));
    }

    /**
     * Test method for {@link RelTransformMapFactory#getMergeMap()}
     */
    @Test(expected = IllegalArgumentException.class)
    public void testGetMergeMap_NoTargetEntityCol() {
        final TransformEntity entity1 = new TransformEntity("entity4NoCols", "transform statement 1", "leadingEntity1",
                Sets.newHashSet("leadingCol1"));
        final Collection<TransformEntity> entities = Collections.singletonList(entity1);

        replayAll();

        final RelTransformMapFactory relTransformMap = new RelTransformMapFactory(schemas, transformSqlStrategy,
                entities, TEST_WINDOW_SIZE);
        try {
            relTransformMap.getMergeMap();
        } catch (final IllegalArgumentException e) {
            assertThat(e.getMessage(), containsString("Failed to find leading column"));
            throw e;
        } finally {
            verifyAll();
        }
    }

    /**
     * Test method for {@link RelTransformMapFactory#getMergeMap()}
     */
    @Test(expected = IllegalArgumentException.class)
    public void testGetMergeMap_NoLeadingEntity() {
        final TransformEntity entity1 = new TransformEntity("entity1", "transform statement 1",
                "leadingEntityNotExisting", Sets.newHashSet("leadingCol1"));
        final Collection<TransformEntity> entities = Collections.singletonList(entity1);

        replayAll();

        final RelTransformMapFactory relTransformMap = new RelTransformMapFactory(schemas, transformSqlStrategy,
                entities, TEST_WINDOW_SIZE);
        try {
            relTransformMap.getMergeMap();
        } catch (final IllegalArgumentException e) {
            assertThat(e.getMessage(), containsString("Failed to find dst clog table name for"));
            throw e;
        } finally {
            verifyAll();
        }
    }

    /**
     * Test method for {@link RelTransformMapFactory#getMergeMap()}
     */
    @Test(expected = IllegalArgumentException.class)
    public void testMergeMap_NoLeadingCol() {
        final TransformEntity entity1 = new TransformEntity("entity1", "transform statement 1", "leadingEntity1",
                Sets.newHashSet("leadingCol1NotExisting"));
        final Collection<TransformEntity> entities = Collections.singletonList(entity1);

        replayAll();

        final RelTransformMapFactory relTransformMap = new RelTransformMapFactory(schemas, transformSqlStrategy,
                entities, TEST_WINDOW_SIZE);
        try {
            relTransformMap.getMergeMap();
        } catch (final IllegalArgumentException e) {
            assertThat(e.getMessage(), containsString("Failed to find leading column"));
            throw e;
        } finally {
            verifyAll();
        }
    }

    private void verifyMigrateMap(final Map<String, TransformInfo> loadMap, final Class<?> patternClass) {
        verifyAll();

        assertThat(loadMap, notNullValue());
        assertThat(loadMap.size(), is(2));
        assertThat(loadMap.keySet(), hasItem("clog_leadingEntity1"));
        assertThat(loadMap.keySet(), hasItem("clog_leadingEntity2"));

        final TransformInfo info1 = loadMap.get("clog_leadingEntity1");
        assertThat(info1.getEntityMapping().src(), is("clog_leadingEntity1"));
        assertThat(info1.getEntityMapping().dst(), is("clog_leadingEntity1"));
        assertThat(info1.getColumnsMapping().src().iterator().next().getName(), is("leadingCol1"));
        assertThat(info1.getColumnsMapping().dst().iterator().next().getName(), is("leadingCol1"));
        assertThat(((ParamQryFunc) info1.getExtractFunc()).getSql(),
                is("select leadingCol1 from clog_leadingEntity1 filter 8 ins_upd_del"));
        assertThat(
                ((ParamDmlLoadFunc) ((RelLoadFuncComposition) info1.getLoadFunc()).getFb()).getParamDmlFunc().getSql(),
                is("insert leadingCol1 into clog_leadingEntity1"));
        assertThat(info1.getTransformFunc(), instanceOf(patternClass));

        final TransformInfo info2 = loadMap.get("clog_leadingEntity2");
        assertThat(info2.getEntityMapping().src(), is("clog_leadingEntity2"));
        assertThat(info2.getEntityMapping().dst(), is("clog_leadingEntity2"));
        assertThat(info2.getColumnsMapping().src().iterator().next().getName(), is("leadingCol2"));
        assertThat(info2.getColumnsMapping().dst().iterator().next().getName(), is("leadingCol2"));
        assertThat(((ParamQryFunc) info2.getExtractFunc()).getSql(),
                is("select leadingCol2 from clog_leadingEntity2 filter 8 ins_upd_del"));
        assertThat(
                ((ParamDmlLoadFunc) ((RelLoadFuncComposition) info2.getLoadFunc()).getFb()).getParamDmlFunc().getSql(),
                is("insert leadingCol2 into clog_leadingEntity2"));
        assertThat(info2.getTransformFunc(), instanceOf(patternClass));
    }

    private static final int TEST_WINDOW_SIZE = 8;

    private DirectedEdge<SchemaInfo, SchemaInfo> schemas;
    private AgentSqlStrategy migrateSqlStrategy;
    private TransformEntity entity1;
    private TransformEntity entity2;
    private List<TransformEntity> twoEntities;
    private List<TransformEntity> oneEntity;
    private AgentSqlStrategy transformSqlStrategy;
}