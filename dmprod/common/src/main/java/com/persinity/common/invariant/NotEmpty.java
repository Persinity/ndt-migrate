/**
 * Copyright (c) 2015 Persinity Inc.
 */
package com.persinity.common.invariant;

import java.util.Collection;

import com.persinity.common.collection.Tree;

/**
 * Not null and not empty {@code String} invariant
 * 
 * @author Doichin Yordanov
 */
public class NotEmpty extends Invariant {

	public NotEmpty(final String... argName) {
		super(argName);
	}

	public void enforce(final String... arg) throws RuntimeException {
		for (int i = 0; i < arg.length; i++) {
			new NotNull(getArgName()[i]).enforce(arg[i]);
			Invariant.assertArg(!arg[i].trim().isEmpty(), getArgName()[i] + " is empty!");
		}
	}

	public void enforce(final Collection<?>... arg) throws RuntimeException {
		for (int i = 0; i < arg.length; i++) {
			new NotNull(getArgName()[i]).enforce(arg[i]);
			Invariant.assertArg(!arg[i].isEmpty(), getArgName()[i] + " is empty!");
		}
	}

	public void enforce(final Tree<?>... arg) throws RuntimeException {
		for (int i = 0; i < arg.length; i++) {
			new NotNull(getArgName()[i]).enforce(arg[i]);
			Invariant.assertArg(arg[i].getRoot() != null, getArgName()[i] + " is empty!");
		}
	}

}
