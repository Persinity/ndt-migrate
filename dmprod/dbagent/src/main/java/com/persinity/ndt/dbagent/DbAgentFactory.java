/**
 * Copyright (c) 2015 Persinity Inc.
 */
package com.persinity.ndt.dbagent;

import static com.persinity.common.db.RelDb.Privs.DELETE;
import static com.persinity.common.db.RelDb.Privs.EXECUTE;
import static com.persinity.common.db.RelDb.Privs.INSERT;
import static com.persinity.common.db.RelDb.Privs.SELECT;
import static com.persinity.common.db.RelDb.Privs.UPDATE;
import static com.persinity.common.invariant.Invariant.notNull;
import static java.util.Arrays.asList;

import java.util.Collection;
import java.util.List;

import org.apache.log4j.Logger;

import com.google.common.base.Function;
import com.persinity.common.collection.CollectionUtils;
import com.persinity.common.db.RelDb;
import com.persinity.common.db.Trimmer;
import com.persinity.common.db.metainfo.Schema;
import com.persinity.common.logging.Log4jLogger;
import com.persinity.ndt.dbagent.relational.AgentSqlStrategy;
import com.persinity.ndt.dbagent.relational.DbAgentTracker;
import com.persinity.ndt.dbagent.relational.RelCdcAgent;
import com.persinity.ndt.dbagent.relational.RelClogAgent;
import com.persinity.ndt.dbagent.relational.RelSchemaAgent;
import com.persinity.ndt.dbagent.relational.SchemaInfo;
import com.persinity.ndt.dbagent.relational.oracle.OracleSchemaInfo;

/**
 * Dispatches and returns DB agents.<BR>
 * Abstracts the details of the DB agents type, i.e. the DB type and vendor.
 *
 * @author Doichin Yordanov
 */
public class DbAgentFactory<T extends Function<?, ?>> {

    /**
     * @param schema
     *         Agents dispatched by this factory will use the meta-info from the provided application {@link Schema}
     *         when building plans.
     * @param sqlStrategy
     *         Used by the agents when building plans.
     * @param dbAgentTracker
     *         Tracks agent dispatching
     */
    public DbAgentFactory(final Schema schema, final AgentSqlStrategy sqlStrategy,
            final DbAgentTracker dbAgentTracker) {
        notNull(schema);
        notNull(sqlStrategy);
        notNull(dbAgentTracker);

        this.schema = schema;
        this.sqlStrategy = sqlStrategy;
        schemaInfo = new OracleSchemaInfo(schema, new Trimmer(), this.sqlStrategy.getMaxNameLength());
        this.dbAgentTracker = dbAgentTracker;
    }

    /**
     * @param ndtDb
     *         which the agent will operate in
     * @return
     */
    @SuppressWarnings("unchecked")
    public synchronized CdcAgent<T> dispatchCdcAgent(final RelDb ndtDb) {
        if (!dbAgentTracker.isAgentDispatched(ndtDb, RelClogAgent.class)) {
            dispatchClogAgent(ndtDb, false);
        }
        final Function<RelDb, RelCdcAgent> dispatchF = new Function<RelDb, RelCdcAgent>() {
            @Override
            public RelCdcAgent apply(final RelDb input) {
                // TODO agent dispatch strategy according the DB type
                ndtDb.executeScript("cdc-preinstall.sql");
                final RelCdcAgent agent = new RelCdcAgent(ndtDb.getUserName(), schemaInfo, sqlStrategy);
                grantCdcPrivs(ndtDb);
                return agent;
            }
        };
        return (CdcAgent<T>) dispatchAgent(ndtDb, RelCdcAgent.class, dispatchF);
    }

    /**
     * @param ndtDb
     *         which the agent will operate in
     * @return
     */
    @SuppressWarnings("unchecked")
    public synchronized ClogAgent<T> dispatchClogAgent(final RelDb ndtDb, final boolean isSrcClog) {
        final Function<RelDb, RelClogAgent> dispatchF = new Function<RelDb, RelClogAgent>() {
            @Override
            public RelClogAgent apply(final RelDb input) {
                checkInstallCommon(ndtDb, isSrcClog);
                // TODO agent dispatch strategy according the DB type
                ndtDb.executeScript("clog-preinstall.sql");
                RelClogAgent agent = new RelClogAgent(ndtDb, schemaInfo, sqlStrategy);
                return agent;
            }
        };
        return (ClogAgent<T>) dispatchAgent(ndtDb, RelClogAgent.class, dispatchF);
    }

