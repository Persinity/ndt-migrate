/**
 * Copyright (c) 2015 Persinity Inc.
 */
package com.persinity.ndt.controller.script;

import java.util.Map;

/**
 * @author Ivan Dachev
 */
class StepFailStub extends BaseStep {
    public StepFailStub(Step prev, Map<Object, Object> ctx) {
        super(prev, Step.NO_DELAY, ctx);
    }

    @Override
    protected void work() {
        throw new RuntimeException("Expected");
    }
}
