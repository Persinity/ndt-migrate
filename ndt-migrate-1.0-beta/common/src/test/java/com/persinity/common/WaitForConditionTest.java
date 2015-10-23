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

import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import org.junit.Test;

/**
 * @author Ivan Dachev
 */
public class WaitForConditionTest {

    @Test
    public void test() throws Exception {
        calledOnce = false;

        new WaitForCondition(500) {
            @Override
            public boolean condition() {
                calledOnce = true;
                return true;
            }
        }.waitOrTimeout();

        assertTrue(calledOnce);

        try {
            new WaitForCondition(501, "msg") {
                @Override
                public boolean condition() {
                    return false;
                }
            }.waitOrTimeout();
            fail("Expected to throw RuntimeException");
        } catch (RuntimeException e) {
            assertThat(e.getMessage(), is("msg"));
        }

        try {
            new WaitForCondition(1) {
                @Override
                public boolean condition() {
                    return false;
                }
            }.waitOrTimeout();
            fail("Expected to throw IllegalArgumentException timeout < tick");
        } catch (IllegalArgumentException e) {
        }

        try {
            new WaitForCondition(1, "", 0) {
                @Override
                public boolean condition() {
                    return false;
                }
            }.waitOrTimeout();
            fail("Expected to throw IllegalArgumentException tick should be positive");
        } catch (IllegalArgumentException e) {
        }

        try {
            new WaitForCondition(1, "", -1) {
                @Override
                public boolean condition() {
                    return false;
                }
            }.waitOrTimeout();
            fail("Expected to throw IllegalArgumentException tick should be positive");
        } catch (IllegalArgumentException e) {
        }

        thrown = null;
        Thread thread = new Thread(new Runnable() {
            public void run() {
                try {
                    new WaitForCondition(2000) {
                        @Override
                        public boolean condition() {
                            return false;
                        }
                    }.waitOrTimeout();
                } catch (RuntimeException e) {
                    thrown = e.getCause();
                }
            }
        });
        thread.start();
        thread.join(250);
        thread.interrupt();
        Thread.sleep(250);

        assertThat(thrown, instanceOf(InterruptedException.class));
    }

    private boolean calledOnce;

    private Throwable thrown;
}