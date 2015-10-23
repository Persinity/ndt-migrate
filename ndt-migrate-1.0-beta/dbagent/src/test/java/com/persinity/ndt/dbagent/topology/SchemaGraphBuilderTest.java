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
package com.persinity.ndt.dbagent.topology;

import static org.easymock.EasyMock.expect;
import static org.hamcrest.CoreMatchers.hasItems;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.Collections;
import java.util.Iterator;
import java.util.Set;

import org.easymock.EasyMockSupport;
import org.junit.Test;

import com.google.common.collect.Sets;
import com.persinity.common.db.metainfo.FKEdge;
import com.persinity.common.db.metainfo.constraint.FK;
import com.persinity.common.db.metainfo.constraint.Unique;
import com.persinity.ndt.dbagent.relational.SchemaInfo;

/**
 * @author Ivan Dachev
 */
public class SchemaGraphBuilderTest extends EasyMockSupport {

    @Test
    public void testNoTables() throws Exception {
        final SchemaInfo schemaInfo = createMock(SchemaInfo.class);
        final SchemaGraphBuilder testee = new SchemaGraphBuilder(schemaInfo);

        expect(schemaInfo.getTableNames()).andReturn(Collections.<String>emptySet()).times(2);

        replayAll();

        final SchemaGraph topology = testee.buildTopology();

        verifyAll();

        final Set<String> vertexes = topology.vertexSet();
        assertThat(vertexes.size(), is(0));

        final Set<FKEdge> edges = topology.edgeSet();
        assertThat(edges.size(), is(0));
    }

    @Test
    public void testSingleTable() throws Exception {
        final SchemaInfo schemaInfo = createMock(SchemaInfo.class);
        final SchemaGraphBuilder testee = new SchemaGraphBuilder(schemaInfo);

        final Set<String> tableNames = Collections.singleton(TABLE_1);
        expect(schemaInfo.getTableNames()).andReturn(tableNames).times(2);

        expect(schemaInfo.getTableFks(TABLE_1)).andReturn(Collections.<FK>emptySet());

        replayAll();

        final SchemaGraph topology = testee.buildTopology();

        verifyAll();

        final Set<String> vertexes = topology.vertexSet();
        assertThat(vertexes.size(), is(1));
        assertThat(vertexes, hasItems(TABLE_1));

        final Set<FKEdge> edges = topology.edgeSet();
        assertThat(edges.size(), is(0));
    }

    @Test
    public void testTwoTablesNoFks() throws Exception {
        final SchemaInfo schemaInfo = createMock(SchemaInfo.class);
        final SchemaGraphBuilder testee = new SchemaGraphBuilder(schemaInfo);

        final Set<String> tableNames = Sets.newHashSet(TABLE_1, TABLE_2);
        expect(schemaInfo.getTableNames()).andReturn(tableNames).times(2);

        expect(schemaInfo.getTableFks(TABLE_1)).andReturn(Collections.<FK>emptySet());
        expect(schemaInfo.getTableFks(TABLE_2)).andReturn(Collections.<FK>emptySet());

        replayAll();

        final SchemaGraph topology = testee.buildTopology();

        verifyAll();

        final Set<String> vertexes = topology.vertexSet();
        assertThat(vertexes.size(), is(2));
        assertThat(vertexes, hasItems(TABLE_1, TABLE_2));

        final Set<FKEdge> edges = topology.edgeSet();
        assertThat(edges.size(), is(0));
    }

    @Test
    public void testTwoTablesOneFk() throws Exception {
        final SchemaInfo schemaInfo = createMock(SchemaInfo.class);
        final SchemaGraphBuilder testee = new SchemaGraphBuilder(schemaInfo);

        final Set<String> tableNames = Sets.newHashSet(TABLE_1, TABLE_2);
        expect(schemaInfo.getTableNames()).andReturn(tableNames).times(2);

        final FK fk = buildFk(TABLE_1, TABLE_2);
        expect(schemaInfo.getTableFks(TABLE_1)).andReturn(Collections.singleton(fk));

        expect(schemaInfo.getTableFks(TABLE_2)).andReturn(Collections.<FK>emptySet());

        replayAll();

        final SchemaGraph topology = testee.buildTopology();

        verifyAll();

        final Set<String> vertexes = topology.vertexSet();
        assertThat(vertexes.size(), is(2));
        assertThat(vertexes, hasItems(TABLE_1, TABLE_2));

        final Set<FKEdge> edges = topology.edgeSet();
        assertThat(edges.size(), is(1));

        final Iterator<FKEdge> iter = edges.iterator();
        assertTrue(iter.hasNext());
        final FKEdge edge = iter.next();
        assertFalse(iter.hasNext());

        assertThat(edge.weight(), is(fk));
    }

    @Test
    public void test3Tables4Fks() throws Exception {
        final SchemaInfo schemaInfo = createMock(SchemaInfo.class);
        final SchemaGraphBuilder testee = new SchemaGraphBuilder(schemaInfo);

        final Set<String> tableNames = Sets.newHashSet(TABLE_1, TABLE_2, TABLE_3);
        expect(schemaInfo.getTableNames()).andReturn(tableNames).times(2);

        final FK fk1 = buildFk(TABLE_1, TABLE_2);
        expect(schemaInfo.getTableFks(TABLE_1)).andReturn(Collections.singleton(fk1));

        final FK fk2 = buildFk(TABLE_2, TABLE_1);
        final FK fk3 = buildFk(TABLE_2, TABLE_3);
        expect(schemaInfo.getTableFks(TABLE_2)).andReturn(Sets.newHashSet(fk2, fk3));

        final FK fk4 = buildFk(TABLE_3, TABLE_1);
        expect(schemaInfo.getTableFks(TABLE_3)).andReturn(Collections.singleton(fk4));

        replayAll();

        final SchemaGraph topology = testee.buildTopology();

        verifyAll();

        final Set<String> vertexes = topology.vertexSet();
        assertThat(vertexes.size(), is(3));
        assertThat(vertexes, hasItems(TABLE_1, TABLE_2, TABLE_3));

        final Set<FKEdge> edges = topology.edgeSet();
        assertThat(edges.size(), is(4));

        for (FKEdge fkEdge : edges) {
            if (fkEdge.weight().equals(fk1)) {
                assertThat(fkEdge.src(), is(TABLE_1));
                assertThat(fkEdge.dst(), is(TABLE_2));
            } else if (fkEdge.weight().equals(fk2)) {
                assertThat(fkEdge.src(), is(TABLE_2));
                assertThat(fkEdge.dst(), is(TABLE_1));
            } else if (fkEdge.weight().equals(fk3)) {
                assertThat(fkEdge.src(), is(TABLE_2));
                assertThat(fkEdge.dst(), is(TABLE_3));
            } else if (fkEdge.weight().equals(fk4)) {
                assertThat(fkEdge.src(), is(TABLE_3));
                assertThat(fkEdge.dst(), is(TABLE_1));
            } else {
                fail("Unexpected FKEdge: " + fkEdge);
            }
        }
    }

    private FK buildFk(final String tableSrc, final String tableDst) {
        final FK fk = createMock(FK.class);
        expect(fk.getTable()).andReturn(tableSrc);
        final Unique dstConstraint = createMock(Unique.class);
        expect(dstConstraint.getTable()).andReturn(tableDst);
        expect(fk.getDstConstraint()).andReturn(dstConstraint);
        return fk;
    }

    private static final String TABLE_1 = "table1";
    private static final String TABLE_2 = "table2";
    private static final String TABLE_3 = "table3";
}