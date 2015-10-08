/**
 * Copyright (c) 2015 Persinity Inc.
 */
package com.persinity.ndt.controller.script;

import java.util.Map;

/**
 * @author Ivan Dachev
 */
class StepLongStub extends BaseStep {
    public StepLongStub(Step prev, Map<Object, Object> ctx) {
        super(prev, Step.NO_DELAY, ctx);
    }

    @Override
    protected void work() {
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}
