/**
 * Copyright (c) 2015 Persinity Inc.
 */
package com.persinity.ndt.etlmodule.relational.transform;

import com.persinity.common.collection.DirectedEdge;
import com.persinity.common.collection.Pool;
import com.persinity.common.db.RelDb;
import com.persinity.ndt.dbagent.relational.AgentSqlStrategy;
import com.persinity.ndt.etlmodule.WindowGenerator;
import com.persinity.ndt.etlmodule.relational.common.BaseWindowGenerator;
import com.persinity.ndt.etlmodule.relational.common.PullFromDbTidsLeftCntF;
import com.persinity.ndt.transform.EntitiesDag;

/**
 * {@link WindowGenerator} for transformations of data between staging area and consistent target schema.
 *
 * @author Doichin Yordanov
 */
public class TransformWindowGenerator extends BaseWindowGenerator implements WindowGenerator<RelDb, RelDb> {

    public TransformWindowGenerator(final DirectedEdge<Pool<RelDb>, Pool<RelDb>> dataPoolBridge, final EntitiesDag dag,
            final AgentSqlStrategy sqlStrategy, final int windowSize) {
        super(dataPoolBridge, new PullFromDbTidsLeftCntF(sqlStrategy), new RelationStrictEntityDagFunc(dag),
                sqlStrategy,
                windowSize);
    }
}
