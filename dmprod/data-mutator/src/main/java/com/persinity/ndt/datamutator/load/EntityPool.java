/**
 * Copyright (c) 2015 Persinity Inc.
 */
package com.persinity.ndt.datamutator.load;

import static com.persinity.common.invariant.Invariant.assertArg;
import static com.persinity.common.invariant.Invariant.notEmpty;
import static com.persinity.common.invariant.Invariant.notNull;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.apache.log4j.Logger;

import com.persinity.common.logging.Log4jLogger;

/**
 * Used as pool of entities from which the loaders could insert, get for update or getBridge for delete.
 * It supports entities ration to keep added entities more then deleted. That will keep to not delete all entities.
 *
 * @author Ivan Dachev
 */
public class EntityPool {

    public EntityPool(final int entitiesRatio) {
        assertArg(entitiesRatio > 0);
        assertArg(entitiesRatio < 100);

        this.entitiesRatio = entitiesRatio;

        entities = new HashMap<>();

        log.info("Created pool with entitiesRatio: {}", entitiesRatio);
    }

    /**
     * @param newEntities
     *         to add
     */
    public void addEntities(final Collection<EntityBase> newEntities) {
        notNull(newEntities);

        if (newEntities.size() == 0) {
            return;
        }

        synchronized (lock) {
            putEntities(newEntities);

            totalAdded += newEntities.size();

            log.info("Added entities: {} lifeEntities: {} totalAdded: {} totalDeleted: {}", newEntities.size(),
                    getEntitiesSize(), totalAdded, totalDeleted);
        }
    }

    /**
     * @param entitiesToRemove
     *         to remove from the pool
     */
    public void removeEntities(final List<EntityBase> entitiesToRemove) {
        notNull(entitiesToRemove);

        if (entitiesToRemove.size() == 0) {
            return;
        }

        synchronized (lock) {
            int deleted = 0;
            for (EntityBase entity : entitiesToRemove) {
                final String type = entity.getType().toLowerCase();
                List<EntityBase> store = entities.get(type);
                if (store != null) {
                    store.remove(entity);
                    deleted++;
                }
            }

            totalDeleted += deleted;

            log.info("Removed entities: {} lifeEntities: {} totalAdded: {} totalDeleted: {}", deleted,
                    getEntitiesSize(), totalAdded, totalDeleted);
        }

    }

    /**
     * If the delete operations fails please return the entities to the pool
     * by calling {@link EntityPool#returnDeletedEntities(Collection)}
     *
     * @param count
     *         how much entities to return
     * @return entities for delete
     */
    public List<EntityBase> takeEntitiesForDelete(final int count) {
        assertArg(count > 0);

        List<EntityBase> res = Collections.emptyList();

        synchronized (lock) {
            final int maxAllowedForDelete = calculateMaxAllowedForDelete();
            if (maxAllowedForDelete > 0) {
                final int actualCount = count < maxAllowedForDelete ? count : maxAllowedForDelete;
                if (actualCount < getEntitiesSize()) {
                    res = takeRandomEntities(actualCount);
                }
            }

            totalDeleted += res.size();

            log.info("Entities for delete: {} lifeEntities: {} totalAdded: {} totalDeleted: {}", res.size(),
                    getEntitiesSize(), totalAdded, totalDeleted);
        }

        return res;
    }

    /**
     * When update is done return the entities to the pool
     * by calling {@link EntityPool#returnUpdatedEntities(Collection)}
     *
     * @param count
     *         how much entities to return
     * @return entities for update
     */
    public List<EntityBase> takeEntitiesForUpdate(final int count) {
        assertArg(count > 0);

        final List<EntityBase> res;

        synchronized (lock) {
            res = takeRandomEntities(count);

            log.info("Entities for update: {} lifeEntities: {} totalAdded: {} totalDeleted: {}", res.size(),
                    getEntitiesSize(), totalAdded, totalDeleted);
        }

        return res;
    }

    /**
     * Used to return entities that was for update.
     *
     * @param returnedEntities
     *         to add back to the pool
     */
    public void returnUpdatedEntities(final Collection<EntityBase> returnedEntities) {
        notNull(returnedEntities);

        if (returnedEntities.size() == 0) {
            return;
        }

        synchronized (lock) {
            putEntities(returnedEntities);

            log.info("Returned entities: {} lifeEntities: {} totalAdded: {} totalDeleted: {}", returnedEntities.size(),
                    getEntitiesSize(), totalAdded, totalDeleted);
        }
    }

    /**
     * Used to return entities on fail to delete.
     *
     * @param returnedEntities
     *         to add back to the pool
     */
    public void returnDeletedEntities(final Collection<EntityBase> returnedEntities) {
        notNull(returnedEntities);

        if (returnedEntities.size() == 0) {
            return;
        }

        synchronized (lock) {
            putEntities(returnedEntities);

            totalDeleted -= returnedEntities.size();

            log.info("Returned deleted entities: {} lifeEntities: {} totalAdded: {} totalDeleted: {}",
                    returnedEntities.size(), getEntitiesSize(), totalAdded, totalDeleted);
        }
    }

