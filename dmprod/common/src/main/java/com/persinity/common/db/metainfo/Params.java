/**
 * Copyright (c) 2015 Persinity Inc.
 */
package com.persinity.common.db.metainfo;

import java.util.ArrayList;
import java.util.List;

import com.persinity.common.MathUtil;
import com.persinity.common.collection.CollectionUtils;
import com.persinity.common.invariant.Invariant;

/**
 * Represents list of "?" suitable for parametrized SQL statement.<BR>
 *
 * @author Doichin Yordanov
 */
public class Params implements SqlPredicate {
    public static enum ParameterCount {
        /**
         * The list is populated with n "?", where n = i^2, i - natural number, so that n > count and n/2 < count.
         */
        ROUNDED_BY_PWR_OF_TWO,
        /**
         * The list is populated with number of "?" as requested.
         */
        EXACT
    }

    private final String sql;
    private final int size;

    /**
     * @param count
     *         Input count
     * @param pCount
     *         The resulting count mode
     */
    public Params(final int count, final ParameterCount pCount) {
        Invariant.assertArg(count > 0, "count");
        int paramCount = 0;
        if (pCount.equals(ParameterCount.ROUNDED_BY_PWR_OF_TWO)) {
            paramCount = MathUtil.ceilingByPowerOfTwo(count);
        } else {
            paramCount = count;
        }
        final List<String> paramPlaceHolders = new ArrayList<>(paramCount);
        for (int i = 0; i < paramCount; i++) {
            paramPlaceHolders.add("?");
        }
        size = paramPlaceHolders.size();
        sql = CollectionUtils.implode(paramPlaceHolders, ", ");
    }

    /**
     * @return The number of parameters (n)
     */
    public int getSize() {
        return size;
    }

    @Override
    public String toString() {
        return sql;
    }

}
