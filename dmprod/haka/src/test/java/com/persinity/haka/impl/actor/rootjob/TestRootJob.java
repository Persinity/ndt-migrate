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
public class TestRootJob extends TestJob {

	private static final long serialVersionUID = 6994653673450807214L;

	public TestRootJob() {
		super(new JobIdentity(), TestRootJobProducer.class, false);
	}

	public TestRootJob(JobIdentity jobId, Class<? extends JobProducer> jobProducer, boolean clone) {
		super(jobId, jobProducer, clone);
	}

	@Override
	public Job clone() {
		TestRootJob clone = new TestRootJob(jobId, jobProducer, false);
		clone.state = state;
		return clone;
	}
}
