/**
 * Copyright (c) 2015 Persinity Inc.
 */
package com.persinity.common.collection;

import com.google.common.collect.TreeTraverser;

/**
 * Rooted tree.
 * 
 * @author Doichin Yordanov
 */
public abstract class Tree<T> extends TreeTraverser<T> {
	/**
	 * @return The root of the tree or {@code null} if the tree is empty.
	 */
	public abstract T getRoot();
}
