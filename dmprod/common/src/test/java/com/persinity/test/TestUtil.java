/**
 * Copyright (c) 2015 Persinity Inc.
 */
package com.persinity.test;

import static com.persinity.common.collection.TestGraphUtils.addDirectedEdges;
import static com.persinity.common.collection.TestGraphUtils.addVertexes;
import static java.util.Arrays.asList;
import static org.junit.Assert.assertTrue;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.hamcrest.CoreMatchers;
import org.hamcrest.Matcher;
import org.jgrapht.experimental.dag.DirectedAcyclicGraph;
import org.junit.Assert;

import com.persinity.common.collection.Dag;
import com.persinity.common.collection.DirectedEdge;

/**
 * @author Ivan Dachev
 */
public class TestUtil {
    public static <T> T serDeser(final T obj) throws IOException, ClassNotFoundException {
        final ByteArrayOutputStream baos = new ByteArrayOutputStream();
        final ObjectOutputStream out = new ObjectOutputStream(baos);
        out.writeObject(obj);
        out.close();
        final byte[] data = baos.toByteArray();
        baos.close();

        final ByteArrayInputStream bais = new ByteArrayInputStream(data);
        final ObjectInputStream ois = new ObjectInputStream(bais);
        @SuppressWarnings("unchecked")
        final T res = (T) ois.readObject();
        ois.close();
        bais.close();

        return res;
    }

    public static <T> void assertIsAnyOf(final T t, final List<? extends T> patterns) {
        final List<Matcher<? super T>> matchers = new LinkedList<>();
        for (final T p : patterns) {
            matchers.add(CoreMatchers.equalTo(p));
        }
        Assert.assertThat(t, CoreMatchers.is(CoreMatchers.anyOf(matchers)));
    }

    public static <T> void assertNextIsAnyOf(final Iterator<T> it, final List<T> patterns) {
        Assert.assertTrue(it.hasNext());
        TestUtil.assertIsAnyOf(it.next(), patterns);
    }

    public static <T> void assertEquals(T expected, T actual, Comparator<T> comparator) {
        if (comparator.compare(expected, actual) != 0) {
            throw new AssertionError("Expected " + expected + " but was " + actual);
        }
    }

    public static <T> void assertNextIs(final Iterator<Set<T>> it, T... expected) {
        assertTrue(it.hasNext());
        Assert.assertEquals(new HashSet<>(asList(expected)), it.next());
    }

    public static <T> void assertNextIs(final Iterator<T> it, T expected) {
        assertTrue(it.hasNext());
        Assert.assertEquals(expected, it.next());
    }

    /**
     * <pre>
     *         10
     *      /  |  \
     *    20  30  40
     *    |   |   |
     *   50   |   |
     *    \  /   /
     *     60   /
     *      \ /
     *      70
     *      |
     *     71
     *    |  \
     *   41   61
     *   |   /  \
     *   |  31  51
     *   |  |   |
     *   \  |  21
     *    \ |  /
     *      11
     * </pre>
     */
    public static DirectedAcyclicGraph<Integer, DirectedEdge<Integer, Integer>> newBigDag() {
        final DirectedAcyclicGraph<Integer, DirectedEdge<Integer, Integer>> dag = new Dag();
        addVertexes(dag, 10, 20, 30, 40, 50, 60, 70, 71, 61, 41, 31, 51, 21, 11);
        addDirectedEdges(dag, 10, 20, 10, 30, 10, 40, 20, 50, 30, 60, 40, 70, 50, 60, 60, 70);
        addDirectedEdges(dag, 70, 71, 71, 41, 71, 61, 41, 11, 61, 31, 61, 51, 31, 11, 51, 21, 21, 11);
        return dag;
    }
}
