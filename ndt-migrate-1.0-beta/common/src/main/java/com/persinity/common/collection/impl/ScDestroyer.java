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

import java.util.Set;

import org.jgrapht.DirectedGraph;

/**
 * Seeks and and tries to destroy the minimal amount of edge(s) in a strongly connected directed graph. After destroy
 * the graph is not strongly connected.
 *
 * @param <V>
 * @param <E>
 * @author Doichin Yordanov
 */
public interface ScDestroyer<V, E> {

    /**
     * Seeks and destroys edges in a strongly connected directed graph, according subclass strategy.
     *
     * @param sc
     *         Strongly connected component to destroy edge in. Note that the graph is modified.
     * @return The destroyed edge(s).
     */
    Set<E> seekNDestroy(DirectedGraph<V, E> sc);

}