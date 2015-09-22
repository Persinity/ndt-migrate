/*
 * Copyright (c) 2015 Persinity Inc.
 */

package com.persinity.ndt.datamutator.common;

/**
 * @author Ivo Yanakiev
 */
public class DefaultIdGenerator implements IdGenerator {

    public DefaultIdGenerator(long start) {
        this.sequence = start;
    }

    @Override
    public synchronized long getNext() {
        return sequence++;
    }

    private long sequence;
}
