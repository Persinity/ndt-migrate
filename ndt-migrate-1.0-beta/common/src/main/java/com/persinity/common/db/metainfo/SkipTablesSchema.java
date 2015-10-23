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

import static com.persinity.common.StringUtils.format;
import static com.persinity.common.StringUtils.formatObj;
import static com.persinity.common.invariant.Invariant.notNull;

import java.util.Collection;
import java.util.Set;

import com.google.common.collect.Sets;
import com.persinity.common.db.metainfo.constraint.FK;
import com.persinity.common.db.metainfo.constraint.PK;

/**
 * @author dyordanov
 */
public class SkipTablesSchema implements Schema {

    public SkipTablesSchema(final Collection<String> skipTables, final Schema schema) {
        notNull(skipTables);
        notNull(schema);

        this.skipTables = Sets.newHashSet(skipTables);
        this.schema = schema;
    }

    @Override
    public Set<Col> getTableCols(final String tableName) {
        return schema.getTableCols(checkSkipped(tableName));
    }

    @Override
    public Set<FK> getTableFks(final String tableName) {
        return schema.getTableFks(checkSkipped(tableName));
    }

    @Override
    public Set<String> getTableNames() {
        final Set<String> tableNames = schema.getTableNames();
        tableNames.removeAll(skipTables);
        return tableNames;
    }

    @Override
    public PK getTablePk(final String tableName) {
        return schema.getTablePk(checkSkipped(tableName));
    }

    @Override
    public String getTableName(final String pkName) {
        String tableName = schema.getTableName(pkName);
        if (skipTables.contains(tableName)) {
            tableName = null;
        }
        return tableName;
    }

    @Override
    public String getUserName() {
        return schema.getUserName();
    }

    @Override
    public String toString() {
        if (toString == null) {
            toString = format("{}({}, {})", formatObj(this), skipTables, schema);
        }
        return toString;
    }

    private String checkSkipped(final String tableName) {
        if (skipTables.contains(tableName)) {
            throw new IllegalArgumentException(format("\"{}\" is in the tables skip list!", tableName));
        } else {
            return tableName;
        }
    }

    private final Set<String> skipTables;
    private final Schema schema;

    private String toString;
}
