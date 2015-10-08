/**
 * Copyright (c) 2015 Persinity Inc.
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
