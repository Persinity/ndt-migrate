/**
 * Copyright (c) 2015 Persinity Inc.
 */
package com.persinity.common.collection;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.number.OrderingComparison.greaterThan;
import static org.hamcrest.number.OrderingComparison.lessThan;

import org.junit.Assert;
import org.junit.Test;

/**
 * @author Doichin Yordanov
 * 
 */
public class WeightedDirectedEdgeTest {

	/**
	 * Test method for {@link com.persinity.common.collection.ComparableDirectedEdge#weight()} with {@code null} weight.
	 */
	@Test(expected = IllegalArgumentException.class)
	public void testWeightInvalid() {
		new ComparableDirectedEdge<String, Integer, String>("src", null, "dst");
	}

	/**
	 * Test method for {@link com.persinity.common.collection.ComparableDirectedEdge#weight()}.
	 */
	@Test
	public void testWeight() {
		final ComparableDirectedEdge<String, Integer, String> wde = new ComparableDirectedEdge<String, Integer, String>(
				"src", 1, "dst");
		Assert.assertEquals(Integer.valueOf(1), wde.weight());

	}

	/**
	 * Test method for
	 * {@link com.persinity.common.collection.ComparableDirectedEdge#compareTo(com.persinity.common.collection.ComparableDirectedEdge)}
	 * .
	 */
	@Test
	public void testCompareTo() {
		final ComparableDirectedEdge<String, Integer, String> testee11 = new ComparableDirectedEdge<String, Integer, String>(
				"src", 1, "dst");
		final ComparableDirectedEdge<String, Integer, String> testee12 = new ComparableDirectedEdge<String, Integer, String>(
				"src", 1, "dst");
		final ComparableDirectedEdge<String, Integer, String> testee2 = new ComparableDirectedEdge<String, Integer, String>(
				"src", 2, "dst");
		Assert.assertThat(testee2, is(greaterThan(testee11)));
		Assert.assertThat(testee11, is(lessThan(testee2)));
		Assert.assertTrue(testee11.compareTo(testee11) == 0);
		Assert.assertTrue(testee11.compareTo(testee12) == 0);
		Assert.assertTrue(testee11.compareTo(null) > 0);
	}

	@Test
	public void testEqualsHashCode() {
		final ComparableDirectedEdge<String, Integer, String> testee11 = new ComparableDirectedEdge<String, Integer, String>(
				"src", 1, "dst");
		final ComparableDirectedEdge<String, Integer, String> testee12 = new ComparableDirectedEdge<String, Integer, String>(
				"src", 1, "dst");
		final ComparableDirectedEdge<String, Integer, String> testee2 = new ComparableDirectedEdge<String, Integer, String>(
				"src", 2, "dst");
		final ComparableDirectedEdge<String, Integer, String> testee3 = new ComparableDirectedEdge<String, Integer, String>(
				"src1", 1, "dst1");

		Assert.assertEquals(testee11, testee11);
		Assert.assertEquals(testee11.hashCode(), testee11.hashCode());
		Assert.assertEquals(testee11, testee12);
		Assert.assertEquals(testee12, testee11);
		Assert.assertEquals(testee11.hashCode(), testee12.hashCode());
		Assert.assertNotEquals(testee11, testee2);
		Assert.assertNotEquals(testee11, testee3);
	}

}
