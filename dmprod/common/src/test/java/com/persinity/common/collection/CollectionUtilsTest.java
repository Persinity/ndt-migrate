/**
 * Copyright (c) 2015 Persinity Inc.
 */
package com.persinity.common.collection;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.junit.Assert;
import org.junit.Test;

import com.google.common.base.Function;

/**
 * @author Doichin Yordanov
 */
public class CollectionUtilsTest {

    @Test(expected = NullPointerException.class)
    public void testExplode_InvalidInput() {
        CollectionUtils.explode(null);
    }

    @Test
    public void testExplode() {
        List<String> actual = CollectionUtils.explode("1, 2, 3");
        List<String> expected = Arrays.asList("1", "2", "3");
        Assert.assertEquals(expected, actual);

        actual = CollectionUtils.explode("");
        expected = Collections.emptyList();
        Assert.assertEquals(expected, actual);

        actual = CollectionUtils.explode("1 ,2,   3");
        expected = Arrays.asList("1", "2", "3");
        Assert.assertEquals(expected, actual);

        actual = CollectionUtils.explode(",1 ,2,   3 , ,");
        expected = Arrays.asList("1", "2", "3");
        Assert.assertEquals(expected, actual);
    }

    /**
     * Test method for {@link com.persinity.common.collection.CollectionUtils#newTree(java.util.List)} with {@code null}
     * input.
     */
    @Test
    public void testNewTreeNull() {
        List<Integer> ints = null;
        Tree<Integer> actual = CollectionUtils.newTree(ints);
        Assert.assertNull(actual);

        ints = Collections.emptyList();
        actual = CollectionUtils.newTree(ints);
        Assert.assertNull(actual.getRoot());

        ints = new LinkedList<Integer>();
        ints.add(1);
        actual = CollectionUtils.newTree(ints);
        Assert.assertNotNull(actual.getRoot());
        Assert.assertEquals(1, actual.getRoot().intValue());
        Assert.assertEquals(1, actual.breadthFirstTraversal(actual.getRoot()).size());
    }

    /**
     * Test method for {@link com.persinity.common.collection.CollectionUtils#newTree(java.util.List)} with empty input.
     */
    @Test
    public void testNewTreeEmpty() {
        final List<Integer> ints = Collections.emptyList();
        final Tree<Integer> actual = CollectionUtils.newTree(ints);
        Assert.assertNull(actual.getRoot());

    }

    /**
     * Test method for {@link com.persinity.common.collection.CollectionUtils#newTree(java.util.List)} with singleton
     * input.
     */
    @Test
    public void testNewTreeSingleton() {
        final List<Integer> ints = new LinkedList<Integer>();
        ints.add(1);
        final Tree<Integer> actual = CollectionUtils.newTree(ints);
        verifyRoot(ints, actual);
    }

    /**
     * Test method for {@link com.persinity.common.collection.CollectionUtils#newTree(java.util.List)}.
     */
    @Test
    public void testNewTree() {
        final List<Integer> ints = Arrays.asList(1, 2, 3);
        final Tree<Integer> actual = CollectionUtils.newTree(ints);
        verifyRoot(ints, actual);
        final Iterator<Integer> it = actual.breadthFirstTraversal(actual.getRoot()).iterator();
        int i = 1;
        while (it.hasNext()) {
            Assert.assertEquals(i++, it.next().intValue());
        }
    }

    /**
     * Test method for {@link com.persinity.common.collection.CollectionUtils#deDuplicate(List)}
     */
    @Test
    public void testDeduplicate() {
        final List<Integer> ints = Arrays.asList(1, 3, 3, 2, 3, 4, 4);
        final List<Integer> expectedInts = Arrays.asList(1, 3, 2, 4);
        final List<Integer> actualInts = CollectionUtils.deDuplicate(ints);
        Assert.assertEquals(actualInts, expectedInts);
    }

    /**
     * Test method for {@link com.persinity.common.collection.CollectionUtils#deDuplicate(List)} with input containing
     * null value(s).
     */
    @Test(expected = NullPointerException.class)
    public void testDeduplicateNulls() {
        final List<Integer> ints = new LinkedList<>();
        ints.add(null);
        ints.add(null);
        ints.add(3);
        ints.add(3);
        ints.add(2);

        CollectionUtils.deDuplicate(ints);
    }

