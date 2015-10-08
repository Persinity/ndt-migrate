/**
 * Copyright (c) 2015 Persinity Inc.
 */
package com.persinity.haka.impl.actor;

import com.persinity.haka.JobProducer;

import java.util.Set;

/**
 * @author Ivan Dachev
 */
public class TestJobProducerThrowOnProcessed implements JobProducer<TestJob, TestJob> {
	@Override
	public Set<TestJob> process(TestJob job) {
		return null;
	}

	@Override
	public void processed(TestJob parentJob, TestJob childJob) {
		throw new RuntimeException("Expected Failure");
	}
}
