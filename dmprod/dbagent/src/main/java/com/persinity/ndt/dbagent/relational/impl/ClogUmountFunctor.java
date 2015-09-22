/**
 * Copyright (c) 2015 Persinity Inc.
 */
package com.persinity.ndt.dbagent.relational.impl;

import com.google.common.base.Function;
import com.persinity.common.db.RelDb;
import com.persinity.common.fp.Functor;
import com.persinity.ndt.dbagent.relational.AgentSqlStrategy;
import com.persinity.ndt.dbagent.relational.SchemaInfo;
import com.persinity.ndt.transform.RelFunc;

/**
 * Functor that returns function, which drops CLOG data structures
 * 
 * @author Doichin Yordanov
 */
public class ClogUmountFunctor implements Functor<RelDb, RelDb, String, Function<RelDb, RelDb>> {

	public ClogUmountFunctor(final SchemaInfo schema, final AgentSqlStrategy strategy) {
		assert schema != null && strategy != null;
		this.schema = schema;
		this.strategy = strategy;
	}

	@Override
	public Function<RelDb, RelDb> apply(final String tableName) {
		final String sql = strategy.dropTable(schema.getClogTableName(tableName));
		final RelFunc clogFunc = new RelFunc(sql);
		return clogFunc;
	}

	private final SchemaInfo schema;
	private final AgentSqlStrategy strategy;
}
