/**
 * Copyright (c) 2015 Persinity Inc.
 */
package com.persinity.haka.impl.actor;

import com.persinity.haka.JobProducer;

import java.util.Set;

/**
 * Testing invalid children.
 *
 * @author Ivan Dachev
 */
public class TestJobProducerNullChildrenInvalid implements JobProducer<TestJob, TestJob> {
	@Override
	public Set<TestJob> process(TestJob job) {
		return null;
	}

	@Override
	public void processed(TestJob parentJob, TestJob childJob) {

	}
}
