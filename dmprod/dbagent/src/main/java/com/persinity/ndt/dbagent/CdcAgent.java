/**
 * Copyright (c) 2015 Persinity Inc.
 */
package com.persinity.ndt.dbagent;

import com.google.common.base.Function;
import com.persinity.common.collection.Tree;

/**
 * Change data capture database agent.
 * 
 * @author Doichin Yordanov
 */
public interface CdcAgent<T extends Function<?, ?>> {

	/**
	 * Generates the plan for the database CDC logic creation
	 */
	Tree<T> mountCdc();

	/**
	 * Generates the plan for the database CDC logic removal
	 */
	Tree<T> umountCdc();

}