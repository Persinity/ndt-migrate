/**
 * Copyright (c) 2015 Persinity Inc.
 */
package com.persinity.common.collection;

import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * General Iterator with preview abilities.
 *
 * @author Ivan Dachev
 */
public class PreviewIterator<T> implements Iterator<T> {
	public PreviewIterator(Iterator<T> iter) {
		this.iter = iter;
	}

	@Override
	public boolean hasNext() {
		return preview != null || iter.hasNext();
	}

	@Override
	public T next() {
		T res = preview();
		preview = null;
		return res;
	}

	@Override
	public void remove() {
		throw new UnsupportedOperationException();
	}

	/**
	 * @return a preview of the next element in the iteration
	 * @throws NoSuchElementException if the iteration has no more elements
	 */
	public T preview() {
		if (preview == null && iter.hasNext()) {
			preview = iter.next();
		}
		if (preview == null) {
			throw new NoSuchElementException();
		}
		return preview;
	}

	private final Iterator<T> iter;
	private T preview;
}
