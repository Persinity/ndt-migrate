/**
 * Copyright (c) 2015 Persinity Inc.
 */
package com.persinity.haka;

import com.persinity.haka.impl.actor.TestJob;

import java.util.Collections;
import java.util.Set;

/**
 * Used for testing of RootJob.
 *
 * @author Ivan Dachev
 */
public class TestJobProducer implements JobProducer<TestJob, TestJob> {
	@Override public Set<TestJob> process(TestJob job) {
		return Collections.emptySet();
	}

	@Override public void processed(TestJob parentJob, TestJob childJob) {
	}
}
