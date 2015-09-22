/**
 * Copyright (c) 2015 Persinity Inc.
 */
package com.persinity.ndt.transform;

import com.google.common.base.Function;
import com.persinity.common.collection.DirectedEdge;
import com.persinity.common.collection.Tree;

/**
 * {@link PlanProcessor} that executes plan, function by function in sequential fashion.<BR>
 * TODO add Haka processor for parallel processing
 *
 * @author Doichin Yordanov
 */
public class SerialPlanProcessor<F, T> implements PlanProcessor<F, T, Function<F, T>> {
    @Override
    public void process(final Tree<Function<F, T>> plan, final F arg) {
        process(plan, arg, null);
    }

    @Override
    public void process(final Tree<Function<F, T>> plan, final F arg,
            final Function<DirectedEdge<F, RuntimeException>, Void> exceptionHandler) {
        final Function<F, T> root = plan.getRoot();
        if (root != null) {
            for (final Function<F, T> f : plan.breadthFirstTraversal(root)) {
                try {
                    f.apply(arg);
                } catch (RuntimeException e) {
                    if (exceptionHandler != null) {
                        exceptionHandler.apply(new DirectedEdge<>(arg, e));
                    } else {
                        throw e;
                    }
                }
            }
        }
    }

}
