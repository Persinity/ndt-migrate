/**
 * Copyright (c) 2015 Persinity Inc.
 */
package com.persinity.haka.impl.actor.rootjob;

import com.persinity.haka.Job;
import com.persinity.haka.JobIdentity;
import com.persinity.haka.JobProducer;
import com.persinity.haka.impl.actor.TestJob;

/**
 * Test root job.
 *
 * @author Ivan Dachev
 */
public class TestRootJobWatchdog extends TestJob {

	private static final long serialVersionUID = 695432713319605185L;

	public TestRootJobWatchdog() {
		super(new JobIdentity(), TestRootJobProducerWatchdog.class, false);
	}

	public TestRootJobWatchdog(JobIdentity jobId, Class<? extends JobProducer> jobProducer, boolean clone) {
		super(jobId, jobProducer, clone);
	}

	@Override
	public Job clone() {
		TestRootJobWatchdog clone = new TestRootJobWatchdog(jobId, jobProducer, false);
		clone.state = state;
		return clone;
	}

	static int resendCount = 0;
}
