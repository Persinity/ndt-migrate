/**
 * Copyright (c) 2015 Persinity Inc.
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
