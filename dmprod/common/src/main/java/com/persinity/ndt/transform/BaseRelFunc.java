/**
 * Copyright (c) 2015 Persinity Inc.
 */
package com.persinity.ndt.transform;

import com.google.common.base.Function;
import com.persinity.common.invariant.NotEmpty;

import static com.persinity.common.StringUtils.format;

/**
 * Represents a relational transformation carried through a SQL statement.
 * 
 * @author Doichin Yordanov
 */
public abstract class BaseRelFunc<F, T> implements Function<F, T> {
	private final String sql;
	private String toString;

	/**
	 * @param sql
	 *            to use for the transformation
	 */
	public BaseRelFunc(final String sql) {
		new NotEmpty("sql").enforce(sql);
		this.sql = sql;
	}

	@Override
	public boolean equals(final Object obj) {
		if (this == obj) {
			return true;
		}
		if (!(obj instanceof BaseRelFunc)) {
			return false;
		}
		@SuppressWarnings("unchecked")
		final BaseRelFunc<F, T> that = (BaseRelFunc<F, T>) obj;
		return sql.equals(that.sql);
	}

	@Override
	public int hashCode() {
		return sql.hashCode();
	}

	@Override
	public String toString() {
		if (toString == null) {
			toString = format("{}({})", this.getClass().getSimpleName(), sql);
		}
		return toString;
	}

	public final String getSql() {
		return sql;
	}

}