    /**
     * Test method for {@link com.persinity.common.collection.CollectionUtils#deDuplicate(List)} with null input
     */
    @Test
    public void testDeduplicateNull() {
        final List<Integer> expectedInts = Collections.emptyList();
        final List<Integer> actualInts = CollectionUtils.deDuplicate(null);
        Assert.assertEquals(expectedInts, actualInts);
    }

    /**
     * Test method for {@link com.persinity.common.collection.CollectionUtils#deDuplicate(List)} with emty input.
     */
    @Test
    public void testDeduplicateEmpty() {
        final List<Integer> ints = Collections.emptyList();
        final List<Integer> expectedInts = Collections.emptyList();
        final List<Integer> actualInts = CollectionUtils.deDuplicate(ints);
        Assert.assertEquals(actualInts, expectedInts);
    }

    /**
     * Test method for {@link com.persinity.common.collection.CollectionUtils#newList(Iterator)}
     */
    @Test
    public void testNewList() {
        final List<Integer> expectedInts = Arrays.asList(1, 2, 3);
        final List<Integer> actualInts = CollectionUtils.newList(expectedInts.iterator());
        Assert.assertEquals(expectedInts, actualInts);
    }

    /**
     * Test method for {@link com.persinity.common.collection.CollectionUtils#newList(Iterator)} with null input
     */
    @Test
    public void testNewListNull() {
        final List<Integer> expectedInts = Collections.emptyList();
        final List<Integer> actualInts = CollectionUtils.newList(null);
        Assert.assertEquals(expectedInts, actualInts);
    }

    /**
     * Test method for {@link com.persinity.common.collection.CollectionUtils#newList(Iterator)} with empty input
     */
    @Test
    public void testNewListEmpty() {
        final List<Integer> expectedInts = Collections.emptyList();
        final List<Integer> actualInts = CollectionUtils.newList(expectedInts.iterator());
        Assert.assertEquals(expectedInts, actualInts);
    }

    /**
     * Test method for {@link com.persinity.common.collection.CollectionUtils#newList(Iterator)} with input containing
     * null values.
     */
    @Test
    public void testNewListNulls() {
        final List<Integer> expectedInts = new LinkedList<>();
        expectedInts.add(null);
        expectedInts.add(1);
        expectedInts.add(2);
        expectedInts.add(3);

        final List<Integer> actualInts = CollectionUtils.newList(expectedInts.iterator());
        Assert.assertEquals(expectedInts, actualInts);
    }

    /**
     * Test for {@link CollectionUtils#implode(Collection, String)}
     */
    @Test
    public void testImplode() {
        Assert.assertTrue(CollectionUtils.implode(null, "").isEmpty());
        Assert.assertTrue(CollectionUtils.implode(Collections.emptyList(), "").isEmpty());
        final String actual = CollectionUtils.implode(Arrays.asList(1, 2, 3), ", ");
        Assert.assertEquals("1, 2, 3", actual);
    }

    /**
     * Test for {@link CollectionUtils#implode(Collection, String, Function)}
     */
    @Test
    public void testImplode_WithFunctor() {
        Assert.assertTrue(CollectionUtils.implode(null, "", null).isEmpty());
        Assert.assertTrue(CollectionUtils.implode(Collections.emptyList(), "", new Function<Object, String>() {
            @Override
            public String apply(final Object integer) {
                return "";
            }
        }).isEmpty());
        final String actual = CollectionUtils.implode(Arrays.asList(1, 2, 3), ", ", new Function<Integer, String>() {
            @Override
            public String apply(final Integer integer) {
                return "" + (integer + 1);
            }
        });
        Assert.assertEquals("2, 3, 4", actual);
    }

    /**
     * Test for {@link CollectionUtils#addPadded(List, List, int, Object)}
     */
    @Test
    public void testAddPadded() {
        final List<Integer> list = Arrays.asList(1, 2, 3);
        final List<Integer> expected = Arrays.asList(1, 2, 3, 3, 3);
        final List<Integer> actual = new ArrayList<>();
        CollectionUtils.addPadded(actual, list, 5, 3);
        Assert.assertEquals(expected, actual);
    }

