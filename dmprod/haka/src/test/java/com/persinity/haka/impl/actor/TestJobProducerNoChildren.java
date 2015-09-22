/**
 * Copyright (c) 2015 Persinity Inc.
 */
package com.persinity.haka.impl.actor;

import com.persinity.haka.JobProducer;

import java.util.Collections;
import java.util.Set;

import static com.persinity.haka.impl.actor.TestJob.STATE_DONE;

/**
 * @author Ivan Dachev
 */
public class TestJobProducerNoChildren implements JobProducer<TestJob, TestJob> {

	@Override
	public Set<TestJob> process(TestJob job) {
		job.state = STATE_DONE;
		return Collections.emptySet();
	}

	@Override
	public void processed(TestJob parentJob, TestJob childJob) {
		throw new IllegalStateException("Should not be called");
	}
}
