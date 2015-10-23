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
import java.util.HashSet;
import java.util.Set;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.persinity.common.collection.ComparableDirectedEdge;

/**
 * @author Doichin Yordanov
 */
public class SharedEdgeScDestroyerTest {

    private TestGraph bag;

    @Before
    public void setUp() {
        bag = new TestGraph();
    }

    @Test
    public void testSeekNDestroy() {
        final ScDestroyer<Integer, ComparableDirectedEdge<Integer, Integer, Integer>> scDestroyer = new SharedEdgeScDestroyer<>();
        Set<ComparableDirectedEdge<Integer, Integer, Integer>> killed = scDestroyer.seekNDestroy(bag.graph);
        Assert.assertEquals(new HashSet<ComparableDirectedEdge<Integer, Integer, Integer>>(Arrays.asList(bag.e122)),
                killed);

        bag.graph.removeEdge(bag.e211);
        bag.graph.removeVertex(bag.v1);

        killed = scDestroyer.seekNDestroy(bag.graph);
        Assert.assertEquals(new HashSet<ComparableDirectedEdge<Integer, Integer, Integer>>(Arrays.asList(bag.e312)),
                killed);
    }
}
