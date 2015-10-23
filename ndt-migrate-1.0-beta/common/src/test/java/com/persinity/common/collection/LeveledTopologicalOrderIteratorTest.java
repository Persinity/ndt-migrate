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

import static com.persinity.test.TestUtil.assertNextIs;
import static java.util.Arrays.asList;
import static org.junit.Assert.assertFalse;

import java.util.Collection;
import java.util.NoSuchElementException;

import org.jgrapht.experimental.dag.DirectedAcyclicGraph;
import org.junit.Assert;
import org.junit.Test;

/**
 * @author dyordanov
 */
public class LeveledTopologicalOrderIteratorTest {

    @Test(expected = NullPointerException.class)
    public void testLeveledTopologicalOrderIterator_NullInput() {
        new LeveledTopologicalOrderIterator(null);
    }

    @Test(expected = NoSuchElementException.class)
    public void testNext_EmptyDag() throws Exception {
        final DirectedAcyclicGraph<Integer, DirectedEdge<Integer, Integer>> dag = new Dag<>();
        LeveledTopologicalOrderIterator<Integer, DirectedEdge<Integer, Integer>> it = getIt(dag);
        Assert.assertFalse(it.hasNext());
        it.next();
    }

    @Test
    public void testNext() {
        final DirectedAcyclicGraph dag = getDag();
        final LeveledTopologicalOrderIterator<Integer, DirectedEdge<Integer, Integer>> it = getIt(dag);
        assertNextIs(it, 1, 2);
        assertNextIs(it, 3);
        assertNextIs(it, 4, 5);
        assertNextIs(it, 6, 7);
        assertNextIs(it, 8, 9);
        assertNextIs(it, 10);
        assertFalse(it.hasNext());
    }

    @Test(expected = UnsupportedOperationException.class)
    public void testRemove() throws Exception {
        final DirectedAcyclicGraph<Integer, DirectedEdge<Integer, Integer>> dag = new Dag<>();
        LeveledTopologicalOrderIterator<Integer, DirectedEdge<Integer, Integer>> it = getIt(dag);
        it.remove();
    }

    /**
     * <pre>
     * <code>
     * v1 v2 -> v3
     *         /  \
     *        v4  v5
     *       / \   \
     *      v6  \  v7
     *     /     \ /
     *    v8      v9 -> v10
     * => [v1, v2], [v3], [v4, v5], [v6, v7], [v8, v9], [v10]
     * </code>
     * </pre>
     */
    private DirectedAcyclicGraph getDag() {
        final Collection<Integer> vertices = asList(1, 2, 3, 4, 5, 6, 7, 8, 9, 10);
        final DirectedAcyclicGraph dag = new Dag(vertices);
        dag.addEdge(2, 3, new DirectedEdge<>(2, 3));
        dag.addEdge(3, 4, new DirectedEdge<>(3, 4));
        dag.addEdge(3, 5, new DirectedEdge<>(3, 5));
        dag.addEdge(4, 6, new DirectedEdge<>(4, 6));
        dag.addEdge(4, 9, new DirectedEdge<>(4, 9));
        dag.addEdge(5, 7, new DirectedEdge<>(5, 7));
        dag.addEdge(6, 8, new DirectedEdge<>(6, 8));
        dag.addEdge(7, 9, new DirectedEdge<>(7, 9));
        dag.addEdge(9, 10, new DirectedEdge<>(9, 10));
        return dag;
    }

    private static <V, E> LeveledTopologicalOrderIterator<V, E> getIt(final DirectedAcyclicGraph<V, E> dag) {
        return new LeveledTopologicalOrderIterator(dag);
    }
}