    /**
     * @param ndtDb
     *         which the agent will operate with
     * @return
     */
    @SuppressWarnings("unchecked")
    public synchronized SchemaAgent<T> dispatchSchemaAgent(final RelDb ndtDb) {
        final Function<RelDb, RelSchemaAgent> dispatchF = new Function<RelDb, RelSchemaAgent>() {
            @Override
            public RelSchemaAgent apply(final RelDb input) {
                checkInstallCommon(ndtDb, false);
                ndtDb.executeScript("schema-preinstall.sql");
                final RelSchemaAgent agent = new RelSchemaAgent(ndtDb.getUserName(), schemaInfo, sqlStrategy);
                grantSchemaPrivs(ndtDb);
                return agent;
            }
        };
        return (SchemaAgent<T>) dispatchAgent(ndtDb, RelSchemaAgent.class, dispatchF);
    }

    <A> A dispatchAgent(final RelDb ndtDb, final Class<? extends A> agentClazz,
            final Function<RelDb, ? extends A> dispatchF) {
        final A agent;
        if (!dbAgentTracker.isAgentDispatched(ndtDb, agentClazz)) {
            agent = dispatchF.apply(ndtDb);
            dbAgentTracker.agentDispatched(ndtDb, agent);
        } else {
            agent = dbAgentTracker.getDispatchedAgent(ndtDb, agentClazz);
        }
        return agent;
    }

    private void checkInstallCommon(final RelDb db, final boolean isSrcClog) {
        if (!dbAgentTracker.isAgentDispatched(db, RelClogAgent.class) && !dbAgentTracker
                .isAgentDispatched(db, RelSchemaAgent.class)) {
            db.executeScript("common.sql");
            if (!isSrcClog) {
                grantCommonPrivs(db);
            } else {
                log.debug("Skipping privileges grant");
            }
        }
    }

    private void grantSchemaPrivs(final RelDb db) {
        grantPrivs(asList(EXECUTE), SchemaInfo.SP_NDT_SCHEMA, db, schema.getUserName());
    }

    private void grantCommonPrivs(final RelDb db) {
        grantPrivs(asList(EXECUTE), SchemaInfo.SP_NDT_COMMON, db, schema.getUserName());
        grantPrivs(asList(SELECT, INSERT), SchemaInfo.TAB_NDT_LOG, db, schema.getUserName());
    }

    private void grantCdcPrivs(final RelDb ndtDb) {
        final Schema ndtDbSchema = ndtDb.metaInfo();
        for (String table : ndtDbSchema.getTableNames()) {
            // TODO Minor improvement: The grant for each clog could be put in the mount plan to parallelize it.
            grantPrivs(asList(SELECT, INSERT, UPDATE, DELETE), table, ndtDb, schema.getUserName());
        }
        grantPrivs(asList(EXECUTE), SchemaInfo.SP_NDT_CLOG, ndtDb, schema.getUserName());
        grantPrivs(asList(SELECT), SchemaInfo.SEQ_GID, ndtDb, schema.getUserName());
    }

    private void grantPrivs(final Collection<RelDb.Privs> privs, String onObject, final RelDb fromDb,
            final String toUser) {
        final List<String> privStrings = CollectionUtils.stringListOf(privs);
        fromDb.executeDmdl(sqlStrategy.grantPrivs(privStrings, onObject, toUser));
    }

    private final Schema schema;
    private final AgentSqlStrategy sqlStrategy;
    private final SchemaInfo schemaInfo;
    private final DbAgentTracker dbAgentTracker;

    private static final Log4jLogger log = new Log4jLogger(Logger.getLogger(DbAgentFactory.class));

    // TODO add revokeAgent methods for uninstalling agents
}
