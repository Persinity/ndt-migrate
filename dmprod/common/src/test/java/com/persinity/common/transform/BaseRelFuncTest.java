/**
 * Copyright (c) 2015 Persinity Inc.
 */
package com.persinity.common.transform;

import org.junit.Assert;
import org.junit.Test;

/**
 * @author Doichin Yordanov
 * 
 */
public class BaseRelFuncTest {

	private static final String DIFF_SQL = "SELECT COUNT(1) FROM another_table";
	private static final String SQL = "SELECT * FROM dual";

	/**
	 * Test method for {@link com.persinity.ndt.transform.BaseRelFunc#hashCode()}.
	 */
	@Test
	public void testHashCode() {
		final BaseRelFuncFake f = new BaseRelFuncFake(SQL);
		final BaseRelFuncFake fSame = new BaseRelFuncFake(SQL);
		Assert.assertEquals(f.hashCode(), fSame.hashCode());
	}

	/**
	 * Test method for {@link com.persinity.ndt.transform.BaseRelFunc#equals(java.lang.Object)}.
	 */
	@Test
	public void testEqualsObject() {
		final BaseRelFuncFake f = new BaseRelFuncFake(SQL);
		final BaseRelFuncFake fSame = new BaseRelFuncFake(SQL);
		final BaseRelFuncFake fDiff = new BaseRelFuncFake(DIFF_SQL);

		Assert.assertEquals(f, f);
		Assert.assertEquals(f, fSame);
		Assert.assertEquals(fSame, f);
		Assert.assertNotEquals(f, fDiff);
		Assert.assertNotEquals(f, null);
	}

}
