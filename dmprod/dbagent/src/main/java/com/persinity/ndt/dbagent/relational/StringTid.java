/**
 * Copyright (c) 2015 Persinity Inc.
 */
package com.persinity.ndt.dbagent.relational;

import com.persinity.common.invariant.NotEmpty;
import com.persinity.ndt.db.TransactionId;

/**
 * @author Doichin Yordanov
 * 
 */
public class StringTid implements TransactionId {

	private final String tidValue;

	public StringTid(final String tidValue) {
		new NotEmpty("tidValue").enforce(tidValue);
		this.tidValue = tidValue;
	}

	@Override
	public boolean equals(final Object arg0) {
		if (this == arg0) {
			return true;
		}
		if (!(arg0 instanceof StringTid)) {
			return false;
		}
		final StringTid that = (StringTid) arg0;
		return tidValue.equals(that.tidValue);
	}

	@Override
	public int hashCode() {
		return tidValue.hashCode();
	}

	@Override
	public String toString() {
		return tidValue;
	}

}
