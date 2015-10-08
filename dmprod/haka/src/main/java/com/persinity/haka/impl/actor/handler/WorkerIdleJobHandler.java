/**
 * Copyright (c) 2015 Persinity Inc.
 */
package com.persinity.haka.impl.actor.handler;

import java.util.HashMap;

import com.persinity.common.Id;
import com.persinity.haka.IdleJob;
import com.persinity.haka.impl.actor.ContextLoggingAdapter;
import com.persinity.haka.impl.actor.JobState;
import com.persinity.haka.impl.actor.Worker;
import com.persinity.haka.impl.actor.WorkerState;

/**
 * Hanlde idle jobs.
 *
 * @author Ivan Dachev
 */
public class WorkerIdleJobHandler implements IdleJobHandler {
    public WorkerIdleJobHandler(Worker worker) {
        this.worker = worker;
        this.log = worker.getLog();
    }

    @Override
    public void handleIdleJob() throws Exception {
        WorkerState state = worker.getState();

        HashMap<Id, JobState> jobStates = state.getJobStates();

        for (JobState jobState : jobStates.values()) {
            for (JobState childJobState : jobState.getChildren().values()) {
                if (childJobState.getJob() instanceof IdleJob && jobState.areChildrenDone()) {
                    handleIdleJob(jobState);
                }
            }
        }
    }

    private void handleIdleJob(JobState jobState) {
        log.warning("Handle idle job %s", jobState);
        worker.sendProcessJobStateMsg(jobState.getJob().getId());
    }

    private final Worker worker;
    private final ContextLoggingAdapter log;
}
