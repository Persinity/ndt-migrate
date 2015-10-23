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
/**
 *
 */
package com.persinity.ndt.controller.step;

import java.util.Map;

import com.persinity.ndt.controller.script.BaseStep;
import com.persinity.ndt.controller.script.Step;

/**
 * Requests stop of a step when monitored step is finished.
 *
 * @author Doichin Yordanov
 */
public class StopOnFinish extends BaseStep implements Step {

    public StopOnFinish(Step monitored, int delaySecs, Step stepToStop, Map<Object, Object> ctx) {
        super(monitored, delaySecs, ctx);
        this.stepToStop = stepToStop;
    }

    @Override
    protected void work() {
        stepToStop.sigStop();
    }

    private final Step stepToStop;
}
