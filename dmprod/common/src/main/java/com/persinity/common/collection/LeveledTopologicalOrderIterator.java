/**
 * Copyright (c) 2015 Persinity Inc.
 */

package com.persinity.common.collection;

import static com.persinity.common.invariant.Invariant.notNull;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;

import org.jgrapht.experimental.dag.DirectedAcyclicGraph;
import org.jgrapht.traverse.TopologicalOrderIterator;

/**
 * Topological order iterator keeping the invariant that
 * for any given level (i-1), all vertices in the returned set belong to level i,
 * such that level i vertices are accessible only from i-1 and parent (already returned) levels.<BR>
 * <pre>
 * <code>
 *     v1
 *    /  \
 *   v2  v3
 *  / \   \
 * v4  \  v5
 *      \ /
 *       v6
 * => [v1], [v2, v3], [v4, v5], [v6]
 * Note how v6 is returned once v5 and v2 (all vertices from parent levels) are returned.
 * </code>
 * </pre>
 * The {@link Iterator#remove()} operation is not supported.
 *
 * @author dyordanov
 */
public class LeveledTopologicalOrderIterator<V, E> implements Iterator<Set<V>> {

    public LeveledTopologicalOrderIterator(final DirectedAcyclicGraph<V, E> dag) {
        notNull(dag);
        this.dag = dag;
        vertexLevelsMap = new HashMap<>();
        for (V v : dag.vertexSet()) {
            vertexLevelsMap.put(v, INITIAL_LEVEL);
        }
        tplogicalPreviewIt = new PreviewIterator<>(new TopologicalOrderIterator<>(dag));
        itLevel = INITIAL_LEVEL;
    }

    @Override
    public boolean hasNext() {
        return tplogicalPreviewIt.hasNext();
    }

    @Override
    public Set<V> next() {
        if (!tplogicalPreviewIt.hasNext()) {
            throw new NoSuchElementException();
        }

        itLevel++;
        final Set<V> result = new HashSet<>();
        while (tplogicalPreviewIt.hasNext()) {
            V v = tplogicalPreviewIt.preview();
            int vLevel = getLevel(v);

            assert vLevel >= vertexLevelsMap.get(v);
            assert vLevel == itLevel || vLevel == itLevel + 1;
            vertexLevelsMap.put(v, vLevel);
            if (vLevel > itLevel) {
                break;
            }

            tplogicalPreviewIt.next();
            result.add(v);
        }

        assert !result.isEmpty();
        return result;
    }

    @Override
    public void remove() {
        throw new UnsupportedOperationException();
    }

    private int getLevel(final V v) {
        int vLevel = vertexLevelsMap.get(v);
        if (vLevel > INITIAL_LEVEL) {
            return vLevel;
        }
        for (E e : dag.incomingEdgesOf(v)) {
            int level = vertexLevelsMap.get(dag.getEdgeSource(e));
            assert level > INITIAL_LEVEL;
            if (level > vLevel) {
                vLevel = level;
            }
        }
        return ++vLevel;
    }

    private static final Integer INITIAL_LEVEL = -1;

    private final Map<V, Integer> vertexLevelsMap;
    private final PreviewIterator<V> tplogicalPreviewIt;
    private final DirectedAcyclicGraph<V, E> dag;
    private int itLevel;

}
