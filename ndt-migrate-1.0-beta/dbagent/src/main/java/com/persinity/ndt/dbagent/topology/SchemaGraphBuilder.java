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

package com.persinity.ndt.dbagent.topology;

import static com.persinity.common.invariant.Invariant.notNull;

import java.util.HashSet;
import java.util.Set;

import org.apache.log4j.Logger;

import com.persinity.common.db.metainfo.FKEdge;
import com.persinity.common.db.metainfo.constraint.FK;
import com.persinity.common.logging.Log4jLogger;
import com.persinity.ndt.dbagent.relational.SchemaInfo;

/**
 * @author Ivo Yanakiev
 */
public class SchemaGraphBuilder {

    public SchemaGraphBuilder(final SchemaInfo schemaInfo) {
        notNull(schemaInfo);

        this.schemaInfo = schemaInfo;
    }

    /**
     * @return topology {@link SchemaGraph}
     */
    public SchemaGraph buildTopology() {
        final SchemaGraph result = new SchemaGraph();

        for (final FK fk : getSchemaFks()) {
            final String srcTable = fk.getTable();
            final String dstTable = fk.getDstConstraint().getTable();
            final FKEdge edge = new FKEdge(srcTable, fk, dstTable);

            result.addVertex(srcTable);
            result.addVertex(dstTable);
            result.addEdge(srcTable, dstTable, edge);
        }

        for (final String table : schemaInfo.getTableNames()) {
            if (!result.containsVertex(table)) {
                result.addVertex(table);
            }
        }

        log.debug("SchemaGraph: {}", result);

        return result;
    }

    private Set<FK> getSchemaFks() {
        final Set<FK> result = new HashSet<>();

        for (final String table : schemaInfo.getTableNames()) {
            result.addAll(schemaInfo.getTableFks(table));
        }

        return result;
    }

    private final SchemaInfo schemaInfo;

    private static final Log4jLogger log = new Log4jLogger(Logger.getLogger(SchemaGraphBuilder.class));
}
