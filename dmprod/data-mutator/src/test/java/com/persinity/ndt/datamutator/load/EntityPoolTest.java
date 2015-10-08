/**
 * Copyright (c) 2015 Persinity Inc.
 */
package com.persinity.ndt.datamutator.load;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import org.junit.Test;

/**
 * @author Ivan Dachev
 */
public class EntityPoolTest {

    @Test
    public void testCalculateMaxAllowedForDelete() throws Exception {
        int res = EntityPool.calculateMaxAllowedForDelete(2, 1, 1);
        assertThat(res, is(0));

        res = EntityPool.calculateMaxAllowedForDelete(100, 30, 70);
        assertThat(res, is(0));

        res = EntityPool.calculateMaxAllowedForDelete(100, 30, 69);
        assertThat(res, is(1));

        res = EntityPool.calculateMaxAllowedForDelete(100, 30, 65);
        assertThat(res, is(5));

        res = EntityPool.calculateMaxAllowedForDelete(256, 78, 15);
        assertThat(res, is(140));

        res = EntityPool.calculateMaxAllowedForDelete(256, 70, 70);
        assertThat(res, is(7));
    }
}