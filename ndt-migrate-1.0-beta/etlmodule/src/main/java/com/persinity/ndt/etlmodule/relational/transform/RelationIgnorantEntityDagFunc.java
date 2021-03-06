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
