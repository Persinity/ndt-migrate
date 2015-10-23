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

import static com.persinity.ndt.datamutator.DataMutatorMetrics.COUNT_TRANSACTION_ROLLBACKS;
import static com.persinity.ndt.datamutator.DataMutatorMetrics.METER_DMLS_PER_SECOND;
import static com.persinity.ndt.datamutator.DataMutatorMetrics.METER_TRANSACTIONS_PER_SECOND;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;

import org.apache.log4j.Logger;

import com.persinity.common.ThreadUtil;
import com.persinity.common.logging.Log4jLogger;
import com.persinity.common.metrics.MetricCounterFunc;
import com.persinity.common.metrics.MetricMeterFunc;

/**
 * Base class that executes the actual loading of data into the tables.
 * It executes transactions with random insert, update and delete statements.
 *
 * @author Ivo Yanakiev
 */
public abstract class LoadBase implements Runnable {

    public LoadBase(final int loadQuantity, final LoadParameters loadParameters, final EntityFactory entityFactory,
            final EntityPoolUtil entityPoolUtil) {

        this.loadQuantity = loadQuantity;
        this.loadParameters = loadParameters;
        this.entityFactory = entityFactory;
        this.entityPoolUtil = entityPoolUtil;
        this.entityPool = entityPoolUtil.getEntityPool();
        this.entitySession = entityFactory.createSession();
        recreateTransactionCounter(entityPool);
    }

    public void executeIteration(final String iterationName) {
        // keep in the begging to be able to start in paused state
        ThreadUtil.sleep(getTransactionDelayInMs());
        while (isRequestPause()) {
            paused = true;
            ThreadUtil.sleep(getTransactionDelayInMs());
        }
        paused = false;

        if (requestStop) {
            return;
        }

        log.info("Iteration {} started.", iterationName);

        List<EntityBase> entitiesForUpdate = null;
        List<EntityBase> entitiesForDeleted = null;
        List<EntityBase> cascadeDeleted = null;

        entitySession.openTransaction();

        final AtomicLong transactionId = new AtomicLong(genNextTransactionId());
        try {
            log.info("Transaction {} started.", transactionId);

            final List<EntityBase> inserted = randomInserts(transactionId);

            if (getRandomCount() % 3 == 0) {
                entitiesForUpdate = entityPool.takeEntitiesForUpdate(getRandomCount());
                updatesEntities(transactionId, entitiesForUpdate);
            }

            entitiesForDeleted = entityPool.takeEntitiesForDelete(getRandomCount());
            cascadeDeleted = deleteEntities(transactionId, entitiesForDeleted);

            // on random here mutate own newly created entity or/and delete it
            int ownDmls = 0;
            if (getRandomCount() % 3 == 1 && inserted.size() > 0) {
                final EntityBase toOwnMutate = inserted.get(0);
                if (getRandomCount() % 2 == 0) {
                    inserted.remove(toOwnMutate);
                    if (getRandomCount() % 2 == 0) {
                        updateEntity(toOwnMutate, transactionId);
                    }
                    deleteEntity(toOwnMutate, transactionId);
                    ownDmls += 1;
                } else {
                    updateEntity(toOwnMutate, transactionId);
                    ownDmls += 1;
                }
            }

            entitySession.commitTransaction();

            meterTps.apply(1);
            meterDps.apply(inserted.size() + cascadeDeleted.size() + ownDmls);
            if (entitiesForUpdate != null) {
                meterDps.apply(entitiesForUpdate.size());
            }

            log.info("Transaction {} committed.", transactionId);

            entityPool.addEntities(inserted);
            entityPool.removeEntities(cascadeDeleted);

        } catch (Exception e) {
            log.error(e, "Transaction {} failed: {}", transactionId, e.getMessage());

            entitySession.rollbackTransaction();

            countTrRollbacks.apply(1);

            if (entitiesForDeleted != null) {
                entityPool.returnDeletedEntities(entitiesForDeleted);
            }
        }

        if (entitiesForUpdate != null) {
            entityPool.returnUpdatedEntities(entitiesForUpdate);
        }

        log.info("Iteration {} completed.", iterationName);
    }

