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
 * Test JobProducer that will block first 2 times on processing TestRootJobWatchdog.
 *
 * @author Ivan Dachev
 */
public class TestRootJobProducerWatchdog implements JobProducer<TestRootJobWatchdog, TestJob> {
    static final String STATE_JOB_CREATED = "JOB_CREATED";

    final LoggingAdapter log = Logging.getLogger(TestSharedSystem.system, this);

    /**
     * Here the first call will block until a second one is made. In which case the second one will succeed and the
     * first one will fail.
     */
    @Override
    public Set<TestJob> process(final TestRootJobWatchdog job) {
        log.info("Process: {}", job);

        if (job.state.equals(STATE_JOB_CREATED) || job.state.equals(STATE_DONE)) {
            log.info("Return empty jobs already created");
            return Collections.emptySet();
        }

        job.state = STATE_JOB_CREATED;

        TestRootJobWatchdog.resendCount += 1;

        if (TestRootJobWatchdog.resendCount <= 1) {
            while (!stopBlockers) {
                sleep(250);
            }
            throw new RuntimeException("Process failed");
        }

        stopBlockers = true;

        final TestJob child = new TestJob(job.getId(), TestJobProducerOneChild.class);

        log.info("Return child: {}", child);

        saveRootJob = job;
        saveChildJob = child;

        return Collections.singleton(child);
    }

    @Override
    public void processed(final TestRootJobWatchdog parentJob, final TestJob childJob) {
        log.info("Processed parentJob: {} childJob: {}", parentJob, childJob);

        Invariant.assertArg(parentJob.state.equals(STATE_JOB_CREATED),
                String.format("Unexpected state: %s", parentJob.state));
        parentJob.state = STATE_DONE;

        saveRootJob = parentJob;
        saveChildJob = childJob;
    }

    static TestRootJobWatchdog saveRootJob;
    static TestJob saveChildJob;

    static boolean stopBlockers = false;
}
