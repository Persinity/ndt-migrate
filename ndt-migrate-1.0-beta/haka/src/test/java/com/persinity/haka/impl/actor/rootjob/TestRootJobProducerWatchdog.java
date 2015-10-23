/*
 * Copyright 2015 Persinity Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
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
