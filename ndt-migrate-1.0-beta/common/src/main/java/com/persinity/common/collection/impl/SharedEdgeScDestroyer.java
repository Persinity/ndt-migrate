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
package com.persinity.common.collection.impl;

import static com.persinity.common.collection.GraphUtils.getMaxWeightEdgesOf;

import java.util.HashSet;
import java.util.Set;

import org.jgrapht.DirectedGraph;

import com.persinity.common.collection.ComparableDirectedEdge;
import com.persinity.common.collection.GraphUtils;
import com.persinity.common.invariant.Invariant;

/**
 * {@link ScDestroyer} following greedy algorithm that tries to impact maximum number of cycles in strongly connected
 * component upon single destroy of edge(s).<BR>
 * <BR>
 * The problem of finding the minimum amount of edges to be removed from cyclic directed graph is NP-hard. It is called
 * - the minimum feedback edge set. Hence heuristics and simplified models are used to try to minimize the feedback edge
 * set. This algorithm's heuristic is based on the following theorems:<BR>
 * T1: If each strongly connected component (SC) is contracted to a single vertex, the resulting graph is a DAG. A
 * directed graph is acyclic if and only if it has no SC subgraphs with more than one vertex, because a directed cycle
 * is SC and every nontrivial SC contains at least one directed cycle.<BR>
 * T2: A DG is SC if and only if it has an ear decomposition, a partition of the edges into a sequence of directed paths
 * and cycles such that the first subgraph in the sequence is a cycle, and each subsequent subgraph is either a cycle
 * sharing one vertex with previous subgraphs, or a path sharing its two end-points with previous subgraphs (shared
 * edges).<BR>
 * <BR>
 * There are a number of well known algorithms that compute SCs of a DG in efficient linear O(|E|+|V|) time.<BR>
 * The algorithm employs T1 and uses SCs to determine if DG is DAG or not. The algorithm employs T2 and uses greedy
 * approach that searches for edges in shared paths, as cutting them will likely destroy more cycles. A vertex that
 * denotes start/end of shared path has unbalanced in-vs-out degree D: |D| > 0. And the shared path(s) start/end as the
 * minority edge from/to it.<BR>
 * <BR>
 *
 * @author Doichin Yordanov
 */
public class SharedEdgeScDestroyer<V, E extends ComparableDirectedEdge<V, Integer, V>> implements ScDestroyer<V, E> {

    /**
     * Searches for edges to cut in a SC following the rules:<BR>
     * <code>
     * v = the max debalanced vertex, with greatest cut potential from sc<BR>
     * e = the v edge with max weight, if equal, then minority edge (in-vs-out) with priority<BR>
     * </code> <BR>
     * <p/>
     * This method works with weighted graphs. Edges are weighted according how suitable for cut are they. The higher
     * the weight, the less desired to be cut, the lower the weight. 0 can be used for edges that should not be cut.<BR>
     */
    @Override
    public Set<E> seekNDestroy(final DirectedGraph<V, E> sc) {
        final GraphInfo<V, E> graphInfo = GraphInfo.of(sc);
        final V v = graphInfo.getTheMostProneToCutVert(graphInfo.getTheMostDegreeDebalancedVerts());
        assert v != null;

        final Set<E> minorityEdges = GraphUtils.getInVsOutEdgeMinority(sc.edgesOf(v), v);
        Invariant.assertArg(minorityEdges != null && !minorityEdges.isEmpty());

        final Set<E> maxWeightEdges = getMaxWeightEdgesOf(v, sc);
        assert maxWeightEdges != null && !maxWeightEdges.isEmpty();

        Set<E> cuts = new HashSet<>(maxWeightEdges);
        cuts.retainAll(minorityEdges);
        if (cuts.isEmpty()) {
            // No suitable minority edges
            cuts = maxWeightEdges;
        }

        cutWithMaxCycleImpact(sc, v, cuts);

        return cuts;
    }

    /**
     * Cuts set of vertex edges in a SC. It is assumed that for shared path joint vertex of SC ear decomposition, its
     * minority in-vs-out edges either take part into single shared path or in multiple different shared paths. If true,
     * then in both cases, all the minority edges should be cut in order to destroy cycles.
     *
     * @param sc
     * @param v
     * @param cuts
     */
    private void cutWithMaxCycleImpact(final DirectedGraph<V, E> sc, final V v, final Set<E> cuts) {
        for (final E e : cuts) {
            final boolean removedFlg = sc.removeEdge(e);
            assert removedFlg;
        }
    }

}
