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

import static com.persinity.common.invariant.Invariant.notNull;

import java.util.Collection;

import com.persinity.common.collection.DirectedEdge;
import com.persinity.ndt.dbagent.relational.AgentSqlStrategy;
import com.persinity.ndt.dbagent.relational.SchemaInfo;

/**
 * @author dyordanov
 */
public class BufferedSchemaDiffGenerator implements SchemaDiffGenerator {

    public BufferedSchemaDiffGenerator(final SchemaDiffGenerator delegate) {
        notNull(delegate);
        this.delegate = delegate;
    }

    @Override
    public synchronized Collection<TransformEntity> generateDiff(final DirectedEdge<SchemaInfo, SchemaInfo> dbSources,
            final AgentSqlStrategy sqlStrategy) {
        if (cache == null) {
            cache = delegate.generateDiff(dbSources, sqlStrategy);
        }
        return cache;
    }

    private Collection<TransformEntity> cache;
    private final SchemaDiffGenerator delegate;

}
