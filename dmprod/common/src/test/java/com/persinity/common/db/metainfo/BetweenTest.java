/**
 * Copyright (c) 2015 Persinity Inc.
 */
package com.persinity.common.db.metainfo;

import org.junit.Assert;
import org.junit.Test;

import com.persinity.common.collection.DirectedEdge;

/**
 * @author Doichin Yordanov
 */
public class BetweenTest {

    /**
     * Test method for
     * {@link com.persinity.common.db.metainfo.Between#Between(com.persinity.common.db.metainfo.Col, com.persinity.common.collection.Pair)}
     * with invalid input.
     */
    @Test(expected = NullPointerException.class)
    public void testBetweenInvalidAll() {
        new Between(null, null);
    }

    /**
     * Test method for
     * {@link com.persinity.common.db.metainfo.Between#Between(com.persinity.common.db.metainfo.Col, com.persinity.common.collection.Pair)}
     * with invalid input.
     */
    @Test(expected = NullPointerException.class)
    public void testBetweenNullPair() {
        new Between(new Col("col"), null);
    }

    /**
     * Test method for
     * {@link com.persinity.common.db.metainfo.Between#Between(com.persinity.common.db.metainfo.Col, com.persinity.common.collection.Pair)}
     * with invalid input.
     */
    @Test(expected = NullPointerException.class)
    public void testBetweenInvalidCol() {
        new Between(null, new DirectedEdge<Integer, Integer>(1, 2));
    }

    /**
     * Test method for {@link com.persinity.common.db.metainfo.Between#getCol()}.
     */
    @Test
    public void testGetCol() {
        final Col col = new Col("test");
        Assert.assertEquals(col, new Between(col, new DirectedEdge<Integer, Integer>(1, 2)).getCol());
    }

    /**
     * Test method for {@link com.persinity.common.db.metainfo.Between#getValue()}.
     */
    @Test
    public void testGetValue() {
        final DirectedEdge<?, ?> val = new DirectedEdge<Integer, Integer>(1, 2);
        Assert.assertEquals(val, new Between(new Col("test"), val).getValue());
    }

    /**
     * Test method for {@link com.persinity.common.db.metainfo.Between#toString()}.
     */
    @Test
    public void testToString() {
        Assert.assertEquals("test BETWEEN 1 AND 2",
                new Between(new Col("test"), new DirectedEdge<Integer, Integer>(1, 2)).toString());
    }

}
