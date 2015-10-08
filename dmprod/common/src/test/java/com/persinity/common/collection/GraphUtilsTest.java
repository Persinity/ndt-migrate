/**
 * Copyright (c) 2015 Persinity Inc.
 */
package com.persinity.common.collection;

import static com.google.common.collect.Sets.newHashSet;
import static com.persinity.common.collection.GraphUtils.NOT_REVERSED;
import static com.persinity.common.collection.GraphUtils.REVERSED;
import static com.persinity.common.collection.GraphUtils.addGraphToGraph;
import static com.persinity.common.collection.GraphUtils.addSinkVertex;
import static com.persinity.common.collection.GraphUtils.addSourceVertex;
import static com.persinity.common.collection.GraphUtils.getEdgesFor;
import static com.persinity.common.collection.GraphUtils.getInVsOutEdgeMinority;
import static com.persinity.common.collection.GraphUtils.reverseEdge;
import static com.persinity.common.collection.GraphUtils.transformDirectedGraph;
import static com.persinity.common.collection.TestGraphUtils.TEST_EDGE_FACTORY;
import static com.persinity.common.collection.TestGraphUtils.addDirectedEdges;
import static com.persinity.common.collection.TestGraphUtils.addVertexes;
import static com.persinity.common.collection.TestGraphUtils.verifyDirectedEdges;
import static com.persinity.common.collection.TestGraphUtils.verifyVertexes;
import static com.persinity.test.TestUtil.assertNextIs;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.collection.IsEmptyCollection.empty;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Set;

import org.jgrapht.DirectedGraph;
import org.jgrapht.EdgeFactory;
import org.jgrapht.Graph;
import org.jgrapht.experimental.dag.DirectedAcyclicGraph;
import org.jgrapht.graph.DefaultDirectedGraph;
import org.jgrapht.graph.DefaultDirectedWeightedGraph;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.google.common.base.Function;
import com.persinity.common.collection.GraphUtils.EdgeDirection;
import com.persinity.common.collection.impl.TestGraph;
import com.persinity.test.TestUtil;

/**
 * @author Doichin Yordanov
 */
public class GraphUtilsTest {

    private TestGraph bag;

    @Before
    public void setUp() {
        bag = new TestGraph();
    }

    @Test(expected = UnsupportedOperationException.class)
    public void testNewDummyTestFactory() {
        final EdgeFactory<Object, DirectedEdge<Object, Object>> dummyEf = GraphUtils.newDummyEdgeFactory();
        dummyEf.createEdge(1, 2);
    }

    @Test
    public void testReverseEdge() {
        final DirectedEdge<Integer, Integer> e = new DirectedEdge<>(1, 2);
        final DirectedEdge<Integer, Integer> actual = GraphUtils.reverseEdge(e);
        final DirectedEdge<Integer, Integer> expected = new DirectedEdge<>(2, 1);
        Assert.assertEquals(expected, actual);

        Assert.assertEquals(e, reverseEdge(reverseEdge(e)));
    }

    @Test
    public void testWeightedEdgeOf() {
        final DirectedEdge<Integer, Integer> e = new DirectedEdge<>(1, 2);
        final DirectedEdge<Integer, Integer> actual = GraphUtils.weightedEdgeOf(e, 1);
        final DirectedEdge<Integer, Integer> expected = new ComparableDirectedEdge<>(e, 1);
        Assert.assertEquals(expected, actual);
    }

    @Test
    public void testFilterSingletons() {
        Collection<Graph<?, ?>> graphs = Collections.emptyList();
        Collection<Graph<?, ?>> actual = GraphUtils.filterSingletons(graphs);
        Assert.assertTrue(actual != null && actual.isEmpty());

        graphs = new LinkedList<>();
        final Graph<Integer, DirectedEdge<Integer, Integer>> singleton = new DefaultDirectedGraph<>(TEST_EDGE_FACTORY);
        singleton.addVertex(1);
        graphs.add(singleton);

        final Graph<Integer, DirectedEdge<Integer, Integer>> nonSingleton = new DefaultDirectedGraph<>(
                TEST_EDGE_FACTORY);
        nonSingleton.addVertex(1);
        nonSingleton.addVertex(2);
        nonSingleton.addEdge(1, 2);
        graphs.add(nonSingleton);

        actual = GraphUtils.filterSingletons(graphs);
        Assert.assertTrue(actual != null && actual.size() == 1);
        Assert.assertEquals(nonSingleton, actual.iterator().next());
    }

