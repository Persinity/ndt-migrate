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

import java.util.Collection;

import org.jgrapht.DirectedGraph;
import org.jgrapht.experimental.dag.DirectedAcyclicGraph;

import com.persinity.common.invariant.Invariant;

/**
 * Directed Acyclic Graph.
 *
 * @author Ivan Dachev
 */
public class Dag<V, E extends DirectedEdge<V, V>> extends DirectedAcyclicGraph<V, E> {

    public Dag() {
        super(GraphUtils.<V, E>newDummyEdgeFactory());
    }

    /**
     * @param vs
     *         - vertexes to populate in the graph
     */
    public Dag(final Collection<V> vs) {
        this();
        Invariant.notNull(vs);
        for (final V v : vs) {
            addVertex(v);
        }
    }

    public static <V, E extends DirectedEdge<V, V>> Dag<V, E> of(final DirectedGraph<V, E> dg) {
        Invariant.notNull(dg);

        final Dag<V, E> result = new Dag<>();
        GraphUtils.addGraphToGraph(dg, result);
        return result;
    }

    private static final long serialVersionUID = -5376960873750984636L;
}
