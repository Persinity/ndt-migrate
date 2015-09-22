package com.persinity.ndt.datamutator.load;

import static com.persinity.ndt.datamutator.DataMutatorMetrics.COUNT_TRANSACTION_ROLLBACKS;
import static com.persinity.ndt.datamutator.DataMutatorMetrics.METER_DMLS_PER_SECOND;
import static com.persinity.ndt.datamutator.DataMutatorMetrics.METER_TRANSACTIONS_PER_SECOND;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

import org.apache.log4j.Logger;

import com.persinity.common.ThreadUtil;
import com.persinity.common.logging.Log4jLogger;
import com.persinity.common.metrics.MetricCounterFunc;
import com.persinity.common.metrics.MetricMeterFunc;
import com.persinity.ndt.datamutator.common.DefaultIdGenerator;
import com.persinity.ndt.datamutator.common.IdGenerator;

/**
 * Base class that executes the actual loading of data into the tables.
 * It executes transactions with random insert, update and delete statements.
 *
 * @author Ivo Yanakiev
 */
public abstract class LoadBase implements Runnable {

    public LoadBase(final int loadQuantity, final LoadParameters loadParameters, final EntityFactory entityFactory,
            final EntityPoolUtil entityPoolUtil, final IdGenerator transactionIdGen) {

        this.loadQuantity = loadQuantity;
        this.loadParameters = loadParameters;
        this.entityFactory = entityFactory;
        this.entityPoolUtil = entityPoolUtil;
        this.entityPool = entityPoolUtil.getEntityPool();
        this.entitySession = entityFactory.createSession();
        this.transactionIdGen = transactionIdGen;
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

        final IdGenerator idGen = new DefaultIdGenerator(transactionIdGen.getNext());

        try {
            log.info("Transaction {} started.", idGen);

            final List<EntityBase> inserted = randomInserts(idGen);

            if (getRandomCount() % 3 == 0) {
                entitiesForUpdate = entityPool.takeEntitiesForUpdate(getRandomCount());
                updatesEntities(idGen, entitiesForUpdate);
            }

            entitiesForDeleted = entityPool.takeEntitiesForDelete(getRandomCount());
            cascadeDeleted = deleteEntities(idGen, entitiesForDeleted);

            // on random here mutate own newly created entity or/and delete it
            int ownDmls = 0;
            if (getRandomCount() % 3 == 1 && inserted.size() > 0) {
                final EntityBase toOwnMutate = inserted.get(0);
                if (getRandomCount() % 2 == 0) {
                    inserted.remove(toOwnMutate);
                    if (getRandomCount() % 2 == 0) {
                        updateEntity(toOwnMutate, idGen);
                    }
                    deleteEntity(toOwnMutate, idGen);
                    ownDmls += 1;
                } else {
                    updateEntity(toOwnMutate, idGen);
                    ownDmls += 1;
                }
            }

            entitySession.commitTransaction();

            meterTps.apply(1);
            meterDps.apply(inserted.size() + cascadeDeleted.size() + ownDmls);
            if (entitiesForUpdate != null) {
                meterDps.apply(entitiesForUpdate.size());
            }

            log.info("Transaction {} committed.", idGen);

            entityPool.addEntities(inserted);
            entityPool.removeEntities(cascadeDeleted);

        } catch (Exception e) {
            log.error(e, "Transaction {} failed: {}", idGen, e.getMessage());

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

    private List<EntityBase> randomInserts(final IdGenerator transactionId) {
        List<EntityBase> entitiesForAdd = new LinkedList<>();

        int count = getRandomCount();
        for (int i = 0; i < count; i++) {
            EntityBase entity = entityFactory.createRandomEntity(transactionId.getNext());
            log.info("Inserting {}", entity);
            entitySession.insert(entity);
            entitiesForAdd.add(entity);
        }

        return entitiesForAdd;
    }

    private void updatesEntities(final IdGenerator transactionId, final List<EntityBase> entitiesForUpdate) {
        for (EntityBase entity : entitiesForUpdate) {
            updateEntity(entity, transactionId);
        }
    }

    private void updateEntity(final EntityBase entity, final IdGenerator transactionId) {
        entity.mutate(transactionId.getNext());
        log.info("Updating {}", entity);
        entitySession.update(entity);
    }

    private List<EntityBase> deleteEntities(final IdGenerator transactionId,
            final List<EntityBase> entitiesForDeletion) {
        final List<EntityBase> cascadeDeleted = new ArrayList<>();
        for (EntityBase entity : entitiesForDeletion) {
            cascadeDeleted.addAll(deleteEntity(entity, transactionId));
        }
        return cascadeDeleted;
    }

    private List<EntityBase> deleteEntity(final EntityBase entity, final IdGenerator transactionId) {
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

    private final int loadQuantity;
    private final LoadParameters loadParameters;
    private final EntitySession entitySession;
    private final EntityPoolUtil entityPoolUtil;
    private final EntityPool entityPool;
    private final EntityFactory entityFactory;

    private final IdGenerator transactionIdGen;

    private boolean requestPause;
    private boolean requestStop;
    private boolean paused;

    private final static MetricMeterFunc meterDps = new MetricMeterFunc(METER_DMLS_PER_SECOND);
    private final static MetricMeterFunc meterTps = new MetricMeterFunc(METER_TRANSACTIONS_PER_SECOND);
    private final static MetricCounterFunc countTrRollbacks = new MetricCounterFunc(COUNT_TRANSACTION_ROLLBACKS);

    private final static Random random = new Random();
    private static final Log4jLogger log = new Log4jLogger(Logger.getLogger(LoadBase.class));
}
