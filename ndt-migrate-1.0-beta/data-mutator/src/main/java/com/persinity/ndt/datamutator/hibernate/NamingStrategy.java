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
package com.persinity.ndt.datamutator.hibernate;

import org.hibernate.cfg.EJB3NamingStrategy;

/**
 * @author Ivan Dachev
 */
public class NamingStrategy extends EJB3NamingStrategy {
    public NamingStrategy(final String prefix) {
        this.prefix = prefix;
    }

    @Override
    public String classToTableName(final String className) {
        return this.addPrefix(super.classToTableName(className));
    }

    @Override
    public String collectionTableName(final String ownerEntity, final String ownerEntityTable,
            final String associatedEntity, final String associatedEntityTable, final String propertyName) {
        return this.addPrefix(
                super.collectionTableName(ownerEntity, ownerEntityTable, associatedEntity, associatedEntityTable,
                        propertyName));
    }

    @Override
    public String logicalCollectionTableName(final String tableName, final String ownerEntityTable,
            final String associatedEntityTable, final String propertyName) {
        return this.addPrefix(
                super.logicalCollectionTableName(tableName, ownerEntityTable, associatedEntityTable, propertyName));
    }

    private String addPrefix(final String tableName) {
        return prefix + tableName;
    }

    private final String prefix;

    private static final long serialVersionUID = 7688299535158049164L;
}
