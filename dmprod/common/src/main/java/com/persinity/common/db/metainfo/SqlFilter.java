/**
 * Copyright (c) 2015 Persinity Inc.
 */
package com.persinity.common.db.metainfo;

/**
 * Represents a SQL WHERE clause: E.g. colName = colValue, or colName < colValue, etc.
 *
 * @author Doichin Yordanov
 */
public interface SqlFilter<V> extends SqlPredicate {
    /**
     * @return Filter column
     */
    Col getCol();

    /**
     * @return Filter value(s)
     */
    V getValue();
}
