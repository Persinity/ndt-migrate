/**
 * Copyright (c) 2015 Persinity Inc.
 */
package com.persinity.common.collection;

import static com.persinity.common.collection.TestGraphUtils.TEST_EDGE_FACTORY;
import static com.persinity.common.collection.TestGraphUtils.addVertexes;
import static com.persinity.test.TestUtil.assertNextIsAnyOf;
import static java.util.Arrays.asList;
import static org.junit.Assert.assertFalse;

import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.jgrapht.DirectedGraph;
import org.jgrapht.alg.StrongConnectivityInspector;
import org.jgrapht.experimental.dag.DirectedAcyclicGraph;
import org.jgrapht.experimental.dag.DirectedAcyclicGraph.CycleFoundException;
import org.jgrapht.experimental.isomorphism.AdaptiveIsomorphismInspectorFactory;
import org.jgrapht.experimental.isomorphism.GraphIsomorphismInspector;
import org.jgrapht.graph.DefaultDirectedGraph;
import org.jgrapht.graph.EdgeReversedGraph;
import org.jgrapht.graph.UnmodifiableDirectedGraph;
import org.jgrapht.traverse.BreadthFirstIterator;
import org.jgrapht.traverse.TopologicalOrderIterator;
import org.junit.Assert;
import org.junit.Test;

import com.persinity.test.TestUtil;

/**
 * Minimal acceptance test of the TP DirectGraph (JGraphT framework) for the purposes of NDT schema traversal.<BR>
 * It also shows how JGraphT can be employed for the purpose of NDT schema traversal, topological sort, etc.
 *
 * @author Doichin Yordanov
 */
public class DirectGraphTest {

    /**
     * Test of equals and hashCode of DAG.
     */
    @Test
    public void testDagEqualsHashCode() {
        final DirectedAcyclicGraph<Integer, DirectedEdge<Integer, Integer>> dag1 = newDag();
        final DirectedAcyclicGraph<Integer, DirectedEdge<Integer, Integer>> dag2 = newDag();

        Assert.assertEquals(dag1, dag2);
        Assert.assertEquals(dag2, dag1);
        Assert.assertEquals(dag1.hashCode(), dag2.hashCode());
        Assert.assertNotEquals(dag1, null);
        try {
            dag2.addDagEdge(2, 0);
        } catch (final CycleFoundException e) {
            Assert.fail(e.getClass().getSimpleName());
        }
        Assert.assertNotEquals(dag1, dag2);
    }

    /**
     * Test of BFS of DAG, used in entity and ETL instruction traversal.
     */
    @Test
    public void testBfs() {
        final DirectedAcyclicGraph<Integer, DirectedEdge<Integer, Integer>> dag = newDag();
        final BreadthFirstIterator<Integer, DirectedEdge<Integer, Integer>> bfsIt = new BreadthFirstIterator<>(dag, 3);
        assertNextIsAnyOf(bfsIt, Collections.singletonList(Integer.valueOf(3)));
        assertNextIsAnyOf(bfsIt, asList(Integer.valueOf(2), Integer.valueOf(1)));
        assertNextIsAnyOf(bfsIt, asList(Integer.valueOf(2), Integer.valueOf(1)));
        assertNextIsAnyOf(bfsIt, Collections.singletonList(Integer.valueOf(0)));
    }

    @Test
    public void testBfs_1Vertex() {
        final DirectedGraph<Integer, DirectedEdge<Integer, Integer>> dg = new DefaultDirectedGraph<>(TEST_EDGE_FACTORY);
        addVertexes(dg, 10);

        final BreadthFirstIterator<Integer, DirectedEdge<Integer, Integer>> bfsIt = new BreadthFirstIterator<>(dg, 10);
        assertNextIsAnyOf(bfsIt, Collections.singletonList(10));
    }

    @Test
    public void testBfs_Vertexes() {
        final DirectedAcyclicGraph<Integer, DirectedEdge<Integer, Integer>> dg = TestUtil.newBigDag();

        final BreadthFirstIterator<Integer, DirectedEdge<Integer, Integer>> bfsIt = new BreadthFirstIterator<>(dg, 10);
        assertNextIsAnyOf(bfsIt, asList(10));
        assertNextIsAnyOf(bfsIt, asList(20, 30));
        assertNextIsAnyOf(bfsIt, asList(20, 30));
        assertNextIsAnyOf(bfsIt, asList(40, 50));
        assertNextIsAnyOf(bfsIt, asList(40, 50));
        assertNextIsAnyOf(bfsIt, asList(60, 70));
        assertNextIsAnyOf(bfsIt, asList(60, 70));
        assertNextIsAnyOf(bfsIt, asList(71));
        assertNextIsAnyOf(bfsIt, asList(41, 61));
        assertNextIsAnyOf(bfsIt, asList(41, 61));
        assertNextIsAnyOf(bfsIt, asList(11));
        assertNextIsAnyOf(bfsIt, asList(31, 51));
        assertNextIsAnyOf(bfsIt, asList(31, 51));
        assertNextIsAnyOf(bfsIt, asList(21));
        assertFalse(bfsIt.hasNext());
    }

