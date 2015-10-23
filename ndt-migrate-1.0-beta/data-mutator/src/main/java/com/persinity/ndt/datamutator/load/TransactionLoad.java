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
 * Executes specified amount of transactions and exit.
 *
 * @author Ivo Yanakiev
 */
public class TransactionLoad extends LoadBase {

    public TransactionLoad(final int loadQuantity, final LoadParameters loadParameters,
            final EntityFactory entityFactory, final EntityPoolUtil entityPoolUtil) {
        super(loadQuantity, loadParameters, entityFactory, entityPoolUtil);
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
