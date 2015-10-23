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
