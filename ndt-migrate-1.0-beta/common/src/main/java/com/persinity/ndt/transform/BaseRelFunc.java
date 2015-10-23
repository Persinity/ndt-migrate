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

import static com.persinity.common.StringUtils.format;

import com.google.common.base.Function;
import com.persinity.common.invariant.NotEmpty;

/**
 * Represents a relational transformation carried through a SQL statement.
 *
 * @author Doichin Yordanov
 */
public abstract class BaseRelFunc<F, T> implements Function<F, T> {
    private final String sql;
    private String toString;

    /**
     * @param sql
     *         to use for the transformation
     */
    public BaseRelFunc(final String sql) {
        new NotEmpty("sql").enforce(sql);
        this.sql = sql;
    }

    @Override
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof BaseRelFunc)) {
            return false;
        }
        @SuppressWarnings("unchecked")
        final BaseRelFunc<F, T> that = (BaseRelFunc<F, T>) obj;
        return sql.equals(that.sql);
    }

    @Override
    public int hashCode() {
        return sql.hashCode();
    }

    @Override
    public String toString() {
        if (toString == null) {
            toString = format("{}({})", this.getClass().getSimpleName(), sql);
        }
        return toString;
    }

    public final String getSql() {
        return sql;
    }

}