    /**
     * Test method for
     * {@link com.persinity.common.collection.GraphUtils#getEdgesFor(org.jgrapht.DirectedGraph, java.lang.Object, com.persinity.common.collection.GraphUtils.EdgeDirection)}
     * .
     */
    @Test
    public void testGetEdgesFor() {
        final DirectedGraph<Integer, DirectedEdge<Integer, Integer>> dg = new DefaultDirectedGraph<>(TEST_EDGE_FACTORY);
        /*<pre>
         1    0
		/\ \
		\/ |
		2  |
		| /
		3
		</pre>*/
        addVertexes(dg, 0, 1, 2, 3);
        addDirectedEdges(dg, 1, 2, 2, 1, 2, 3, 1, 3);

        Set<DirectedEdge<Integer, Integer>> edges = getEdgesFor(dg, 0, EdgeDirection.OUT);
        assertTrue(edges.isEmpty());

        edges = getEdgesFor(dg, 3, EdgeDirection.OUT);
        assertTrue(edges.isEmpty());

        edges = getEdgesFor(dg, 3, EdgeDirection.IN);
        assertEquals(edges, new HashSet<>(Arrays.asList(new DirectedEdge<>(1, 3), new DirectedEdge<>(2, 3))));
    }

    @Test
    public void testGetMaxWeightEdgesOf() {
        final EdgeFactory<Integer, ComparableDirectedEdge<Integer, Integer, Integer>> dummyFactory = GraphUtils
                .newDummyEdgeFactory();
        final DirectedGraph<Integer, ComparableDirectedEdge<Integer, Integer, Integer>> dg = new DefaultDirectedWeightedGraph<>(
                dummyFactory);
        dg.addVertex(1);
        Set<ComparableDirectedEdge<Integer, Integer, Integer>> actual = new HashSet<>(
                GraphUtils.getMaxWeightEdgesOf(1, dg));
        Assert.assertThat(actual, is(empty()));

        dg.addVertex(2);
        dg.addVertex(3);
        final ComparableDirectedEdge<Integer, Integer, Integer> wde112 = new ComparableDirectedEdge<>(1, 1, 2);
        final ComparableDirectedEdge<Integer, Integer, Integer> wde211 = new ComparableDirectedEdge<>(2, 1, 1);
        final ComparableDirectedEdge<Integer, Integer, Integer> wde203 = new ComparableDirectedEdge<>(2, 0, 3);
        dg.addEdge(1, 2, wde112);
        dg.addEdge(2, 1, wde211);
        dg.addEdge(1, 2, wde203);
        final Integer v = 2;
        actual = new HashSet<>(GraphUtils.getMaxWeightEdgesOf(v, dg));
        final Set<ComparableDirectedEdge<Integer, Integer, Integer>> expected = new HashSet<>(
                Arrays.asList(wde112, wde211));
        Assert.assertEquals(expected, actual);
    }

