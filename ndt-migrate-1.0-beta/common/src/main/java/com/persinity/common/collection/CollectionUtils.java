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

import static com.persinity.common.StringUtils.format;
import static com.persinity.common.invariant.Invariant.assertArg;
import static com.persinity.common.invariant.Invariant.notEmpty;
import static com.persinity.common.invariant.Invariant.notNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import com.google.common.base.Function;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;

/**
 * Utility methods for collections.
 *
 * @author Doichin Yordanov
 */
public class CollectionUtils {
    /**
     * Same as {@link #addPadded(List, List, int, Object)} but use the last element from srcList to pad
     */
    public static <X extends Y, Y> void addPadded(final List<Y> dstList, final List<X> srcList, final int size) {
        notEmpty(srcList, "srcList");
        addPadded(dstList, srcList, size, srcList.get(srcList.size() - 1));
    }

    /**
     * Pads a {@link List} to the given new larger size with the given element.
     *
     * @param dstList
     *         list to add the padded list
     * @param srcList
     *         list to pad
     * @param size
     *         pad size
     * @param element
     *         to fulfill up to pad size
     * @param <X>
     *         source list type must extend Y
     * @param <Y>
     *         destination list type must be one of the super of the X
     */
    public static <X extends Y, Y> void addPadded(final List<Y> dstList, final List<X> srcList, final int size,
            final Y element) {
        notNull(dstList, "dstList");
        notNull(srcList, "srcList");
        assertArg(size >= srcList.size(), "New list size: {} can not be smaller than old one: {} for padding!", size,
                srcList.size());
        if (srcList.size() == size) {
            dstList.addAll(srcList);
            return;
        }
        dstList.addAll(srcList);
        for (int i = srcList.size(); i < size; i++) {
            dstList.add(element);
        }
    }

    /**
     * @param elements
     * @return Single branch tree representation of the passed elements
     */
    public static <T> Tree<T> newTree(final List<T> elements) {
        if (elements == null) {
            return null;
        }
        final Tree<T> result = new SingleBranchTree<T>(elements);
        return result;
    }

    /**
     * Removes duplicates among the specified elements, preserving their order.
     *
     * @param elements
     * @return Immutable list with removed duplicate elements and preserved order.
     */
    public static <T> List<T> deDuplicate(final List<T> elements) {
        if (elements == null || elements.isEmpty()) {
            return Collections.emptyList();
        }
        final ImmutableList<T> immutableList = ImmutableSet.copyOf(elements).asList();
        return immutableList;
    }

    /**
     * Fills in a list by iterating through an iterator.
     *
     * @param it
     * @return
     */
    public static <T> List<T> newList(final Iterator<T> it) {
        if (it == null) {
            return Collections.emptyList();
        }
        final List<T> result = Lists.newArrayList(it);
        return result;
    }

    /**
     * Glues all elements in a collection into a string.
     * {@code StringUtils#implode(Arrays.asList(1, 2, 3), "|") // -> "1|2|3"}
     *
     * @param c
     * @param glue
     *         Mandatory glue, that can be any, including empty string.
     * @return Empty string on empty or null collection.
     */
    public static String implode(final Collection<?> c, final String glue) {
        return implode(c, glue, null);
    }

    /**
     * Glues all elements in a collection into a string.
     * {@code StringUtils#implode(Arrays.asList(1, 2, 3), "|") // -> "1|2|3"}
     *
     * @param c
     * @param glue
     *         Mandatory glue, that can be any, including empty string.
     * @param f
     *         Function to convert the element to string
     * @return Empty string on empty or null collection.
     */
    public static <T> String implode(final Collection<T> c, final String glue, final Function<T, String> f) {
        assertArg(glue != null);
        if (c == null || c.isEmpty()) {
            return "";
        }
        final StringBuilder sb = new StringBuilder();
        for (final T object : c) {
            if (sb.length() > 0) {
                sb.append(glue);
            }
            if (f != null) {
                sb.append(f.apply(object));
            } else {
                sb.append(object);
            }
        }
        return sb.toString();
    }

    /**
     * @param elements
     * @return List of string representations of the elements in the passed collection.
     */
    public static List<String> stringListOf(final Collection<?> elements) {
        assertArg(elements != null);
        final List<String> result = new LinkedList<>();
        for (final Object element : elements) {
            result.add(element.toString());
        }
        return result;
    }

    /**
     * @param vals
     * @return List with quoted elements
     */
    public static List<String> quote(final List<String> vals) {
        final List<String> result = new ArrayList<>(vals.size());
        for (final String val : vals) {
            result.add(format("'{}'", val));
        }
        return result;
    }

    /**
     * @param sorted
     *         list of elements sorted in descending order.
     * @param indexF
     *         that for each element returns index that corresponds to the elements order
     * @return The first group of elements from the sorted list or empty set of the input is empty.
     */
    public static <E> Set<E> getFirstGroup(final List<E> sorted, final Function<E, Integer> indexF) {
        assertArg(sorted != null && indexF != null);

        final Set<E> result = new HashSet<>();
        int lastIndex = Integer.MIN_VALUE;
        for (final E e : sorted) {
            if (lastIndex > (lastIndex = indexF.apply(e))) {
                // group finished
                break;
            }
            result.add(e);
        }
        return result;
    }

    /**
     * @param src
     *         list to transform from
     * @param dst
     *         list to transform to
     * @param transformF
     *         transform function
     * @param <F>
     *         type to transform from
     * @param <T>
     *         type to transform to
     */
    public static <F, T> void transform(final List<F> src, final List<T> dst, final Function<F, T> transformF) {
        notNull(src);
        notNull(dst);
        notNull(transformF);

        for (F f : src) {
            dst.add(transformF.apply(f));
        }
    }

    /**
     * @param csv
     * @return List of the comma separated tokens of the trimmed input or empty list.
     */
    public static List<String> explode(String csv) {
        final List<String> tokens = Arrays.asList(csv.trim().split("\\s*,\\s*"));
        final List<String> result = new LinkedList<>();
        for (String token : tokens) {
            if (token != null && !(token = token.trim()).isEmpty()) {
                result.add(token);
            }
        }
        return result;
    }

    /**
     * @param it
     * @param <E>
     * @return Iterator that wraps each element into singleton set.
     */
    public static <E> Iterator<Set<E>> getSingletonSetIterator(final Iterator<E> it) {
        return new Iterator<Set<E>>() {
            @Override
            public boolean hasNext() {
                return it.hasNext();
            }

            @Override
            public Set<E> next() {
                return Collections.singleton(it.next());
            }

            @Override
            public void remove() {
                it.remove();
            }
        };
    }
}
