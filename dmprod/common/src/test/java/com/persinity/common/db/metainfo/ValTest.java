/**
 * Copyright (c) 2015 Persinity Inc.
 */
package com.persinity.common.db.metainfo;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import org.junit.Test;

/**
 * @author Ivan Dachev
 */
public class ValTest {

    @Test
    public void testToString() throws Exception {
        assertThat(new Val("x").toString(), is("x"));
    }

    @Test(expected = NullPointerException.class)
    public void testToString_NullLeft() throws Exception {
        new Val(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testToString_EmptyLeft() throws Exception {
        new Val("");
    }
}