    @Test
    public void testTransformDirectedGraph() throws Exception {
        final DirectedGraph<Integer, DirectedEdge<Integer, Integer>> dg = new DefaultDirectedGraph<>(TEST_EDGE_FACTORY);
        addVertexes(dg, 0, 1, 2, 3);
        addDirectedEdges(dg, 0, 2, 1, 2, 2, 3, 3, 2);

        final DirectedGraph<String, ComparableDirectedEdge<String, Integer, String>> res = new DefaultDirectedGraph<>(
                new EdgeFactory<String, ComparableDirectedEdge<String, Integer, String>>() {
                    @Override
                    public ComparableDirectedEdge<String, Integer, String> createEdge(final String s, final String t) {
                        return new ComparableDirectedEdge<>(s, 0, t);
                    }
                });

        final Function<Integer, String> fv = new Function<Integer, String>() {
            @Override
            public String apply(final Integer integer) {
                return "a" + integer;
            }
        };

        final Function<Triple<DirectedEdge<Integer, Integer>, String, String>, ComparableDirectedEdge<String, Integer, String>> fe = new Function<Triple<DirectedEdge<Integer, Integer>, String, String>, ComparableDirectedEdge<String, Integer, String>>() {
            @Override
            public ComparableDirectedEdge<String, Integer, String> apply(
                    final Triple<DirectedEdge<Integer, Integer>, String, String> args) {
                return new ComparableDirectedEdge<>(args.getSecond(),
                        1000 + args.getFirst().src() + args.getFirst().dst(), args.getThird());
            }
        };

        transformDirectedGraph(dg, res, fv, fe, NOT_REVERSED);

        assertEquals(res.vertexSet(), newHashSet("a0", "a1", "a2", "a3"));
        assertEquals(res.edgeSet(), newHashSet(new ComparableDirectedEdge<>("a0", 1002, "a2"),
                new ComparableDirectedEdge<>("a1", 1003, "a2"), new ComparableDirectedEdge<>("a2", 1005, "a3"),
                new ComparableDirectedEdge<>("a3", 1005, "a2")));
    }

    @Test
    public void testTransformDirectedGraph_Reversed() throws Exception {
        final DirectedGraph<Integer, DirectedEdge<Integer, Integer>> dg = new DefaultDirectedGraph<>(TEST_EDGE_FACTORY);
        addVertexes(dg, 0, 1, 2, 3);
        addDirectedEdges(dg, 0, 2, 1, 2, 2, 3, 3, 2);

        final DirectedGraph<String, ComparableDirectedEdge<String, Integer, String>> res = new DefaultDirectedGraph<>(
                new EdgeFactory<String, ComparableDirectedEdge<String, Integer, String>>() {
                    @Override
                    public ComparableDirectedEdge<String, Integer, String> createEdge(final String s, final String t) {
                        return new ComparableDirectedEdge<>(s, 0, t);
                    }
                });

        final Function<Integer, String> fv = new Function<Integer, String>() {
            @Override
            public String apply(final Integer integer) {
                return "a" + integer;
            }
        };

        final Function<Triple<DirectedEdge<Integer, Integer>, String, String>, ComparableDirectedEdge<String, Integer, String>> fe = new Function<Triple<DirectedEdge<Integer, Integer>, String, String>, ComparableDirectedEdge<String, Integer, String>>() {
            @Override
            public ComparableDirectedEdge<String, Integer, String> apply(
                    final Triple<DirectedEdge<Integer, Integer>, String, String> args) {
                return new ComparableDirectedEdge<>(args.getSecond(),
                        1000 + args.getFirst().src() + args.getFirst().dst(), args.getThird());
            }
        };

        transformDirectedGraph(dg, res, fv, fe, REVERSED);

        assertEquals(res.vertexSet(), newHashSet("a0", "a1", "a2", "a3"));
        assertEquals(res.edgeSet(), newHashSet(new ComparableDirectedEdge<>("a2", 1002, "a0"),
                new ComparableDirectedEdge<>("a2", 1003, "a1"), new ComparableDirectedEdge<>("a2", 1005, "a3"),
                new ComparableDirectedEdge<>("a3", 1005, "a2")));
    }

