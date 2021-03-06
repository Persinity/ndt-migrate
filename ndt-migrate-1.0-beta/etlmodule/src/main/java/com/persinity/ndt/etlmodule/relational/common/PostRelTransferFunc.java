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

import static com.persinity.common.collection.CollectionUtils.addPadded;

import java.util.ArrayList;
import java.util.List;

import com.persinity.common.MathUtil;
import com.persinity.common.collection.DirectedEdge;
import com.persinity.common.db.RelDb;
import com.persinity.common.metrics.MetricMeterFunc;
import com.persinity.ndt.db.TransactionId;
import com.persinity.ndt.dbagent.relational.AgentSqlStrategy;
import com.persinity.ndt.dbagent.relational.SchemaInfo;
import com.persinity.ndt.transform.ParamDmlFunc;

/**
 * Cleanup all records from trlog table at the end of TransferWindow.
 *
 * @author Ivan Dachev
 */
public class PostRelTransferFunc extends RelTransferFunc {

    public PostRelTransferFunc(final List<? extends TransactionId> tids,
            final DirectedEdge<SchemaInfo, SchemaInfo> schemas, final AgentSqlStrategy sqlStrategy) {
        super(tids, schemas, sqlStrategy);
        tpsMeter = new MetricMeterFunc(RelMetrics.METER_TRANSFORM_TPS);
    }

    @Override
    public Integer apply(final DirectedEdge<RelDb, RelDb> dataBridge) {
        final int tidsSize = MathUtil.ceilingByPowerOfTwo(getTids().size());
        final ParamDmlFunc cleanupF = createTrlogCleanupFunction(tidsSize);
        final List<String> params = new ArrayList<>();
        addPadded(params, getTids(), tidsSize);
        final DirectedEdge<RelDb, List<?>> dbToParams = new DirectedEdge<RelDb, List<?>>(dataBridge.src(), params);
        final Integer res = cleanupF.apply(dbToParams);

        dataBridge.src().commit();
        dataBridge.dst().commit();

        tpsMeter.apply(getTids().size());

        return res;
    }

    private ParamDmlFunc createTrlogCleanupFunction(int tidsSize) {
        final String cleanupSql = getSqlStrategy().trlogCleanupStatement(SchemaInfo.TAB_TRLOG, tidsSize);
        return new ParamDmlFunc(cleanupSql);
    }

    private final MetricMeterFunc tpsMeter;
}
