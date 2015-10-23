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

import static com.persinity.ndt.dbagent.topology.TestTopologyBuilderUtil.addEdges;
import static com.persinity.ndt.dbagent.topology.TestTopologyBuilderUtil.buildFkEdge;
import static com.persinity.ndt.dbagent.topology.TestTopologyBuilderUtil.toSet;
import static org.easymock.EasyMock.createNiceMock;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.replay;

import java.util.Arrays;
import java.util.Collection;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import com.persinity.common.db.metainfo.Col;
import com.persinity.ndt.transform.EntitiesDag;

/**
 * @author Ivo Yanakiev
 */
@RunWith(Parameterized.class)
public class NormalizedTopologyBuilderTest {

    public NormalizedTopologyBuilderTest(SchemaGraph input, EntitiesDag expected) {

        this.input = input;
        this.expected = expected;
    }

    @Test
    public void testBuildNormalizedTopology() {

        SchemaGraphBuilder topologyBuilder = createNiceMock(SchemaGraphBuilder.class);
        expect(topologyBuilder.buildTopology()).andReturn(input);

        replay(topologyBuilder);

        NormalizedSchemaGraphBuilder builder = new NormalizedSchemaGraphBuilder(topologyBuilder);

        EntitiesDag result = builder.buildNormalizedTopology();
        Assert.assertEquals(expected, result);
    }

    @Parameterized.Parameters
    public static Collection<Object[]> data() {

        return Arrays.asList(cuttableCircularDependency(), compositeDependency(), twoCompositeDependencies());
    }

    private static Object[] cuttableCircularDependency() {

        SchemaGraph input = new SchemaGraph();
        addEdges(input, buildFkEdge("table1", "table2", toSet(new Col("column1", "t", false)),
                        toSet(new Col("column2", "t", false))),
                buildFkEdge("table2", "table3", toSet(new Col("column2", "t", false)),
                        toSet(new Col("column3", "t", false))),
                buildFkEdge("table3", "table1", toSet(new Col("column3", "t", true)),
                        toSet(new Col("column1", "t", false))));

        EntitiesDag expected = new EntitiesDag();
        addEdges(expected, buildFkEdge("table1", "table2", toSet(new Col("column1", "t", false)),
                        toSet(new Col("column2", "t", false))),
                buildFkEdge("table2", "table3", toSet(new Col("column2", "t", false)),
                        toSet(new Col("column3", "t", false))));

        return new Object[] { input, expected };
    }

    private static Object[] compositeDependency() {

        SchemaGraph input = new SchemaGraph();
        addEdges(input, buildFkEdge("table1", "table2",
                        toSet(new Col("column1-1", "t", false), new Col("column1-2", "t", true)),
                        toSet(new Col("column2", "t", false), new Col("column2", "t", false))),
                buildFkEdge("table2", "table3", toSet(new Col("column2", "t", false)),
                        toSet(new Col("column3", "t", false))));

        EntitiesDag expected = new EntitiesDag();
        addEdges(expected, buildFkEdge("table1", "table2",
                        toSet(new Col("column1-1", "t", false), new Col("column1-2", "t", true)),
                        toSet(new Col("column2", "t", false))),
                buildFkEdge("table2", "table3", toSet(new Col("column2", "t", false)),
                        toSet(new Col("column3", "t", false))));

        return new Object[] { input, expected };
    }

    private static Object[] twoCompositeDependencies() {

        SchemaGraph input = new SchemaGraph();
        addEdges(input, buildFkEdge("table1", "table2",
                        toSet(new Col("column1-1", "t", false), new Col("column1-2", "t", true)),
                        toSet(new Col("column2", "t", false), new Col("column2", "t", false))),
                buildFkEdge("table1", "table2", toSet(new Col("column1-3", "t", true), new Col("column1-4", "t", true)),
                        toSet(new Col("column2", "t", false), new Col("column2", "t", false))),
                buildFkEdge("table2", "table3", toSet(new Col("column2", "t", false)),
                        toSet(new Col("column3", "t", false))));

        EntitiesDag expected = new EntitiesDag();
        addEdges(expected, buildFkEdge("table1", "table2",
                        toSet(new Col("column1-1", "t", false), new Col("column1-2", "t", true)),
                        toSet(new Col("column2", "t", false))), buildFkEdge("table1", "table2",
                        toSet(new Col("column1-3", "t", false), new Col("column1-4", "t", true)),
                        toSet(new Col("column2", "t", false), new Col("column2", "t", false))),
                buildFkEdge("table2", "table3", toSet(new Col("column2", "t", false)),
                        toSet(new Col("column3", "t", false))));

        return new Object[] { input, expected };
    }

    private SchemaGraph input;
    private EntitiesDag expected;
}
