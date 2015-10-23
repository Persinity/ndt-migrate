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

import com.google.common.base.Function;
import com.persinity.common.db.RelDb;
import com.persinity.common.db.metainfo.constraint.FK;
import com.persinity.common.fp.Functor;
import com.persinity.ndt.dbagent.relational.AgentSqlStrategy;
import com.persinity.ndt.transform.RelFunc;

/**
 * Functor that returns function, which disables a FK constraint.
 *
 * @author Doichin Yordanov
 */
public class FkDisableFunctor implements Functor<RelDb, RelDb, FK, Function<RelDb, RelDb>> {

    public FkDisableFunctor(final AgentSqlStrategy sqlStrategy) {
        this.sqlStrategy = sqlStrategy;
    }

    @Override
    public Function<RelDb, RelDb> apply(final FK input) {
        final String sql = sqlStrategy.disableConstraint(input);
        final Function<RelDb, RelDb> result = new RelFunc(sql);
        return result;
    }

    private final AgentSqlStrategy sqlStrategy;

}
