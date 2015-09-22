/**
 * Copyright (c) 2015 Persinity Inc.
 */
package com.persinity.common.logging;

/**
 * @author Ivan Dachev
 */
public interface ContextProvider {
	/**
	 * @param sb used to append context
	 */
	void appendContext(StringBuilder sb);
}
