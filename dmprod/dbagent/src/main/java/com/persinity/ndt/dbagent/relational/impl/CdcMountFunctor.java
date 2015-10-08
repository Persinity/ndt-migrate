/**
 * Copyright (c) 2015 Persinity Inc.
 */
package com.persinity.ndt.dbagent.relational.impl;

import static com.persinity.common.invariant.Invariant.notEmpty;
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
 * Functor that returns function, which mounts CDC triggers on given table.
 *
 * @author Doichin Yordanov
 */
public class CdcMountFunctor implements Functor<RelDb, RelDb, String, Function<RelDb, RelDb>> {

    /**
     * @param ndtUserName
     * @param appSchemaInfo
     * @param ddlSql
     */
    public CdcMountFunctor(final String ndtUserName, final SchemaInfo appSchemaInfo, final AgentSqlStrategy ddlSql) {
        notEmpty(ndtUserName);
        notNull(appSchemaInfo);
        notNull(ddlSql);

        this.ndtUserName = ndtUserName;
        this.appSchemaInfo = appSchemaInfo;
        this.ddlSql = ddlSql;
    }

    @Override
    public Function<RelDb, RelDb> apply(final String tableName) {
        final String clogName = appSchemaInfo.getClogTableName(tableName);

        final String clogTrigSql = ddlSql
                .createCdcClogTrigger(ndtUserName, appSchemaInfo.getClogTriggerName(tableName), tableName,
                        appSchemaInfo.getTableCols(tableName), appSchemaInfo.getTablePk(tableName), clogName);
        final RelFunc clogTrigFunc = new RelFunc(clogTrigSql);

        final String trlogTrigSql = ddlSql
                .createCdcTrlogTrigger(ndtUserName, appSchemaInfo.getTrlogTriggerName(tableName), tableName, clogName,
                        SchemaInfo.TAB_TRLOG);
        final RelFunc trlogTrigFunc = new RelFunc(trlogTrigSql);

        final Function<RelDb, RelDb> logFunc = new LoggingRepeaterFunc<>(log, "Cdc mount at table \"{}\"", tableName);

        final Function<RelDb, RelDb> createTrigsFunc = Functions
                .compose(logFunc, Functions.compose(trlogTrigFunc, clogTrigFunc));
        return createTrigsFunc;
    }

    private final String ndtUserName;
    private final SchemaInfo appSchemaInfo;
    private final AgentSqlStrategy ddlSql;

    private static final Log4jLogger log = new Log4jLogger(Logger.getLogger(CdcMountFunctor.class));
}
