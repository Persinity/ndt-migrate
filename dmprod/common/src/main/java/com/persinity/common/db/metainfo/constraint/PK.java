package com.persinity.common.db.metainfo.constraint;

import java.util.Set;

import com.persinity.common.db.metainfo.Col;

/**
 * @author Ivo Yanakiev
 */
public class PK extends Unique {

    public PK(final String table, final Set<Col> columns) {
        this(PK.class.getSimpleName(), table, columns);
    }

    public PK(final String name, final String table, final Set<Col> columns) {
        super(name, table, columns);
    }

}
