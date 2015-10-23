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
