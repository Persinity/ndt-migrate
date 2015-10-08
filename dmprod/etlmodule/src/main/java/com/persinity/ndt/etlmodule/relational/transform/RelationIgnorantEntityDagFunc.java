/**
 * Copyright (c) 2015 Persinity Inc.
 */

package com.persinity.ndt.etlmodule.relational.transform;

import java.util.Set;

import com.persinity.ndt.etlmodule.relational.common.EntityDagFunc;
import com.persinity.ndt.transform.EntitiesDag;

/**
 * {@link EntityDagFunc} that disregards entity relations and returns flat relations-free {@link EntitiesDag} of the
 * {@link EntitiesDag} passed to its constructor.
 *
 * @author dyordanov
 */
public class RelationIgnorantEntityDagFunc implements EntityDagFunc {
    public RelationIgnorantEntityDagFunc(final EntitiesDag entityDag) {
        this.entityDag = new EntitiesDag(entityDag.vertexSet());
    }

    @Override
    public EntitiesDag apply(final Set<String> input) {
        return entityDag;
    }

    private final EntitiesDag entityDag;
}
