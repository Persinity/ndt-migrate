/**
 * Copyright (c) 2015 Persinity Inc.
 */
package com.persinity.ndt.etlmodule.relational.migrate;

import java.util.Set;

import com.persinity.ndt.etlmodule.relational.common.EntityDagFunc;
import com.persinity.ndt.transform.EntitiesDag;

/**
 * {@link EntityDagFunc} that disregards entity relations and returns flat relations-free {@link EntitiesDag} of the
 * entities passed to its {@link EntityDagFunc#apply(Set)} method.
 *
 * @author Doichin Yordanov
 */
public class RepeaterEntityDagFunc implements EntityDagFunc {
    @Override
    public EntitiesDag apply(final Set<String> input) {
        return new EntitiesDag(input);
    }
}
