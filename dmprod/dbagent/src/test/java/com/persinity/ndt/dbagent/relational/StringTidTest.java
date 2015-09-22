/**
 * Copyright (c) 2015 Persinity Inc.
 */
package com.persinity.ndt.dbagent.relational;

import org.junit.Assert;
import org.junit.Test;

/**
 * @author Doichin Yordanov
 * 
 */
public class StringTidTest {

	/**
	 * Test method for {@link com.persinity.ndt.dbagent.relational.StringTid#hashCode()}.
	 */
	@Test
	public void testHashCode() {
		final StringTid tid = new StringTid("T1");
		final StringTid tidSame = new StringTid("T1");
		Assert.assertEquals(tid.hashCode(), tidSame.hashCode());
	}

	/**
	 * Test method for {@link com.persinity.ndt.dbagent.relational.StringTid#equals(java.lang.Object)}.
	 */
	@Test
	public void testEqualsObject() {
		final StringTid tid = new StringTid("T1");
		final StringTid tidSame = new StringTid("T1");
		final StringTid tidDiff = new StringTid("T2");

		Assert.assertEquals(tid, tid);
		Assert.assertEquals(tid, tidSame);
		Assert.assertEquals(tidSame, tid);
		Assert.assertNotEquals(tid, null);
		Assert.assertNotEquals(tid, tidDiff);
	}

}
