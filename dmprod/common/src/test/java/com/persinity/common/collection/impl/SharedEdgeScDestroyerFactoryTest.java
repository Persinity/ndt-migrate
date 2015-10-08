/**
 * Copyright (c) 2015 Persinity Inc.
 */
package com.persinity.common.collection.impl;

import org.jgrapht.DirectedGraph;
import org.junit.Assert;
import org.junit.Test;

import com.persinity.common.collection.ComparableDirectedEdge;

/**
 * @author Doichin Yordanov
 * 
 */
public class SharedEdgeScDestroyerFactoryTest {

	@Test
	public void testNewDestroyerFor() {
		final TestGraph bag = new TestGraph();
		final DirectedGraph<Integer, ComparableDirectedEdge<Integer, Integer, Integer>> dg = bag.graph;
		final ScDestroyer<Integer, ComparableDirectedEdge<Integer, Integer, Integer>> scDestroyer = new SharedEdgeScDestroyerFactory<Integer, ComparableDirectedEdge<Integer, Integer, Integer>>()
				.newDestroyerFor(dg);
		Assert.assertNotNull(scDestroyer);
		Assert.assertTrue(scDestroyer instanceof SharedEdgeScDestroyer);
	}
}
