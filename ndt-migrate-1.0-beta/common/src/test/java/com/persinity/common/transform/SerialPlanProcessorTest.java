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
package com.persinity.common.transform;

import static org.easymock.EasyMock.createStrictMock;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.verify;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.Arrays;

import org.easymock.IAnswer;
import org.junit.Test;

import com.google.common.base.Function;
import com.persinity.common.collection.CollectionUtils;
import com.persinity.common.collection.DirectedEdge;
import com.persinity.ndt.transform.PlanProcessor;
import com.persinity.ndt.transform.SerialPlanProcessor;

/**
 * @author Doichin Yordanov
 */
public class SerialPlanProcessorTest {

    private static final int INPUT = 1;
    private static final int SOME = 38;

    /**
     * Test method for
     * {@link PlanProcessor#process(com.persinity.common.collection.Tree, Object, Function)}
     * with null input.
     */
    @Test(expected = RuntimeException.class)
    public void testProcessNull() {
        final SerialPlanProcessor<Integer, Integer> testee = new SerialPlanProcessor<Integer, Integer>();
        testee.process(null, INPUT);
    }

    /**
     * Test method for
     * {@link PlanProcessor#process(com.persinity.common.collection.Tree, Object, Function)}
     * .
     */
    @SuppressWarnings("unchecked")
    @Test
    public void testProcess() {
        final Function<Integer, Integer> func1 = createStrictMock(Function.class);
        final Function<Integer, Integer> func2 = createStrictMock(Function.class);
        expect(func1.apply(INPUT)).andStubReturn(SOME);
        expect(func2.apply(INPUT)).andStubReturn(SOME);

        final SerialPlanProcessor<Integer, Integer> testee = new SerialPlanProcessor<>();

        replay(func1, func2);

        testee.process(CollectionUtils.newTree(Arrays.asList(func1, func2)), INPUT);

        verify(func1, func2);
    }

    /**
     * Test method for
     * {@link PlanProcessor#process(com.persinity.common.collection.Tree, Object, Function)}
     * .
     */
    @SuppressWarnings("unchecked")
    @Test
    public void testProcessExceptionHandler() {
        final Function<Integer, Integer> func1 = createStrictMock(Function.class);
        final Function<Integer, Integer> func2 = createStrictMock(Function.class);
        expect(func1.apply(INPUT)).andStubReturn(SOME);
        expect(func2.apply(INPUT)).andAnswer(new IAnswer<Integer>() {
            @Override
            public Integer answer() throws Throwable {
                throw new RuntimeException("Expected");
            }
        });

        final SerialPlanProcessor<Integer, Integer> testee = new SerialPlanProcessor<>();
        final boolean[] handlerTriggered = new boolean[1];
        final Function<DirectedEdge<Integer, RuntimeException>, Void> exceptionHandler = new Function<DirectedEdge<Integer, RuntimeException>, Void>() {
            @Override
            public Void apply(final DirectedEdge<Integer, RuntimeException> input) {
                assertThat(input.dst().getMessage(), is("Expected"));
                handlerTriggered[0] = true;
                return null;
            }
        };

        replay(func1, func2);

        testee.process(CollectionUtils.newTree(Arrays.asList(func1, func2)), INPUT, exceptionHandler);

        verify(func1, func2);

        assertTrue(handlerTriggered[0]);
    }

    /**
     * Test method for
     * {@link PlanProcessor#process(com.persinity.common.collection.Tree, Object, Function)}
     * .
     */
    @SuppressWarnings("unchecked")
    @Test
    public void testProcess_FuncApplyThrowsException() {
        final Function<Integer, Integer> func1 = createStrictMock(Function.class);
        expect(func1.apply(INPUT)).andAnswer(new IAnswer<Integer>() {
            @Override
            public Integer answer() throws Throwable {
                throw new RuntimeException("Expected");
            }
        });

        final SerialPlanProcessor<Integer, Integer> testee = new SerialPlanProcessor<>();

        replay(func1);

        try {
            testee.process(CollectionUtils.newTree(Arrays.asList(func1)), INPUT, null);
            fail("Expected to fail");
        } catch (RuntimeException e) {
            assertThat(e.getMessage(), is("Expected"));
        }

        verify(func1);
    }
}
