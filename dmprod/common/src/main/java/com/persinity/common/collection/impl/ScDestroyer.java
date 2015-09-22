/**
 * Copyright (c) 2015 Persinity Inc.
 */
package com.persinity.common.collection.impl;

import java.util.Set;

import org.jgrapht.DirectedGraph;

/**
 * Seeks and and tries to destroy the minimal amount of edge(s) in a strongly connected directed graph. After destroy
 * the graph is not strongly connected.
 * 
 * @author Doichin Yordanov
 * 
 * @param <V>
 * @param <E>
 */
public interface ScDestroyer<V, E> {

	/**
	 * Seeks and destroys edges in a strongly connected directed graph, according subclass strategy.
	 * 
	 * @param sc
	 *            Strongly connected component to destroy edge in. Note that the graph is modified.
	 * @return The destroyed edge(s).
	 */
	Set<E> seekNDestroy(DirectedGraph<V, E> sc);

}