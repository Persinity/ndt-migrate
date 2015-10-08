/**
 * Copyright (c) 2015 Persinity Inc.
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
            final AgentSqlStrategy sqlStrategy,
            final int windowSize) {
        super(dataPoolBridge, new PullFromDbTidsLeftCntF(sqlStrategy), new RepeaterEntityDagFunc(), sqlStrategy,
                windowSize);
    }

}