    /**
     * @param entityType
     *         to get entity for
     * @return entity that is from specified type, it is not removed from the pool from this get
     */
    public EntityBase getEntity(final String entityType) {
        notEmpty(entityType);

        EntityBase entity = null;

        synchronized (lock) {
            final List<EntityBase> store = entities.get(entityType.toLowerCase());
            if (store != null && store.size() > 0) {
                entity = store.get(random.nextInt(store.size()));
            }
        }

        return entity;
    }

    /**
     * @param entityType
     * @return entities from given type or empty List if no such one
     */
    public List<EntityBase> getEntities(final String entityType) {
        notEmpty(entityType);

        final List<EntityBase> res = new ArrayList<>();

        synchronized (lock) {
            final List<EntityBase> store = entities.get(entityType.toLowerCase());
            if (store != null) {
                res.addAll(store);
            }
        }

        return res;
    }

    /**
     * @return live entities
     */
    public int getLive() {
        synchronized (lock) {
            return getEntitiesSize();
        }
    }

    /**
     * @return total added entities
     */
    public int getAdded() {
        synchronized (lock) {
            return totalAdded;
        }
    }

    /**
     * @return total deleted entities
     */
    public int getDeleted() {
        synchronized (lock) {
            return totalDeleted;
        }
    }

    /**
     * Clear entities from pool and reset added/detleted statistics.
     */
    public void reset() {
        synchronized (lock) {
            entities.clear();
            totalAdded = 0;
            totalDeleted = 0;
        }
    }

    private void putEntities(final Collection<EntityBase> entitiesToPut) {
        log.debug("Put entities: {}", entitiesToPut);

        for (EntityBase entity : entitiesToPut) {
            final String type = entity.getType().toLowerCase();
            List<EntityBase> store = entities.get(type);
            if (store == null) {
                store = new ArrayList<>();
                entities.put(type, store);
            }
            if (!store.contains(entity)) {
                store.add(entity);
            }
        }
    }

    /**
     * Takes random entities sorted by simple class name and then by its comparable state.
     */
    private List<EntityBase> takeRandomEntities(final int count) {
        if (getEntitiesSize() == 0) {
            return Collections.emptyList();
        }

        final int actualCount = count < getEntitiesSize() ? count : getEntitiesSize();

        final List<EntityBase> res = new ArrayList<>();

        final Object[] keys = entities.keySet().toArray();

        while (res.size() < actualCount) {
            final String key = (String) keys[random.nextInt(keys.length)];
            final List<EntityBase> store = entities.get(key);
            if (store.size() == 0) {
                continue;
            }
            final EntityBase entity = store.get(random.nextInt(store.size()));
            if (!res.contains(entity)) {
                res.add(entity);
                store.remove(entity);
            }
        }

        sortEntities(res);

        log.debug("Taken entities: {}", res);

        return res;
    }

    private void sortEntities(final List<EntityBase> entities) {
        Collections.sort(entities, new Comparator<EntityBase>() {
            @SuppressWarnings("unchecked")
            @Override
            public int compare(final EntityBase o1, final EntityBase o2) {
                int res = o1.getType().compareTo(o2.getType());
                if (res == 0) {
                    return o1.compareTo(o2);
                } else {
                    return res;
                }
            }
        });
    }

    private int getEntitiesSize() {
        int res = 0;
        for (List<EntityBase> store : entities.values()) {
            res += store.size();
        }
        return res;
    }

    private int calculateMaxAllowedForDelete() {
        if (totalAdded == 0) {
            return 0;
        }

        int maxAllowedForDelete = calculateMaxAllowedForDelete(totalAdded, totalDeleted, entitiesRatio);
        if (maxAllowedForDelete > getEntitiesSize()) {
            maxAllowedForDelete = getEntitiesSize() - 1;
        }

        return maxAllowedForDelete;
    }

    static int calculateMaxAllowedForDelete(final int added, final int deleted, final int entitiesRatio) {
        assertArg(added >= 0);
        assertArg(deleted >= 0);
        assertArg(added > deleted);
        assertArg(entitiesRatio > 0);
        assertArg(entitiesRatio < 100);

        final int deletedPercent = (deleted * 100 / added);
        final int toDeletePercent = (100 - entitiesRatio) - deletedPercent;
        if (toDeletePercent > 0) {
            final int maxAllowedForDelete = (toDeletePercent * added) / 100;
            return maxAllowedForDelete;
        } else {
            return 0;
        }
    }

    public long getMaxId() {
        long maxId = 0;

        for (final List<EntityBase> store : entities.values()) {
            for (final EntityBase entity : store) {
                final long id = getMaxId(entity);
                maxId = Math.max(maxId, id);
            }
        }

        return maxId;
    }

    private long getMaxId(final EntityBase entity) {
        long maxId = 0;

        for (Object idValue : entity.getId()) {
            long id = 0;
            if (idValue instanceof Number) {
                id = ((Number) idValue).longValue();
            } else {
                try {
                    id = Long.parseLong(idValue.toString());
                } catch (NumberFormatException e) {
                    // silent
                }
            }
            maxId = Math.max(maxId, id);
        }

        return maxId;
    }

    private final int entitiesRatio;
    private final Map<String, List<EntityBase>> entities;
    private final Object lock = new Object();
    private final Random random = new Random();

    private int totalAdded;
    private int totalDeleted;

    private static final Log4jLogger log = new Log4jLogger(Logger.getLogger(EntityPool.class));
}
