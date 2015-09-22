/**
 * Copyright (c) 2015 Persinity Inc.
 */
package com.persinity.common.transform;

import java.util.List;

import com.persinity.common.collection.DirectedEdge;
import com.persinity.common.db.RelDb;
import com.persinity.ndt.transform.BaseRelFunc;

/**
 * {@link BaseRelFunc} for testing purposes with faked {@link BaseRelFunc#apply(Object)} implementation.
 * 
 * @author Doichin Yordanov
 */
public class BaseRelFuncFake extends BaseRelFunc<DirectedEdge<RelDb, List<?>>, Integer> {

	/**
	 * @param sql
	 */
	public BaseRelFuncFake(final String sql) {
		super(sql);
	}

	@Override
	public Integer apply(final DirectedEdge<RelDb, List<?>> input) {
		return 1;
	}

}
