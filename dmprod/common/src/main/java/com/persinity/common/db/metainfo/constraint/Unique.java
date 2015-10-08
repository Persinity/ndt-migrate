package com.persinity.common.db.metainfo.constraint;

import java.util.Set;

import com.persinity.common.db.metainfo.Col;

/**
 * @author Ivo Yanakiev
 */
public class Unique extends Constraint {

    public Unique(final String table, final Set<Col> columns) {
        this(Unique.class.getSimpleName(), table, columns);
    }

    public Unique(final String name, final String table, final Set<Col> columns) {
        super(name, table, columns);
    }

}
