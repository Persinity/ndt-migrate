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
package com.persinity.ndt.dbagent.relational.impl;

import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import com.google.common.base.Function;
import com.persinity.common.collection.CollectionUtils;
import com.persinity.common.collection.Tree;
import com.persinity.common.db.RelDb;
import com.persinity.common.fp.Functor;

/**
 * Plan builder utils.
 *
 * @author Doichin Yordanov
 */
public class PlanBuilder {

    /**
     * Builds a tree plan of functions using the supplied functor.
     *
     * @param args
     * @param functor
     * @return
     */
    public <T> Tree<Function<RelDb, RelDb>> build(final Set<T> args,
            final Functor<RelDb, RelDb, T, Function<RelDb, RelDb>> functor) {
        final List<Function<RelDb, RelDb>> funcs = new LinkedList<>();
        for (final T arg : args) {
            final Function<RelDb, RelDb> func = functor.apply(arg);
            funcs.add(func);
        }
        final Tree<Function<RelDb, RelDb>> plan = CollectionUtils.newTree(funcs);
        return plan;
    }

}
