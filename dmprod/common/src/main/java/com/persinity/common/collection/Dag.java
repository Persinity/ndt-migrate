/**
 * Copyright (c) 2015 Persinity Inc.
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
        super(GraphUtils.<V, E> newDummyEdgeFactory());
    }

    /**
     * @param vs
     *            - vertexes to populate in the graph
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
