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
package com.persinity.ndt.dbagent.relational.impl;

import static com.persinity.common.invariant.Invariant.notNull;

import org.apache.log4j.Logger;

import com.google.common.base.Function;
import com.google.common.base.Functions;
import com.persinity.common.db.RelDb;
import com.persinity.common.fp.Functor;
import com.persinity.common.logging.Log4jLogger;
import com.persinity.common.logging.LoggingRepeaterFunc;
import com.persinity.ndt.dbagent.relational.AgentSqlStrategy;
import com.persinity.ndt.dbagent.relational.SchemaInfo;
import com.persinity.ndt.transform.RelFunc;

/**
 * Functor that returns function, which unmounts CDC triggers from given table.
 *
 * @author Doichin Yordanov
 */
public class CdcUmountFunctor implements Functor<RelDb, RelDb, String, Function<RelDb, RelDb>> {

    /**
     * @param appSchemaInfo
     * @param ddlSql
     */
    public CdcUmountFunctor(final SchemaInfo appSchemaInfo, final AgentSqlStrategy ddlSql) {
        notNull(appSchemaInfo);
        notNull(ddlSql);

        this.appSchemaInfo = appSchemaInfo;
        this.ddlSql = ddlSql;
    }

    @Override
    public Function<RelDb, RelDb> apply(final String tableName) {
        final String clogTrigSql = ddlSql.dropTrigger(appSchemaInfo.getClogTriggerName(tableName));
        final RelFunc clogTrigFunc = new RelFunc(clogTrigSql);

        final String trlogTrigSql = ddlSql.dropTrigger(appSchemaInfo.getTrlogTriggerName(tableName));
        final RelFunc trlogTrigFunc = new RelFunc(trlogTrigSql);

        final Function<RelDb, RelDb> logFunc = new LoggingRepeaterFunc<>(log, "Cdc umount at table \"{}\"", tableName);

        final Function<RelDb, RelDb> dropTrigsFunc = Functions
                .compose(Functions.compose(trlogTrigFunc, clogTrigFunc), logFunc);
        return dropTrigsFunc;
    }

    private final SchemaInfo appSchemaInfo;
    private final AgentSqlStrategy ddlSql;

    private static final Log4jLogger log = new Log4jLogger(Logger.getLogger(CdcUmountFunctor.class));
}
