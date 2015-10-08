/**
 * Copyright (c) 2015 Persinity Inc.
 */
package com.persinity.ndt.dbagent;

import com.google.common.base.Function;
import com.persinity.common.collection.Tree;
import com.persinity.common.db.RelDb;
import com.persinity.ndt.transform.EntitiesDag;

/**
 * Responsible for providing plans for maintaining schema and its referential integrity during migration.
 * Use with target DB.
 *
 * @author Doichin Yordanov
 */
public interface SchemaAgent<T extends Function<?, ?>> {
    /**
     * @return Plan of functions that break referential integrity cycles if such.
     */
    Tree<T> breakRefIntegrityCycles();

    /**
     * @return Graph representing the DB schema
     */
    EntitiesDag getSchema();

    /**
     * @return Plan of functions that recover entity referential integrity cycles if such.
     */
    Tree<T> renewRefIntegrityCycles();

    /**
     * @return Plan of functions that enable referential integrity
     */
    Tree<T> enableIntegrity();

    /**
     * @return Plan of functions that disables referential integrity
     */
    Tree<T> disableIntegrity();

    /**
     * @return Plan for unmounting the schema agent
     */
    Tree<Function<RelDb, RelDb>> umount();
}
