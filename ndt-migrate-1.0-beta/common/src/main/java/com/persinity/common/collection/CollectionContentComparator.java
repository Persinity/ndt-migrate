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

import static com.persinity.common.invariant.Invariant.notNull;

import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

/**
 * Compares that the content of two collection is the same. Order is not significant.
 *
 * @author dyordanov
 */
public class CollectionContentComparator<E> implements Comparator<Collection<E>> {

    public CollectionContentComparator(final Comparator<E> elementComparator) {
        notNull(elementComparator);
        this.elementComparator = elementComparator;
    }

    @Override
    public int compare(final Collection<E> o1, final Collection<E> o2) {
        if (o1 != null && o2 != null) {
            final int sizeDiff = o1.size() - o2.size();
            if (sizeDiff != 0) {
                return sizeDiff;
            }
            final List<E> l1 = new LinkedList<>(o1);
            final List<E> l2 = new LinkedList<>(o2);
            Collections.sort(l1, elementComparator);
            Collections.sort(l2, elementComparator);
            final Iterator<E> it1 = l1.iterator();
            final Iterator<E> it2 = l2.iterator();
            while (it1.hasNext()) {
                final E e1 = it1.next();
                final E e2 = it2.next();
                final int elementComparisonResult = elementComparator.compare(e1, e2);
                if (elementComparisonResult != 0) {
                    return elementComparisonResult;
                }
            }
        } else if (o1 == null && o2 != null) {
            return -1;
        } else if (o1 != null && o2 == null) {
            return 1;
        }
        return 0;
    }

    private final Comparator<E> elementComparator;
}
