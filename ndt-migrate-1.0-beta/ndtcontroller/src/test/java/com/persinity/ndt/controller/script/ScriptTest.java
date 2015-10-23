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
package com.persinity.ndt.controller.script;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;

/**
 * @author Ivan Dachev
 */
public class ScriptTest {

    @Before
    public void setUp() {
        ctx = Collections.synchronizedMap(new HashMap<>());
    }

    @Test
    public void test() {
        final StepStub prev = new StepStub(null, Step.NO_DELAY, ctx);
        Script script = new Script(prev, Step.NO_DELAY, ctx);

        final StepStub step1 = new StepStub(null, Step.NO_DELAY, ctx);
        final StepStub step2 = new StepStub(step1, Step.NO_DELAY, ctx);

        script.addStep(step1);
        script.addStep(step2);

        new Thread(prev).start();
        script.run();

        assertFalse(prev.isStopRequested());
        assertTrue(prev.isFinished());
        assertFalse(prev.isFailed());
        assertThat(prev.getFailedException(), nullValue());
        prev.assertStepDoneResult();

        assertFalse(step1.isStopRequested());
        assertTrue(step1.isFinished());
        assertFalse(step1.isFailed());
        assertThat(step1.getFailedException(), nullValue());
        step1.assertStepDoneResult();

        assertFalse(step2.isStopRequested());
        assertTrue(step2.isFinished());
        assertFalse(step2.isFailed());
        assertThat(step2.getFailedException(), nullValue());
        step2.assertStepDoneResult();

        assertFalse(script.isStopRequested());
        assertTrue(script.isFinished());
        assertFalse(script.isFailed());
        assertThat(script.getFailedException(), nullValue());
    }

    @Test
    public void test_NoPrev() {
        Script script = new Script(null, Step.NO_DELAY, ctx);

        final StepStub step1 = new StepStub(null, Step.NO_DELAY, ctx);

        script.addStep(step1);

        script.run();

        assertFalse(step1.isStopRequested());
        assertTrue(step1.isFinished());
        assertFalse(step1.isFailed());
        assertThat(step1.getFailedException(), nullValue());
        step1.assertStepDoneResult();

        assertFalse(script.isStopRequested());
        assertTrue(script.isFinished());
        assertFalse(script.isFailed());
        assertThat(script.getFailedException(), nullValue());
    }

    @Test
    public void test_PrevFails() {
        final StepFailStub prev = new StepFailStub(null, ctx);
        Script script = new Script(prev, Step.NO_DELAY, ctx);

        final StepStub step1 = new StepStub(null, Step.NO_DELAY, ctx);
        final StepStub step2 = new StepStub(step1, Step.NO_DELAY, ctx);

        script.addStep(step1);
        script.addStep(step2);

        new Thread(prev).start();
        try {
            script.run();
            fail("Expecting to fail");
        } catch (RuntimeException e) {
            assertThat(e.getMessage(), is("Expected"));
        }

        assertFalse(prev.isStopRequested());
        assertTrue(prev.isFinished());
        assertTrue(prev.isFailed());
        assertThat(prev.getFailedException(), notNullValue());

        RuntimeException re = prev.getFailedException();
        assertThat(re.getMessage(), is("Expected"));

        // the steps threads was never started so step1/2 are neither stopped nor finished

        assertFalse(step1.isStopRequested());
        assertFalse(step1.isFinished());
        assertFalse(step1.isFailed());
        assertThat(step1.getFailedException(), nullValue());
        step1.assertStepNotDoneResult();

        assertFalse(step2.isStopRequested());
        assertFalse(step2.isFinished());
        assertFalse(step2.isFailed());
        assertThat(step2.getFailedException(), nullValue());
        step2.assertStepNotDoneResult();

        assertFalse(script.isStopRequested());
        assertTrue(script.isFinished());
        assertTrue(script.isFailed());
        assertThat(script.getFailedException(), notNullValue());

        re = script.getFailedException();
        assertThat(re.getMessage(), is("Expected"));
    }

    @Test
    public void test_StepFails() {
        Script script = new Script(null, Step.NO_DELAY, ctx);

        final StepFailStub step1 = new StepFailStub(null, ctx);
        final StepStub step2 = new StepStub(null, Step.NO_DELAY, ctx);

        script.addStep(step1);
        script.addStep(step2);

        try {
            script.run();
            fail("Expecting to fail");
        } catch (RuntimeException e) {
            assertThat(e.getMessage(), is("Expected"));
        }

        assertTrue(step1.isStopRequested());
        assertTrue(step1.isFinished());
        assertTrue(step1.isFailed());
        assertThat(step1.getFailedException(), notNullValue());

        RuntimeException re = step1.getFailedException();
        assertThat(re.getMessage(), is("Expected"));

        assertTrue(step2.isStopRequested());
        assertTrue(step2.isFinished());
        assertFalse(step2.isFailed());
        assertThat(step2.getFailedException(), nullValue());

        assertFalse(script.isStopRequested());
        assertTrue(script.isFinished());
        assertTrue(script.isFailed());
        assertThat(script.getFailedException(), notNullValue());

        re = script.getFailedException();
        assertThat(re.getMessage(), is("Expected"));
    }

    private Map<Object, Object> ctx;
}