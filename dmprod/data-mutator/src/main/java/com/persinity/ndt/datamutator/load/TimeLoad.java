package com.persinity.ndt.datamutator.load;

import org.apache.log4j.Logger;

import com.persinity.common.logging.Log4jLogger;
import com.persinity.ndt.datamutator.common.IdGenerator;

/**
 * Execute transactions for specified amount of time.
 *
 * @author Ivo Yanakiev
 */
public class TimeLoad extends LoadBase {

    public TimeLoad(final int executionDurationSeconds, final LoadParameters loadParameters,
            final EntityFactory entityFactory, final EntityPoolUtil entityPoolUtil, final IdGenerator idGenerator) {
        super(executionDurationSeconds, loadParameters, entityFactory, entityPoolUtil, idGenerator);
    }

    @Override
    public void run() {
        running = true;

        final long loadTimeInMs = getLoadQuantity() * 1000;
        log.info("Running for {} seconds", loadTimeInMs / 1000);

        final long totalStart = System.currentTimeMillis();

        long totalTime = 0;
        int i = 0;
        while (((getLoadQuantity() < 0) || (totalTime < loadTimeInMs)) && !isRequestStop()) {
            final long start = System.currentTimeMillis();

            executeIteration("" + ++i);

            final long end = System.currentTimeMillis();
            final long elapsedTime = end - start;
            totalTime += (elapsedTime > 0) ? elapsedTime : 0;
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

    private static final Log4jLogger log = new Log4jLogger(Logger.getLogger(TimeLoad.class));
}
