/**
 * Copyright (c) 2015 Persinity Inc.
 */
package com.persinity.common.transform;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.persinity.ndt.transform.RelFunc;

/**
 * @author Doichin Yordanov
 * 
 */
public class RelFuncTest {

	private static final String SELECT_2 = "SELECT 2 FROM dual";
	private static final String SELECT_1 = "SELECT 1 FROM dual";

	private RelFunc testee11, testee12, testee20;

	@Before
	public void setUp() {
		testee11 = testee12 = new RelFunc(SELECT_1);
		testee20 = new RelFunc(SELECT_2);
	}

	/**
	 * Test method for {@link com.persinity.common.fp.RelFunc#hashCode()}.
	 */
	@Test
	public void testHashCode() {
		Assert.assertEquals(testee11.hashCode(), testee12.hashCode());
	}

	/**
	 * Test method for {@link com.persinity.common.fp.RelFunc#equals(java.lang.Object)}.
	 */
	@Test
	public void testEqualsObject() {
		Assert.assertTrue(testee11.equals(testee12));
		Assert.assertFalse(testee11.equals(null));
		Assert.assertFalse(testee11.equals(testee20));
	}

	/**
	 * Test method for {@link com.persinity.common.fp.RelFunc#toString()}.
	 */
	@Test
	public void testToString() {
		Assert.assertNotNull(testee11.toString());
	}

}
