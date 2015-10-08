/**
 * Copyright (c) 2015 Persinity Inc.
 */
package com.persinity.common.db.metainfo;

import static com.persinity.common.StringUtils.format;
import static com.persinity.common.invariant.Invariant.notEmpty;

import java.util.List;

import com.persinity.common.collection.CollectionUtils;

/**
 * @author Doichin Yordanov
 */
public class And implements SqlPredicate {

    private final String sql;

    public And(final List<? extends SqlPredicate> predicates) {
        notEmpty(predicates);
        if (predicates.size() == 1) {
            sql = predicates.get(0).toString();
        } else {
            sql = format("({})", CollectionUtils.implode(predicates, ") AND ("));
        }
    }

    @Override
    public String toString() {
        return sql;
    }
}
