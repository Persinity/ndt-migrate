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

import static com.persinity.common.invariant.Invariant.assertArg;
import static com.persinity.common.invariant.Invariant.notNull;

import java.util.ArrayDeque;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Deque;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.Set;

import org.jgrapht.DirectedGraph;
import org.jgrapht.EdgeFactory;
import org.jgrapht.Graph;
import org.jgrapht.alg.StrongConnectivityInspector;
import org.jgrapht.experimental.dag.DirectedAcyclicGraph;

import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.google.common.collect.Collections2;
import com.persinity.common.collection.impl.ScDestroyer;
import com.persinity.common.collection.impl.ScDestroyerFactory;
import com.persinity.common.collection.impl.SharedEdgeScDestroyerFactory;
import com.persinity.common.invariant.Invariant;

/**
 * Utils for graphs
 *
 * @author Doichin Yordanov
 */
public class GraphUtils {
    public static final boolean NOT_REVERSED = false;
    public static final boolean REVERSED = true;

    public enum EdgeDirection {
        IN, OUT, ANY
    }

    /**
     * Discriminates the majority of in-vs-out edges and returns the minority edge set if available.<BR>
     * Can be used to get the edges shared by multiple cycles as according the ear decomposition theorem for strongly
     * connected (SC) components, each SC can be decomposed into ears, each sharing either a vertex or path in prev.
     * ear. Sharing a path, means that the joint start-end vertices denoting that path have in-vs-out degree |D| > 0,
     * i.e. such vertices have minority of edges that take part in shared path(s).
     *
     * @param v
     * @param edges
     *         set of directed edges to modify (in place) set of directed edges to modify (in place)
     * @return The minority set of the in-vs-out edge sets of given vertex. If the output degree is equal to the input
     * degree, then the output set is returned. If edges only of one type are available, they are returned.
     */
    public static <V, E extends DirectedEdge<V, V>> Set<E> getInVsOutEdgeMinority(final Collection<E> edges,
            final V v) {
        final Set<E> outgoingEdges = new HashSet<>(Collections2.filter(edges, new Predicate<E>() {
            @Override
            public boolean apply(final E input) {
                return v.equals(input.src());
            }
        }));

        final Set<E> incomingEdges = new HashSet<>(edges);
        incomingEdges.removeAll(outgoingEdges);

        Set<E> result = null;
        if (incomingEdges.isEmpty() || !outgoingEdges.isEmpty() && outgoingEdges.size() <= incomingEdges.size()) {
            result = outgoingEdges;
        } else {
            result = incomingEdges;
        }

        return result;
    }

    /**
     * @return dummy {@link EdgeFactory} to bypass insufficiency in the construction of graphs throughout JGraphT, which
     * all require an {@link EdgeFactory} although it is rendered useless by the
     * {@link Graph#addEdge(Object, Object, Object)} method.
     */
    public static <V, E> EdgeFactory<V, E> newDummyEdgeFactory() {
        return new EdgeFactory<V, E>() {
            @Override
            public E createEdge(final V sourceVertex, final V targetVertex) {
                throw new UnsupportedOperationException();
            }
        };
    }

    /**
     * Reverses a {@link DirectedEdge}
     *
     * @param e
     * @return
     */
    public static <L, R> DirectedEdge<R, L> reverseEdge(final DirectedEdge<L, R> e) {
        return new DirectedEdge<R, L>(e.dst(), e.src());
    }

    /**
     * Adds a weight to an {@link DirectedEdge}
     * <p/>
     *
     * @param e
     * @param weight
     * @return
     */
    public static <L, W extends Comparable<? super W>, R, E extends DirectedEdge<L, R>> E weightedEdgeOf(final E e,
            final W weight) {
        @SuppressWarnings("unchecked")
        final E weightedEdge = (E) new ComparableDirectedEdge<L, W, R>(e, weight);
        return weightedEdge;
    }

