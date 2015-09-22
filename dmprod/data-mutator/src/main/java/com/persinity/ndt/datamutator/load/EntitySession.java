/**
 * Copyright (c) 2015 Persinity Inc.
 */
package com.persinity.ndt.datamutator.load;

import java.util.List;

/**
 * Entity session, should be used only in one Thread!
 *
 * @author Ivan Dachev
 */
public interface EntitySession {

    /**
     * @param entity
     *         to insert
     */
    void insert(EntityBase entity);

    /**
     * @param entity
     *         to update
     */
    void update(EntityBase entity);

    /**
     * @param entity
     *         to delete
     * @return Entities that were cascade deleted
     */
    List<EntityBase> delete(EntityBase entity);

    /**
     * Open new transaction.
     */
    void openTransaction();

    /**
     * Commit the last opened transaction.
     */
    void commitTransaction();

    /**
     * Rollback the last opened transaction.
     */
    void rollbackTransaction();

    /**
     * Close the session.
     */
    void close();

}
