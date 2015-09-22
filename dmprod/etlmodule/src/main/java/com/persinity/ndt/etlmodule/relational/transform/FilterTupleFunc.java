/**
 * Copyright (c) 2015 Persinity Inc.
 */

package com.persinity.ndt.etlmodule.relational.transform;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import com.persinity.common.db.metainfo.SqlFilter;
import com.persinity.common.invariant.Invariant;
import com.persinity.ndt.transform.TupleFunc;

/**
 * Filters out (excludes out) tuples that do not/match the supplied {@link SqlFilter}.<BR>
 * negate, eval(tuple) -> tuple in/out:<BR>
 * false, true -> out<BR>
 * false, false -> in<BR>
 * true, true -> in<BR>
 * true, false -> out<BR>
 *
 * @author dyordanov
 */
public class FilterTupleFunc implements TupleFunc {
    /**
     * @param filter
     * @param negate
     *         {@code false}: filters out tuples that match the filter; {@code true} ...that do not match the filter.
     */
    public FilterTupleFunc(final SqlFilter<?> filter, boolean negate) {
        Invariant.notNull(filter);
        this.filter = filter;
        this.negate = negate;
    }

    @Override
    public Iterator<Map<String, Object>> apply(final Iterator<Map<String, Object>> input) {
        if (input == null)
            return input;
        final List<Map<String, Object>> result = new LinkedList<>();
        while (input.hasNext()) {
            final Map<String, Object> tuple = input.next();
            final Object val = tuple.get(filter.getCol().getName());
            if (negate ^ filter.getValue().equals(val))
                continue;
            result.add(tuple);
        }
        return result.iterator();
    }

    private final SqlFilter<?> filter;
    private final boolean negate;
}
