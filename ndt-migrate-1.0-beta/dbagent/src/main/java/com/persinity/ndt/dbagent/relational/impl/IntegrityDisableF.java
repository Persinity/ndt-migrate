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
import com.persinity.common.StringUtils;
import com.persinity.common.db.RelDb;
import com.persinity.ndt.dbagent.relational.SchemaInfo;

/**
 * Disables integrity constraints in a {@link RelDb}
 *
 * @author dyordanov
 */
public class IntegrityDisableF implements Function<RelDb, RelDb> {

    /**
     * @param ndtUserName
     *         to disable integrity constrains for
     */
    public IntegrityDisableF(final String ndtUserName) {
        this.ndtUserName = ndtUserName;
    }

    @Override
    public RelDb apply(final RelDb input) {
        input.executeSp(StringUtils.format("{}.{}", ndtUserName, SchemaInfo.SP_NDT_SCHEMA_INTEGRITY_DISABLE));
        return input;
    }

    private final String ndtUserName;
}
