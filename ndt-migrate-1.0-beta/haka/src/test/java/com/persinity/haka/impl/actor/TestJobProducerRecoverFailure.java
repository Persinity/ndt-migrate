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

import java.util.Collections;
import java.util.Set;

import akka.event.Logging;
import akka.event.LoggingAdapter;
import com.persinity.haka.Job;
import com.persinity.haka.JobProducer;

/**
 * Test JobProducer that will fail on first call and succeed on second one.
 *
 * @author Ivan Dachev
 */
public class TestJobProducerRecoverFailure implements JobProducer<TestChildJob, Job> {
    public static LoggingAdapter log;

    public enum INTERNAL_STATE {
        INIT, CALL_1, CALL_2
    }

    public static INTERNAL_STATE internal_state = INTERNAL_STATE.INIT;

    @Override
    public Set<Job> process(TestChildJob job) {
        setLogger();

        log.info("Process: {}", job);

        if (internal_state == INTERNAL_STATE.INIT) {

            internal_state = INTERNAL_STATE.CALL_1;

            throw new RuntimeException("Expected failure");

        } else if (internal_state == INTERNAL_STATE.CALL_1) {

            internal_state = INTERNAL_STATE.CALL_2;

            job.state = TestJob.STATE_DONE;

            return Collections.emptySet();
        } else {
            throw new IllegalStateException("Unexpected internal state: %s" + internal_state);
        }
    }

    @Override
    public void processed(TestChildJob parentJob, Job childJob) {
        throw new IllegalStateException("Should not be called");
    }

    private void setLogger() {
        if (log == null) {
            log = Logging.getLogger(TestSharedSystem.system, TestJobProducerRecoverFailure.class);
        }
    }
}
