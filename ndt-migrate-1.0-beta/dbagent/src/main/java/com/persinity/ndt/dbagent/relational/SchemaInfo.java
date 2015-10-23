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
package com.persinity.ndt.dbagent.relational;

import java.util.Set;

import com.persinity.common.db.metainfo.Col;
import com.persinity.common.db.metainfo.constraint.FK;
import com.persinity.common.db.metainfo.constraint.PK;

/**
 * Provides DB object definitions for the NDT agents
 *
 * @author Doichin Yordanov
 */
public interface SchemaInfo {

    String COL_GID = "gid";
    String COL_TID = "tid";
    String COL_CTYPE = "ctype";
    String COL_ID = "id";
    String COL_LAST_GID = "last_gid";
    String COL_TABLE_NAME = "tab_name";
    String TAB_TRLOG = "trlog";
    String TAB_NDT_LOG = "ndt_log";
    String COL_STATUS = "status";
    String SP_NDT_COMMON = "ndt_common";
    String SP_NDT_CLOG = "clog";
    String SP_NDT_SCHEMA = "ndt_schema";
    String SP_NDT_SCHEMA_INTEGRITY_DISABLE = SP_NDT_SCHEMA + "." + "integrity_disable";
    String SP_NDT_SCHEMA_INTEGRITY_ENABLE = SP_NDT_SCHEMA + "." + "integrity_enable";
    String SEQ_GID = "seq_gid";

    enum ChangeType {
        /**
         * Insert
         */
        I,
        /**
         * Update
         */
        U,
        /**
         * Delete
         */
        D
    }

    /**
     *
     */
    enum TrlogStatusType {
        /**
         * Loading
         */
        L,
        /**
         * Ready for process
         */
        R,
        /**
         * Processing
         */
        P
    }

    /**
     * @return The table names in the current schema
     */
    Set<String> getTableNames();

    /**
     * Generates DB object name by joining prefix and suffix.<BR>
     * If the resulting string is lengthier than the maximum DB name length, then the suffix is trimmed and unique
     * counter is added to guarantee that the trimmed name is unique across all trimmed names.
     *
     * @param prefix
     * @param suffix
     * @return
     */
    String newName(String prefix, String suffix);

    /**
     * Returns a change log name by given tracked table name.
     *
     * @param tableName
     * @return
     */
    String getClogTableName(String tableName);

    /**
     * Returns a CDC change log trigger name by given tracked table name.
     *
     * @param tableName
     * @return
     */
    String getClogTriggerName(String tableName);

    /**
     * @param tableName
     *         table name from the application schema
     * @return The column names listed in their order of definition in the table.
     */
    Set<Col> getTableCols(String tableName);

    /**
     * @param tableName
     *         table name from the application schema
     * @return The primary key constraint for the table or null if not exits.
     */
    PK getTablePk(String tableName);

    /**
     * @param tableName
     *         table name from the application schema
     * @return The FKs constraint for the table or empty if not exits.
     */
    Set<FK> getTableFks(String tableName);

    /**
     * Returns the column names of a change log table by given application schema table.
     *
     * @param tableName
     * @return The column names listed in their order of definition in the table.
     */
    Set<Col> getClogTableCols(String tableName);

    /**
     * Returns a CDC trlog trigger name by given tracked table name.
     *
     * @param tableName
     * @return
     */
    String getTrlogTriggerName(String tableName);

}