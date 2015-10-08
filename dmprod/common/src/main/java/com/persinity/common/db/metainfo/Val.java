/**
 * Copyright (c) 2015 Persinity Inc.
 */
package com.persinity.common.db.metainfo;

import static com.persinity.common.invariant.Invariant.notEmpty;

/**
 * @author Ivan Dachev
 */
public class Val implements SqlPredicate {

    public Val(final String sql) {
        notEmpty(sql);
        this.sql = sql;
    }

    @Override
    public String toString() {
        return sql;
    }

    private final String sql;
}