    /**
     * A feedback edge set is a set of edges which, when removed from a graph, leave DAG.<BR>
     * http://en.wikipedia.org/wiki/Feedback_arc_set<BR>
     * Dropping as few edges as possible, obtaining a minimum feedback edge set is NP-hard, so minimal set is by no way
     * guaranteed by this implementation.<BR>
     * <BR>
     * The method first builds a map for each of the DG vertices with:<BR>
     * - disbalanceFactor = sum of the number of incoming minus the number of outgoing edges<BR>
     * - cut potential = sum of the weights of incoming minus the weights of outgoing edges.<BR>
     * The passed DG is broken into strongly connected components (SC)s and for each of them an edge is searched and
     * destroyed. The resulting sub-graphs are decycled recursively.<BR>
     * <BR>
     * The complexity of finding SCs is O(n). The upper bound complexity of this alg is O(n.m/2), where: n is the number
     * of vertices; m is the number of edges in the graph. The lower bound however is Omega(n.log(m/2)), which is
     * achieved when each cut edge breaks the result graph into two or more SCs.
     *
     * @param dg
     *         Directed weighted graph to decycle. Edges are weighted according how suitable for cut are they. The
     *         lower the weight, the less desired to be cut. 0 can be used for edges that
     *         should not be cut.
     * @return (Probably the minimal) feedback edge set which when removed turns the DG into DAG.
     */
    public static <V, E extends ComparableDirectedEdge<V, Integer, V>> Set<E> feedbackEdgeSetOf(
            final DirectedGraph<V, E> dg) {
        Invariant.notNull(dg);

        final ScDestroyerFactory<V, E> edgeDestroyerFactory = new SharedEdgeScDestroyerFactory<>();
        final ScDestroyer<V, E> cycle = edgeDestroyerFactory.newDestroyerFor(dg);

        final Deque<DirectedGraph<V, E>> scStack = new ArrayDeque<>();
        Collection<? extends DirectedGraph<V, E>> scGraphs = filterSingletons(getScSubGraphsOf(dg));
        pushScSubGraphs(scGraphs, scStack);

        final Set<E> result = new HashSet<>();
        while (!scStack.isEmpty()) {
            final DirectedGraph<V, E> subGraph = scStack.pop();
            assert subGraph != null && subGraph.vertexSet().size() > 1;

            final Set<E> destroyed = cycle.seekNDestroy(subGraph);
            result.addAll(destroyed);

            scGraphs = filterSingletons(getScSubGraphsOf(subGraph));
            pushScSubGraphs(scGraphs, scStack);
        }

        return result;
    }

    /**
     * @param dg
     *         Directed weighted graph.
     * @return All loops edges from the directed graph.
     */
    public static <V, E extends DirectedEdge<V, V>> Set<E> loopEdgesOf(final DirectedGraph<V, E> dg) {

        Set<E> result = new HashSet<>();

        for (E edge : dg.edgeSet()) {
            if (Objects.equals(edge.src(), edge.dst())) {
                result.add(edge);
            }
        }

        return result;
    }

    /**
     * @param dg
     * @return The strongly connected components found in the passed directed graph.
     */
    public static <V, E> Collection<? extends DirectedGraph<V, E>> getScSubGraphsOf(final DirectedGraph<V, E> dg) {
        final StrongConnectivityInspector<V, E> scInspect = new StrongConnectivityInspector<>(dg);
        return scInspect.stronglyConnectedSubgraphs();
    }

    /**
     * @param graphs
     *         The input collection to filter, not modified.
     * @return The returned collection is view over the supplied collection and does not support removal.
     */
    public static <G extends Graph<?, ?>> Collection<G> filterSingletons(final Collection<G> graphs) {
        Invariant.assertArg(graphs != null);
        final Collection<G> result = Collections2.filter(graphs, new Predicate<G>() {
            @Override
            public boolean apply(final G input) {
                return input.vertexSet().size() > 1;
            }
        });
        return result;
    }

    /**
     * Retrieves in/out edges of a vertex in a graph.
     *
     * @param dg
     * @param vertex
     * @param direction
     * @return
     */
    public static <V, E> Set<E> getEdgesFor(final DirectedGraph<V, E> dg, final V vertex,
            final EdgeDirection direction) {
        final Set<E> vertexEdges = dg.edgesOf(vertex);
        final Set<E> result = new HashSet<E>();

        for (final E e : vertexEdges) {
            if (EdgeDirection.IN.equals(direction)) {
                if (vertex.equals(dg.getEdgeTarget(e))) {
                    result.add(e);
                }
            } else if (EdgeDirection.OUT.equals(direction)) {
                if (vertex.equals(dg.getEdgeSource(e))) {
                    result.add(e);
                }
            } else if (EdgeDirection.ANY.equals(direction)) {
                result.add(e);
            }
        }

        return result;
    }

    /**
     * @param dg
     * @return The strong components of a graph that contain more than one vertex.
     */
    public static <V, E> List<Set<V>> getScNonSingletonComponents(final DirectedGraph<V, E> dg) {
        final StrongConnectivityInspector<V, E> scInspect = new StrongConnectivityInspector<>(dg);
        final List<Set<V>> scSets = scInspect.stronglyConnectedSets();
        final Iterator<Set<V>> scSetIt = scSets.iterator();
        while (scSetIt.hasNext()) {
            final Set<V> scSet = scSetIt.next();
            if (scSet.size() < 2) {
                scSetIt.remove(); // Singleton SCS can not be cut further.
            }
        }
        return scSets;
    }

