/**
 * Copyright (c) 2015 Persinity Inc.
 */
package com.persinity.common.collection;

import org.junit.Assert;
import org.junit.Test;

/**
 * @author Doichin Yordanov
 */
public class DirectedEdgeTest {

    /**
     * Test method for {@link DirectedEdge#DirectedEdge(Object, Object)) with invalid input
     */
    @Test(expected = IllegalArgumentException.class)
    public void testDirectedEdgeInvalid() {
        new DirectedEdge<>(null, null);
    }

    /**
     * Test method for {@link com.persinity.common.collection.DirectedEdge#src()}.
     */
    @Test
    public void testSrc() {
        final DirectedEdge<Integer, Integer> e = new DirectedEdge<>(0, 1);
        Assert.assertEquals(Integer.valueOf(0), e.src());
    }

    /**
     * Test method for {@link com.persinity.common.collection.DirectedEdge#dst()}.
     */
    @Test
    public void testDst() {
        final DirectedEdge<Integer, Integer> e = new DirectedEdge<>(0, 1);
        Assert.assertEquals(Integer.valueOf(1), e.dst());
    }

    /**
     * Test method for {@link com.persinity.common.collection.DirectedEdge#equals(java.lang.Object)}.
     */
    @Test
    public void testEqualsHashCode() {
        final DirectedEdge<Integer, Integer> eNull11 = new DirectedEdge<>(null, 1);
        final DirectedEdge<Integer, Integer> eNull12 = new DirectedEdge<>(null, 1);
        final DirectedEdge<Integer, Integer> eNull2 = new DirectedEdge<>(null, 2);

        final DirectedEdge<Integer, Integer> e11Null = new DirectedEdge<>(1, null);
        final DirectedEdge<Integer, Integer> e12Null = new DirectedEdge<>(1, null);
        final DirectedEdge<Integer, Integer> e2Null = new DirectedEdge<>(2, null);

        final DirectedEdge<Integer, Integer> e = new DirectedEdge<>(0, 1);
        final DirectedEdge<Integer, Integer> eSame = new DirectedEdge<>(0, 1);
        final DirectedEdge<Integer, Integer> eDiff = new DirectedEdge<>(1, 2);

        Assert.assertEquals(eNull11, eNull11);
        Assert.assertEquals(eNull11.hashCode(), eNull11.hashCode());
        Assert.assertEquals(eNull11, eNull12);
        Assert.assertEquals(eNull11.hashCode(), eNull12.hashCode());
        Assert.assertEquals(eNull12, eNull11);
        Assert.assertNotEquals(eNull11, eNull2);
        Assert.assertNotEquals(eNull11, null);
        Assert.assertNotEquals(eNull11, e11Null);
        Assert.assertNotEquals(eNull11, e2Null);

        Assert.assertEquals(e11Null, e11Null);
        Assert.assertEquals(e11Null.hashCode(), e11Null.hashCode());
        Assert.assertEquals(e11Null, e12Null);
        Assert.assertEquals(e11Null.hashCode(), e12Null.hashCode());
        Assert.assertEquals(e12Null, e11Null);
        Assert.assertNotEquals(e11Null, e2Null);
        Assert.assertNotEquals(e11Null, null);
        Assert.assertNotEquals(e11Null, eNull11);
        Assert.assertNotEquals(e11Null, eNull2);

        Assert.assertEquals(e, e);
        Assert.assertEquals(e.hashCode(), e.hashCode());
        Assert.assertEquals(e, eSame);
        Assert.assertEquals(e.hashCode(), eSame.hashCode());
        Assert.assertEquals(eSame, e);
        Assert.assertNotEquals(e, eDiff);
        Assert.assertNotEquals(e, null);
        Assert.assertNotEquals(e, e11Null);
        Assert.assertNotEquals(eDiff, e12Null);
    }
}
