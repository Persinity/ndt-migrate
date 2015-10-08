/**
 * Copyright (c) 2015 Persinity Inc.
 */
package com.persinity.ndt.controller.script;

import java.util.Map;

/**
 * @author Ivan Dachev
 */
class StepStopStub extends BaseStep {
    public StepStopStub(Step prev, Map<Object, Object> ctx) {
        super(prev, Step.NO_DELAY, ctx);
    }

    @Override
    protected void work() {
        while (!isStopRequested()) {
            try {
                Thread.sleep(250);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
