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

import static com.persinity.common.StringUtils.formatObj;
import static com.persinity.common.invariant.Invariant.notEmpty;
import static com.persinity.common.invariant.Invariant.notNull;

import java.util.List;
import java.util.Objects;

import com.persinity.common.StringUtils;
import com.persinity.common.collection.CollectionUtils;
import com.persinity.common.collection.DirectedEdge;
import com.persinity.common.db.RelDb;
import com.persinity.ndt.db.TransactionId;
import com.persinity.ndt.dbagent.relational.AgentSqlStrategy;
import com.persinity.ndt.dbagent.relational.SchemaInfo;
import com.persinity.ndt.transform.TransferFunc;

/**
 * Relational {@link TransferFunc} implementation.
 *
 * @author Ivan Dachev
 */
public abstract class RelTransferFunc implements TransferFunc<RelDb, RelDb> {

    public RelTransferFunc(final List<? extends TransactionId> tids, DirectedEdge<SchemaInfo, SchemaInfo> schemas,
            final AgentSqlStrategy sqlStrategy) {
        notEmpty(tids);
        notNull(schemas);
        notNull(sqlStrategy);

        this.tids = CollectionUtils.stringListOf(tids);
        this.schemas = schemas;
        this.sqlStrategy = sqlStrategy;
    }

    public List<String> getTids() {
        return tids;
    }

    public DirectedEdge<SchemaInfo, SchemaInfo> getSchemas() {
        return schemas;
    }

    public AgentSqlStrategy getSqlStrategy() {
        return sqlStrategy;
    }

    @Override
    public boolean equals(Object object) {

        if (this == object) {
            return true;
        }
        if (!(object instanceof RelTransferFunc)) {
            return false;
        }
        RelTransferFunc that = (RelTransferFunc) object;

        return Objects.equals(getClass(), that.getClass()) && Objects.equals(getTids(), that.getTids());
    }

    @Override
    public int hashCode() {
        if (hashCode == null) {
            hashCode = Objects.hash(getClass(), getTids());
        }
        return hashCode;
    }

    @Override
    public String toString() {
        if (toString == null) {
            toString = StringUtils.format("{}({})", formatObj(this), getTids());
        }
        return toString;
    }

    private final List<String> tids;
    private final DirectedEdge<SchemaInfo, SchemaInfo> schemas;
    private final AgentSqlStrategy sqlStrategy;

    private Integer hashCode;
    private String toString;

}
