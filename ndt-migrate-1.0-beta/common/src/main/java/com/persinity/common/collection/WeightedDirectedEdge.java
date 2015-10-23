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

import static com.persinity.common.StringUtils.format;

import com.persinity.common.invariant.Invariant;

/**
 * {@link DirectedEdge} with weight
 *
 * @author Doichin Yordanov
 */
public class WeightedDirectedEdge<S, W, D> extends DirectedEdge<S, D> {

    private static final int THIS_IS_GREATER_THAN_THAT = 1;
    private final W weight;
    private String toString;
    private Integer hashCode;

    public WeightedDirectedEdge(final DirectedEdge<S, D> e, final W weight) {
        this(e.src(), weight, e.dst());
    }

    public WeightedDirectedEdge(final S src, final W weight, final D dst) {
        super(src, dst);
        Invariant.assertArg(weight != null, "weight");
        this.weight = weight;
    }

    public W weight() {
        return weight;
    }

    @Override
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }
        if (!super.equals(obj) || !(obj instanceof WeightedDirectedEdge)) {
            return false;
        }
        @SuppressWarnings("unchecked")
        final WeightedDirectedEdge<S, W, D> that = (WeightedDirectedEdge<S, W, D>) obj;
        return this.weight.equals(that.weight());
    }

    @Override
    public int hashCode() {
        if (hashCode == null) {
            hashCode = 31 * super.hashCode() + weight().hashCode();
        }
        return hashCode;
    }

    @Override
    public String toString() {
        if (toString == null) {
            toString = format("{}-{}->{}", src(), weight(), dst());
        }
        return toString;
    }

}
