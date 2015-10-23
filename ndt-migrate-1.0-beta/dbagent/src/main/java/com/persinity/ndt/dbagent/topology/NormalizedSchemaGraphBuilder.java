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

package com.persinity.ndt.dbagent.topology;

import static com.persinity.common.collection.GraphUtils.NOT_REVERSED;
import static com.persinity.common.invariant.Invariant.notNull;

import java.util.HashSet;
import java.util.Set;

import org.apache.log4j.Logger;
import org.jgrapht.DirectedGraph;

import com.google.common.base.Function;
import com.persinity.common.collection.ComparableDirectedEdge;
import com.persinity.common.collection.Dg;
import com.persinity.common.collection.GraphUtils;
import com.persinity.common.collection.Triple;
import com.persinity.common.db.metainfo.FKEdge;
import com.persinity.common.db.metainfo.constraint.FK;
import com.persinity.common.fp.RepeaterFunc;
import com.persinity.common.logging.Log4jLogger;
import com.persinity.ndt.transform.EntitiesDag;

/**
 * @author Ivo Yanakiev
 */
public class NormalizedSchemaGraphBuilder {

    public NormalizedSchemaGraphBuilder(final SchemaGraphBuilder schemaGraphBuilder) {

        notNull(schemaGraphBuilder, "schemaGraphBuilder");
        this.schemaGraphBuilder = schemaGraphBuilder;
    }

    public EntitiesDag buildNormalizedTopology() {
        if (entitiesDag == null) {
            build();
        }
        return entitiesDag;
    }

    public Set<FK> getDecycleFks() {
        if (decycleFks == null) {
            build();
        }
        return decycleFks;
    }

    private void build() {
        final SchemaGraph schemaGraph = schemaGraphBuilder.buildTopology();
        final DirectedGraph<String, IntWeightedFKEdge> muleGraph = intWeightedFkGraphOf(schemaGraph);

        final Set<IntWeightedFKEdge> feedbackEdges = GraphUtils.feedbackEdgeSetOf(muleGraph);
        muleGraph.removeAllEdges(feedbackEdges);

        final Set<IntWeightedFKEdge> loopEdges = GraphUtils.loopEdgesOf(muleGraph);
        muleGraph.removeAllEdges(loopEdges);

        entitiesDag = entitiesDagOf(muleGraph);
        try {
            decycleFks = toWeakFks(feedbackEdges);
        } catch (final IllegalArgumentException e) {
            throw new IllegalArgumentException("Schema " + schemaGraph
                    + " is immutable as it contains unbreakable cycle of referential integrity: ", e);
        }
        if (!decycleFks.isEmpty()) {
            log.debug("Ref integrity cycle(s) found. The following FKs can be disabled to break them: {}", decycleFks);
        }

        log.debug("Build entitiesDag: {} decycleFks: {}", entitiesDag, decycleFks);
    }

    private EntitiesDag entitiesDagOf(final DirectedGraph<String, IntWeightedFKEdge> intWeightedFKDg) {
        final EntitiesDag result = new EntitiesDag();
        final Function<Triple<IntWeightedFKEdge, String, String>, FKEdge> fe = new Function<Triple<IntWeightedFKEdge, String, String>, FKEdge>() {
            @Override
            public FKEdge apply(final Triple<IntWeightedFKEdge, String, String> input) {
                return input.getFirst().getFkEdge();
            }
        };
        GraphUtils.transformDirectedGraph(intWeightedFKDg, result, FV_REPEATER, fe, NOT_REVERSED);
        return result;
    }

    private DirectedGraph<String, IntWeightedFKEdge> intWeightedFkGraphOf(final SchemaGraph schemaGraph) {
        final DirectedGraph<String, IntWeightedFKEdge> result = new Dg<>();
        final Function<Triple<FKEdge, String, String>, IntWeightedFKEdge> fe = new Function<Triple<FKEdge, String, String>, NormalizedSchemaGraphBuilder.IntWeightedFKEdge>() {
            @Override
            public IntWeightedFKEdge apply(final Triple<FKEdge, String, String> input) {
                return new IntWeightedFKEdge(input.getFirst());
            }
        };
        GraphUtils.transformDirectedGraph(schemaGraph, result, FV_REPEATER, fe, NOT_REVERSED);
        return result;
    }

    private static class IntWeightedFKEdge extends ComparableDirectedEdge<String, Integer, String> {

        public IntWeightedFKEdge(final FKEdge fkEdge) {
            super(fkEdge.src(), intWeightOf(fkEdge.weight()), fkEdge.dst());
            this.fkEdge = fkEdge;
        }

        public FKEdge getFkEdge() {
            return fkEdge;
        }

        private static int intWeightOf(final FK fk) {
            return fk.isWeakRef() ? WEAK_REF_WEIGHT : STRONG_REF_WEIGHT;
        }

        public static final int STRONG_REF_WEIGHT = 0;

        public static final int WEAK_REF_WEIGHT = 1;

        private final FKEdge fkEdge;

    }

    /**
     * Converts the feedback edge set to weak FKs by extracting them out of the edge set.
     *
     * @param feedbackEdges
     * @return
     * @throws IllegalArgumentException
     *         If the set of edges contains non weak FK or can not be converted to set of weak FKs
     */
    static Set<FK> toWeakFks(final Set<IntWeightedFKEdge> feedbackEdges) throws IllegalArgumentException {
        final Set<FK> result = new HashSet<>();
        for (final IntWeightedFKEdge intWeightedFKEdge : feedbackEdges) {
            final FK fk = intWeightedFKEdge.getFkEdge().weight();
            if (!fk.isWeakRef()) {
                throw new IllegalArgumentException(fk + " should be over nullable col(s).");
            }
            result.add(fk);
        }
        return result;
    }

    private static final Function<String, String> FV_REPEATER = new RepeaterFunc<>();
    private EntitiesDag entitiesDag;
    private Set<FK> decycleFks;
    private final SchemaGraphBuilder schemaGraphBuilder;

    private static final Log4jLogger log = new Log4jLogger(Logger.getLogger(NormalizedSchemaGraphBuilder.class));
}
