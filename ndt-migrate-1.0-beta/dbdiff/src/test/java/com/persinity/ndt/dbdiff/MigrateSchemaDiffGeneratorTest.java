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
package com.persinity.ndt.dbdiff;

import static org.easymock.EasyMock.expect;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;

import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;

import org.easymock.EasyMockSupport;
import org.junit.Before;
import org.junit.Test;

import com.google.common.collect.Sets;
import com.persinity.common.collection.DirectedEdge;
import com.persinity.common.db.metainfo.Col;
import com.persinity.common.db.metainfo.constraint.PK;
import com.persinity.ndt.dbagent.relational.AgentSqlStrategy;
import com.persinity.ndt.dbagent.relational.SchemaInfo;

/**
 * @author Ivan Dachev
 */
public class MigrateSchemaDiffGeneratorTest extends EasyMockSupport {

    @Before
    public void setUp() throws Exception {
        sqlStrategy = createMock(AgentSqlStrategy.class);
        final SchemaInfo srcSchemaInfo = createMock(SchemaInfo.class);
        final SchemaInfo dstSchemaInfo = createMock(SchemaInfo.class);
        dbSources = new DirectedEdge<>(srcSchemaInfo, dstSchemaInfo);
        testee = new MigrateSchemaDiffGenerator();

        expectsForTable(TAB1, TAB1_PK_NAME, TAB1_SELECT_ALL);
        expectsForTable(TAB2, TAB2_PK_NAME, TAB2_SELECT_ALL);
        expectsForTable(TAB3, TAB3_PK_NAME, TAB3_SELECT_ALL);
    }

    @Test
    public void testGenerateDiff_EmptySchema() throws Exception {
        expect(dbSources.src().getTableNames()).andReturn(Collections.<String>emptySet());
        expect(dbSources.dst().getTableNames()).andReturn(Collections.<String>emptySet());

        replayAll();

        final Collection<TransformEntity> res = testee.generateDiff(dbSources, sqlStrategy);

        verifyAll();

        assertThat(res.size(), is(0));
    }

    @Test
    public void testGenerateDiff_OneTable() throws Exception {
        expect(dbSources.src().getTableNames()).andReturn(Collections.singleton(TAB1));
        expect(dbSources.dst().getTableNames()).andReturn(Collections.singleton(TAB1));

        replayAll();

        final Collection<TransformEntity> res = testee.generateDiff(dbSources, sqlStrategy);

        verifyAll();

        assertThat(res.size(), is(1));

        final Iterator<TransformEntity> iter = res.iterator();

        verifyEntity(iter.next(), TAB1, TAB1_PK_NAME, TAB1_SELECT_ALL);

        assertFalse(iter.hasNext());
    }

    @Test
    public void testGenerateDiff_MoreTables() throws Exception {
        expect(dbSources.src().getTableNames()).andReturn(Sets.newHashSet(TAB1, TAB2, TAB3));
        expect(dbSources.dst().getTableNames()).andReturn(Sets.newHashSet(TAB1, TAB2, TAB3));

        replayAll();

        final Collection<TransformEntity> res = testee.generateDiff(dbSources, sqlStrategy);

        verifyAll();

        assertThat(res.size(), is(3));

        final Iterator<TransformEntity> iter = res.iterator();

        verifyEntity(iter.next(), TAB1, TAB1_PK_NAME, TAB1_SELECT_ALL);
        verifyEntity(iter.next(), TAB2, TAB2_PK_NAME, TAB2_SELECT_ALL);
        verifyEntity(iter.next(), TAB3, TAB3_PK_NAME, TAB3_SELECT_ALL);

        assertFalse(iter.hasNext());
    }

    @Test
    public void testGenerateDiff_MissingSourceTable() throws Exception {
        expect(dbSources.src().getTableNames()).andReturn(Sets.newHashSet(TAB1, TAB2));
        expect(dbSources.dst().getTableNames()).andReturn(Sets.newHashSet(TAB1, TAB2, TAB3));

        replayAll();

        try {
            testee.generateDiff(dbSources, sqlStrategy);
            fail("Should throw exception");
        } catch (RuntimeException e) {
            assertThat(e.getMessage(), is("Could not find destination table: \"tab3\" at source DB"));
        }

        verifyAll();
    }

    @Test
    public void testGenerateDiff_MissingDestinationTable() throws Exception {
        expect(dbSources.src().getTableNames()).andReturn(Sets.newHashSet(TAB1, TAB2, TAB3));
        expect(dbSources.dst().getTableNames()).andReturn(Sets.newHashSet(TAB1, TAB2));

        replayAll();

        try {
            testee.generateDiff(dbSources, sqlStrategy);
            fail("Should throw exception");
        } catch (RuntimeException e) {
            assertThat(e.getMessage(), is("Could not find source table: \"tab3\" at destination DB"));
        }

        verifyAll();
    }

    private void verifyEntity(final TransformEntity entity, final String tab, final String pkName,
            final String selectAll) {
        assertThat(entity.getTargetEntity(), is(tab));
        assertThat(entity.getSourceLeadingEntity(), is(tab));
        assertThat(entity.getSourceLeadingColumns(), is(Collections.singleton(pkName)));
        assertThat(entity.getTransformStatement(), is(selectAll));
    }

    private void expectsForTable(final String tableName, final String pkName, final String selectAll) {
        final PK pk = createMock(PK.class);
        expect(dbSources.src().getTablePk(tableName)).andStubReturn(pk);
        final Col pkColumn = new Col(pkName, "int", true);
        expect(pk.getColumns()).andStubReturn(Collections.singleton(pkColumn));
        expect(sqlStrategy.selectAllStatement(tableName)).andStubReturn(selectAll);
    }

    private static final String TAB1 = "tab1";
    private static final String TAB1_PK_NAME = "pkIdTab1";
    private static final String TAB1_SELECT_ALL = "select all from tab1";

    private static final String TAB2 = "tab2";
    private static final String TAB2_PK_NAME = "pkIdTab2";
    private static final String TAB2_SELECT_ALL = "select all from tab2";

    private static final String TAB3 = "tab3";
    private static final String TAB3_PK_NAME = "pkIdTab3";
    private static final String TAB3_SELECT_ALL = "select all from tab3";

    private MigrateSchemaDiffGenerator testee;
    private AgentSqlStrategy sqlStrategy;
    private DirectedEdge<SchemaInfo, SchemaInfo> dbSources;
}