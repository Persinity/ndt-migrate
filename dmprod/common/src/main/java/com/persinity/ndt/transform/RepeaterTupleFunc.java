/**
 * Copyright (c) 2015 Persinity Inc.
 */
package com.persinity.ndt.transform;

import java.util.Iterator;
import java.util.Map;

import com.persinity.common.fp.RepeaterFunc;

/**
 * @author Doichin Yordanov
 * 
 */
public class RepeaterTupleFunc implements TupleFunc {

    private final RepeaterFunc<Iterator<Map<String, Object>>> repeater;

    public RepeaterTupleFunc() {
        repeater = new RepeaterFunc<Iterator<Map<String, Object>>>();
    }

    @Override
    public Iterator<Map<String, Object>> apply(final Iterator<Map<String, Object>> input) {
        return repeater.apply(input);
    }

    @Override
    public String toString() {
        return this.getClass().getSimpleName();
    }

}
