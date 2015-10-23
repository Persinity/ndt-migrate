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

package com.persinity.common;

import static com.persinity.common.StringUtils.format;
import static com.persinity.common.StringUtils.formatObj;

import java.util.Objects;

/**
 * @author Ivo Yanakiev
 */
public abstract class PairBase<F, S> {

    public PairBase(final F first, final S second) {
        this.first = first;
        this.second = second;
    }

    @Override
    public boolean equals(final Object object) {
        if (this == object) {
            return true;
        }

        if (!(object instanceof PairBase)) {
            return false;
        }

        PairBase<?, ?> other = (PairBase<?, ?>) object;

        return Objects.equals(this.first, other.first) && Objects.equals(this.second, other.second);
    }

    @Override
    public int hashCode() {
        if (hashCode == null) {
            hashCode = Objects.hash(first, second);
        }
        return hashCode;
    }

    @Override
    public String toString() {
        if (toString == null) {
            toString = format("{}({} : {})", formatObj(this), first, second);
        }
        return toString;
    }

    protected final F first;
    protected final S second;

    private Integer hashCode;
    private String toString;
}