    /**
     * Test for {@link CollectionUtils#addPadded(List, List, int, Object)}
     */
    @Test
    public void testAddPadded_StringToObject() {
        final List<String> list = Arrays.asList("1", "2", "3");
        final List<Object> expected = Arrays.<Object>asList(1, "1", "2", "3", "3", "3");
        final List<Object> actual = new ArrayList<>();
        actual.add(1);
        CollectionUtils.addPadded(actual, list, 5, "3");
        Assert.assertEquals(expected, actual);
    }

    /**
     * Test for {@link CollectionUtils#addPadded(List, List, int)}
     */
    @Test
    public void testAddPadded_LastElement() {
        final List<String> list = Arrays.asList("1", "2", "3");
        final List<String> expected = Arrays.asList("1", "2", "3", "3", "3");
        final List<Object> actual = new ArrayList<>();
        CollectionUtils.addPadded(actual, list, 5);
        Assert.assertEquals(expected, actual);
    }

    /**
     * Test for {@link CollectionUtils#addPadded(List, List, int, Object)}
     */
    @Test
    public void testAddPadded_EmptyList() {
        final List<Integer> expected = Collections.singletonList(1);
        final List<Integer> actual = new ArrayList<>();
        CollectionUtils.addPadded(actual, new LinkedList<Integer>(), 1, 1);
        Assert.assertEquals(expected, actual);
    }

    /**
     * Test for {@link CollectionUtils#addPadded(List, List, int, Object)}
     */
    @Test
    public void testAddPadded_Exact() {
        final List<Integer> expected = Arrays.asList(1, 2, 3);
        final List<Integer> actual = new ArrayList<>();
        CollectionUtils.addPadded(actual, expected, expected.size(), null);
        Assert.assertEquals(expected, actual);
    }

    /**
     * Test for {@link CollectionUtils#addPadded(List, List, int, Object)}
     */
    @Test(expected = NullPointerException.class)
    public void testAddPadded_NullSrcList() {
        CollectionUtils.addPadded(null, new ArrayList<Integer>(), 1, 1);
    }

    /**
     * Test for {@link CollectionUtils#addPadded(List, List, int, Object)}
     */
    @Test(expected = NullPointerException.class)
    public void testAddPadded_NullDstList() {
        CollectionUtils.addPadded(new ArrayList<Integer>(), null, 1, 1);
    }

    /**
     * Test for {@link CollectionUtils#addPadded(List, List, int, Object)}
     */
    @Test(expected = IllegalArgumentException.class)
    public void testAddPadded_NegativeSize() {
        CollectionUtils.addPadded(new LinkedList<Integer>(), new LinkedList<Integer>(), -1, 1);
    }

    /**
     * Test for {@link CollectionUtils#addPadded(List, List, int, Object)}
     */
    @Test(expected = IllegalArgumentException.class)
    public void testAddPadded_InvalidSize() {
        CollectionUtils.addPadded(new LinkedList<Integer>(), Arrays.asList(1, 2, 3), 2, 3);
    }

    /**
     * Test for {@link CollectionUtils#getFirstGroup(List, Function)}
     */
    @Test
    public void testGetFirstGroup() {
        final Function<String, Integer> indexF = new Function<String, Integer>() {
            @Override
            public Integer apply(final String input) {
                return input.length();
            }
        };

        List<String> list = Collections.emptyList();
        Set<String> expected = Collections.emptySet();
        Set<String> actual = CollectionUtils.getFirstGroup(list, indexF);
        Assert.assertEquals(expected, actual);

        list = Arrays.asList("ab", "bc", "a", "b");
        expected = new HashSet<>(Arrays.asList("ab", "bc"));
        actual = CollectionUtils.getFirstGroup(list, indexF);
        Assert.assertEquals(expected, actual);
    }

    private void verifyRoot(final List<Integer> ints, final Tree<Integer> actual) {
        Assert.assertNotNull(actual.getRoot());
        Assert.assertEquals(1, actual.getRoot().intValue());
        Assert.assertEquals(ints.size(), actual.breadthFirstTraversal(actual.getRoot()).size());
    }

}
