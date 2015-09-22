/**
 * Copyright (c) 2015 Persinity Inc.
 */
package com.persinity.ndt.transform;

import static com.persinity.common.StringUtils.format;
import static com.persinity.common.invariant.Invariant.notEmpty;
import static com.persinity.common.invariant.Invariant.notNull;

import java.util.List;

import com.persinity.common.collection.DirectedEdge;
import com.persinity.common.db.RelDb;
import com.persinity.common.db.metainfo.Col;

/**
 * Execute insert prepared DML statement on duplicate keys failure execute failure function.
 *
 * @author Ivan Dachev
 */
public class InsertOnFailureTemplateFunc extends ParamDmlFunc {

    public InsertOnFailureTemplateFunc(final String insertSql, final List<Col> cols, final ParamDmlFunc failureF) {
        super(format("{} ON FAILURE {}", insertSql, failureF), cols);

        notEmpty(insertSql);
        notNull(failureF);

        this.insertSql = insertSql;
        this.failureF = failureF;
    }

    @Override
    public Integer apply(final DirectedEdge<RelDb, List<?>> input) {
        try {
            return input.src().executePreparedDml(insertSql, input.dst());
        } catch (final RuntimeException e) {
            final Throwable cause = e.getCause();
            if (input.src().getSqlStrategy().isIntegrityConstraintViolation(cause)) {
                return failureF.apply(input);
            }
            throw e;
        }
    }

    private final String insertSql;
    private final ParamDmlFunc failureF;
}
