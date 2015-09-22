/**
 * Copyright (c) 2015 Persinity Inc.
 */
package com.persinity.common.invariant;

/**
 * @author Doichin Yordanov
 * 
 */
public final class NotNull extends Invariant {

	public NotNull(final String... argName) {
		super(argName);
	}

	@Override
	public void enforce(final Object... arg) throws RuntimeException {
		assert getArgName().length == arg.length;
		for (int i = 0; i < arg.length; i++) {
			if (arg[i] == null) {
				throw new NullPointerException(toString() + " - " + getArgName()[i] + " == null");
			}
		}
	}

}
