/**
 * Copyright (c) 2015 Persinity Inc.
 */
package com.persinity.ndt.dbagent;

import com.google.common.base.Function;
import com.persinity.common.collection.DirectedEdge;
import com.persinity.common.db.RelDb;
import com.persinity.ndt.transform.SerialPlanProcessor;

/**
 * TODO this can be abstracted in DbAgentExecutor interface and Serial/Haka impl
 * when we introduce the HakaPlanProcessor
 * TODO generify as the rest of the interfaces in this package, to avoid RelDb dependency
 *
 * @author Ivan Dachev
 */
public class DbAgentExecutor {

    public DbAgentExecutor() {
        processor = new SerialPlanProcessor<>();
        safeDropTableExceptionHandler = new Function<DirectedEdge<RelDb, RuntimeException>, Void>() {
            @Override
            public Void apply(final DirectedEdge<RelDb, RuntimeException> input) {
                final RuntimeException e = input.dst();
                if (input.src().getSqlStrategy().isAccessRuleViolation(e.getCause())) {
                    return null;
                }
                throw e;
            }
        };
    }

    /**
     * Disables the supplied {@link CdcAgent} from the supplied application {@link RelDb}
     *
     * @param cdcAgent
     * @param appDb
     */
    public void cdcAgentUnmount(final CdcAgent<Function<RelDb, RelDb>> cdcAgent, final RelDb appDb) {
        processor.process(cdcAgent.umountCdc(), appDb, safeDropTableExceptionHandler);
    }

    /**
     * Unmounts the artifacts related to the supplied {@link SchemaAgent} from the supplied {@link RelDb},
     * e.g. the ndt_common package
     *
     * @param schemaAgent
     * @param ndtDb
     */
    public void schemaAgentUnmount(final SchemaAgent<Function<RelDb, RelDb>> schemaAgent, final RelDb ndtDb) {
        processor.process(schemaAgent.umount(), ndtDb, safeDropTableExceptionHandler);
    }

    /**
     * Disables the supplied {@link ClogAgent} from the supplied application {@link RelDb}
     *
     * @param clogAgent
     * @param ndtDb
     */
    public void clogAgentUnmount(final ClogAgent<Function<RelDb, RelDb>> clogAgent, final RelDb ndtDb) {
        processor.process(clogAgent.clogUmount(), ndtDb, safeDropTableExceptionHandler);
    }

    /**
     * Executes GC on clog
     *
     * @param clogAgent
     * @param ndtDb
     */
    public void clogAgentGc(final ClogAgent<Function<RelDb, RelDb>> clogAgent, final RelDb ndtDb) {
        processor.process(clogAgent.clogGc(), ndtDb, safeDropTableExceptionHandler);
        ndtDb.commit();
    }

    /**
     * Installs and enables {@link CdcAgent} in the specified NDT {@link RelDb}, capturing changes from the specified
     * application {@link RelDb}
     *
     * @param dbAgentFactory
     * @param appDb
     * @param ndtDb
     * @return
     */
    public CdcAgent<Function<RelDb, RelDb>> cdcAgentInstallMount(
            final DbAgentFactory<Function<RelDb, RelDb>> dbAgentFactory, final RelDb appDb, final RelDb ndtDb) {
        final CdcAgent<Function<RelDb, RelDb>> cdcAgent = dbAgentFactory.dispatchCdcAgent(ndtDb);
        processor.process(cdcAgent.mountCdc(), appDb, safeDropTableExceptionHandler);
        return cdcAgent;
    }

    /**
     * Installs and enables {@link ClogAgent} in the specified NDT {@link RelDb}
     *
     * @param dbAgentFactory
     * @param ndtDb
     * @return
     */
    public ClogAgent<Function<RelDb, RelDb>> clogAgentInstallMount(
            final DbAgentFactory<Function<RelDb, RelDb>> dbAgentFactory, final RelDb ndtDb) {
        final ClogAgent<Function<RelDb, RelDb>> clogAgent = dbAgentFactory.dispatchClogAgent(ndtDb);
        processor.process(clogAgent.clogUmount(), ndtDb, safeDropTableExceptionHandler);
        processor.process(clogAgent.clogMount(), ndtDb, safeDropTableExceptionHandler);
        return clogAgent;
    }

    /**
     * Installs and enables {@link SchemaAgent} in the specified application {@link RelDb}.
     * Note that the DB ref integrity is relaxed when the agent is installed.
     *
     * @param dbAgentFactory
     * @param ndtDb
     * @return
     */
    public SchemaAgent<Function<RelDb, RelDb>> schemaAgentInstallMount(
            final DbAgentFactory<Function<RelDb, RelDb>> dbAgentFactory, final RelDb ndtDb, final RelDb appDb) {
        SchemaAgent<Function<RelDb, RelDb>> dstSchemaAgent = dbAgentFactory.dispatchSchemaAgent(ndtDb);
        processor.process(dstSchemaAgent.disableIntegrity(), appDb, safeDropTableExceptionHandler);
        return dstSchemaAgent;
    }

    /**
     * Executes cleanup on trlog
     *
     * @param clogAgent
     * @param ndtDb
     */
    public void trlogCleanup(final ClogAgent<Function<RelDb, RelDb>> clogAgent, final RelDb ndtDb) {
        processor.process(clogAgent.trlogCleanup(), ndtDb, safeDropTableExceptionHandler);
        ndtDb.commit();
    }

    private final SerialPlanProcessor<RelDb, RelDb> processor;
    private final Function<DirectedEdge<RelDb, RuntimeException>, Void> safeDropTableExceptionHandler;
}
