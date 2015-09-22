/**
 * Copyright (c) 2015 Persinity Inc.
 */
package com.persinity.common.invariant;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Assert;
import org.junit.Test;

/**
 * @author Doichin Yordanov
 */
public class InvariantTest {

	private static final String MSG = "Argument expression failed!";

	/**
	 * Test method for {@link com.persinity.common.invariant.Invariant#assertArg(boolean, java.lang.String)}.
	 */
	@Test
	public void testArgExpressionTrue() {
		Invariant.assertArg(true, "Never mind!");
	}

	/**
	 * Test method for {@link com.persinity.common.invariant.Invariant#assertArg(boolean, java.lang.String)}.
	 */
	@Test
	public void testArgExpressionFalse() {
		try {
			Invariant.assertArg(false, MSG);
			Assert.fail();
		} catch (final IllegalArgumentException e) {
			Assert.assertEquals(MSG, e.getMessage());
		}
	}

	@Test
	public void testNotNull() {
		Invariant.notNull(new Object(), "obj");
	}

	@Test
	public void testNotEmpty() {
		Assert.assertTrue(Invariant.isNotEmpty("something"));
		Assert.assertFalse(Invariant.isNotEmpty(null));
		Assert.assertFalse(Invariant.isNotEmpty(""));
		Assert.assertFalse(Invariant.isNotEmpty("   "));
	}

	@Test(expected = NullPointerException.class)
	public void testNotNullNegative() {
		Invariant.notNull(null, "obj");
	}

	@Test(expected = NullPointerException.class)
	public void testNotNullArgNameNegative() {
		Invariant.notNull(null, null);
	}

	@Test(expected = NullPointerException.class)
	public void testNotNullArgNameEmptyNegative() {
		Invariant.notNull(null, "");
	}

	@Test
	public void testNotEmptyCharSeq() {
		Invariant.notEmpty("test", "test");
	}

	@Test(expected = IllegalArgumentException.class)
	public void testNotEmptyCharSeqNegative() {
		Invariant.notEmpty("", "test");
	}

	@Test(expected = IllegalArgumentException.class)
	public void testNotEmptyCharSeqNegativeNoParamName() {
		Invariant.notEmpty("");
	}

	@Test(expected = NullPointerException.class)
	public void testNotEmptyCharSeqNullNegative() {
		Invariant.notEmpty((String) null, "test");
	}

	@Test
	public void testNotEmptyCollection() {
		List<String> test = new ArrayList<>();
		test.add("");
		Invariant.notEmpty(test, "test");
	}

	@Test(expected = IllegalArgumentException.class)
	public void testNotEmptyCollectionNegative() {
		List<String> test = new ArrayList<>();
		Invariant.notEmpty(test, "test");
	}

	@Test(expected = IllegalArgumentException.class)
	public void testNotEmptyCollectionNegativeNoParamName() {
		List<String> test = new ArrayList<>();
		Invariant.notEmpty(test);
	}

	@Test(expected = NullPointerException.class)
	public void testNotEmptyCollectionNullNegative() {
		Invariant.notEmpty((List) null, "test");
	}

	@Test
	public void testNotEmptyMap() {
		Map<String, String> test = new HashMap<>();
		test.put("", "");
		Invariant.notEmpty(test, "test");
	}

	@Test(expected = IllegalArgumentException.class)
	public void testNotEmptyMapNegative() {
		Map<String, String> test = new HashMap<>();
		Invariant.notEmpty(test, "test");
	}

	@Test(expected = IllegalArgumentException.class)
	public void testNotEmptyMapNegativeNoParamName() {
		Map<String, String> test = new HashMap<>();
		Invariant.notEmpty(test);
	}

	@Test(expected = NullPointerException.class)
	public void testNotEmptyMapNullNegative() {
		Invariant.notEmpty((Map) null, "test");
	}
}