    @Test
    public void testTransformDirectedGraph_NoEdges() throws Exception {
        final DirectedGraph<Integer, DirectedEdge<Integer, Integer>> dg = new DefaultDirectedGraph<>(TEST_EDGE_FACTORY);
        addVertexes(dg, 0, 1, 2, 3);

        final DirectedGraph<String, ComparableDirectedEdge<String, Integer, String>> res = new DefaultDirectedGraph<>(
                new EdgeFactory<String, ComparableDirectedEdge<String, Integer, String>>() {
                    @Override
                    public ComparableDirectedEdge<String, Integer, String> createEdge(final String s, final String t) {
                        return new ComparableDirectedEdge<>(s, 0, t);
                    }
                });

        final Function<Integer, String> fv = new Function<Integer, String>() {
            @Override
            public String apply(final Integer integer) {
                return "b" + integer;
            }
        };

        final Function<Triple<DirectedEdge<Integer, Integer>, String, String>, ComparableDirectedEdge<String, Integer, String>> fe = new Function<Triple<DirectedEdge<Integer, Integer>, String, String>, ComparableDirectedEdge<String, Integer, String>>() {
            @Override
            public ComparableDirectedEdge<String, Integer, String> apply(
                    final Triple<DirectedEdge<Integer, Integer>, String, String> args) {
                return null;
            }
        };

        transformDirectedGraph(dg, res, fv, fe, NOT_REVERSED);

        assertEquals(res.vertexSet(), newHashSet("b0", "b1", "b2", "b3"));
        assertEquals(res.edgeSet().size(), 0);
    }

    @Test
    public void testAddSourceVertex() throws Exception {
        DirectedGraph<Integer, DirectedEdge<Integer, Integer>> dg = new DefaultDirectedGraph<>(TEST_EDGE_FACTORY);
        addVertexes(dg, 0, 1, 2, 3);
        addDirectedEdges(dg, 0, 2, 1, 2, 2, 3);

        addSourceVertex(dg, 4);
        assertThat(dg.vertexSet(), is((Set<Integer>) newHashSet(0, 1, 2, 3, 4)));
        assertThat(dg.edgeSet(),
                is((Set<DirectedEdge<Integer, Integer>>) newHashSet(new DirectedEdge<>(4, 0), new DirectedEdge<>(4, 1),
                        new DirectedEdge<>(0, 2), new DirectedEdge<>(1, 2), new DirectedEdge<>(2, 3))));

        dg = new DefaultDirectedGraph<>(TEST_EDGE_FACTORY);
        addVertexes(dg, 0, 1, 2, 3);
        addDirectedEdges(dg, 0, 2, 1, 2, 2, 3);

        final Function<DirectedEdge<Integer, Integer>, DirectedEdge<Integer, Integer>> edgeBuilder = new Function<DirectedEdge<Integer, Integer>, DirectedEdge<Integer, Integer>>() {
            @Override
            public DirectedEdge<Integer, Integer> apply(
                    final DirectedEdge<Integer, Integer> integerIntegerDirectedEdge) {
                return new ComparableDirectedEdge<>(integerIntegerDirectedEdge.src(), 7L,
                        integerIntegerDirectedEdge.dst());
            }
        };
        dg.addVertex(4);
        addSourceVertex(dg, 4, edgeBuilder);
        assertEquals(dg.vertexSet(), newHashSet(0, 1, 2, 3, 4));
        assertEquals(dg.edgeSet(),
                newHashSet(new ComparableDirectedEdge<>(4, 7L, 0), new ComparableDirectedEdge<>(4, 7L, 1),
                        new DirectedEdge<>(0, 2), new DirectedEdge<>(1, 2), new DirectedEdge<>(2, 3)));
    }

    @Test
    public void testAddSinkVertex() throws Exception {
        DirectedGraph<Integer, DirectedEdge<Integer, Integer>> dg = new DefaultDirectedGraph<>(TEST_EDGE_FACTORY);
        addVertexes(dg, 0, 1, 2, 3);
        addDirectedEdges(dg, 0, 2, 0, 1, 2, 3);

        addSinkVertex(dg, 4);
        assertEquals(dg.vertexSet(), newHashSet(0, 1, 2, 3, 4));
        assertEquals(dg.edgeSet(),
                newHashSet(new DirectedEdge<>(1, 4), new DirectedEdge<>(3, 4), new DirectedEdge<>(0, 2),
                        new DirectedEdge<>(0, 1), new DirectedEdge<>(2, 3)));

        dg = new DefaultDirectedGraph<>(TEST_EDGE_FACTORY);
        addVertexes(dg, 0, 1, 2, 3);
        addDirectedEdges(dg, 0, 2, 0, 1, 2, 3);

        final Function<DirectedEdge<Integer, Integer>, DirectedEdge<Integer, Integer>> edgeBuilder = new Function<DirectedEdge<Integer, Integer>, DirectedEdge<Integer, Integer>>() {
            @Override
            public DirectedEdge<Integer, Integer> apply(
                    final DirectedEdge<Integer, Integer> integerIntegerDirectedEdge) {
                return new ComparableDirectedEdge<>(integerIntegerDirectedEdge.src(), "Q",
                        integerIntegerDirectedEdge.dst());
            }
        };
        dg.addVertex(4);
        addSinkVertex(dg, 4, edgeBuilder);
        assertEquals(dg.vertexSet(), newHashSet(0, 1, 2, 3, 4));
        assertEquals(dg.edgeSet(),
                newHashSet(new ComparableDirectedEdge<>(1, "Q", 4), new ComparableDirectedEdge<>(3, "Q", 4),
                        new DirectedEdge<>(0, 2), new DirectedEdge<>(0, 1), new DirectedEdge<>(2, 3)));
    }

