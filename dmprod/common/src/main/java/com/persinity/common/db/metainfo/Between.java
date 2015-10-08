/**
 * Copyright (c) 2015 Persinity Inc.
 */
package com.persinity.common.db.metainfo;

import static com.persinity.common.StringUtils.format;

import com.persinity.common.collection.DirectedEdge;
import com.persinity.common.invariant.NotNull;

/**
 * {@link SqlFilter} with BETWEEN clause, e.g. col BETWEEN (val1 AND val2)
 *
 * @author Doichin Yordanov
 */
public class Between implements SqlFilter<DirectedEdge<?, ?>> {

    private final DirectedEdge<?, ?> vals;
    private final Col col;
    private final String sql;

    public Between(final Col col, final DirectedEdge<?, ?> vals) {
        new NotNull("col", "vals").enforce(col, vals);
        this.col = col;
        this.vals = vals;
        sql = format("{} BETWEEN {} AND {}", col, vals.src(), vals.dst());
    }

    @Override
    public Col getCol() {
        return col;
    }

    @Override
    public DirectedEdge<?, ?> getValue() {
        return vals;
    }

    @Override
    public String toString() {
        return sql;
    }

}
