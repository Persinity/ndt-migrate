/**
 * Copyright (c) 2015 Persinity Inc.
 */
package com.persinity.haka.impl.actor;

import static com.persinity.common.ThreadUtil.sleep;

import java.util.Collections;
import java.util.Set;

import akka.event.Logging;
import akka.event.LoggingAdapter;
import com.persinity.haka.Job;
import com.persinity.haka.JobProducer;

/**
 * Test JobProducer that will block the first process and will wait for second call.
 * Then it will block the second call and return the first one.
 * And finally ubblock the second call too.
 *
 * @author Ivan Dachev
 */
public class TestJobProducerIgnoreOldJobStatus implements JobProducer<TestChildJob, Job> {
    public static final String STATE_DONE_1 = "DONE_1";
    public static final String STATE_DONE_2 = "DONE_2";

    public static LoggingAdapter log;

    public enum INTERNAL_STATE {
        INIT, CALL_1, CALL_2
    }

    public static INTERNAL_STATE internal_state = INTERNAL_STATE.INIT;

    public static final long CALL_1_SLEEP_BEFORE_RETURN_MS = 2000;

    @Override
    public Set<Job> process(TestChildJob job) {
        setLogger();

        log.info("Process: {}", job);

        if (internal_state == INTERNAL_STATE.INIT) {
            internal_state = INTERNAL_STATE.CALL_1;
            while (internal_state != INTERNAL_STATE.CALL_2) {
                sleep(250);
            }

            job.state = STATE_DONE_1;

            return Collections.emptySet();
        } else if (internal_state == INTERNAL_STATE.CALL_1) {
            internal_state = INTERNAL_STATE.CALL_2;

            // wait here in order call 1 to send its processed status
            sleep(CALL_1_SLEEP_BEFORE_RETURN_MS);

            job.state = STATE_DONE_2;

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
            log = Logging.getLogger(TestSharedSystem.system, TestJobProducerIgnoreOldJobStatus.class);
        }
    }
}
