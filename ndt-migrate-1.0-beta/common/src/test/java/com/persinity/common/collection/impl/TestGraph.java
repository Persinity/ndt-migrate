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
package com.persinity.common.collection.impl;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.jgrapht.DirectedGraph;
import org.jgrapht.EdgeFactory;
import org.jgrapht.graph.DefaultDirectedGraph;

import com.persinity.common.collection.ComparableDirectedEdge;
import com.persinity.common.collection.GraphUtils;

/**
 * SC directed graph for testing.
 *
 * @author Doichin Yordanov
 */
public class TestGraph {
    /**
     * @see TestGraph#graph
     */
    public final GraphInfo<Integer, ComparableDirectedEdge<Integer, Integer, Integer>> gi;

    /**
     * <code>
     * |  | -2-> |  | -0-> |  |<BR>
     * |v1|      |v2|      |v3|<BR>
     * |  | <-1- |  | <-1- |  |
     * </code>
     */
    public final DirectedGraph<Integer, ComparableDirectedEdge<Integer, Integer, Integer>> graph;

    public final Integer v1;
    public final Integer v2;
    public final Integer v3;
    public final ComparableDirectedEdge<Integer, Integer, Integer> e122;
    public final Set<ComparableDirectedEdge<Integer, Integer, Integer>> v2Edges;
    public final ComparableDirectedEdge<Integer, Integer, Integer> e312;
    public final ComparableDirectedEdge<Integer, Integer, Integer> e211;
    public final ComparableDirectedEdge<Integer, Integer, Integer> e203;

    public final Map<Integer, VertexInfo<Integer, ComparableDirectedEdge<Integer, Integer, Integer>>> graphInfoMap;

    public TestGraph() {
        v1 = 1;
        v2 = 2;
        v3 = 3;
        e122 = new ComparableDirectedEdge<Integer, Integer, Integer>(v1, 2, v2);
        e211 = new ComparableDirectedEdge<Integer, Integer, Integer>(v2, 1, v1);
        e203 = new ComparableDirectedEdge<Integer, Integer, Integer>(v2, 0, v3);
        e312 = new ComparableDirectedEdge<Integer, Integer, Integer>(v3, 1, v2);

        final Set<ComparableDirectedEdge<Integer, Integer, Integer>> v1Edges = new HashSet<>();
        v1Edges.add(e122);
        v1Edges.add(e211);
        v2Edges = new HashSet<>();
        v2Edges.add(e122);
        v2Edges.add(e211);
        v2Edges.add(e203);
        v2Edges.add(e312);
        final Set<ComparableDirectedEdge<Integer, Integer, Integer>> v3Edges = new HashSet<>();
        v3Edges.add(e203);
        v3Edges.add(e312);

        final VertexInfo<Integer, ComparableDirectedEdge<Integer, Integer, Integer>> vi1 = new VertexInfo<Integer, ComparableDirectedEdge<Integer, Integer, Integer>>(
                v1, v1Edges);
        final VertexInfo<Integer, ComparableDirectedEdge<Integer, Integer, Integer>> vi2 = new VertexInfo<Integer, ComparableDirectedEdge<Integer, Integer, Integer>>(
                v2, v2Edges);
        final VertexInfo<Integer, ComparableDirectedEdge<Integer, Integer, Integer>> vi3 = new VertexInfo<Integer, ComparableDirectedEdge<Integer, Integer, Integer>>(
                v3, v3Edges);

        graphInfoMap = new HashMap<>();
        graphInfoMap.put(v1, vi1);
        graphInfoMap.put(v2, vi2);
        graphInfoMap.put(v3, vi3);
        gi = new GraphInfo<>(graphInfoMap);

        final EdgeFactory<Integer, ComparableDirectedEdge<Integer, Integer, Integer>> ef = GraphUtils
                .newDummyEdgeFactory();
        graph = new DefaultDirectedGraph<>(ef);
        graph.addVertex(v1);
        graph.addVertex(v2);
        graph.addVertex(v3);
        graph.addEdge(v1, v2, e122);
        graph.addEdge(v2, v1, e211);
        graph.addEdge(v2, v3, e203);
        graph.addEdge(v3, v2, e312);
    }

}
