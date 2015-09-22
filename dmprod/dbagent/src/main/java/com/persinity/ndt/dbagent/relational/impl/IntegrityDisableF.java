/**
 * Copyright (c) 2015 Persinity Inc.
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
