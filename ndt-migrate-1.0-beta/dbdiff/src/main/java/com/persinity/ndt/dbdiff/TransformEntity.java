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
package com.persinity.ndt.dbdiff;

import static com.persinity.common.StringUtils.format;
import static com.persinity.common.invariant.Invariant.notEmpty;

import java.util.Set;

import com.google.common.base.Objects;

/**
 * Describe one transform entity.
 *
 * @author Ivan Dachev
 */
public class TransformEntity {
    public TransformEntity(String targetEntity, String transformStatement, String sourceLeadingEntity,
            Set<String> sourceLeadingColumns) {
        notEmpty(targetEntity);
        notEmpty(transformStatement);
        notEmpty(sourceLeadingEntity);
        notEmpty(sourceLeadingColumns);
        this.targetEntity = targetEntity;
        this.transformStatement = transformStatement;
        this.sourceLeadingEntity = sourceLeadingEntity;
        this.sourceLeadingColumns = sourceLeadingColumns;
    }

    /**
     * @return Target entity to transform to.
     */
    public String getTargetEntity() {
        return targetEntity;
    }

    /**
     * @return Transform statement, should return a result set with columns with its target names
     */
    public String getTransformStatement() {
        return transformStatement;
    }

    /**
     * @return The leading source entity for which the transform will be applied
     */
    public String getSourceLeadingEntity() {
        return sourceLeadingEntity;
    }

    /**
     * @return The leading columns from the source transformation that must match the primary keys of the target.
     */
    public Set<String> getSourceLeadingColumns() {
        return sourceLeadingColumns;
    }

    @Override
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }

        if (!(obj instanceof TransformEntity)) {
            return false;
        }

        final TransformEntity that = (TransformEntity) obj;
        return getTargetEntity().equals(that.getTargetEntity()) && getTransformStatement()
                .equals(that.getTransformStatement()) && getSourceLeadingEntity().equals(that.getSourceLeadingEntity())
                && getSourceLeadingColumns().equals(that.getSourceLeadingColumns());
    }

    @Override
    public int hashCode() {
        if (hashCode == null) {
            hashCode = Objects.hashCode(getTargetEntity(), getTransformStatement(), getSourceLeadingEntity(),
                    getSourceLeadingColumns());
        }
        return hashCode;
    }

    @Override
    public String toString() {
        if (toString == null) {
            toString = format("{}@{}({}, {}, {}, {})", this.getClass().getSimpleName(), Integer.toHexString(hashCode()),
                    getTargetEntity(), getTransformStatement(), getSourceLeadingEntity(), getSourceLeadingColumns());
        }
        return toString;
    }

    private final String targetEntity;
    private final String transformStatement;
    private final String sourceLeadingEntity;
    private final Set<String> sourceLeadingColumns;

    private Integer hashCode;
    private String toString;
}
