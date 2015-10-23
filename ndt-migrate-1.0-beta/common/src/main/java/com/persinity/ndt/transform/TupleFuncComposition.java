/*
 * Copyright 2015 Persinity Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
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