    @Test
    public void testGetInVsOutEdgeMinority() {
        bag.graph.removeEdge(bag.e122);

        verifyMinority(new HashSet<>(Collections.singletonList(bag.e312)), bag.graph, bag.v2);
        verifyMinority(new HashSet<>(Collections.singletonList(bag.e312)), bag.graph, bag.v3);

        bag.graph.removeEdge(bag.e312);

        verifyMinority(new HashSet<>(Collections.singletonList(bag.e211)), bag.graph, bag.v1);
        verifyMinority(new HashSet<>(Arrays.asList(bag.e211, bag.e203)), bag.graph, bag.v2);
        verifyMinority(new HashSet<>(Collections.singletonList(bag.e203)), bag.graph, bag.v3);
    }

    @Test
    public void testFeedbackEdgeSetOf0() {
        final EdgeFactory<Integer, ComparableDirectedEdge<Integer, Integer, Integer>> edgeFactory = GraphUtils
                .newDummyEdgeFactory();
        final DirectedGraph<Integer, ComparableDirectedEdge<Integer, Integer, Integer>> graph = new DefaultDirectedGraph<>(
                edgeFactory);
        final Integer v1 = 1;

        Set<ComparableDirectedEdge<Integer, Integer, Integer>> cuts = GraphUtils.feedbackEdgeSetOf(graph);
        Assert.assertNotNull(cuts);
        Assert.assertEquals(0, cuts.size());

        graph.addVertex(v1);

        cuts = GraphUtils.feedbackEdgeSetOf(graph);
        Assert.assertNotNull(cuts);
        Assert.assertEquals(0, cuts.size());
    }

    @Test
    public void testFeedbackEdgeSetOf1() {
        final EdgeFactory<Integer, ComparableDirectedEdge<Integer, Integer, Integer>> edgeFactory = GraphUtils
                .newDummyEdgeFactory();
        final DirectedGraph<Integer, ComparableDirectedEdge<Integer, Integer, Integer>> graph = new DefaultDirectedGraph<>(
                edgeFactory);
        final Integer v1 = 1; // simple cycle
        final Integer v2 = 2;
        graph.addVertex(v1);
        graph.addVertex(v2);
        final ComparableDirectedEdge<Integer, Integer, Integer> e112 = new ComparableDirectedEdge<>(v1, 1, v2);
        final ComparableDirectedEdge<Integer, Integer, Integer> e211 = new ComparableDirectedEdge<>(v2, 1, v1);
        graph.addEdge(v1, v2, e112);
        graph.addEdge(v2, v1, e211);

        final Set<ComparableDirectedEdge<Integer, Integer, Integer>> cuts = GraphUtils.feedbackEdgeSetOf(graph);
        Assert.assertNotNull(cuts);
        Assert.assertEquals(1, cuts.size());
        TestUtil.assertIsAnyOf(cuts.iterator().next(), Arrays.asList(e112, e211));
    }

