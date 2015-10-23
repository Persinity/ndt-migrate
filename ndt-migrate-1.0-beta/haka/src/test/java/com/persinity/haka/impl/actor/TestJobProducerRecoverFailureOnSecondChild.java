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
import java.util.HashMap;
import java.util.Set;

import akka.event.Logging;
import akka.event.LoggingAdapter;
import com.persinity.common.Id;
import com.persinity.haka.JobProducer;

/**
 * Test JobProducer that:
 * - will produce one child on first call
 * - will fail on second call
 * - will produce next child on third call
 * - will return no more children on forth call
 *
 * @author Ivan Dachev
 */
public class TestJobProducerRecoverFailureOnSecondChild implements JobProducer<TestJob, TestChildJob> {
    static final String STATE_CHILDREN_1_CREATED = "CHILDREN_1_CREATED";
    static final String STATE_CHILDREN_2_CREATED = "CHILDREN_2_CREATED";

    public static LoggingAdapter log;

    public enum INTERNAL_STATE {
        INIT, CALL_1, CALL_2, CALL_3, CALL_4
    }

    public static INTERNAL_STATE internal_state = INTERNAL_STATE.INIT;

    // this is the same holder as TestJob.children to handle bugs
    // with the TestJob snapshot state in the worker if all is OK
    // both children Maps should be same with same states
    public static HashMap<Id, TestChildJob> processedChildren = new HashMap<>();

    @Override
    public Set<TestChildJob> process(TestJob job) {
        setLogger();

        log.info("internal_state: {} process: {}", internal_state, job);

        if (internal_state == INTERNAL_STATE.INIT) {

            internal_state = INTERNAL_STATE.CALL_1;

            job.state = STATE_CHILDREN_1_CREATED;

            job.children.clear();

            TestChildJob child1 = new TestChildJob(job, TestChildJobProducer.class);
            child1.state = TestChildJobProducer.STATE_INIT;
            job.children.put(child1.getId().id, child1);
            processedChildren.put(child1.getId().id, child1);

            log.info("Return children: {} all children: {}", child1, job.children);

            return Collections.singleton(child1);

        } else if (internal_state == INTERNAL_STATE.CALL_1) {

            internal_state = INTERNAL_STATE.CALL_2;

            throw new RuntimeException("Expected failure");

        } else if (internal_state == INTERNAL_STATE.CALL_2) {

            internal_state = INTERNAL_STATE.CALL_3;

            job.state = STATE_CHILDREN_2_CREATED;

            TestChildJob child2 = new TestChildJob(job, TestChildJobProducer.class);
            child2.state = TestChildJobProducer.STATE_INIT;
            job.children.put(child2.getId().id, child2);
            processedChildren.put(child2.getId().id, child2);

            log.info("Return children: {} all children: {}", child2, job.children);

            return Collections.singleton(child2);

        } else if (internal_state == INTERNAL_STATE.CALL_3) {

            internal_state = INTERNAL_STATE.CALL_4;

            return Collections.emptySet();
        } else {
            throw new IllegalStateException("Unexpected internal state: %s" + internal_state);
        }
    }

    @Override
    public void processed(TestJob parentJob, TestChildJob childJob) {
        log.info("internal_state: {} Processed parentJob: {} childJob: {}", internal_state, parentJob, childJob);

        if (!parentJob.state.equals(STATE_CHILDREN_1_CREATED) && !parentJob.state.equals(STATE_CHILDREN_2_CREATED)) {
            throw new IllegalArgumentException(
                    String.format("Expected parentJob: %s to have state: %s or %s", parentJob, STATE_CHILDREN_1_CREATED,
                            STATE_CHILDREN_2_CREATED));
        }

        if (!childJob.state.equals(TestChildJobProducer.STATE_CHILD_DONE)) {
            throw new IllegalArgumentException(String.format("Expected childJob: %s to have state: %s", childJob,
                    TestChildJobProducer.STATE_CHILD_DONE));
        }

        TestChildJob processed_child = processedChildren.get(childJob.getId().id);
        if (!processed_child.state.equals(TestChildJobProducer.STATE_INIT)) {
            throw new IllegalArgumentException(String.format("Expected childJob: %s to have state: %s", childJob,
                    TestChildJobProducer.STATE_INIT));
        }

        processed_child.state = childJob.state;

        TestChildJob child = parentJob.children.get(childJob.getId().id);
        if (processed_child != child && !child.state.equals(TestChildJobProducer.STATE_INIT)) {
            throw new IllegalArgumentException(String.format("Expected childJob: %s to have state: %s", childJob,
                    TestChildJobProducer.STATE_INIT));
        }

        child.state = childJob.state;

        boolean allChildrenDone = parentJob.children.size() == 2;
        for (TestChildJob iter : parentJob.children.values()) {
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

    private void setLogger() {
        if (log == null) {
            log = Logging.getLogger(TestSharedSystem.system, TestJobProducerRecoverFailureOnSecondChild.class);
        }
    }
}
