/**
 * Copyright (c) 2015 Persinity Inc.
 */
package com.persinity.ndt.transform;

import static com.persinity.common.StringUtils.format;
import static com.persinity.common.invariant.Invariant.notEmpty;

import java.util.Collections;
import java.util.List;
import java.util.StringTokenizer;

import com.persinity.common.collection.DirectedEdge;
import com.persinity.common.db.RelDb;
import com.persinity.common.db.metainfo.Col;
import com.persinity.common.db.metainfo.SqlFormatter;

/**
 * Represents parameterized relational DML function.
 * 
 * @author Doichin Yordanov
 */
public class ParamDmlFunc extends BaseRelFunc<DirectedEdge<RelDb, List<?>>, Integer> {

    public ParamDmlFunc(final String sql) {
        super(sql);
        cols = Collections.emptyList();
    }

    public ParamDmlFunc(final String sql, final List<Col> cols) {
        super(sql);
        notEmpty(cols, "cols");
        this.cols = cols;
    }

    /**
     * @return the columns for which the List input should be ordered.
     */
    public List<Col> getCols() {
        return cols;
    }

    @Override
    public Integer apply(final DirectedEdge<RelDb, List<?>> input) {
        return input.src().executePreparedDml(getSql(), input.dst());
    }

    @Override
    public String toString() {
        if (toString == null) {
            final String tableName = SqlFormatter.getTableClause(getSql());
            String dmlClause = "MODIFY";
            dmlClause = new StringTokenizer(getSql()).nextToken();
            toString = format("{}({})...", dmlClause, tableName);
        }
        return toString;
    }

    private final List<Col> cols;
    private String toString;
}
