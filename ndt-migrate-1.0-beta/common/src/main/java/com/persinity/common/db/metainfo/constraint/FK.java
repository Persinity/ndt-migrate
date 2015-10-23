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

package com.persinity.common.db.metainfo.constraint;

import static com.persinity.common.invariant.Invariant.notNull;

import java.util.Objects;
import java.util.Set;

import com.persinity.common.StringUtils;
import com.persinity.common.db.metainfo.Col;

/**
 * @author Ivo Yanakiev
 */
public class FK extends Constraint {

    public FK(final String name, final String table, final Set<Col> columns, final Unique dstConstraint) {
        super(name, table, columns);

        notNull(dstConstraint);

        constraint = dstConstraint;
        isWeakRef = isWeakRef(columns);
    }

    public Unique getDstConstraint() {
        return constraint;
    }

    public boolean isWeakRef() {
        return isWeakRef;
    }

    private boolean isWeakRef(final Set<Col> columns) {

        for (final Col col : columns) {

            // the reference is mandatory (not-null) if at least one column is mandatory (not-null)
            if (!col.isNullAllowed()) {
                return false;
            }
        }
        return true;
    }

    @Override
    public boolean equals(final Object object) {

        if (this == object) {
            return true;
        }
        if (!(object instanceof FK)) {
            return false;
        }

        final FK fk = (FK) object;

        return super.equals(fk) &&
                Objects.equals(getDstConstraint(), fk.getDstConstraint()) &&
                Objects.equals(isWeakRef(), fk.isWeakRef());
    }

    @Override
    public int hashCode() {
        if (hashCode == null) {
            hashCode = Objects.hash(super.hashCode(), getDstConstraint(), isWeakRef());
        }
        return hashCode;
    }

    @Override
    public String toString() {
        if (toString == null) {
            toString = StringUtils.format("{} {} -> {}", super.toString(), isWeakRef(), getDstConstraint());
        }
        return toString;
    }

    private final Unique constraint;
    private final boolean isWeakRef;

    private String toString;
    private Integer hashCode;
}
