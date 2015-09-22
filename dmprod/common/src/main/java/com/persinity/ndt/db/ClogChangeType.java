/**
 * Copyright (c) 2015 Persinity Inc.
 */
package com.persinity.ndt.db;

/**
 * Describes the type of the recorded change in the change logs
 *
 * @author Doichin Yordanov
 */
public enum ClogChangeType {
    /**
     * Inserted record.
     */
    I,

    /**
     * Updated record
     */
    U,

    /**
     * Deleted record
     */
    D
}
