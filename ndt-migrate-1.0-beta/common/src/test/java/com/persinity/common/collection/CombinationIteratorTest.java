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

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

/**
 * @author Ivan Dachev
 */
@RunWith(Parameterized.class)
public class CombinationIteratorTest {

    public CombinationIteratorTest(final String dataName, final List<List<Integer>> inputLists,
            final List<List<Integer>> expectedCombinations) {
        this.inputLists = inputLists;
        this.expectedCombinations = expectedCombinations;
    }

    @Parameters(name = "{0}"/*print the first argument from the data array*/)
    public static Iterable<Object[]> data() {
        return Arrays.asList(new Object[][] { {

                "SingleArray",

                Arrays.asList(

                        Arrays.asList(1, 2, 3)

                ),

                Arrays.asList(

                        Arrays.asList(1),

                        Arrays.asList(2),

                        Arrays.asList(3)

                )

        }, {

                "MultipleArrays",

                Arrays.asList(

                        Arrays.asList(10, 11, 12),

                        Arrays.asList(21, 22, 23, 24),

                        Arrays.asList(31, 32)

                ),

                Arrays.asList(

                        Arrays.asList(10, 21, 31),

                        Arrays.asList(10, 21, 32),

                        Arrays.asList(10, 22, 31),

                        Arrays.asList(10, 22, 32),

                        Arrays.asList(10, 23, 31),

                        Arrays.asList(10, 23, 32),

                        Arrays.asList(10, 24, 31),

                        Arrays.asList(10, 24, 32),

                        Arrays.asList(11, 21, 31),

                        Arrays.asList(11, 21, 32),

                        Arrays.asList(11, 22, 31),

                        Arrays.asList(11, 22, 32),

                        Arrays.asList(11, 23, 31),

                        Arrays.asList(11, 23, 32),

                        Arrays.asList(11, 24, 31),

                        Arrays.asList(11, 24, 32),

                        Arrays.asList(12, 21, 31),

                        Arrays.asList(12, 21, 32),

                        Arrays.asList(12, 22, 31),

                        Arrays.asList(12, 22, 32),

                        Arrays.asList(12, 23, 31),

                        Arrays.asList(12, 23, 32),

                        Arrays.asList(12, 24, 31),

                        Arrays.asList(12, 24, 32)

                )

        }, });
    }

    @Test
    public void test() throws Exception {
        final CombinationIterator<Integer> testee = new CombinationIterator<>(inputLists);
        for (List<Integer> expectedCombination : expectedCombinations) {
            assertTrue(testee.hasNext());
            assertThat(testee.next(), is(expectedCombination));
        }
        assertFalse(testee.hasNext());
    }

    private final List<List<Integer>> inputLists;
    private final List<List<Integer>> expectedCombinations;
}