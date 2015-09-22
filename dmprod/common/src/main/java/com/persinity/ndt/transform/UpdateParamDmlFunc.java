/**
 * Copyright (c) 2015 Persinity Inc.
 */

package com.persinity.ndt.transform;

import static com.persinity.common.invariant.Invariant.assertArg;
import static com.persinity.common.invariant.Invariant.notEmpty;

import java.util.ArrayList;
import java.util.List;

import com.persinity.common.collection.DirectedEdge;
import com.persinity.common.db.RelDb;
import com.persinity.common.db.metainfo.Col;

/**
 * {@link ParamDmlFunc} that issues update statement.
 *
 * @author dyordanov
 */
public class UpdateParamDmlFunc extends ParamDmlFunc {

    /**
     * @param sql
     * @param cols
     *         taking part into the SET clause
     * @param updateIdCols
     *         taking part into the WHERE clause, must be subset of the cols list
     */
    public UpdateParamDmlFunc(final String sql, final List<Col> cols, final List<Col> updateIdCols) {
        super(sql, cols);
        notEmpty(updateIdCols);
        updateIdColIndxes = new ArrayList<>();

        for (final Col updateIdCol : updateIdCols) {
            final int indx = cols.indexOf(updateIdCol);
            assertArg(indx != -1, "Failed to find index for updateIdCol: {}", updateIdCol);
            updateIdColIndxes.add(indx);
        }
    }

    @Override
    public Integer apply(final DirectedEdge<RelDb, List<?>> input) {
        final List<Object> params = new ArrayList<>(input.dst().size() + 1);
        params.addAll(input.dst());
        for (int updateIdColIndex : updateIdColIndxes) {
            params.add(input.dst().get(updateIdColIndex));
        }

        final DirectedEdge<RelDb, List<?>> updateInput = new DirectedEdge<RelDb, List<?>>(input.src(), params);
        return super.apply(updateInput);
    }

    private final List<Integer> updateIdColIndxes;
}
