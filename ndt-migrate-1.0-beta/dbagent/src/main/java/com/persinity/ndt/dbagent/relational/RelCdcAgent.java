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