    /**
     * Same test for graph from {@link #testBfs_Vertexes}
     */
    @Test
    public void testTopo_Vertexes() {
        final DirectedGraph<Integer, DirectedEdge<Integer, Integer>> dg = TestUtil.newBigDag();

        final TopologicalOrderIterator<Integer, DirectedEdge<Integer, Integer>> bfsIt = new TopologicalOrderIterator<>(
                dg);
        assertNextIsAnyOf(bfsIt, asList(10));
        assertNextIsAnyOf(bfsIt, asList(20, 30));
        assertNextIsAnyOf(bfsIt, asList(20, 30));
        assertNextIsAnyOf(bfsIt, asList(40, 50));
        assertNextIsAnyOf(bfsIt, asList(40, 50));
        assertNextIsAnyOf(bfsIt, asList(60, 70));
        assertNextIsAnyOf(bfsIt, asList(60, 70));
        assertNextIsAnyOf(bfsIt, asList(71));
        assertNextIsAnyOf(bfsIt, asList(41, 61));
        assertNextIsAnyOf(bfsIt, asList(41, 61));
        assertNextIsAnyOf(bfsIt, asList(31, 51));
        assertNextIsAnyOf(bfsIt, asList(31, 51));
        assertNextIsAnyOf(bfsIt, asList(21));
        assertNextIsAnyOf(bfsIt, asList(11));
        assertFalse(bfsIt.hasNext());
    }

    /**
     * Test of graph reversing and equality (topological isomorphism + vertex set), used in DML instruction traversal.
     */
    @Test
    public void testReverseAndIsomorphic() {
        final DirectedGraph<Integer, DirectedEdge<Integer, Integer>> dag = newDag();
        final DirectedGraph<Integer, DirectedEdge<Integer, Integer>> reversedDag = new EdgeReversedGraph<>(dag);
        final DirectedGraph<Integer, DirectedEdge<Integer, Integer>> doubleReversedDag = new EdgeReversedGraph<>(
                reversedDag);

        Assert.assertNotEquals(dag, reversedDag);
        @SuppressWarnings("unchecked")
        final GraphIsomorphismInspector<DirectedEdge<Integer, Integer>> nonIsomorphic = AdaptiveIsomorphismInspectorFactory
                .createIsomorphismInspector(dag, reversedDag);
        assertFalse(nonIsomorphic.isIsomorphic());
        Assert.assertEquals(dag.vertexSet(), reversedDag.vertexSet());

        Assert.assertNotEquals(dag, doubleReversedDag);
        @SuppressWarnings("unchecked")
        final GraphIsomorphismInspector<DirectedEdge<Integer, Integer>> isomorphic = AdaptiveIsomorphismInspectorFactory
                .createIsomorphismInspector(dag, doubleReversedDag);
        Assert.assertTrue(isomorphic.isIsomorphic());
        Assert.assertEquals(dag.vertexSet(), doubleReversedDag.vertexSet());
    }

    @Test(expected = UnsupportedOperationException.class)
    public void testUnmodifiable() {
        final DirectedGraph<Integer, DirectedEdge<Integer, Integer>> dg = new UnmodifiableDirectedGraph<>(newDag());
        dg.removeVertex(0);
    }

    /**
     * Strongly connected vertices are those that contain cycle
     */
    @Test
    public void testStronglyConnected() {
        final DirectedGraph<Integer, DirectedEdge<Integer, Integer>> dg = new DefaultDirectedGraph<>(TEST_EDGE_FACTORY);
        dg.addVertex(4);
        dg.addVertex(3);
        dg.addVertex(2);
        dg.addVertex(1);
        dg.addVertex(0);
        dg.addEdge(4, 3); // singleton strong component
        dg.addEdge(3, 2); // diamond
        dg.addEdge(2, 1);
        dg.addEdge(1, 3);
        dg.addEdge(1, 0); // adjacent diamond
        dg.addEdge(0, 1);

        final StrongConnectivityInspector<Integer, DirectedEdge<Integer, Integer>> inspector = new StrongConnectivityInspector<>(
                dg);

        final List<Set<Integer>> stronglyConnectedVerticeSets = inspector.stronglyConnectedSets();
        final Iterator<Set<Integer>> scSetIt = stronglyConnectedVerticeSets.iterator();
        final Set<Integer> sc1 = new HashSet<>(asList(4));
        final Set<Integer> sc2 = new HashSet<>(asList(0, 1, 2, 3));
        assertNextIsAnyOf(scSetIt, asList(sc1, sc2));
        assertNextIsAnyOf(scSetIt, asList(sc1, sc2));

    }

    private static DirectedAcyclicGraph<Integer, DirectedEdge<Integer, Integer>> newDag() {
        final DirectedAcyclicGraph<Integer, DirectedEdge<Integer, Integer>> dag = new DirectedAcyclicGraph<>(
                TEST_EDGE_FACTORY);
        dag.addVertex(0);
        dag.addVertex(1);
        dag.addVertex(3);
        dag.addVertex(2);
        try {
            dag.addDagEdge(3, 2);
            dag.addDagEdge(2, 1);
            dag.addDagEdge(3, 1);
            dag.addDagEdge(1, 0);
        } catch (final CycleFoundException e) {
            System.err.println(e);
        }
        return dag;
    }
}
