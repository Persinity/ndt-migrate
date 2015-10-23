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

import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Set;

import org.jgrapht.DirectedGraph;

import com.google.common.base.Function;
import com.google.common.collect.Collections2;
import com.persinity.common.StringUtils;
import com.persinity.common.collection.CollectionUtils;
import com.persinity.common.collection.ComparableDirectedEdge;
import com.persinity.common.invariant.Invariant;

/**
 * Provides statistics for graph vertices and edges.<BR>
 *
 * @author Doichin Yordanov
 */
public class GraphInfo<V, E extends ComparableDirectedEdge<V, Integer, V>> {

    private final Map<V, VertexInfo<V, E>> graphInfoMap;
    private final List<VertexInfo<V, E>> degreeBalancesChart;
    private String toString;
    private Integer hashCode;

    public static <V, E extends ComparableDirectedEdge<V, Integer, V>> GraphInfo<V, E> of(
            final DirectedGraph<V, E> sc) {
        Invariant.assertArg(sc != null);
        final GraphInfo<V, E> graphInfo = new GraphInfo<>(calcGraphInfoMap(sc));
        return graphInfo;
    }

    protected GraphInfo(final Map<V, VertexInfo<V, E>> graphInfoMap) {
        Invariant.assertArg(graphInfoMap != null);
        this.graphInfoMap = graphInfoMap;
        degreeBalancesChart = updateDegreeBalanceChart(new LinkedList<>(graphInfoMap.values()));
    }

    /**
     * @return the vertex with greatest edge cutting potential. This is the vertex with most dis-balanced collective
     * edge weight
     */
    public V getTheMostProneToCutVert(final List<V> verts) {
        assert !verts.isEmpty();

        Collections.sort(verts, new Comparator<V>() {
            @Override
            public int compare(final V o1, final V o2) {
                return Math.abs(graphInfoMap.get(o2).weightBalance()) - Math.abs(graphInfoMap.get(o1).weightBalance());
            }
        });

        return verts.get(0);
    }

    /**
     * Re/orders chart of vertex informaion according their decreasing order of degree balances, with most debalanced
     * first.
     *
     * @param chart
     * @return
     */
    private List<VertexInfo<V, E>> updateDegreeBalanceChart(final List<VertexInfo<V, E>> chart) {
        Collections.sort(chart, new Comparator<VertexInfo<V, E>>() {
            @Override
            public int compare(final VertexInfo<V, E> o1, final VertexInfo<V, E> o2) {
                return Math.abs(o2.degreeBalance()) - Math.abs(o1.degreeBalance());
            }
        });

        // Clean vertices
        final ListIterator<VertexInfo<V, E>> it = chart.listIterator(chart.size());
        while (it.hasPrevious()) {
            final VertexInfo<V, E> vi = it.previous();
            if (vi.degreeBalance() > 0) {
                break;
            }
            if (vi.degree() == 0) {
                it.remove();
            }
        }

        return chart;
    }

    /**
     * @return the vertices with maximum disbalance in terms of their in/out edge degree
     */
    public List<V> getTheMostDegreeDebalancedVerts() {
        final Set<VertexInfo<V, E>> viSet = CollectionUtils
                .getFirstGroup(degreeBalancesChart, new Function<VertexInfo<V, E>, Integer>() {
                    @Override
                    public Integer apply(final VertexInfo<V, E> input) {
                        return Math.abs(input.degreeBalance());
                    }
                });
        final List<V> result = new LinkedList<>(Collections2.transform(viSet, new Function<VertexInfo<V, E>, V>() {
            @Override
            public V apply(final VertexInfo<V, E> input) {
                return input.vertex();
            }
        }));

        return result;
    }

    /**
     * @param v
     * @return Vertex information for the supplied vertex or null if such not found
     */
    public VertexInfo<V, E> getVertexInfoOf(final V v) {
        return graphInfoMap.get(v);
    }

    @Override
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof GraphInfo)) {
            return false;
        }
        @SuppressWarnings("unchecked")
        final GraphInfo<V, ComparableDirectedEdge<V, Integer, V>> that = (GraphInfo<V, ComparableDirectedEdge<V, Integer, V>>) obj;
        return this.graphInfoMap.equals(that.graphInfoMap);
    }

    @Override
    public int hashCode() {
        if (hashCode == null) {
            hashCode = graphInfoMap.hashCode();
        }
        return hashCode;
    }

    @Override
    public String toString() {
        if (toString == null) {
            toString = StringUtils.format("{}({})", this.getClass().getSimpleName(), graphInfoMap);
        }
        return toString;
    }

    private static <V, E extends ComparableDirectedEdge<V, Integer, V>> Map<V, VertexInfo<V, E>> calcGraphInfoMap(
            final DirectedGraph<V, E> dg) {

        final Map<V, VertexInfo<V, E>> result = new HashMap<V, VertexInfo<V, E>>();
        for (final V v : dg.vertexSet()) {
            final VertexInfo<V, E> info = VertexInfo.of(dg, v);
            result.put(v, info);
        }
        return result;
    }

}
