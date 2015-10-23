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

package com.persinity.ndt.datamutator.load;

import org.apache.log4j.Logger;

import com.persinity.common.logging.Log4jLogger;

/**
 * Execute transactions for specified amount of time.
 *
 * @author Ivo Yanakiev
 */
public class TimeLoad extends LoadBase {

    public TimeLoad(final int executionDurationSeconds, final LoadParameters loadParameters,
            final EntityFactory entityFactory, final EntityPoolUtil entityPoolUtil) {
        super(executionDurationSeconds, loadParameters, entityFactory, entityPoolUtil);
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
