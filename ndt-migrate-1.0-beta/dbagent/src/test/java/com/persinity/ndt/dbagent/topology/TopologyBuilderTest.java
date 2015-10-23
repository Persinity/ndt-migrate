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
import static com.persinity.ndt.dbagent.topology.TestTopologyBuilderUtil.buildFK;
import static com.persinity.ndt.dbagent.topology.TestTopologyBuilderUtil.buildFkEdge;
import static com.persinity.ndt.dbagent.topology.TestTopologyBuilderUtil.toSet;
import static org.easymock.EasyMock.capture;
import static org.easymock.EasyMock.createNiceMock;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.expectLastCall;
import static org.easymock.EasyMock.newCapture;
import static org.easymock.EasyMock.replay;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.easymock.Capture;
import org.easymock.IAnswer;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import com.persinity.common.db.metainfo.Col;
import com.persinity.common.db.metainfo.constraint.FK;
import com.persinity.ndt.dbagent.relational.SchemaInfo;

/**
 * @author Ivo Yanakiev
 */
@RunWith(Parameterized.class)
public class TopologyBuilderTest {

    public TopologyBuilderTest(final Map<String, Set<FK>> input, final SchemaGraph expected) {

        this.expected = expected;
        this.input = input;
    }

    @Test
    public void testBuildTopology() {

        SchemaInfo schema = createNiceMock(SchemaInfo.class);
        expect(schema.getTableNames()).andReturn(input.keySet()).anyTimes();

        final Capture<String> argument = newCapture();
        expect(schema.getTableFks(capture(argument)));

        expectLastCall().andAnswer(new IAnswer() {

            @Override
            public Object answer() throws Throwable {
                return input.get(argument.getValue());
            }
        }).anyTimes();

        replay(schema);

        SchemaGraphBuilder builder = new SchemaGraphBuilder(schema);

        SchemaGraph result = builder.buildTopology();
        Assert.assertEquals(expected, result);
    }

    @Parameterized.Parameters
    public static Collection<Object[]> data() {

        return Arrays.asList(new Object[][] { circularDependency() });
    }

    @SuppressWarnings("serial")
    private static Object[] circularDependency() {

        Map<String, Set<FK>> input = new HashMap<String, Set<FK>>() {{
            put("table1", toSet(

                    buildFK("table1", "table2", toSet(new Col("column1", "t", false)),
                            toSet(new Col("column2", "t", false)))));

            put("table2", toSet(

                    buildFK("table2", "table3", toSet(new Col("column1", "t", false)),
                            toSet(new Col("column2", "t", false)))));

            put("table3", toSet(

                    buildFK("table3", "table1", toSet(new Col("column1", "t", false)),
                            toSet(new Col("column2", "t", false)))));
        }};

        SchemaGraph expected = new SchemaGraph();

        addEdges(expected, buildFkEdge("table1", "table2", toSet(new Col("column1", "t", false)),
                        toSet(new Col("column2", "t", false))),

                buildFkEdge("table2", "table3", toSet(new Col("column1", "t", false)),
                        toSet(new Col("column2", "t", false))),

                buildFkEdge("table3", "table1", toSet(new Col("column1", "t", false)),
                        toSet(new Col("column2", "t", false))));

        return new Object[] { input, expected };
    }

    private final SchemaGraph expected;
    private final Map<String, Set<FK>> input;

}
