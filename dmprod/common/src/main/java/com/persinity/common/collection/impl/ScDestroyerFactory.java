/**
 * Copyright (c) 2015 Persinity Inc.
 */
package com.persinity.common.collection.impl;

import org.jgrapht.DirectedGraph;

/**
 * Factory for {@link ScDestroyer}.
 * 
 * @author Doichin Yordanov
 * 
 */
public interface ScDestroyerFactory<V, E> {
	ScDestroyer<V, E> newDestroyerFor(final DirectedGraph<V, E> dg);
}
