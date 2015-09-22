/*
 * Copyright (c) 2015 Persinity Inc.
 */

package com.persinity.ndt.datamutator.common;

/**
 * @author Ivo Yanakiev
 */
public interface IdGenerator {

    /**
     * Gets the next available ID.
     */
    long getNext();
}
