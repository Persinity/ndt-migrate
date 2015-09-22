/*
 *
 *  * Copyright (c) 2015 Persinity Inc.
 *  *
 *
 */

package com.persinity.ndt.datamutator;

import com.persinity.common.invariant.Invariant;

/**
 * @author Ivo Yanakiev
 */
public enum LoadType {
    TIME("i"), TRANSACTIONS("r");

    LoadType(String value) {
        Invariant.notNull(value);
        this.value = value;
    }

    public String getUpperCaseValue() {
        return value.toUpperCase();
    }

    public String getLowerCaseValue() {
        return value.toLowerCase();
    }

    public static LoadType getByString(String value) {

        for (LoadType loadType : values()) {
            if (loadType.getLowerCaseValue().equalsIgnoreCase(value)) {
                return loadType;
            }
        }
        throw new RuntimeException("Invalid load type: " + value);
    }

    private final String value;
}
