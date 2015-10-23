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

package com.persinity.ndt.transform;

import static org.easymock.EasyMock.createNiceMock;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import java.util.Arrays;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.junit.Test;

/**
 * @author dyordanov
 */
public class TupleFuncCompositionTest {

    @Test(expected = NullPointerException.class)
    public void testTupleFuncComposition_InvalidInput1() {
        final TupleFunc f = createNiceMock(TupleFunc.class);
        new TupleFuncComposition(null, f);
    }

    @Test(expected = NullPointerException.class)
    public void testTupleFuncComposition_InvalidInput2() {
        final TupleFunc g = createNiceMock(TupleFunc.class);
        new TupleFuncComposition(g, null);
    }

    @Test
    public void apply() {
        final List<TupleFunc> callStack = new LinkedList<>();
        final TupleFunc f = new DummyTupleFunc(callStack);
        final TupleFunc g = new DummyTupleFunc(callStack);
        final List<TupleFunc> expectedCallStack = Arrays.asList(f, g);

        final TupleFuncComposition testee = new TupleFuncComposition(g, f);
        final Iterator<Map<String, Object>> res = testee.apply(null);
        assertNull(res);
        assertEquals(expectedCallStack, callStack);
    }

    private static class DummyTupleFunc implements TupleFunc {

        public DummyTupleFunc(final List<TupleFunc> callStack) {
            this.callStack = callStack;
        }

        @Override
        public Iterator<Map<String, Object>> apply(final Iterator<Map<String, Object>> input) {
            callStack.add(this);
            return input;
        }

        private final List<TupleFunc> callStack;
    }

}