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

import static com.persinity.common.StringUtils.format;
import static com.persinity.common.invariant.Invariant.notEmpty;
import static com.persinity.common.invariant.Invariant.notNull;

import java.util.List;
import java.util.Map;
import java.util.Objects;

import com.persinity.common.MathUtil;
import com.persinity.common.StringUtils;
import com.persinity.common.collection.DirectedEdge;
import com.persinity.common.db.RelDb;
import com.persinity.common.db.metainfo.Col;
import com.persinity.common.db.metainfo.SqlFilter;
import com.persinity.ndt.dbagent.relational.AgentSqlStrategy;
import com.persinity.ndt.etlmodule.relational.Partitioner;

/**
 * Creates uniform k-1 partitions, each with equal size, except the last k-th partition which contains the tail <= size.
 *
 * @author Doichin Yordanov
 */
public class SizePartitioner implements Partitioner {

    static final String MIN_KEY_ALIAS = "min_key";
    static final String MAX_KEY_ALIAS = "max_key";

    public SizePartitioner(final int size, final AgentSqlStrategy sqlStrategy) {
        assert size >= 0 && sqlStrategy != null;

        this.size = size;
        this.sqlStrategy = sqlStrategy;
    }

    @Override
    public PartitionData partition(final RelDb db, final String table, final List<Col> keyCols,
            final SqlFilter<?> filter) {
        notNull(db);
        notEmpty(table);
        notEmpty(keyCols);
        notNull(filter);

        final String modValue = sqlStrategy.hash(keyCols);
        final int distinctCount = db
                .getInt(format("SELECT {} FROM {} WHERE {}", sqlStrategy.count(sqlStrategy.distinct(modValue)), table,
                        filter));

        final int modBase = distinctCount + 1;
        final String modKey = sqlStrategy.mod(modValue, "" + modBase);

        final String sql = format(QRY_TEMPLATE, sqlStrategy.max(modKey), sqlStrategy.min(modKey), table, filter);

        final Map<String, Object> rec = db.executeQuery(sql).next();

        final int minKey = ((Number) rec.get(MIN_KEY_ALIAS)).intValue();
        final int maxKey = ((Number) rec.get(MAX_KEY_ALIAS)).intValue();

        final List<DirectedEdge<Integer, Integer>> partition = MathUtil.partition(minKey, maxKey, size);

        return new PartitionData(partition, modBase);
    }

    @Override
    public boolean equals(final Object obj) {
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof SizePartitioner)) {
            return false;
        }
        final SizePartitioner that = (SizePartitioner) obj;
        return size == that.size && sqlStrategy.equals(that.sqlStrategy);
    }

    @Override
    public int hashCode() {
        return Objects.hash(size, sqlStrategy);
    }

    @Override
    public String toString() {
        if (toString == null) {
            toString = StringUtils.format("{}({})", this.getClass().getSimpleName(), size);
        }
        return toString;
    }

    private final int size;
    private final AgentSqlStrategy sqlStrategy;
    private String toString;
    private static final String QRY_TEMPLATE =
            "SELECT {} AS " + MAX_KEY_ALIAS + ", {} AS " + MIN_KEY_ALIAS + " FROM {} WHERE {}";
}
