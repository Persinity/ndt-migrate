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
package com.persinity.common.fp;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThat;

import java.util.concurrent.TimeUnit;

import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;

import com.google.common.base.Function;
import com.google.common.base.Stopwatch;
import com.persinity.common.collection.DirectedEdge;

/**
 * @author Ivan Dachev
 */
public class FunctionUtilTest {

    @Test
    public void testExecuteAndCaptureSysOut() throws Exception {
        final Function<String, Integer> f = new Function<String, Integer>() {
            @Override
            public Integer apply(final String s) {
                System.out.println("Test " + s + " done");
                return 4;
            }
        };
        final DirectedEdge<Integer, String> res = FunctionUtil.executeAndCaptureSysOut(f, "arg1");
        assertThat(res.src(), is(4));
        assertThat(res.dst(), is("Test arg1 done\n"));
    }

    @Test(expected = NullPointerException.class)
    public void testExecuteAndCaptureSysOut_NullF() throws Exception {
        FunctionUtil.executeAndCaptureSysOut(null, "arg");
    }

    @Test(expected = RuntimeException.class)
    public void testExecuteAndCaptureSysOut_FFails() throws Exception {
        final Function<String, ? extends Object> f = new Function<String, Object>() {
            @Override
            public Object apply(final String s) {
                throw new RuntimeException("expected");
            }
        };
        FunctionUtil.executeAndCaptureSysOut(f, "arg");
    }

    @Test(expected = NullPointerException.class)
    public void testTimeOf_InvalidInput() {
        FunctionUtil.timeOf(null);
    }

    @Test
    public void testTimeOf() {
        final Function<Integer, Integer> f = new Function<Integer, Integer>() {
            @Override
            public Integer apply(final Integer input) {
                try {
                    Thread.sleep(SLEEP_PERIOD);
                } catch (InterruptedException e) {
                    Assert.fail(e.getMessage());
                }
                return input * FACTOR;
            }
        };

        final DirectedEdge<Integer, Stopwatch> result = FunctionUtil.timeOf(f, INPUT);

        assertEquals(new Integer(INPUT * FACTOR), result.src());
        assertFalse(result.dst().isRunning());
        assertThat(result.dst().elapsed(TimeUnit.MILLISECONDS), Matchers.greaterThanOrEqualTo(SLEEP_PERIOD));
    }

    public static final int INPUT = 1;
    public static final int FACTOR = 2;
    public static final long SLEEP_PERIOD = 1000L;
}