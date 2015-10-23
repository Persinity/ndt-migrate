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
package com.persinity.ndt.etlmodule.relational;

import java.util.List;

import com.persinity.common.collection.DirectedEdge;
import com.persinity.common.db.RelDb;
import com.persinity.common.db.metainfo.Col;
import com.persinity.common.db.metainfo.SqlFilter;

/**
 * @author Doichin Yordanov
 */
public interface Partitioner {
    class PartitionData {
        public PartitionData(final List<DirectedEdge<Integer, Integer>> partition, final int modBase) {
            this.partition = partition;
            this.modBase = modBase;
        }

        public List<DirectedEdge<Integer, Integer>> getPartition() {
            return partition;
        }

        public int getModBase() {
            return modBase;
        }

        private final List<DirectedEdge<Integer, Integer>> partition;
        private final int modBase;
    }

    /**
     * Partitions a table's data by the supplied key column, including only the rows that satisfy the supplied filter.
     *
     * @param db
     * @param table
     * @param keyCols
     * @param filter
     * @return Ordered set of partition ranges over the key column.
     */
    PartitionData partition(RelDb db, String table, List<Col> keyCols, SqlFilter<?> filter);
}
