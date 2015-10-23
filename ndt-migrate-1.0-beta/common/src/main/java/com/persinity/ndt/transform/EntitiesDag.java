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
