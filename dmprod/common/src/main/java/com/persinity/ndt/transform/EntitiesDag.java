/**
 * Copyright (c) 2015 Persinity Inc.
 */
package com.persinity.ndt.transform;

import java.util.Collection;

import com.persinity.common.collection.Dag;
import com.persinity.common.db.metainfo.FKEdge;
import com.persinity.common.invariant.Invariant;

/**
 * Used to shorten the class definition and to be more domain oriented.
 *
 * @author Ivan Dachev
 */
public class EntitiesDag extends Dag<String, FKEdge> {

    public EntitiesDag() {
        super();
    }

    public EntitiesDag(final Collection<String> entities) {

        super(entities);
        Invariant.assertArg(entities != null);
    }

    private static final long serialVersionUID = -8523201920621771462L;
}