    @Test
    public void testFeedbackEdgeSetOf2() {
        final TestGraph bag = new TestGraph(); // SC with shared vertex (2 cycles)
        final Set<ComparableDirectedEdge<Integer, Integer, Integer>> cuts = GraphUtils.feedbackEdgeSetOf(bag.graph);
        Assert.assertEquals(new HashSet<>(Arrays.asList(bag.e122, bag.e312)), cuts);
    }

    @Test
    public void testFeedbackEdgeSetOf3() {
        final TestGraph bag = new TestGraph(); // SC with shared vertex
        final Integer v4 = 4; // ear with shared path 2 -> 4 -> 3
        final Integer v5 = 5; // dead-end 4 -> 5, connected to SC
        final Integer v6 = 6; // disconnected SC 6 <-> 7
        final Integer v7 = 7;
        final Integer v8 = 8; // disconnected dead-end 8 -> 9
        final Integer v9 = 9;
        final Integer v10 = 10; // stand-alone
        final ComparableDirectedEdge<Integer, Integer, Integer> e214 = new ComparableDirectedEdge<>(bag.v2, 1, v4);
        final ComparableDirectedEdge<Integer, Integer, Integer> e413 = new ComparableDirectedEdge<>(v4, 1, bag.v3);
        final ComparableDirectedEdge<Integer, Integer, Integer> e415 = new ComparableDirectedEdge<>(v4, 1, v5);
        final ComparableDirectedEdge<Integer, Integer, Integer> e617 = new ComparableDirectedEdge<>(v6, 1, v7);
        final ComparableDirectedEdge<Integer, Integer, Integer> e706 = new ComparableDirectedEdge<>(v7, 0, v6);
        final ComparableDirectedEdge<Integer, Integer, Integer> e809 = new ComparableDirectedEdge<>(v8, 0, v9);
        addVertexes(bag.graph, v4, v5, v6, v7, v8, v9, v10);
        bag.graph.addEdge(bag.v2, v4, e214);
        bag.graph.addEdge(v4, bag.v3, e413);
        bag.graph.addEdge(v4, v5, e415);
        bag.graph.addEdge(v6, v7, e617);
        bag.graph.addEdge(v7, v6, e706);
        bag.graph.addEdge(v8, v9, e809);

        final Set<ComparableDirectedEdge<Integer, Integer, Integer>> cuts = GraphUtils.feedbackEdgeSetOf(bag.graph);
        Assert.assertEquals(new HashSet<>(Arrays.asList(bag.e122, bag.e312, e617)), cuts);
    }

    @Test
    public void testAddGraphToGraph() {
        final DirectedGraph<Integer, DirectedEdge<Integer, Integer>> dstDg = new DefaultDirectedGraph<>(
                TEST_EDGE_FACTORY);
        addVertexes(dstDg, 0, 1, 2, 3);
        addDirectedEdges(dstDg, 0, 2, 1, 2, 2, 3, 3, 2);

        final DirectedGraph<Integer, DirectedEdge<Integer, Integer>> srcDg = new DefaultDirectedGraph<>(
                TEST_EDGE_FACTORY);
        addVertexes(srcDg, 10, 11, 20, 30, 40);
        addDirectedEdges(srcDg, 10, 20, 11, 20, 20, 30, 30, 40);

        addGraphToGraph(srcDg, dstDg);

        verifyVertexes(dstDg, 0, 1, 2, 3, 10, 11, 20, 30, 40);
        verifyDirectedEdges(dstDg, 0, 2, 1, 2, 2, 3, 3, 2, 10, 20, 11, 20, 20, 30, 30, 40);
    }

    @Test
    public void testAddGraphToGraph_SingleVertexToGraph() {
        final DirectedGraph<Integer, DirectedEdge<Integer, Integer>> dstDg = new DefaultDirectedGraph<>(
                TEST_EDGE_FACTORY);
        addVertexes(dstDg, 0, 1, 2, 3);
        addDirectedEdges(dstDg, 0, 2, 1, 2, 2, 3, 3, 2);

        final DirectedGraph<Integer, DirectedEdge<Integer, Integer>> srcDg = new DefaultDirectedGraph<>(
                TEST_EDGE_FACTORY);
        addVertexes(srcDg, 10);

        addGraphToGraph(srcDg, dstDg);

        verifyVertexes(dstDg, 0, 1, 2, 3, 10);
        verifyDirectedEdges(dstDg, 0, 2, 1, 2, 2, 3, 3, 2);
    }

