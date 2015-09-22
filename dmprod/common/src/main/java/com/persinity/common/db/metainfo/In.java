/**
 * Copyright (c) 2015 Persinity Inc.
 */
package com.persinity.common.db.metainfo;

import static com.persinity.common.StringUtils.format;

import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import com.persinity.common.collection.CollectionUtils;
import com.persinity.common.invariant.Invariant;
import com.persinity.common.invariant.NotNull;

/**
 * {@link SqlFilter} with IN clause, e.g. col IN (val1, ..., valn)
 *
 * @author Doichin Yordanov
 */
public class In<T> implements SqlFilter<List<T>> {
    private final Col col;
    private final List<T> vals;
    private final String sql;
    private static final List<?> PARASITES = Arrays.asList((Object) null);

    @SuppressWarnings("unchecked")
    public In(final Col col, List<T> vals) {
        Invariant.assertArg(col != null, "");
        Invariant.assertArg(vals != null && !vals.isEmpty(), "");

        this.col = col;
        (vals = new LinkedList<T>(vals)).removeAll(PARASITES);
        if (vals.get(0) instanceof String) {
            vals = (List<T>) CollectionUtils.quote((List<String>) vals);
        }
        this.vals = vals;

        sql = format("{} IN ({})", col, CollectionUtils.implode(this.vals, ", "));
    }

    public In(final Col col, final Params params) {
        new NotNull("col").enforce(col);
        new NotNull("params").enforce(params);
        this.col = col;
        vals = Collections.emptyList();
        sql = format("{} IN ({})", col, params);
    }

    @Override
    public Col getCol() {
        return col;
    }

    @Override
    public List<T> getValue() {
        return vals;
    }

    @Override
    public String toString() {
        return sql;
    }

}
