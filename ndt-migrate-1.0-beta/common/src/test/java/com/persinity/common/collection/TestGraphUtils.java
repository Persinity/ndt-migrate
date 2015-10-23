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
package com.persinity.common.collection;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.HashSet;
import java.util.Set;

import org.jgrapht.DirectedGraph;
import org.jgrapht.EdgeFactory;

/**
 * @author Ivan Dachev
 */
@SuppressWarnings("unchecked")
public class TestGraphUtils {
    public static EdgeFactory<Integer, DirectedEdge<Integer, Integer>> TEST_EDGE_FACTORY = new EdgeFactory<Integer, DirectedEdge<Integer, Integer>>() {
        @Override
        public DirectedEdge<Integer, Integer> createEdge(final Integer sourceVertex, final Integer targetVertex) {
            return new DirectedEdge<>(sourceVertex, targetVertex);
        }
    };

    public static <V, E> void addVertexes(final DirectedGraph<V, E> graph, final V... vs) {
        for (V v : vs) {
            graph.addVertex(v);
        }
    }

    public static <V, E> void verifyVertexes(final DirectedGraph<V, E> graph, final V... vs) {
        assertEquals(vs.length, graph.vertexSet().size());
        for (V v : vs) {
            assertTrue(graph.containsVertex(v));
        }
    }

    public static <V, E extends DirectedEdge<V, V>> void verifyDirectedEdges(final DirectedGraph<V, E> graph,
            final V... evs) {
        assertEquals(evs.length, graph.edgeSet().size() * 2);
        for (int i = 0; i < evs.length; i += 2) {
            assertTrue(graph.containsEdge((E) new DirectedEdge<V, V>(evs[i], evs[i + 1])));
        }
    }

    public static <V, E extends DirectedEdge<V, V>> void addDirectedEdges(final DirectedGraph<V, E> graph,
            final V... evs) {
        for (int i = 0; i < evs.length; i += 2) {
            final V srcV = evs[i];
            final V dstV = evs[i + 1];
            graph.addEdge(srcV, dstV, (E) new DirectedEdge<V, V>(srcV, dstV));
        }
    }

    public static <T> Set<DirectedEdge<T, T>> buildDirectedEdgeSet(T... vertices) {
        Set<DirectedEdge<T, T>> result = new HashSet<>();
        for (int i = 0; i < vertices.length; i += 2) {
            final T srcV = vertices[i];
            final T dstV = vertices[i + 1];
            result.add(new DirectedEdge<>(srcV, dstV));
        }
        return result;
    }

}
