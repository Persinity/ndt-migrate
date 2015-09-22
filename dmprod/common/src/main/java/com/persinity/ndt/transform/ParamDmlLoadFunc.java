/**
 * Copyright (c) 2015 Persinity Inc.
 */
package com.persinity.ndt.transform;

import static com.persinity.common.StringUtils.format;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import com.persinity.common.collection.DirectedEdge;
import com.persinity.common.db.RelDb;
import com.persinity.common.db.metainfo.Col;

/**
 * Represents load function for parameterized relational DML.
 * 
 * TODO think for a JDBC batch for optimized DML performance
 * 
 * @author Ivan Dachev
 */
public class ParamDmlLoadFunc implements RelLoadFunc {

    public ParamDmlLoadFunc(final ParamDmlFunc paramDmlFunc) {
        this.paramDmlFunc = paramDmlFunc;
    }

    @SuppressWarnings("ConstantConditions")
    @Override
    public Integer apply(final DirectedEdge<RelDb, Iterator<Map<String, Object>>> dbIteratorPair) {
        final Iterator<Map<String, Object>> dataIt = dbIteratorPair.dst();
        int recCount = 0;
        while (dataIt.hasNext()) {
            final Map<String, Object> rsRec = dataIt.next();
            final List<Object> loadParams = rsRecToParams(rsRec);
            recCount += paramDmlFunc.apply(new DirectedEdge<RelDb, List<?>>(dbIteratorPair.src(), loadParams));
        }
        return recCount;
    }

    /**
     * @return ParamDmlFunc that works on
     */
    public ParamDmlFunc getParamDmlFunc() {
        return paramDmlFunc;
    }

    @Override
    public String toString() {
        if (toString == null) {
            toString = format("{}({})", this.getClass().getSimpleName(), paramDmlFunc);
        }
        return toString;
    }

    private List<Object> rsRecToParams(final Map<String, Object> rsRec) {
        final List<Object> loadParams = new LinkedList<>();
        for (final Col col : paramDmlFunc.getCols()) {
            final Object param = rsRec.get(col.getName());
            loadParams.add(param);
        }
        return loadParams;
    }

    private final ParamDmlFunc paramDmlFunc;
    private String toString;
}
