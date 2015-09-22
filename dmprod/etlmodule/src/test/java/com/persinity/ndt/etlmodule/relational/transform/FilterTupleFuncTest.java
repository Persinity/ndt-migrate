/**
 * Copyright (c) 2015 Persinity Inc.
 */

package com.persinity.ndt.etlmodule.relational.transform;

import static com.persinity.test.TestUtil.assertNextIs;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;

import com.persinity.common.db.metainfo.Col;
import com.persinity.common.db.metainfo.SqlFilter;

/**
 * @author dyordanov
 */
public class FilterTupleFuncTest {

    @Before
    public void setUp() throws Exception {
        tuples = Arrays.asList(tuple(COL, 1), tuple(COL, 2), tuple(COL, 2), tuple(COL, 3), tuple(COL, 3), tuple(COL, 2),
                tuple(COL, 5), tuple(COL, 4)).iterator();

    }

    @Test(expected = NullPointerException.class)
    public void testFilterTupleFunc_InvalidInput() {
        new FilterTupleFunc(null, false);
    }

    @Test
    public void testApply() {
        final FilterTupleFunc testee = new FilterTupleFunc(filter, false);
        final Iterator<Map<String, Object>> result = testee.apply(tuples);
        assertNextIs(result, tuple(COL, 1));
        assertNextIs(result, tuple(COL, 3));
        assertNextIs(result, tuple(COL, 3));
        assertNextIs(result, tuple(COL, 5));
        assertNextIs(result, tuple(COL, 4));
    }

    @Test
    public void testApplyNegate() {
        final FilterTupleFunc testee = new FilterTupleFunc(filter, true);
        final Iterator<Map<String, Object>> result = testee.apply(tuples);
        assertNextIs(result, tuple(COL, 2));
        assertNextIs(result, tuple(COL, 2));
        assertNextIs(result, tuple(COL, 2));
    }

    private static Map<String, Object> tuple(final Col col, final Object val) {
        final Map<String, Object> result = new HashMap<>();
        result.put(col.getName(), val);
        return result;
    }

    private static final Col COL = new Col("col");
    private static final Integer FILTER_VAL = 2;

    private SqlFilter<?> filter = new SqlFilter<Integer>() {
        @Override
        public Col getCol() {
            return COL;
        }

        @Override
        public Integer getValue() {
            return FILTER_VAL;
        }
    };

    private Iterator<Map<String, Object>> tuples;
}