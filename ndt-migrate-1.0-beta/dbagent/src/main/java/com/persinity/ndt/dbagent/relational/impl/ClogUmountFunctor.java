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
import com.persinity.common.fp.Functor;
import com.persinity.ndt.dbagent.relational.AgentSqlStrategy;
import com.persinity.ndt.dbagent.relational.SchemaInfo;
import com.persinity.ndt.transform.RelFunc;

/**
 * Functor that returns function, which drops CLOG data structures
 *
 * @author Doichin Yordanov
 */
public class ClogUmountFunctor implements Functor<RelDb, RelDb, String, Function<RelDb, RelDb>> {

    public ClogUmountFunctor(final SchemaInfo schema, final AgentSqlStrategy strategy) {
        assert schema != null && strategy != null;
        this.schema = schema;
        this.strategy = strategy;
    }

    @Override
    public Function<RelDb, RelDb> apply(final String tableName) {
        final String sql = strategy.dropTable(schema.getClogTableName(tableName));
        final RelFunc clogFunc = new RelFunc(sql);
        return clogFunc;
    }

    private final SchemaInfo schema;
    private final AgentSqlStrategy strategy;
}
