/**
 * Copyright (c) 2015 Persinity Inc.
 */
package com.persinity.haka.impl.actor.handler;

import static com.persinity.common.invariant.Invariant.assertState;

import java.util.Set;

import com.persinity.haka.Job;
import com.persinity.haka.JobIdentity;
import com.persinity.haka.JobProducer;
import com.persinity.haka.impl.actor.ContextLoggingAdapter;
import com.persinity.haka.impl.actor.JobState;
import com.persinity.haka.impl.actor.Worker;
import com.persinity.haka.impl.actor.WorkerState;

/**
 * Handler for processing a {@link Job} by {@link JobProducer}.
 *
 * @author Ivan Dachev
 */
public class ProcessJobStateHandler {

    public static class ProcessJobStateMsg {
        public ProcessJobStateMsg(JobIdentity jobStateId) {
            this.jobStateId = jobStateId;
        }

        public JobIdentity getJobStateId() {
            return jobStateId;
        }

        private final JobIdentity jobStateId;
    }

    public ProcessJobStateHandler(Worker worker) {
        this.worker = worker;
        this.log = worker.getLog();
    }

    @SuppressWarnings("unchecked")
    public void processJobState(JobIdentity jobStateId) {
        WorkerState state = worker.getState();

        JobState jobState = state.getJobState(jobStateId.id);
        if (jobState == null) {
            log.warning("We do not have such JobState ignoring jobStateId: %s", jobStateId);
            return;
        }

        boolean areChildrenDone = jobState.areChildrenDone();

        if (jobState.getStatus() == JobState.Status.DONE) {
            assertState(areChildrenDone);
            return;
        }

        boolean isNewJob = jobState.getStatus() == JobState.Status.NEW;

        jobState.setStatus(JobState.Status.PROCESSING);

        if (!areChildrenDone) {
            log.debug("Skip process still have children in progress: %s", jobState);
            return;
        }

        jobState.cleanupIdleJobs();

        JobProducer jobProducer = jobState.getJobProducer();

        log.debug("Calling process %s jobProducerInstance: %s", jobState, Integer.toHexString(jobProducer.hashCode()));

        Set<Job> childrenJobs;
        try {
            childrenJobs = jobProducer.process(jobState.getJob());
        } catch (Throwable t) {
            log.error(t, "Call process failed %s jobProducerInstance: %s", jobState,
                    Integer.toHexString(jobProducer.hashCode()));
            throw t;
        }

        assertState(childrenJobs != null, "Returned children jobs cannot be null, jobProducer: {}",
                jobProducer.getClass().getName());

        log.debug("Done process %s children: %d jobProducerInstance: %s", jobState, childrenJobs.size(),
                Integer.toHexString(jobProducer.hashCode()));

        if (childrenJobs.size() != 0) {
            jobState.appendChildren(childrenJobs);

            worker.sendNewChildrenJobMsg(jobState);

            worker.doSnapshot();
        } else {
            jobState.setStatus(JobState.Status.DONE);

            final boolean needAck = !isNewJob;
            worker.sendMsgProcessed(jobState, needAck);

            if (isNewJob) {
                worker.cleanupJobState(jobState);
            }
        }
    }

    private final Worker worker;
    private final ContextLoggingAdapter log;
}
