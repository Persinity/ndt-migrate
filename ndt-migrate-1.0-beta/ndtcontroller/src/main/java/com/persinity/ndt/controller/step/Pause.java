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

import static com.persinity.ndt.controller.NdtViewController.PROGRESS_OFF;

import java.util.Map;

import com.persinity.ndt.controller.NdtEvent;
import com.persinity.ndt.controller.NdtViewController;
import com.persinity.ndt.controller.script.BaseStep;
import com.persinity.ndt.controller.script.Step;

/**
 * Pauses until key is hit
 *
 * @author Doichin Yordanov
 */
public class Pause extends BaseStep implements Step {

    public Pause(Step prev, int delaySecs, NdtEvent fireEvent, final Map<Object, Object> ctx) {
        super(prev, delaySecs, ctx);
        this.fireEvent = fireEvent;
    }

    @Override
    protected void work() {
        final NdtViewController view = getController().getView();
        view.setProgress(PROGRESS_OFF);
        view.sendBlockingEvent(fireEvent);
    }

    private final NdtEvent fireEvent;
}
