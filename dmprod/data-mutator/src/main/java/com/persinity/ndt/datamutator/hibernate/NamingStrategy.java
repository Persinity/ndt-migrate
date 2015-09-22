/**
 * Copyright (c) 2015 Persinity Inc.
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