    /**
     * Transform one DirectedGraph<VS,ES> into a new DirectedGraph<VD,ED>.
     *
     * @param srcDg
     *         source graph to transform from
     * @param dstDg
     *         destination graph to transform to
     * @param fv
     *         function to transform graph vertexes
     * @param fe
     *         function to transform graph edges
     * @param reverse
     *         reverse the edges
     * @param <VS>
     *         source vertex type
     * @param <ES>
     *         source edge type
     * @param <VD>
     *         destination vertex type
     * @param <ED>
     *         destination edge type
     */
    public static <VS, ES extends DirectedEdge<VS, VS>, VD, ED extends DirectedEdge<VD, VD>> void transformDirectedGraph(
            final DirectedGraph<VS, ES> srcDg, final DirectedGraph<VD, ED> dstDg, final Function<VS, VD> fv,
            final Function<Triple<ES, VD, VD>, ED> fe, final boolean reverse) {

        assertArg(srcDg != null, "srcDg");
        assertArg(dstDg != null, "dstDg");
        assertArg(fv != null, "fv");
        assertArg(fe != null, "fe");

        final HashMap<VS, VD> vmap = new HashMap<>();

        Iterator<VS> iter = srcDg.vertexSet().iterator();
        while (iter.hasNext()) {
            final VS vs = iter.next();
            final VD vd = fv.apply(vs);
            assertArg(vd != null, "Expected not null destination vertex for source: {}", vs);
            dstDg.addVertex(vd);
            vmap.put(vs, vd);
        }

        iter = srcDg.vertexSet().iterator();
        while (iter.hasNext()) {
            final VS vs = iter.next();
            final VD vd = vmap.get(vs);

            final Set<ES> edges = srcDg.outgoingEdgesOf(vs);
            for (final ES es : edges) {
                final VS vsTarget = es.dst();
                final VD vdTarget = vmap.get(vsTarget);
                Triple<ES, VD, VD> t;
                if (reverse) {
                    t = new Triple<>(es, vdTarget, vd);
                } else {
                    t = new Triple<>(es, vd, vdTarget);
                }
                final ED ed = fe.apply(t);
                assertArg(ed != null, "Expected not null destination edge for source: {}", es);
                if (reverse) {
                    dstDg.addEdge(vdTarget, vd, ed);
                } else {
                    dstDg.addEdge(vd, vdTarget, ed);
                }
            }
        }
    }

    /**
     * See {@link GraphUtils#addSourceVertex(DirectedGraph, V, Function)}
     */
    public static <V, E extends DirectedEdge<V, V>> void addSourceVertex(final DirectedGraph<V, E> graph,
            final V rootSourceVertex) {
        addSourceVertex(graph, rootSourceVertex, null);
    }

    /**
     * Add a root source vertex linked to all source vertexes.
     * <p/>
     * A source vertex is a vertex with in-degree zero
     *
     * @param graph
     *         directed graph to add to
     * @param rootSourceVertex
     *         vertex to add
     * @param buildEdgeF
     *         edge builder
     * @param <V>
     *         vertex type
     * @param <E>
     *         edge type should extend {@link DirectedEdge}
     */
    public static <V, E extends DirectedEdge<V, V>> void addSourceVertex(final DirectedGraph<V, E> graph,
            final V rootSourceVertex, final Function<DirectedEdge<V, V>, E> buildEdgeF) {
        notNull(graph, "graph");
        notNull(rootSourceVertex, "rootSourceVertex");

        if (graph.containsVertex(rootSourceVertex)) {
            assertArg(graph.outDegreeOf(rootSourceVertex) == 0 && graph.inDegreeOf(rootSourceVertex) == 0,
                    "Expected base sink vertex to has in and out degree to be 0");
        } else {
            graph.addVertex(rootSourceVertex);
        }

        final HashSet<V> sourceVertexes = new HashSet<>();
        for (final V v : graph.vertexSet()) {
            if (v == rootSourceVertex) {
                continue;
            }
            if (graph.inDegreeOf(v) == 0) {
                sourceVertexes.add(v);
            }
        }

        for (final V sourceVertex : sourceVertexes) {
            if (buildEdgeF != null) {
                final E edge = buildEdgeF.apply(new DirectedEdge<>(rootSourceVertex, sourceVertex));
                assert edge != null;
                graph.addEdge(rootSourceVertex, sourceVertex, edge);
            } else {
                graph.addEdge(rootSourceVertex, sourceVertex);
            }
        }
    }

