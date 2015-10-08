/**
 * Copyright (c) 2015 Persinity Inc.
 */
package com.persinity.common.db.metainfo;

import static com.persinity.common.StringUtils.format;
import static com.persinity.common.invariant.Invariant.notEmpty;
import static com.persinity.common.invariant.Invariant.notNull;

/**
 * @author Ivan Dachev
 */
public class Equal implements SqlPredicate {

    public Equal(final SqlPredicate left, final SqlPredicate right) {
        notNull(left);
        notNull(right);
        notEmpty(left.toString());
        notEmpty(right.toString());

        sql = format("{} = {}", left, right);
    }

    @Override
    public String toString() {
        return sql;
    }

    private final String sql;
}
