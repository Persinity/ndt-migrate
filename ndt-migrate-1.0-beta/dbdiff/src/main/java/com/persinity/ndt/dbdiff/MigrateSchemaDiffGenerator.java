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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import com.google.common.collect.Sets;
import com.persinity.common.collection.DirectedEdge;
import com.persinity.common.db.metainfo.Col;
import com.persinity.common.db.metainfo.constraint.PK;
import com.persinity.ndt.dbagent.relational.AgentSqlStrategy;
import com.persinity.ndt.dbagent.relational.SchemaInfo;

/**
 * Implement simple migrate of all source tables to destination.
 *
 * @author Ivan Dachev
 */
public class MigrateSchemaDiffGenerator implements SchemaDiffGenerator {
    @Override
    public Collection<TransformEntity> generateDiff(DirectedEdge<SchemaInfo, SchemaInfo> dbSources,
            AgentSqlStrategy sqlStrategy) {
        ArrayList<TransformEntity> transformEntities = new ArrayList<>();
        final Set<String> destinationEntities = dbSources.dst().getTableNames();

        final List<String> sourceEntities = new ArrayList<>();
        sourceEntities.addAll(dbSources.src().getTableNames());
        Collections.sort(sourceEntities);

        final HashSet<String> diffSet = Sets.newHashSet(destinationEntities);
        diffSet.removeAll(sourceEntities);
        if (diffSet.size() > 0) {
            throw new RuntimeException(
                    format("Could not find destination table: \"{}\" at source DB", diffSet.iterator().next()));
        }

        for (String sourceEntity : sourceEntities) {
            if (!destinationEntities.contains(sourceEntity)) {
                throw new RuntimeException(
                        format("Could not find source table: \"{}\" at destination DB", sourceEntity));
            }
            final PK pk = dbSources.src().getTablePk(sourceEntity);
            if (pk == null) {
                throw new RuntimeException(format("Expected primary key for source table: \"{}\"", sourceEntity));
            }
            final LinkedHashSet<String> sourceLeadingColumns = new LinkedHashSet<>();
            for (Col col : pk.getColumns()) {
                sourceLeadingColumns.add(col.getName());
            }
            transformEntities
                    .add(new TransformEntity(sourceEntity, sqlStrategy.selectAllStatement(sourceEntity), sourceEntity,
                            sourceLeadingColumns));
        }

        return transformEntities;
    }
}
