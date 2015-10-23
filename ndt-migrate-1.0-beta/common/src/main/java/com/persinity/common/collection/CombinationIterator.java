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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;

/**
 * Combination iterator of Lists of different lengths.
 * <p/>
 * For constructed with:
 * ([1, 2], [3, 4, 5])
 * <p/>
 * The iterator will be over:
 * [1, 3]
 * [1, 4]
 * [1, 5]
 * [2, 3]
 * [2, 4]
 * [2, 5]
 *
 * @author Ivan Dachev
 */
public class CombinationIterator<T> implements Iterator<List<T>> {

    public CombinationIterator(final Collection<? extends List<T>> listsCollection) {
        this.lists = new ArrayList<>(listsCollection);

        sizes = new int[lists.size()];
        counters = new int[lists.size()];

        int combinationsCount = 1;
        for (int i = 0; i < lists.size(); ++i) {
            sizes[i] = lists.get(i).size();
            combinationsCount *= lists.get(i).size();
        }

        countdown = combinationsCount;
    }

    @Override
    public boolean hasNext() {
        return countdown > 0;
    }

    @Override
    public List<T> next() {
        if (!hasNext()) {
            throw new NoSuchElementException();
        }

        List<T> res = getCombination();
        nextCombinationIndexes();
        return res;
    }

    @Override
    public void remove() {
        throw new UnsupportedOperationException();
    }

    private List<T> getCombination() {
        List<T> res = new ArrayList<>();
        for (int i = 0; i < lists.size(); ++i) {
            res.add(lists.get(i).get(counters[i]));
        }
        return res;
    }

    private void nextCombinationIndexes() {
        for (int incIndex = lists.size() - 1; incIndex >= 0; --incIndex) {
            if (counters[incIndex] + 1 < sizes[incIndex]) {
                ++counters[incIndex];
                break;
            }
            counters[incIndex] = 0;
        }

        --countdown;
    }

    private final List<List<T>> lists;
    private final int[] sizes;
    private final int[] counters;
    private int countdown;
}
