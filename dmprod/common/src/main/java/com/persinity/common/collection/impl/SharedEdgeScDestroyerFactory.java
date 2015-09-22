/**
 * Copyright (c) 2015 Persinity Inc.
 */
package com.persinity.common.collection.impl;

import org.jgrapht.DirectedGraph;

import com.persinity.common.collection.ComparableDirectedEdge;

/**
 * Factory for {@link SharedEdgeScDestroyerTest}s
 * 
 * @author Doichin Yordanov
 */
public class SharedEdgeScDestroyerFactory<V, E extends ComparableDirectedEdge<V, Integer, V>> implements
		ScDestroyerFactory<V, E> {

	@Override
	public ScDestroyer<V, E> newDestroyerFor(final DirectedGraph<V, E> dg) {
		return new SharedEdgeScDestroyer<>();
	}

}
