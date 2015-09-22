package com.persinity.ndt.datamutator.load;

import org.apache.log4j.Logger;

import com.persinity.common.logging.Log4jLogger;
import com.persinity.ndt.datamutator.common.IdGenerator;

/**
 * Executes specified amount of transactions and exit.
 *
 * @author Ivo Yanakiev
 */
public class TransactionLoad extends LoadBase {

    public TransactionLoad(final int loadQuantity, final LoadParameters loadParameters,
            final EntityFactory entityFactory, final EntityPoolUtil entityPoolUtil, final IdGenerator idGenerator) {
        super(loadQuantity, loadParameters, entityFactory, entityPoolUtil, idGenerator);
    }

    public void run() {
        running = true;

        final int transactions = getLoadQuantity();
        log.info("Executing {} transactions", transactions);

        final long totalStart = System.currentTimeMillis();

        for (int i = 0; i < transactions && !isRequestStop(); i++) {
            executeIteration("" + i);
        }

        final long totalEnd = System.currentTimeMillis();
        log.info("DONE for {} ms", (totalEnd - totalStart));

        running = false;
        closeSession();
    }

    @Override
    public boolean isRunning() {
        return running;
    }

    private boolean running;

    private static final Log4jLogger log = new Log4jLogger(Logger.getLogger(TransactionLoad.class));
}
