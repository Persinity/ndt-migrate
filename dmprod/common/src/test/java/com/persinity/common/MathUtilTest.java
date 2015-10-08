/**
 * Copyright (c) 2015 Persinity Inc.
 */
package com.persinity.common;

import static com.persinity.common.StringUtils.format;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;

import java.util.Collection;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;

import com.persinity.common.collection.DirectedEdge;

/**
 * @author Doichin Yordanov
 */
public class MathUtilTest {

    /**
     * Test method for {@link com.persinity.common.MathUtil#ceilingByPowerOfTwo(int)} with argument of 0.
     */
    @Test
    public void testCeilingByPowerOfTwoZero() {
        final int actual = MathUtil.ceilingByPowerOfTwo(0);
        Assert.assertEquals(1, actual);
    }

    /**
     * Test method for {@link com.persinity.common.MathUtil#ceilingByPowerOfTwo(int)} with argument of 1.
     */
    @Test
    public void testCeilingByPowerOfTwoOne() {
        final int actual = MathUtil.ceilingByPowerOfTwo(1);
        Assert.assertEquals(1, actual);
    }

    /**
     * Test method for {@link com.persinity.common.MathUtil#ceilingByPowerOfTwo(int)} with argument of 2.
     */
    @Test
    public void testCeilingByPowerOfTwoTwo() {
        final int actual = MathUtil.ceilingByPowerOfTwo(2);
        Assert.assertEquals(2, actual);
    }

    /**
     * Test method for {@link com.persinity.common.MathUtil#ceilingByPowerOfTwo(int)} with argument of 99.
     */
    @Test
    public void testCeilingByPowerOfTwo99() {
        final int actual = MathUtil.ceilingByPowerOfTwo(99);
        Assert.assertEquals(128, actual);
    }

    /**
     * Test method for {@link com.persinity.common.MathUtil#ceilingByPowerOfTwo(int)} with argument of 100.
     */
    @Test
    public void testCeilingByPowerOfTwo100() {
        final int actual = MathUtil.ceilingByPowerOfTwo(100);
        Assert.assertEquals(128, actual);
    }

    /**
     * Test method for {@link com.persinity.common.MathUtil#ceilingByPowerOfTwo(int)} with argument of 100.
     */
    @Test
    public void testCeilingByPowerOfTwoUpTo1024Power2() {
        for (int j = 1; j < 1024; j = j << 1) {
            for (int i = j + 1; i <= j << 1; i++) {
                final int actual = MathUtil.ceilingByPowerOfTwo(i);
                Assert.assertEquals(format("Unexpected for i: {}", i), j << 1, actual);
            }
        }
    }

    /**
     * Test method for {@link com.persinity.common.MathUtil#partition(int, int, int) with invalid size
     */
    @Test(expected = IllegalArgumentException.class)
    public void testPartitionInvalidInput1() {
        MathUtil.partition(1, 2, 0);
    }

    /**
     * Test method for {@link com.persinity.common.MathUtil#partition(int, int, int) with invalid minKey
     */
    @Test(expected = IllegalArgumentException.class)
    public void testPartitionInvalidInput2() {
        MathUtil.partition(-1, 2, 1);
    }

    /**
     * Test method for {@link com.persinity.common.MathUtil#partition(int, int, int) with invalid maxKey
     */
    @Test(expected = IllegalArgumentException.class)
    public void testPartitionInvalidInput3() {
        MathUtil.partition(1, -2, 1);
    }

    /**
     * Test method for {@link com.persinity.common.MathUtil#partition(int, int, int) with maxKey < minKey
     */
    @Test
    public void testPartitionEmptyRange() {
        final Collection<? extends Object> actual = MathUtil.partition(2, 1, 1);
        assertThat(actual, is(empty()));
    }

    /**
     * Test method for {@link MathUtil#partition(int, int, int) with empty range
     */
    @Test
    public void testPartitionEmpty() {
        final List<DirectedEdge<Integer, Integer>> actual = MathUtil.partition(0, 0, 2);
        assertEquals(1, actual.size());
        final DirectedEdge<Integer, Integer> partition = actual.get(0);
        verifyPartition(partition, 0, 0);
    }

    /**
     * Test method for {@link MathUtil#partition(int, int, int) with exact range
     */
    @Test
    public void testPartitionExactRange() {
        final List<DirectedEdge<Integer, Integer>> actual = MathUtil.partition(1, 10, 2);
        assertEquals(5, actual.size());
        verifyPartition(actual.get(0), 1, 2);
        verifyPartition(actual.get(1), 3, 4);
        verifyPartition(actual.get(2), 5, 6);
        verifyPartition(actual.get(3), 7, 8);
        verifyPartition(actual.get(4), 9, 10);
    }

    /**
     * Test method for {@link MathUtil#partition(int, int, int) with non-exact range
     */
    @Test
    public void testPartitionNonExactRange() {
        final List<DirectedEdge<Integer, Integer>> actual = MathUtil.partition(1, 10, 3);
        assertEquals(4, actual.size());
        verifyPartition(actual.get(0), 1, 3);
        verifyPartition(actual.get(1), 4, 6);
        verifyPartition(actual.get(2), 7, 9);
        verifyPartition(actual.get(3), 10, 10);
    }

    /**
     * Test method for {@link MathUtil#partition(int, int, int) with empty range
     */
    @Test
    public void testPartitionSingleId() {
        final List<DirectedEdge<Integer, Integer>> actual = MathUtil.partition(0, 1, 10);
        assertEquals(1, actual.size());
        final DirectedEdge<Integer, Integer> partition = actual.get(0);
        verifyPartition(partition, 0, 1);
    }

    private void verifyPartition(final DirectedEdge<Integer, Integer> actual, final int l, final int r) {
        final DirectedEdge<Integer, Integer> expected = new DirectedEdge<Integer, Integer>(l, r);
        assertEquals(expected, actual);
    }
}
