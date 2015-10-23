/*
 * Copyright 2015 Persinity Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.persinity.common.collection;

import java.util.Arrays;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;

/**
 * @author Doichin Yordanov
 */
public class SingleBranchTreeTest {

    /**
     * Test method for {@link SingleBranchTree#getRoot()}.
     */
    @Test
    public void testGetRoot() {
        final SingleBranchTree<Integer> testee = new SingleBranchTree<>(Arrays.asList(1, 2, 3));
        Assert.assertEquals(new Integer(1), testee.getRoot());
    }

    /**
     * Test method for {@link SingleBranchTree#SingleBranchTree(List)}.
     */
    @SuppressWarnings("unchecked")
    @Test(expected = NullPointerException.class)
    public void testSingeBranchTreeNull() {
        new SingleBranchTree(null);
    }

    /**
     * Test method for {@link SingleBranchTree#children(java.lang.Object)}.
     */
    @Test
    public void testChildrenT() {
        final SingleBranchTree<Integer> testee = new SingleBranchTree<>(Arrays.asList(1, 2, 3));
        Assert.assertEquals(Arrays.asList(2), testee.children(1));
        Assert.assertEquals(Arrays.asList(3), testee.children(2));
    }

    /**
     * Test method for {@link SingleBranchTree#equals(java.lang.Object)}.
     */
    @Test
    public void testEqualsObjectHashCode() {
        final SingleBranchTree<Integer> o11 = new SingleBranchTree<>(Arrays.asList(1, 2, 3));
        final SingleBranchTree<Integer> o12 = new SingleBranchTree<>(Arrays.asList(1, 2, 3));
        final SingleBranchTree<Integer> o2 = new SingleBranchTree<>(Arrays.asList(4, 5));

        Assert.assertEquals(o11, o11);
        Assert.assertEquals(o11.hashCode(), o11.hashCode());
        Assert.assertEquals(o11, o12);
        Assert.assertEquals(o11.hashCode(), o12.hashCode());
        Assert.assertEquals(o12, o11);

        Assert.assertNotEquals(o11, null);
        Assert.assertNotEquals(o11, o2);
    }
}
