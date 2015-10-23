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

import java.util.Collection;

import com.persinity.common.collection.DirectedEdge;
import com.persinity.ndt.dbagent.relational.AgentSqlStrategy;
import com.persinity.ndt.dbagent.relational.SchemaInfo;

/**
 * Schema diff generator interface.
 *
 * @author Ivan Dachev
 */
public interface SchemaDiffGenerator {
    /**
     * Generates schema diff.
     *
     * @param dbSources
     *         schemas to be used
     * @param sqlStrategy
     *         AgentSqlStrategy to use
     */
    Collection<TransformEntity> generateDiff(DirectedEdge<SchemaInfo, SchemaInfo> dbSources,
            AgentSqlStrategy sqlStrategy);
}
