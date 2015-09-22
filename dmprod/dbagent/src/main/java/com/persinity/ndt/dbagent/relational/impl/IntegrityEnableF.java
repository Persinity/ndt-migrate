/**
 * Copyright (c) 2015 Persinity Inc.
 */

package com.persinity.ndt.dbagent.relational.impl;

import com.google.common.base.Function;
import com.persinity.common.StringUtils;
import com.persinity.common.db.RelDb;
import com.persinity.ndt.dbagent.relational.SchemaInfo;

/**
 * Enables integrity constraints in a {@link RelDb}
 *
 * @author dyordanov
 */
public class IntegrityEnableF implements Function<RelDb, RelDb> {

    /**
     * @param ndtUserName
     *         to enable integrity constrains for
     */
    public IntegrityEnableF(final String ndtUserName) {
        this.ndtUserName = ndtUserName;
    }

    @Override
    public RelDb apply(final RelDb input) {
        input.executeSp(StringUtils.format("{}.{}", ndtUserName, SchemaInfo.SP_NDT_SCHEMA_INTEGRITY_ENABLE));
        return input;
    }

    private final String ndtUserName;
}
