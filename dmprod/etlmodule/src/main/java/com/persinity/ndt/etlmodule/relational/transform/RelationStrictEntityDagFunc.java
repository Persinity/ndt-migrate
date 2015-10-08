/**
 * Copyright (c) 2015 Persinity Inc.
 */
package com.persinity.ndt.etlmodule.relational.transform;

import static com.persinity.common.StringUtils.format;

import java.util.Set;

import com.persinity.common.invariant.Invariant;
import com.persinity.ndt.etlmodule.relational.common.EntityDagFunc;
import com.persinity.ndt.transform.EntitiesDag;

/**
 * {@link EntityDagFunc} that honors entity relations strictly. It disregards the exact entities passed in the
 * {@link EntityDagFunc#apply(Object)} method and always returns DAG based on the supplied at construction time entities
 * DAG.
 *
 * @author Doichin Yordanov
 */
public class RelationStrictEntityDagFunc implements EntityDagFunc {

    private final EntitiesDag dag;

    public RelationStrictEntityDagFunc(final EntitiesDag dag) {
        Invariant.assertArg(dag != null);

        this.dag = dag;
    }

    @Override
    public EntitiesDag apply(final Set<String> input) {
        return dag;
    }

    @Override
    public String toString() {
        if (toString == null) {
            toString = format("{}@{}({})", getClass().getSimpleName(), Integer.toHexString(hashCode()),
                    dag.vertexSet());
        }
        return toString;
    }

    private String toString;
}
