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

import java.util.Collections;
import java.util.Set;

import org.easymock.EasyMock;

import akka.event.Logging;
import akka.event.LoggingAdapter;
import com.persinity.common.invariant.Invariant;
import com.persinity.haka.Job;
import com.persinity.haka.JobProducer;

/**
 * Test JobProducer that returns only one child.
 *
 * @author Ivan Dachev
 */
public class TestJobProducerOneChild implements JobProducer<TestJob, Job> {
    public static final String STATE_ONE_CHILD_CREATED = "ONE_CHILD_CREATED";

    public static Class<?> CHILD_JOB_PRODUCER_CLASS;
    public static Class<?> CHILD_JOB_CLASS;

    public static void setDefaultChildClasses() {
        CHILD_JOB_PRODUCER_CLASS = TestChildJobProducer.class;
        CHILD_JOB_CLASS = TestChildJob.class;
    }

    static {
        setDefaultChildClasses();
    }

    public static TestJob lastParentJob = null;
    public static Job lastChildJob = null;

    @Override
    public Set<Job> process(final TestJob job) {
        resolveLogger();

        log.info("Process: {}", job);

        if (job.state.equals(STATE_ONE_CHILD_CREATED) || job.state.equals(STATE_DONE)) {
            return Collections.emptySet();
        } else {
            job.state = STATE_ONE_CHILD_CREATED;

            Job child;
            try {
                child = (Job) CHILD_JOB_CLASS.getConstructor(TestJob.class, Class.class)
                        .newInstance(job, CHILD_JOB_PRODUCER_CLASS);
            } catch (final Exception e) {
                throw new RuntimeException(e);
            }

            log.info("Return child: {}", child);

            return Collections.singleton(child);
        }
    }

    @Override
    public void processed(final TestJob parentJob, final Job childJob) {
        resolveLogger();

        log.info("Processed parentJob: {} childJob: {}", parentJob, childJob);

        Invariant.assertArg(parentJob.state.equals(STATE_ONE_CHILD_CREATED),
                String.format("Expected parentJob: %s to have state: %s", parentJob, STATE_ONE_CHILD_CREATED));
        Invariant.assertArg(
                childJob.getJobProducerClass() != TestChildJobProducer.class || ((TestChildJob) childJob).state
                        .equals(TestChildJobProducer.STATE_CHILD_DONE),
                String.format("Expected childJob: %s to have state: %s", childJob,
                        TestChildJobProducer.STATE_CHILD_DONE));

        log.info("Children done mark parent as done: {}", parentJob);

        parentJob.state = STATE_DONE;

        lastParentJob = parentJob;
        lastChildJob = childJob;
    }

    private static void resolveLogger() {
        if (log == null) {
            if (TestSharedSystem.system != null) {
                log = Logging.getLogger(TestSharedSystem.system, TestJobProducerOneChild.class);
            } else {
                log = EasyMock.createNiceMock(LoggingAdapter.class);
            }
        }
    }

    private static LoggingAdapter log;
}
