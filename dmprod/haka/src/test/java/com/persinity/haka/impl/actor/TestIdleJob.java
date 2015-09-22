/**
 * Copyright (c) 2015 Persinity Inc.
 */
package com.persinity.haka.impl.actor;

import com.persinity.haka.IdleJob;

/**
 * @author Ivan Dachev
 */
public class TestIdleJob extends TestChildJob implements IdleJob {
    public TestIdleJob(final TestJob job, final Class<TestChildJobProducer> testChildJobProducerClass) {
        super(job, testChildJobProducerClass);
    }
}