    @Test
    public void testAddGraphToGraph_GraphToSingleVertex() {
        final DirectedGraph<Integer, DirectedEdge<Integer, Integer>> dstDg = new DefaultDirectedGraph<>(
                TEST_EDGE_FACTORY);
        addVertexes(dstDg, 0);

        final DirectedGraph<Integer, DirectedEdge<Integer, Integer>> srcDg = new DefaultDirectedGraph<>(
                TEST_EDGE_FACTORY);
        addVertexes(srcDg, 10, 11, 20, 30, 40);
        addDirectedEdges(srcDg, 10, 20, 11, 20, 20, 30, 30, 40);

        addGraphToGraph(srcDg, dstDg);

        verifyVertexes(dstDg, 0, 10, 11, 20, 30, 40);
        verifyDirectedEdges(dstDg, 10, 20, 11, 20, 20, 30, 30, 40);
    }

    @Test
    public void testAddGraphToGraph_EmptyToEmpty() {
        final DirectedGraph<Integer, DirectedEdge<Integer, Integer>> dstDg = new DefaultDirectedGraph<>(
                TEST_EDGE_FACTORY);
        final DirectedGraph<Integer, DirectedEdge<Integer, Integer>> srcDg = new DefaultDirectedGraph<>(
                TEST_EDGE_FACTORY);

        addGraphToGraph(srcDg, dstDg);

        verifyVertexes(dstDg);
        verifyDirectedEdges(dstDg);
    }

    @Test
    public void testAddGraphToGraph_GrpahToEmpty() {
        final DirectedGraph<Integer, DirectedEdge<Integer, Integer>> dstDg = new DefaultDirectedGraph<>(
                TEST_EDGE_FACTORY);

        final DirectedGraph<Integer, DirectedEdge<Integer, Integer>> srcDg = new DefaultDirectedGraph<>(
                TEST_EDGE_FACTORY);
        addVertexes(srcDg, 10, 11, 20, 30, 40);
        addDirectedEdges(srcDg, 10, 20, 11, 20, 20, 30, 30, 40);

        addGraphToGraph(srcDg, dstDg);

        assertEquals(srcDg, dstDg);

        verifyVertexes(dstDg, 10, 11, 20, 30, 40);
        verifyDirectedEdges(dstDg, 10, 20, 11, 20, 20, 30, 30, 40);
    }

