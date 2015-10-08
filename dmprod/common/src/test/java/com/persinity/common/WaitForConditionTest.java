/**
 * Copyright (c) 2015 Persinity Inc.
 */
package com.persinity.common;

import org.junit.Test;

import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.*;

/**
 * @author Ivan Dachev
 */
public class WaitForConditionTest {

	@Test
	public void test() throws Exception {
		calledOnce = false;

		new WaitForCondition(500) {
			@Override
			public boolean condition() {
				calledOnce = true;
				return true;
			}
		}.waitOrTimeout();

		assertTrue(calledOnce);

		try {
			new WaitForCondition(501, "msg") {
				@Override
				public boolean condition() {
					return false;
				}
			}.waitOrTimeout();
			fail("Expected to throw RuntimeException");
		} catch(RuntimeException e) {
			assertThat(e.getMessage(), is("msg"));
		}

		try {
			new WaitForCondition(1) {
				@Override
				public boolean condition() {
					return false;
				}
			}.waitOrTimeout();
			fail("Expected to throw IllegalArgumentException timeout < tick");
		} catch(IllegalArgumentException e) {
		}

		try {
			new WaitForCondition(1, "", 0) {
				@Override
				public boolean condition() {
					return false;
				}
			}.waitOrTimeout();
			fail("Expected to throw IllegalArgumentException tick should be positive");
		} catch(IllegalArgumentException e) {
		}

		try {
			new WaitForCondition(1, "", -1) {
				@Override
				public boolean condition() {
					return false;
				}
			}.waitOrTimeout();
			fail("Expected to throw IllegalArgumentException tick should be positive");
		} catch(IllegalArgumentException e) {
		}

		thrown = null;
		Thread thread = new Thread(new Runnable() {
			public void run() {
				try {
					new WaitForCondition(2000) {
						@Override
						public boolean condition() {
							return false;
						}
					}.waitOrTimeout();
				} catch (RuntimeException e) {
					thrown = e.getCause();
				}
			}
		});
		thread.start();
		thread.join(250);
		thread.interrupt();
		Thread.sleep(250);

		assertThat(thrown, instanceOf(InterruptedException.class));
	}

	private boolean calledOnce;

	private Throwable thrown;
}