    /**
     * See {@link GraphUtils#addSinkVertex(DirectedGraph, V, Function)}
     */
    public static <V, E extends DirectedEdge<V, V>> void addSinkVertex(final DirectedGraph<V, E> graph,
            final V baseSinkVertex) {
        addSinkVertex(graph, baseSinkVertex, null);
    }

    /**
     * Add a base sink vertex and link all existing sink vertexes to it.
     * <p/>
     * A sink vertex is a vertex with out-degree zero.
     *
     * @param graph
     *         directed graph to add to
     * @param baseSinkVertex
     *         vertex to add
     * @param buildEdgeF
     *         edge builder
     * @param <V>
     *         vertex type
     * @param <E>
     *         edge type should extend {@link DirectedEdge}
     */
    public static <V, E extends DirectedEdge<V, V>> void addSinkVertex(final DirectedGraph<V, E> graph,
            final V baseSinkVertex, final Function<DirectedEdge<V, V>, E> buildEdgeF) {
        notNull(graph, "graph");
        notNull(baseSinkVertex, "baseSinkVertex");

        if (graph.containsVertex(baseSinkVertex)) {
            assertArg(graph.outDegreeOf(baseSinkVertex) == 0 && graph.inDegreeOf(baseSinkVertex) == 0,
                    "Expected base sink vertex to has in and out degree to be 0");
        } else {
            graph.addVertex(baseSinkVertex);
        }

        final HashSet<V> sinkVertexes = new HashSet<>();
        for (final V v : graph.vertexSet()) {
            if (v == baseSinkVertex) {
                continue;
            }
            if (graph.outDegreeOf(v) == 0) {
                sinkVertexes.add(v);
            }
        }

        for (final V sinkVertex : sinkVertexes) {
            if (buildEdgeF != null) {
                final E edge = buildEdgeF.apply(new DirectedEdge<>(sinkVertex, baseSinkVertex));
                assert edge != null;
                graph.addEdge(sinkVertex, baseSinkVertex, edge);
            } else {
                graph.addEdge(sinkVertex, baseSinkVertex);
            }
        }
    }

    /**
     * @param v
     *         vertex
     * @param dg
     *         graph
     * @param <V>
     *         vertex type
     * @param <E>
     *         edge type must extend {@link ComparableDirectedEdge}
     * @return the edges with max weight among all edges of given graph vertex, or empty set if no edges found.
     */
    public static <V, E extends ComparableDirectedEdge<V, Integer, V>> Set<E> getMaxWeightEdgesOf(final V v,
            final DirectedGraph<V, E> dg) {
        final List<E> edges = new LinkedList<>(getEdgesFor(dg, v, EdgeDirection.ANY));

        Collections.sort(edges, new Comparator<E>() {
            @Override
            public int compare(final E o1, final E o2) {
                return o2.weight() - o1.weight();
            }
        });

        final Set<E> result = CollectionUtils.getFirstGroup(edges, new Function<E, Integer>() {
            @Override
            public Integer apply(final E input) {
                return Math.abs(input.weight());
            }
        });
        return result;
    }

    /**
     * Add one graph to other.
     *
     * @param src
     *         graph to add
     * @param dst
     *         graph to add to
     * @param <V>
     *         vertex type
     * @param <E>
     *         edge type must extend {@link DirectedEdge}
     */
    public static <V, E extends DirectedEdge<V, V>> void addGraphToGraph(final DirectedGraph<V, E> src,
            final DirectedGraph<V, E> dst) {
        notNull(dst, "dst");
        notNull(src, "src");

        for (final V v : src.vertexSet()) {
            assertArg(!dst.containsVertex(v), "Found src vertex: {} in dst: {}", v, dst);
            dst.addVertex(v);
        }

        for (final E e : src.edgeSet()) {
            dst.addEdge(e.src(), e.dst(), e);
        }
    }

    /**
     * Returns leveled topological order of vertices in the given graph. {@see LeveledTopologicalOrderIterator}.
     *
     * @param dag
     * @param <V>
     * @param <E>
     * @return {@link Iterator<V>} that returns graph vertices level by level by their depth, each level contained in a {@link Set}.
     */
    public static <V, E> Iterator<Set<V>> leveledTopologicalOrderIteratorOf(final DirectedAcyclicGraph<V, E> dag) {
        return new LeveledTopologicalOrderIterator<>(dag);
    }

    private static <V, E> void pushScSubGraphs(final Collection<? extends DirectedGraph<V, E>> scGraphs,
            final Deque<DirectedGraph<V, E>> scStack) {

        for (final DirectedGraph<V, E> directedSubgraph : scGraphs) {
            scStack.push(directedSubgraph);
        }
    }
}
