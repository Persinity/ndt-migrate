/**
 * Copyright (c) 2015 Persinity Inc.
 */
package com.persinity.haka.impl.actor;

/**
 * @author Ivan Dachev
 */
public interface ContextLoggingProvider {
	/**
	 * @param sb used to append context
	 */
	void appendContext(StringBuilder sb);
}
