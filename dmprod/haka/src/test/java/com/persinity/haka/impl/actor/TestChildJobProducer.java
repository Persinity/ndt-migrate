/**
 * Copyright (c) 2015 Persinity Inc.
 */
package com.persinity.haka.impl.actor;

import akka.event.Logging;
import akka.event.LoggingAdapter;
import com.persinity.haka.JobProducer;

import java.util.Collections;
import java.util.Set;

/**
 * @author Ivan Dachev
 */
public class TestChildJobProducer implements JobProducer<TestChildJob, TestChildJob> {
	public static final String STATE_CHILD_DONE = "child-done";
	public static final String STATE_INIT = "child-init";

	@Override
	public Set<TestChildJob> process(TestChildJob job) {
		log.info("Process job: {}", job);

		job.state = STATE_CHILD_DONE;
		return Collections.emptySet();
	}

	@Override
	public void processed(TestChildJob parentJob, TestChildJob childJob) {
		throw new IllegalStateException("Should not be called");
	}

	final LoggingAdapter log = Logging.getLogger(TestSharedSystem.system, this);
}
