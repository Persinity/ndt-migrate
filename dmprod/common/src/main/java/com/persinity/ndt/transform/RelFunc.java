/**
 * Copyright (c) 2015 Persinity Inc.
 */
package com.persinity.ndt.transform;

import com.persinity.common.db.RelDb;

/**
 * Represents a relational transformation over given schema.<BR>
 * E.g.:<BR>
 * - Transforming data: "INSERT INTO y (collist) SELECT collist FROM x"<BR>
 * - Transforming schema: "ALTER TABLE y ADD COLUMN x..."<BR>
 * - etc.<BR>
 * 
 * @author Doichin Yordanov
 */
public class RelFunc extends BaseRelFunc<RelDb, RelDb> {

	public RelFunc(final String sql) {
		super(sql);
	}

	@Override
	public RelDb apply(final RelDb db) {
		db.executeDmdl(getSql());
		return db;
	}

}
