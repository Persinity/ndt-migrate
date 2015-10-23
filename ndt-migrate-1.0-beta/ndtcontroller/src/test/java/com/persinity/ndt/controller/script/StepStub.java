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
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.assertThat;

import java.util.Map;

/**
 * @author Ivan Dachev
 */
class StepStub extends BaseStep {
    public StepStub(final Step prev, final int delaySecs, final Map<Object, Object> ctx) {
        super(prev, delaySecs, ctx);
    }

    @Override
    protected void work() {
        getCtx().put(DONE_KEY + this.hashCode(), DONE_VALUE);
    }

    /**
     * Assert step done result.
     */
    public void assertStepDoneResult() {
        final String result = (String) getCtx().get(DONE_KEY + hashCode());
        assertThat(result, is(DONE_VALUE));
    }

    /**
     * Assert step not done result.
     */
    public void assertStepNotDoneResult() {
        final String result = (String) getCtx().get(DONE_KEY + hashCode());
        assertThat(result, nullValue());
    }

    private static final String DONE_KEY = "done_key";
    private static final String DONE_VALUE = "done_value";
}
