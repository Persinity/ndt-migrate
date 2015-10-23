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
package com.persinity.haka.impl.actor.handler;

import java.util.HashMap;

import com.persinity.common.Id;
import com.persinity.haka.impl.actor.ContextLoggingAdapter;
import com.persinity.haka.impl.actor.JobState;
import com.persinity.haka.impl.actor.Worker;
import com.persinity.haka.impl.actor.WorkerState;

/**
 * Handle watchdog.
 *
 * @author Ivan Dachev
 */
public class WorkerWatchdogHandler implements WatchdogHandler {
    public WorkerWatchdogHandler(Worker worker) {
        this.worker = worker;
        this.log = worker.getLog();
    }

    @Override
    public void handleWatchdog() throws Exception {
        WorkerState state = worker.getState();

        HashMap<Id, JobState> jobStates = state.getJobStates();

        for (JobState jobState : jobStates.values()) {
            if (jobState.getStatus() == JobState.Status.NEW) {
                handleWatchdogJobNew(jobState);
            } else if (jobState.getStatus() == JobState.Status.PROCESSING) {
                handleWatchdogJobProcessing(jobState);
            } else if (jobState.getStatus() == JobState.Status.DONE) {
                handleWatchdogJobDone(jobState);
            }
        }
    }

    private void handleWatchdogJobNew(JobState jobState) {
        log.warning("Found parent Job in NEW state: %s", jobState);
        // occurs during high system load between new msg received and process job state
        // take no action will wait the message queue to be fully processed
    }

    private void handleWatchdogJobProcessing(JobState jobState) {
        jobState.resetStatusUpdateTime(false);

        if (jobState.areChildrenDone()) {
            worker.sendProcessJobStateMsg(jobState.getJob().getId());
        } else {
            worker.sendMsgProgress(jobState);

            for (JobState childJobState : jobState.getChildren().values()) {
                if (isStatusUpdateTimeout(childJobState)) {
                    if (childJobState.getStatus() == JobState.Status.NEW) {
                        handleTimeoutNewChildJob(childJobState);
                    } else if (childJobState.getStatus() == JobState.Status.PROCESSING) {
                        handleTimeoutProcessingChildJob(childJobState);
                    }
                }
            }
        }
    }

    private void handleWatchdogJobDone(JobState jobState) {
        if (isStatusUpdateTimeout(jobState)) {
            log.warning("Timeout: resend msg processed %s", jobState);

            jobState.resetStatusUpdateTime(false);

            worker.sendMsgProcessed(jobState, true);
        }
    }

    private boolean isStatusUpdateTimeout(JobState jobState) {
        return System.currentTimeMillis() - jobState.getStatusUpdateTime() > worker.getSettings()
                .getStatusUpdateTimeout().toMillis();
    }

    private void handleTimeoutNewChildJob(JobState childJobState) {
        log.warning("Timeout: resend new child %s", childJobState);

        worker.sendNewChildJobMsg(childJobState);
    }

    private void handleTimeoutProcessingChildJob(JobState childJobState) {
        log.warning("Timeout: reset to new and resend child %s", childJobState);

        childJobState.setStatus(JobState.Status.NEW);

        worker.sendNewChildJobMsg(childJobState);
    }

    private final Worker worker;
    private final ContextLoggingAdapter log;
}
