/**
 * Copyright (c) 2015 Persinity Inc.
 */
package com.persinity.common.collection.impl;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.persinity.common.collection.ComparableDirectedEdge;

/**
 * @author Doichin Yordanov
 */
public class SharedEdgeScDestroyerTest {

	private TestGraph bag;

	@Before
	public void setUp() {
		bag = new TestGraph();
	}

	@Test
	public void testSeekNDestroy() {
		final ScDestroyer<Integer, ComparableDirectedEdge<Integer, Integer, Integer>> scDestroyer = new SharedEdgeScDestroyer<>();
		Set<ComparableDirectedEdge<Integer, Integer, Integer>> killed = scDestroyer.seekNDestroy(bag.graph);
		Assert.assertEquals(new HashSet<ComparableDirectedEdge<Integer, Integer, Integer>>(Arrays.asList(bag.e122)),
				killed);

		bag.graph.removeEdge(bag.e211);
		bag.graph.removeVertex(bag.v1);

		killed = scDestroyer.seekNDestroy(bag.graph);
		Assert.assertEquals(new HashSet<ComparableDirectedEdge<Integer, Integer, Integer>>(Arrays.asList(bag.e312)),
				killed);
	}
}
