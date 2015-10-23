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
