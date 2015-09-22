/**
 * Copyright (c) 2015 Persinity Inc.
 */
package com.persinity.haka.impl.actor;

import akka.event.Logging;
import akka.event.LoggingAdapter;
import com.persinity.common.invariant.Invariant;
import com.persinity.haka.JobProducer;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import static com.persinity.haka.impl.actor.TestJob.STATE_DONE;

/**
 * Test JobProducer that returns two children at once.
 *
 * @author Ivan Dachev
 */
public class TestJobProducerTwoChildrenAtOnce implements JobProducer<TestJob, TestChildJob> {
	static final String STATE_CHILDREN_CREATED = "CHILDREN_CREATED";
	final LoggingAdapter log = Logging.getLogger(TestSharedSystem.system, this);

	@Override
	public Set<TestChildJob> process(final TestJob job) {
		log.info("Process: {}", job);

		if (job.state.equals(STATE_CHILDREN_CREATED) || job.state.equals(STATE_DONE)) {
			log.info("Return empty set");

			return Collections.emptySet();
		} else {
			job.state = STATE_CHILDREN_CREATED;

			job.children.clear();

			final TestChildJob child1 = new TestChildJob(job, TestChildJobProducer.class);
			child1.state = TestChildJobProducer.STATE_INIT;
			job.children.put(child1.getId().id, child1);

			final TestChildJob child2 = new TestChildJob(job, TestChildJobProducer.class);
			child2.state = TestChildJobProducer.STATE_INIT;
			job.children.put(child2.getId().id, child2);

			log.info("Return children: {} {}", child1, child2);

			return new HashSet<>(job.children.values());
		}
	}

	@Override
	public void processed(final TestJob parentJob, final TestChildJob childJob) {
		log.info("Processed parentJob: {} childJob: {}", parentJob, childJob);

		Invariant.assertArg(parentJob.state.equals(STATE_CHILDREN_CREATED),
				String.format("Expected parentJob: %s to have state: %s", parentJob, STATE_CHILDREN_CREATED));
		Invariant.assertArg(childJob.state.equals(TestChildJobProducer.STATE_CHILD_DONE),
				String.format("Expected childJob: %s to have state: %s", childJob,
						TestChildJobProducer.STATE_CHILD_DONE));

		final TestChildJob child = parentJob.children.get(childJob.getId().id);
		Invariant.assertArg(child.state.equals(TestChildJobProducer.STATE_INIT),
				String.format("Expected childJob: %s to have state: %s", childJob, TestChildJobProducer.STATE_INIT));

		child.state = childJob.state;

		boolean allChildrenDone = true;
		for (final TestChildJob iter : parentJob.children.values()) {
			if (!iter.state.equals(TestChildJobProducer.STATE_CHILD_DONE)) {
				allChildrenDone = false;
				break;
			}
		}

		if (allChildrenDone) {
			log.info("All children done mark parent as done: {}", parentJob);
			parentJob.state = STATE_DONE;
		}
	}
}
