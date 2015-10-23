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
package com.persinity.ndt.etlmodule.relational.transform;

import static com.persinity.common.StringUtils.format;
import static com.persinity.common.invariant.Invariant.notEmpty;
import static com.persinity.common.invariant.Invariant.notNull;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import com.persinity.common.collection.DirectedEdge;
import com.persinity.ndt.dbagent.relational.AgentSqlStrategy;
import com.persinity.ndt.dbagent.relational.SchemaInfo;
import com.persinity.ndt.etlmodule.relational.Partitioner;
import com.persinity.ndt.etlmodule.relational.TransferFunctorFactory;
import com.persinity.ndt.etlmodule.relational.TransformInfo;

/**
 * Relational {@link TransferFunctorFactory} implementation.
 *
 * @author Ivan Dachev
 */
public abstract class TransformFunctorFactory implements TransferFunctorFactory {

    public TransformFunctorFactory(final Map<String, TransformInfo> etlMap, final Partitioner pidPartitioner,
            final DirectedEdge<SchemaInfo, SchemaInfo> schemas, final AgentSqlStrategy sqlStrategy) {
        this.etlMap = etlMap;
        this.pidPartitioner = pidPartitioner;
        this.schemas = schemas;
        this.sqlStrategy = sqlStrategy;

        dstToSrcMap = buildDstToSrcMap(etlMap);
    }

    @Override
    public String toString() {
        if (toString == null) {
            toString = format("{}{}", this.getClass().getSimpleName(), pidPartitioner.toString());
        }
        return toString;
    }

    protected boolean isDstEntityAffected(final String dstEntity, final Set<String> affectedSrcEntities) {
        notEmpty(dstEntity);
        notNull(affectedSrcEntities);

        final Set<String> srcEntities = dstToSrcMap.get(dstEntity);
        notEmpty(srcEntities);

        for (String srcEntity : srcEntities) {
            if (affectedSrcEntities.contains(srcEntity)) {
                return true;
            }
        }

        return false;
    }

    static Map<String, Set<String>> buildDstToSrcMap(final Map<String, TransformInfo> etlMap) {
        notNull(etlMap);

        final Map<String, Set<String>> res = new HashMap<>();

        for (TransformInfo info : etlMap.values()) {
            final String srcEntity = info.getEntityMapping().src();
            final String dstEntity = info.getEntityMapping().dst();
            Set<String> srcSet = res.get(dstEntity);
            if (srcSet == null) {
                srcSet = new HashSet<>();
                res.put(dstEntity, srcSet);
            }
            srcSet.add(srcEntity);
        }

        return res;
    }

    protected final Map<String, TransformInfo> etlMap;
    protected final Partitioner pidPartitioner;
    protected final DirectedEdge<SchemaInfo, SchemaInfo> schemas;
    protected final AgentSqlStrategy sqlStrategy;
    protected final Map<String, Set<String>> dstToSrcMap;

    private String toString;
}
