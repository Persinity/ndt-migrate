/**
 * Copyright (c) 2015 Persinity Inc.
 */

package com.persinity.ndt.transform;

import static com.persinity.common.invariant.Invariant.notNull;

import java.util.Iterator;
import java.util.Map;

/**
 * Composes g and f typle funcs, so that result = g(f(tuples))
 *
 * @author dyordanov
 */
public class TupleFuncComposition implements TupleFunc {
    public TupleFuncComposition(final TupleFunc g, final TupleFunc f) {
        notNull(g);
        notNull(f);
        this.g = g;
        this.f = f;
    }

    @Override
    public Iterator<Map<String, Object>> apply(final Iterator<Map<String, Object>> input) {
        return g.apply(f.apply(input));
    }

    private final TupleFunc g;
    private final TupleFunc f;
}