    private List<EntityBase> randomInserts(final AtomicLong transactionId) {
        List<EntityBase> entitiesForAdd = new LinkedList<>();

        int count = getRandomCount();
        for (int i = 0; i < count; i++) {
            EntityBase entity = entityFactory.createRandomEntity(transactionId.incrementAndGet());
            log.info("Inserting {}", entity);
            entitySession.insert(entity);
            entitiesForAdd.add(entity);
        }

        return entitiesForAdd;
    }

    private void updatesEntities(final AtomicLong transactionId, final List<EntityBase> entitiesForUpdate) {
        for (EntityBase entity : entitiesForUpdate) {
            updateEntity(entity, transactionId);
        }
    }

    private void updateEntity(final EntityBase entity, final AtomicLong transactionId) {
        entity.mutate(transactionId.incrementAndGet());
        log.info("Updating {}", entity);
        entitySession.update(entity);
    }

    private List<EntityBase> deleteEntities(final AtomicLong transactionId,
            final List<EntityBase> entitiesForDeletion) {
        final List<EntityBase> cascadeDeleted = new ArrayList<>();
        for (EntityBase entity : entitiesForDeletion) {
            cascadeDeleted.addAll(deleteEntity(entity, transactionId));
        }
        return cascadeDeleted;
    }

    private List<EntityBase> deleteEntity(final EntityBase entity, final AtomicLong transactionId) {
        log.info("Deleting in {} {}", transactionId, entity);
        return entitySession.delete(entity);
    }

    public abstract boolean isRunning();

    public int getLoadQuantity() {
        return loadQuantity;
    }

    public long getTransactionDelayInMs() {
        return loadParameters.getTransactionDelayInMs();
    }

    public boolean isRequestStop() {
        return requestStop;
    }

    public boolean isRequestPause() {
        return requestPause;
    }

    public boolean isPaused() {
        return paused;
    }

    private int getRandomCount() {
        return 1 + (int) (random.nextInt(loadParameters.getDmlsPerTransaction()) / 3D);
    }

    private static long genNextTransactionId() {
        return transactionIdGen.addAndGet(TRANSACTION_ID_DELTA);
    }

    public void requestPause() {
        if (!requestStop) {
            requestPause = true;
        }
    }

    public void requestResume() {
        requestPause = false;
    }

    public void requestStop() {
        requestStop = true;
        requestPause = false;
    }

    protected void closeSession() {
        try {
            entitySession.close();
        } catch (RuntimeException e) {
            log.error(e, "Failed to close session: {}", entitySession);
        }
    }

    /**
     * Recreate initial transaction ID generator by choosing the maximum ID from the pool across all tables.
     *
     * @param entityPool
     */
    private static void recreateTransactionCounter(final EntityPool entityPool) {
        if (transactionIdGen.get() <= 0) {
            transactionIdGen.set((entityPool.getMaxId() / TRANSACTION_ID_DELTA + 1) * TRANSACTION_ID_DELTA);
            log.info("Start transaction ID generator from {}", transactionIdGen.get());
        }
    }

    private final int loadQuantity;
    private final LoadParameters loadParameters;
    private final EntitySession entitySession;
    private final EntityPoolUtil entityPoolUtil;
    private final EntityPool entityPool;
    private final EntityFactory entityFactory;

    private final static long TRANSACTION_ID_DELTA = 10000;
    private final static AtomicLong transactionIdGen = new AtomicLong();

    private boolean requestPause;
    private boolean requestStop;
    private boolean paused;

    private final static MetricMeterFunc meterDps = new MetricMeterFunc(METER_DMLS_PER_SECOND);
    private final static MetricMeterFunc meterTps = new MetricMeterFunc(METER_TRANSACTIONS_PER_SECOND);
    private final static MetricCounterFunc countTrRollbacks = new MetricCounterFunc(COUNT_TRANSACTION_ROLLBACKS);

    private final static Random random = new Random();
    private static final Log4jLogger log = new Log4jLogger(Logger.getLogger(LoadBase.class));
}
