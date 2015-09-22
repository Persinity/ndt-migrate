package com.persinity.common.collection;

import org.jgrapht.graph.DefaultDirectedWeightedGraph;

/**
 * Directed Graph.
 *
 * @author Ivo Yanakiev
 */
public class Dg<V, E extends DirectedEdge<V, V>> extends DefaultDirectedWeightedGraph<V, E> {

	public Dg() {
		super(GraphUtils.<V, E>newDummyEdgeFactory());
	}
}
