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

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import org.jgrapht.Graph;

import com.persinity.common.db.metainfo.Col;
import com.persinity.common.db.metainfo.FKEdge;
import com.persinity.common.db.metainfo.constraint.FK;
import com.persinity.common.db.metainfo.constraint.Unique;

/**
 * @author Ivo Yanakiev
 */
public class TestTopologyBuilderUtil {

    @SuppressWarnings("unchecked")
    public static void addEdges(Graph graph, FKEdge... edges) {

        for (FKEdge edge : edges) {
            graph.addVertex(edge.src());
            graph.addVertex(edge.dst());
            graph.addEdge(edge.src(), edge.dst(), edge);
        }
    }

    public static FKEdge buildFkEdge(String src, String dst, Set<Col> srcCols, Set<Col> dstCols) {
        return new FKEdge(src, buildFK(src, dst, srcCols, dstCols), dst);
    }

    public static FK buildFK(String src, String dst, Set<Col> srcCols, Set<Col> dstCols) {
        return new FK(src, src, srcCols, new Unique(dst, dstCols));
    }

    // TODO replace with Sets.newHashSet
    @SuppressWarnings("unchecked")
    public static <T> Set<T> toSet(T... fks) {
        return new HashSet(Arrays.asList(fks));
    }

}
