/**
 * Copyright (c) 2015 Persinity Inc.
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