    @Test
    public void testAddGraphToGraph_EmptyToGraph() {
        final DirectedGraph<Integer, DirectedEdge<Integer, Integer>> dstDg = new DefaultDirectedGraph<>(
                TEST_EDGE_FACTORY);
        addVertexes(dstDg, 0, 1, 2, 3);
        addDirectedEdges(dstDg, 0, 2, 1, 2, 2, 3, 3, 2);

        final DirectedGraph<Integer, DirectedEdge<Integer, Integer>> srcDg = new DefaultDirectedGraph<>(
                TEST_EDGE_FACTORY);

        addGraphToGraph(srcDg, dstDg);

        verifyVertexes(dstDg, 0, 1, 2, 3);
        verifyDirectedEdges(dstDg, 0, 2, 1, 2, 2, 3, 3, 2);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testAddGraphToGraph_SrcVertexInDstGraph() {
        final DirectedGraph<Integer, DirectedEdge<Integer, Integer>> dstDg = new DefaultDirectedGraph<>(
                TEST_EDGE_FACTORY);
        addVertexes(dstDg, 0, 1, 2, 3);
        final DirectedGraph<Integer, DirectedEdge<Integer, Integer>> srcDg = new DefaultDirectedGraph<>(
                TEST_EDGE_FACTORY);
        addVertexes(srcDg, 10, 1, 20, 30);

        addGraphToGraph(srcDg, dstDg);
    }

    @Test
    public void testLoopEdgesOf() throws Exception {

        // one loop
        final DirectedGraph<Integer, DirectedEdge<Integer, Integer>> dg1 = new DefaultDirectedGraph<>(TEST_EDGE_FACTORY);
        addVertexes(dg1, 0, 1, 2, 3);
        addDirectedEdges(dg1, 0, 2, 1, 2, 2, 3, 3, 2, 1, 1);
        Set<DirectedEdge<Integer, Integer>> result1 = GraphUtils.loopEdgesOf(dg1);
        Set<DirectedEdge<Integer, Integer>> expected1 = TestGraphUtils.buildDirectedEdgeSet(1, 1);
        assertEquals(expected1, result1);

        // two loops
        final DirectedGraph<Integer, DirectedEdge<Integer, Integer>> dg2 = new DefaultDirectedGraph<>(TEST_EDGE_FACTORY);
        addVertexes(dg2, 0, 1, 2, 3);
        addDirectedEdges(dg2, 0, 2, 1, 2, 2, 3, 3, 2, 1, 1, 2, 2);
        Set<DirectedEdge<Integer, Integer>> result2 = GraphUtils.loopEdgesOf(dg2);
        Set<DirectedEdge<Integer, Integer>> expected2 = TestGraphUtils.buildDirectedEdgeSet(1, 1, 2, 2);
        assertEquals(expected2, result2);

        // one double loop
        final DirectedGraph<Integer, DirectedEdge<Integer, Integer>> dg3 = new DefaultDirectedGraph<>(TEST_EDGE_FACTORY);
        addVertexes(dg3, 0, 1, 2, 3);
        addDirectedEdges(dg3, 0, 2, 1, 2, 2, 3, 3, 2, 1, 1, 1, 1);
        Set<DirectedEdge<Integer, Integer>> result3 = GraphUtils.loopEdgesOf(dg3);
        Set<DirectedEdge<Integer, Integer>> expected3 = TestGraphUtils.buildDirectedEdgeSet(1, 1, 1, 1);
        assertEquals(expected3, result3);

        // two double loops
        final DirectedGraph<Integer, DirectedEdge<Integer, Integer>> dg4 = new DefaultDirectedGraph<>(TEST_EDGE_FACTORY);
        addVertexes(dg4, 0, 1, 2, 3);
        addDirectedEdges(dg4, 0, 2, 1, 2, 2, 3, 3, 2, 1, 1, 1, 1, 2, 2, 2, 2);
        Set<DirectedEdge<Integer, Integer>> result4 = GraphUtils.loopEdgesOf(dg4);
        Set<DirectedEdge<Integer, Integer>> expected4 = TestGraphUtils.buildDirectedEdgeSet(1, 1, 1, 1, 2, 2, 2, 2);
        assertEquals(expected4, result4);
    }

    @Test
    public void testLeveledTopologyIterator() {
        final DirectedAcyclicGraph<Integer, DirectedEdge<Integer, Integer>> dag = TestUtil.newBigDag();

        Iterator<Set<Integer>> it = GraphUtils.leveledTopologicalOrderIteratorOf(dag);
        assertNextIs(it, 10);
        assertNextIs(it, 20, 30, 40);
        assertNextIs(it, 50);
        assertNextIs(it, 60);
        assertNextIs(it, 70);
        assertNextIs(it, 71);
        assertNextIs(it, 41, 61);
        assertNextIs(it, 31, 51);
        assertNextIs(it, 21);
        assertNextIs(it, 11);
        assertFalse(it.hasNext());
    }

    private void verifyMinority(final Set<ComparableDirectedEdge<Integer, Integer, Integer>> expectedMinority,
            final DirectedGraph<Integer, ComparableDirectedEdge<Integer, Integer, Integer>> graph, final Integer v) {

        final Set<ComparableDirectedEdge<Integer, Integer, Integer>> actualVMinorityEdges = getInVsOutEdgeMinority(
                graph.edgesOf(v), v);
        Assert.assertNotNull(actualVMinorityEdges);
        Assert.assertEquals(expectedMinority, actualVMinorityEdges);
    }

}
