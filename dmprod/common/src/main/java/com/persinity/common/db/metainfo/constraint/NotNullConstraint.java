package com.persinity.common.db.metainfo.constraint;

import java.util.Set;

import com.persinity.common.db.metainfo.Col;

/**
 * @author Ivo Yanakiev
 */
public class NotNullConstraint extends Constraint {

    public NotNullConstraint(final String table, final Set<Col> columns) {
        super(NotNullConstraint.class.getSimpleName(), table, columns);
    }

    public NotNullConstraint(final String name, final String table, final Set<Col> columns) {
        super(name, table, columns);
    }

}
