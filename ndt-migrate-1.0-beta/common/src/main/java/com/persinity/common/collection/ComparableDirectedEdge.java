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
package com.persinity.common.collection;

import com.persinity.common.invariant.Invariant;

/**
 * {@link WeightedDirectedEdge} with comparable weight
 *
 * @author Doichin Yordanov
 */
public class ComparableDirectedEdge<S, W extends Comparable<? super W>, D> extends WeightedDirectedEdge<S, W, D>
        implements Comparable<ComparableDirectedEdge<S, W, D>> {

    private static final int THIS_IS_GREATER_THAN_THAT = 1;

    public ComparableDirectedEdge(final DirectedEdge<S, D> e, final W weight) {
        this(e.src(), weight, e.dst());
    }

    public ComparableDirectedEdge(final S src, final W weight, final D dst) {
        super(src, weight, dst);
        Invariant.assertArg(weight != null, "weight");
    }

    @Override
    public int compareTo(final ComparableDirectedEdge<S, W, D> that) {
        if (that == null) {
            return THIS_IS_GREATER_THAN_THAT;
        }
        return weight().compareTo(that.weight());
    }

}
