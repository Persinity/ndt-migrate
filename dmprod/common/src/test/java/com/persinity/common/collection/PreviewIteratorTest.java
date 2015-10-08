/**
 * Copyright (c) 2015 Persinity Inc.
 */
package com.persinity.common.collection;

import org.junit.Before;
import org.junit.Test;

import java.util.*;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.*;

/**
 * @author Ivan Dachev
 */
public class PreviewIteratorTest {

	@Before
	public void setUp() {
		iter = Arrays.asList(1, 2, 3).iterator();
	}

	@Test
	public void test() throws Exception {
		PreviewIterator<Integer> previewIter = new PreviewIterator<>(iter);
		int value;

		assertTrue(previewIter.hasNext());
		value = previewIter.preview();
		assertThat(value, is(1));
		value = previewIter.next();
		assertThat(value, is(1));

		assertTrue(previewIter.hasNext());
		value = previewIter.preview();
		assertThat(value, is(2));
		value = previewIter.next();
		assertThat(value, is(2));

		assertTrue(previewIter.hasNext());
		value = previewIter.preview();
		assertThat(value, is(3));
		value = previewIter.next();
		assertThat(value, is(3));

		assertFalse(previewIter.hasNext());
	}

	@Test
	public void testEmptyHasNext() throws Exception {
		PreviewIterator previewIter = new PreviewIterator<>(Collections.emptyList().iterator());
		assertFalse(previewIter.hasNext());
	}

	@Test(expected = UnsupportedOperationException.class)
	public void testRemove() throws Exception {
		PreviewIterator previewIter = new PreviewIterator<>(Collections.emptyList().iterator());
		previewIter.remove();
	}

	@Test(expected = NoSuchElementException.class)
	public void testEmptyNext() throws Exception {
		PreviewIterator previewIter = new PreviewIterator<>(Collections.emptyList().iterator());
		previewIter.next();
	}

	@Test(expected = NoSuchElementException.class)
	public void testEmptyPreview() throws Exception {
		PreviewIterator previewIter = new PreviewIterator<>(Collections.emptyList().iterator());
		previewIter.preview();
	}

	private Iterator<Integer> iter;
}