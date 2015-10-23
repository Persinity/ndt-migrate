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
package com.persinity.ndt.etlmodule.relational.migrate;

import com.persinity.common.collection.DirectedEdge;
import com.persinity.common.collection.Pool;
import com.persinity.common.db.RelDb;
import com.persinity.ndt.dbagent.relational.AgentSqlStrategy;
import com.persinity.ndt.etlmodule.WindowGenerator;
import com.persinity.ndt.etlmodule.relational.common.BaseWindowGenerator;
import com.persinity.ndt.etlmodule.relational.common.PullFromDbTidsLeftCntF;

/**
 * {@link WindowGenerator} for migrations of data between relational DBs.
 *
 * @author Doichin Yordanov
 */
public class MigrateWindowGenerator extends BaseWindowGenerator implements WindowGenerator<RelDb, RelDb> {

    /**
     * @param dataPoolBridge
     * @param sqlStrategy
     * @param windowSize
     */
    public MigrateWindowGenerator(final DirectedEdge<Pool<RelDb>, Pool<RelDb>> dataPoolBridge,
            final AgentSqlStrategy sqlStrategy, final int windowSize) {
        super(dataPoolBridge, new PullFromDbTidsLeftCntF(sqlStrategy), new RepeaterEntityDagFunc(), sqlStrategy,
                windowSize);
    }

}
