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
