/**
 * Copyright (c) 2015 Persinity Inc.
 */
package com.persinity.haka.impl.actor.rootjob;

import static com.persinity.common.ThreadUtil.sleep;
import static com.persinity.haka.impl.actor.TestJob.STATE_DONE;

import java.util.Collections;
import java.util.Set;

import akka.event.Logging;
import akka.event.LoggingAdapter;
import com.persinity.common.invariant.Invariant;
import com.persinity.haka.JobProducer;
import com.persinity.haka.impl.actor.TestJob;
import com.persinity.haka.impl.actor.TestJobProducerOneChild;
import com.persinity.haka.impl.actor.TestSharedSystem;

/**
 * Test JobProducer that returns only one child.
 *
 * @author Ivan Dachev
 */
public class TestRootJobProducer implements JobProducer<TestRootJob, TestJob> {
    static final String STATE_JOB_CREATED = "JOB_CREATED";

    final LoggingAdapter log = Logging.getLogger(TestSharedSystem.system, this);

    @Override
    public Set<TestJob> process(final TestRootJob job) {
        log.info("Process: {}", job);

        if (job.state.equals(STATE_JOB_CREATED) || job.state.equals(STATE_DONE)) {
            log.info("Return empty jobs already created");
            return Collections.emptySet();
        }

        job.state = STATE_JOB_CREATED;

        final TestJob child = new TestJob(job.getId(), TestJobProducerOneChild.class);

        saveRootJob = job;
        saveChildJob = child;

        long doDelayMsLocal = doDelayMs;
        if (doDelayMsLocal > 0) {
            log.info("Delay {} ms before return", doDelayMsLocal);

            // wait until delay was elapsed or doDelayMs is set to 0
            while (doDelayMs > 0 && doDelayMsLocal > 0) {
                sleep(250);
                doDelayMsLocal -= 250;
            }
        }

        log.info("Return child: {}", child);

        return Collections.singleton(child);
    }

    @Override
    public void processed(final TestRootJob parentJob, final TestJob childJob) {
        log.info("Processed parentJob: {} childJob: {}", parentJob, childJob);

        Invariant.assertArg(parentJob.state.equals(STATE_JOB_CREATED),
                String.format("Unexpected state: %s", parentJob.state));
        parentJob.state = STATE_DONE;

        saveRootJob = parentJob;
        saveChildJob = childJob;
    }

    static TestRootJob saveRootJob;
    static TestJob saveChildJob;

    static long doDelayMs = 0;
}
