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

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Tree with one branch in it.
 *
 * @param <T>
 *         element type
 * @author Doichin Yordanov
 */
class SingleBranchTree<T> extends Tree<T> {
    private final Map<T, T> parent2ChildMap = new HashMap<T, T>();
    private final T root;
    private final List<T> EMPTY_LIST = Collections.emptyList();
    private String toString;

    public SingleBranchTree(final List<T> elements) {
        T prev;
        if (!elements.isEmpty()) {
            prev = root = elements.get(0);
            for (final T next : elements.subList(1, elements.size())) {
                parent2ChildMap.put(prev, next);
                prev = next;
            }
        } else {
            prev = root = null;
        }
        parent2ChildMap.put(prev, null);
    }

    @Override
    public Iterable<T> children(final T parent) {
        final T child = parent2ChildMap.get(parent);
        return child != null ? Collections.singletonList(child) : EMPTY_LIST;
    }

    @Override
    public T getRoot() {
        return root;
    }

    @Override
    public String toString() {
        if (toString == null) {
            toString = parent2ChildMap.toString().replaceAll("=", "->").replaceAll("->null", "");
        }
        return toString;
    }

    @Override
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof SingleBranchTree)) {
            return false;
        }
        @SuppressWarnings("rawtypes")
        final SingleBranchTree that = (SingleBranchTree) obj;
        return parent2ChildMap.equals(that.parent2ChildMap);
    }

    @Override
    public int hashCode() {
        return parent2ChildMap.hashCode();
    }

}