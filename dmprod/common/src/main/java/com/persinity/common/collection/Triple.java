/**
 * Copyright (c) 2015 Persinity Inc.
 */
package com.persinity.common.collection;

/**
 * Simple triple no need for hashcode and equals.
 *
 * @author Ivan Dachev
 */
public class Triple<T1, T2, T3> {
	public Triple(final T1 first, final T2 second, final T3 third) {
		this.first = first;
		this.second = second;
		this.third = third;
	}

	public T1 getFirst() {
		return first;
	}

	public T2 getSecond() {
		return second;
	}

	public T3 getThird() {
		return third;
	}

	private final T1 first;
	private final T2 second;
	private final T3 third;
}
