/**
 * Copyright (c) 2015 Persinity Inc.
 */
package com.persinity.ndt.transform;

import java.util.Iterator;
import java.util.Map;

import com.persinity.common.db.RelDb;

/**
 * Represents query function.
 * 
 * @author Doichin Yordanov
 */
public class QueryFunc extends BaseRelFunc<RelDb, Iterator<Map<String, Object>>> {

	/**
	 * @param sql
	 */
	public QueryFunc(final String sql) {
		super(sql);
	}

	@Override
	public Iterator<Map<String, Object>> apply(final RelDb input) {
		return input.executeQuery(getSql());
	}

}
