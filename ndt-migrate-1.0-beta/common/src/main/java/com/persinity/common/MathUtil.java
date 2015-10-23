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
package com.persinity.common;

import static com.persinity.common.invariant.Invariant.assertArg;

import java.util.ArrayList;
import java.util.List;

import com.persinity.common.collection.DirectedEdge;

/**
 * @author Doichin Yordanov
 */
public class MathUtil {
    /**
     * @param number
     *         non-negative integer
     * @return A number c = i^2, i is natural number, so that c >= number and c/2 <= number.
     */
    public static int ceilingByPowerOfTwo(int number) {
        assertArg(number >= 0, "Negative numbers not supported!");
        if (number == 1) {
            return 1;
        }

        int _number = 1;
        while (_number < number) {
            _number = _number << 1;
        }

        assert _number >= number && _number / 2 <= number;
        return _number;
    }

    /**
     * Partitions int range into equally sized partitions, according partition size. Trims the last partition if
     * necessary to fit the range.<BR>
     * For example for the range 1..10 and size 3, the following partitions will be returned: [{1, 3}, {4, 6}, {7, 9},
     * {10, 10}]
     *
     * @param minKey
     *         range start
     * @param maxKey
     *         range end
     * @param partitionSize
     * @return
     */
    public static List<DirectedEdge<Integer, Integer>> partition(final int minKey, final int maxKey,
            final int partitionSize) {
        assertArg(partitionSize > 0, "partitionSize must be positive integer!");
        assertArg(!((minKey | maxKey) < 0), "minKey and maxKey must be non negative integers");

        int i = minKey;
        final List<DirectedEdge<Integer, Integer>> result = new ArrayList<>((maxKey - minKey) / partitionSize + 1);
        for (; i <= maxKey; i += partitionSize) {
            final int end = i + partitionSize - 1;
            result.add(new DirectedEdge<Integer, Integer>(i, end));
        }

        if (i > maxKey && maxKey >= minKey) { // Trim if necessary the last partition to fit maxKey
            final int lastIdx = result.size() - 1;
            result.set(lastIdx, new DirectedEdge<Integer, Integer>(result.get(lastIdx).src(), maxKey));
        }

        return result;

    }
}
