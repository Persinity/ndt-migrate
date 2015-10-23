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

package com.persinity.common.db.metainfo;

import java.util.Set;

import com.persinity.common.db.metainfo.constraint.FK;
import com.persinity.common.db.metainfo.constraint.PK;

/**
 * Database schema reflection.
 *
 * @author dyordanov
 */
public interface Schema {
    /**
     * @param tableName
     * @return Sorted set of columns for the given table or empty set if none found
     */
    Set<Col> getTableCols(String tableName);

    /**
     * @param tableName
     * @return The FKs that are defined in this table
     */
    Set<FK> getTableFks(String tableName);

    /**
     * @return The set of tables from the schema of the current connection or empty set if none found.
     */
    Set<String> getTableNames();

    /**
     * @param tableName
     * @return The PK constraint or null if none found
     */
    PK getTablePk(String tableName);

    /**
     * @param pkName
     *         PK name to look table for
     * @return Table name for given PK name
     */
    String getTableName(String pkName);

    /**
     * @return The user name of the schema.
     */
    String getUserName();
}
