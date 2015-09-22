/**
 * Copyright (c) 2015 Persinity Inc.
 */
package com.persinity.ndt.transform;

import com.google.common.base.Function;
import com.persinity.common.collection.DirectedEdge;
import com.persinity.common.collection.Tree;

/**
 * Processes given work plan.
 *
 * @author Doichin Yordanov
 */
public interface PlanProcessor<F, T, G extends Function<F, T>> {
    /**
     * @param plan
     *         represented as hierarchy of functions
     * @param arg
     */
    void process(Tree<Function<F, T>> plan, F arg);

    /**
     * @param plan
     *         represented as hierarchy of functions
     * @param arg
     *         argument to call function from the plan
     * @param exceptionHandler
     *         exception handler can be null
     */
    void process(Tree<G> plan, F arg, Function<DirectedEdge<F, RuntimeException>, Void> exceptionHandler);
}
