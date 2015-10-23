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

import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.verify;

import java.util.HashSet;
import java.util.Set;

import org.easymock.EasyMock;
import org.jgrapht.DirectedGraph;
import org.junit.Assert;
import org.junit.Test;

import com.persinity.common.collection.ComparableDirectedEdge;

/**
 * @author Doichin Yordanov
 */
public class VertexInfoTest {

    @Test
    public void testEqualsHashCode() {
        final Integer v1 = 1;
        final Integer v2 = 2;
        final ComparableDirectedEdge<Integer, Integer, Integer> e12 = new ComparableDirectedEdge<Integer, Integer, Integer>(
                1, 1, 2);
        final ComparableDirectedEdge<Integer, Integer, Integer> e21 = new ComparableDirectedEdge<Integer, Integer, Integer>(
                2, 3, 1);
        final ComparableDirectedEdge<Integer, Integer, Integer> e13 = new ComparableDirectedEdge<Integer, Integer, Integer>(
                1, 1, 3);
        final Set<ComparableDirectedEdge<Integer, Integer, Integer>> v1Edges = new HashSet<>();
        final Set<ComparableDirectedEdge<Integer, Integer, Integer>> v2Edges = new HashSet<>();
        v1Edges.add(e12);
        v1Edges.add(e21);
        v1Edges.add(e13);
        v2Edges.add(e12);
        v2Edges.add(e21);
        final VertexInfo<Integer, ComparableDirectedEdge<Integer, Integer, Integer>> vi11 = new VertexInfo<>(v1,
                v1Edges);
        final VertexInfo<Integer, ComparableDirectedEdge<Integer, Integer, Integer>> vi12 = new VertexInfo<>(v1,
                v1Edges);
        final VertexInfo<Integer, ComparableDirectedEdge<Integer, Integer, Integer>> vi2 = new VertexInfo<>(v2,
                v2Edges);

        Assert.assertEquals(vi11, vi11);
        Assert.assertEquals(vi11.hashCode(), vi11.hashCode());
        Assert.assertEquals(vi11, vi12);
        Assert.assertEquals(vi12, vi11);
        Assert.assertEquals(vi11.hashCode(), vi12.hashCode());
        Assert.assertNotEquals(vi11, null);
        Assert.assertNotEquals(vi11, vi2);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testOfInvalidDg() {
        final DirectedGraph<Integer, ComparableDirectedEdge<Integer, Integer, Integer>> dg = null;
        final Integer v = null;
        VertexInfo.of(dg, v);
    }

    @SuppressWarnings("unchecked")
    @Test(expected = IllegalArgumentException.class)
    public void testOfInvalidVertex() {
        VertexInfo.of(EasyMock.createNiceMock(DirectedGraph.class), null);
    }

    @Test
    public void testOf() {
        final Integer v1 = 1;
        final Set<ComparableDirectedEdge<Integer, Integer, Integer>> v1Edges = new HashSet<>();
        final ComparableDirectedEdge<Integer, Integer, Integer> e12 = new ComparableDirectedEdge<Integer, Integer, Integer>(
                1, 3, 2);
        final ComparableDirectedEdge<Integer, Integer, Integer> e21 = new ComparableDirectedEdge<Integer, Integer, Integer>(
                2, 1, 1);
        final ComparableDirectedEdge<Integer, Integer, Integer> e31 = new ComparableDirectedEdge<Integer, Integer, Integer>(
                3, 1, 1);
        v1Edges.add(e12);
        v1Edges.add(e21);
        v1Edges.add(e31);

        @SuppressWarnings("unchecked")
        final DirectedGraph<Integer, ComparableDirectedEdge<Integer, Integer, Integer>> dg = EasyMock
                .createStrictMock(DirectedGraph.class);
        expect(dg.edgesOf(v1)).andReturn(v1Edges);
        replay(dg);

        final VertexInfo<Integer, ComparableDirectedEdge<Integer, Integer, Integer>> vi = VertexInfo.of(dg, v1);

        verify(dg);

        Assert.assertNotNull(vi);
        Assert.assertEquals(v1, vi.vertex());
        Assert.assertEquals(1, vi.degreeBalance());
        Assert.assertEquals(-1, vi.weightBalance());
    }

}
