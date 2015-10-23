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
package com.persinity.ndt.etlmodule.relational.common;

import static com.persinity.common.invariant.Invariant.assertArg;
import static com.persinity.common.invariant.Invariant.notNull;

import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

import com.persinity.common.StringUtils;
import com.persinity.common.collection.DirectedEdge;
import com.persinity.ndt.db.TransactionId;
import com.persinity.ndt.dbagent.relational.AgentSqlStrategy;
import com.persinity.ndt.dbagent.relational.SchemaInfo;

/**
 * Extends {@link RelTransferFunc} with ID range implementation.
 *
 * @author Ivan Dachev
 */
public abstract class IdRangeRelTransferFunc extends RelTransferFunc {

    /**
     * @param rangeModBase
     *         the mod base used to calculate ranges
     * @param idRange
     *         the range of ID values or their hash codes of the records
     * @param tids
     *         window TIDs, padded with last TID to fit power of two nums for stmt caching
     * @param schemas
     * @param sqlStrategy
     */
    public IdRangeRelTransferFunc(final int rangeModBase, final DirectedEdge<Integer, Integer> idRange,
            final List<? extends TransactionId> tids, final DirectedEdge<SchemaInfo, SchemaInfo> schemas,
            final AgentSqlStrategy sqlStrategy) {
        super(tids, schemas, sqlStrategy);

        assertArg(rangeModBase > 0, "rangeModBase should be positive");
        notNull(idRange);

        this.rangeModBase = rangeModBase;
        this.idRange = idRange;

    }

    /**
     * @return mod base used to calculate ranges
     */
    public int getRangeModBase() {
        return rangeModBase;
    }

    /**
     * @return The range of ID values or their hash codes of the records to be ETLed from the leading source entity by
     * {@code this} function.
     */
    public DirectedEdge<Integer, Integer> getIdRange() {
        return idRange;
    }

    /**
     * @return prepared params for the range
     */
    protected List<Object> prepareParams() {

        final List<Object> params = new LinkedList<>();
        params.add(rangeModBase);
        params.add(idRange.src());
        params.add(idRange.dst());
        params.addAll(getTids());

        return params;
    }

    @Override
    public boolean equals(Object object) {

        if (this == object) {
            return true;
        }
        if (!(object instanceof IdRangeRelTransferFunc)) {
            return false;
        }
        IdRangeRelTransferFunc that = (IdRangeRelTransferFunc) object;

        return super.equals(that) &&
                Objects.equals(getIdRange(), that.getIdRange()) &&
                Objects.equals(getRangeModBase(), that.getRangeModBase());
    }

    @Override
    public int hashCode() {
        if (hashCode == null) {
            hashCode = Objects.hash(super.hashCode(), getIdRange(), getRangeModBase());
        }
        return hashCode;
    }

    @Override
    public String toString() {
        if (toString == null) {
            toString = StringUtils.format("{} [{} / {}]", super.toString(), getIdRange(), getRangeModBase());
        }
        return toString;
    }

    private final int rangeModBase;

    private final DirectedEdge<Integer, Integer> idRange;

    private Integer hashCode;
    private String toString;

}
