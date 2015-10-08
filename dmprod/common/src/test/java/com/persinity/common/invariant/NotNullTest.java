/**
 * Copyright (c) 2015 Persinity Inc.
 */
package com.persinity.common.invariant;

import org.junit.Test;

/**
 * @author Doichin Yordanov
 */
public class NotNullTest {

	@Test(expected = NullPointerException.class)
	public void testEnforceNull() {
		final NotNull nn = new NotNull("a", "b");
		nn.enforce(null, null);
	}

	@Test
	public void testEnforce() {
		final NotNull nn = new NotNull("a", "b");
		nn.enforce(1, 2);
	}
}
