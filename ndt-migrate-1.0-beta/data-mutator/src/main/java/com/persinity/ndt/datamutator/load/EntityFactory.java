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

import java.util.Properties;

import com.persinity.common.db.Closeable;

/**
 * Provides way to create, update, delete entities.
 * Creates sessions for applying above operations.
 *
 * @author Ivan Dachev
 */
public interface EntityFactory extends Closeable {

    /**
     * Initialize the factory.
     *
     * @param dbConfigProps
     * @param dbConfigSource
     * @param entityPoolUtil
     */
    void init(Properties dbConfigProps, String dbConfigSource, EntityPoolUtil entityPoolUtil);

    /**
     * Used to load initial entities from DB.
     *
     * @param initialTableEntitiesRead
     */
    void readSchema(int initialTableEntitiesRead);

    /**
     * Create a new {@link EntitySession} used for Insert, Update and Delete operations.
     *
     * @return
     */
    EntitySession createSession();

    /**
     * Recreate schema tables.
     */
    void initSchema();

    /**
     * Cleanup all records in schema tables.
     */
    void cleanupSchema();

    /**
     * Drop schema tables.
     */
    void dropSchema();

    /**
     * @param id
     *         to use for primary key
     * @return new random entity
     */
    EntityBase createRandomEntity(final long id);

    /**
     * @return User friendly representation of connection information DB URL and user
     */
    String getConnectionInfo();

    /**
     * @return Information about the schema.
     */
    String getSchemaInfo();

}
