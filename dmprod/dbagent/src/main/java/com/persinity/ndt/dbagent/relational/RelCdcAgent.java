/**
 * Copyright (c) 2015 Persinity Inc.
 */
package com.persinity.ndt.dbagent.relational;

import static com.persinity.common.invariant.Invariant.notEmpty;
import static com.persinity.common.invariant.Invariant.notNull;

import com.google.common.base.Function;
import com.persinity.common.collection.Tree;
import com.persinity.common.db.RelDb;
import com.persinity.ndt.dbagent.CdcAgent;
import com.persinity.ndt.dbagent.relational.impl.CdcMountFunctor;
import com.persinity.ndt.dbagent.relational.impl.CdcUmountFunctor;
import com.persinity.ndt.dbagent.relational.impl.PlanBuilder;

/**
 * {@link CdcAgent} implementation for relational databases with triggers.<BR>
 *
 * @author Doichin Yordanov
 */
public class RelCdcAgent implements CdcAgent<Function<RelDb, RelDb>> {

    /**
     * @param ndtUserName
     *         NDT user name for the source staging database
     * @param appSchemaInfo
     *         Meta information for the application database
     * @param ddlSql
     *         Database specific SQL strategy
     */
    public RelCdcAgent(final String ndtUserName, final SchemaInfo appSchemaInfo, final AgentSqlStrategy ddlSql) {
        notEmpty(ndtUserName);
        notNull(appSchemaInfo);
        notNull(ddlSql);

        this.ndtUserName = ndtUserName;
        this.appSchemaInfo = appSchemaInfo;
        this.ddlSql = ddlSql;
        pb = new PlanBuilder();
    }

    @Override
    public Tree<Function<RelDb, RelDb>> mountCdc() {
        return pb.build(appSchemaInfo.getTableNames(), new CdcMountFunctor(ndtUserName, appSchemaInfo, ddlSql));
    }

    @Override
    public Tree<Function<RelDb, RelDb>> umountCdc() {
        return pb.build(appSchemaInfo.getTableNames(), new CdcUmountFunctor(appSchemaInfo, ddlSql));
    }

    private final String ndtUserName;
    private final SchemaInfo appSchemaInfo;
    private final AgentSqlStrategy ddlSql;
    private final PlanBuilder pb;
}
