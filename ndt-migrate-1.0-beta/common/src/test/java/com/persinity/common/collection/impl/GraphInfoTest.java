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

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.persinity.common.collection.ComparableDirectedEdge;
import com.persinity.test.TestUtil;

/**
 * @author Doichin Yordanov
 */
public class GraphInfoTest {

    private TestGraph bag;

    @Before
    public void setUp() {
        bag = new TestGraph();
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    @Test(expected = IllegalArgumentException.class)
    public void testGraphInfoInvalidInput() {
        new GraphInfo(null);
    }

    @Test
    public void testStats() {
        verifyDegreeDisb(new HashSet<>(Arrays.asList(bag.v1, bag.v2, bag.v3)), bag.gi);
    }

    @Test
    public void testEdgeDropped() {
        bag.graph.removeEdge(bag.e122);

        verifyDegreeDisb(new HashSet<>(Arrays.asList(bag.v1, bag.v2)), GraphInfo.of(bag.graph));
        Integer actualMostProneToCut = GraphInfo.of(bag.graph)
                .getTheMostProneToCutVert(Arrays.asList(bag.v1, bag.v2, bag.v3));
        TestUtil.assertIsAnyOf(actualMostProneToCut, Arrays.asList(bag.v1, bag.v3));

        bag.graph.removeEdge(bag.e312);

        verifyDegreeDisb(new HashSet<>(Arrays.asList(bag.v2)), GraphInfo.of(bag.graph));
        actualMostProneToCut = GraphInfo.of(bag.graph).getTheMostProneToCutVert(Arrays.asList(bag.v1, bag.v2, bag.v3));
        TestUtil.assertIsAnyOf(actualMostProneToCut, Arrays.asList(bag.v1, bag.v2));

    }

    @Test
    public void testEqualsHashCode() {
        GraphInfo<Integer, ComparableDirectedEdge<Integer, Integer, Integer>> actualGi = new GraphInfo<>(
                new HashMap<>(bag.graphInfoMap));
        Assert.assertEquals(actualGi, actualGi);
        Assert.assertEquals(actualGi.hashCode(), actualGi.hashCode());
        Assert.assertEquals(bag.gi, actualGi);
        Assert.assertEquals(actualGi, bag.gi);
        Assert.assertEquals(actualGi.hashCode(), bag.gi.hashCode());

        final Map<Integer, VertexInfo<Integer, ComparableDirectedEdge<Integer, Integer, Integer>>> infoMap = new HashMap<>(
                bag.graphInfoMap);
        infoMap.remove(infoMap.keySet().iterator().next());
        actualGi = new GraphInfo<>(infoMap);
        Assert.assertNotEquals(bag.gi, actualGi);
        Assert.assertNotEquals(actualGi, null);
    }

    @Test
    public void testOf() {
        final GraphInfo<Integer, ComparableDirectedEdge<Integer, Integer, Integer>> gi = GraphInfo.of(bag.graph);
        Assert.assertEquals(bag.gi, gi);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testOfInvalidInput() {
        @SuppressWarnings("unused")
        final GraphInfo<Integer, ComparableDirectedEdge<Integer, Integer, Integer>> gi = GraphInfo.of(null);
    }

    private void verifyDegreeDisb(final Set<Integer> expectedDegrDisbVerbs,
            final GraphInfo<Integer, ComparableDirectedEdge<Integer, Integer, Integer>> gi) {

        final List<Integer> actualDegrDisbVerts = gi.getTheMostDegreeDebalancedVerts();
        Assert.assertNotNull(actualDegrDisbVerts);
        Assert.assertEquals(expectedDegrDisbVerbs, new HashSet<>(actualDegrDisbVerts));
    }

}
