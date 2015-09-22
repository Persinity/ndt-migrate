/**
 * Copyright (c) 2015 Persinity Inc.
 */
package com.persinity.haka.impl.actor;

import com.persinity.haka.JobProducer;

import java.util.Set;

/**
 * @author Ivan Dachev
 */
public class TestJobProducerThrowOnConstruct implements JobProducer<TestJob, TestJob> {
	public TestJobProducerThrowOnConstruct() throws ReflectiveOperationException {
		throw new ReflectiveOperationException();
	}

	@Override
	public Set<TestJob> process(TestJob job) {
		return null;
	}

	@Override
	public void processed(TestJob parentJob, TestJob childJob) {

	}
}
