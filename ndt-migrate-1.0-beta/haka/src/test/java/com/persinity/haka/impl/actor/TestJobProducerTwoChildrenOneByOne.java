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
package com.persinity.haka.impl.actor;

import static com.persinity.haka.impl.actor.TestJob.STATE_DONE;
import static com.persinity.haka.impl.actor.TestJob.STATE_INIT;

import java.util.Collections;
import java.util.Set;

import akka.event.Logging;
import akka.event.LoggingAdapter;
import com.persinity.common.invariant.Invariant;
import com.persinity.haka.JobProducer;

/**
 * Test JobProducer that returns two children one by one.
 *
 * @author Ivan Dachev
 */
public class TestJobProducerTwoChildrenOneByOne implements JobProducer<TestJob, TestChildJob> {
    static final String STATE_CHILD_1_CREATED = "CHILD_1_CREATED";
    static final String STATE_CHILD_2_CREATED = "CHILD_2_CREATED";
    final LoggingAdapter log = Logging.getLogger(TestSharedSystem.system, this);

    @Override
    public Set<TestChildJob> process(final TestJob job) {
        log.info("Process: {}", job);

        if (job.state.equals(STATE_CHILD_2_CREATED) || job.state.equals(STATE_DONE)) {
            log.info("Return empty set");

            return Collections.emptySet();
        } else {
            switch (job.state) {
            case STATE_INIT: {
                job.state = STATE_CHILD_1_CREATED;

                job.children.clear();

                final TestChildJob child = new TestChildJob(job, TestChildJobProducer.class);
                child.state = TestChildJobProducer.STATE_INIT;
                job.children.put(child.getId().id, child);

                log.info("Return child 1: {}", child);

                return Collections.singleton(child);
            }
            case STATE_CHILD_1_CREATED: {
                job.state = STATE_CHILD_2_CREATED;

                final TestChildJob child = new TestChildJob(job, TestChildJobProducer.class);
                child.state = TestChildJobProducer.STATE_INIT;
                job.children.put(child.getId().id, child);

                log.info("Return child 2: {}", child);

                return Collections.singleton(child);
            }
            default:
                throw new IllegalArgumentException(String.format("Unexpected job state: %s", job));
            }
        }
    }

    @Override
    public void processed(final TestJob parentJob, final TestChildJob childJob) {
        log.info("Processed parentJob: {} childJob: {}", parentJob, childJob);

        Invariant.assertArg(
                parentJob.state.equals(STATE_CHILD_1_CREATED) || parentJob.state.equals(STATE_CHILD_2_CREATED),
                String.format("Expected parentJob: %s to have state: %s or %s", parentJob, STATE_CHILD_1_CREATED,
                        STATE_CHILD_2_CREATED));

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

        if (allChildrenDone && parentJob.children.size() == 2) {
            log.info("All children done mark parent as done: {}", parentJob);
            parentJob.state = STATE_DONE;
        }
    }
}
