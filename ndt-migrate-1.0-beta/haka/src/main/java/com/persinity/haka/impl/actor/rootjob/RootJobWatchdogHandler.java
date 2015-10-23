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

import java.util.HashMap;

import com.persinity.common.Id;
import com.persinity.haka.impl.actor.ContextLoggingAdapter;
import com.persinity.haka.impl.actor.JobState;
import com.persinity.haka.impl.actor.WorkerState;
import com.persinity.haka.impl.actor.handler.WatchdogHandler;

/**
 * Handle watchdog
 *
 * @author Ivan Dachev
 */
public class RootJobWatchdogHandler implements WatchdogHandler {
    public RootJobWatchdogHandler(RootJobWorker worker) {
        this.worker = worker;
        this.log = worker.getLog();
    }

    public void handleWatchdog() throws Exception {
        WorkerState state = worker.getState();

        HashMap<Id, JobState> jobStates = state.getJobStates();

        if (jobStates.isEmpty()) {
            return;
        }

        JobState jobState = jobStates.values().iterator().next();

        if (jobState.getStatus() == JobState.Status.NEW) {
            handleWatchdogJobNew(jobState);
        } else if (jobState.getStatus() == JobState.Status.PROCESSING) {
            handleWatchdogJobProcessing(jobState);
        } else if (jobState.getStatus() == JobState.Status.DONE) {
            handleWatchdogJobDone(jobState);
        }
    }

    private void handleWatchdogJobNew(JobState jobState) {
        throw new IllegalStateException(String.format("System job should never be in NEW status: %s", jobState));
    }

    private void handleWatchdogJobProcessing(JobState jobState) {
        if (isRootJobTimeout(jobState)) {
            resendRootJob(jobState);
        }
    }

    private void handleWatchdogJobDone(JobState jobState) {
        log.warning("Timeout: cleanup state detected job done: %s", jobState);

        if (isRootJobTimeout(jobState)) {
            worker.cleanupJobState(jobState);
        }
    }

    protected boolean isRootJobTimeout(JobState jobState) {
        return System.currentTimeMillis() - jobState.getStatusUpdateTime() > worker.getRootJobSettings().getJobTimeout()
                .toMillis();
    }

    protected void resendRootJob(JobState jobState) {
        log.warning("Timeout: resend root job cleanup old one: %s", jobState);

        worker.cleanupJobState(jobState);

        worker.resendRootJob();
    }

    private final RootJobWorker worker;
    private final ContextLoggingAdapter log;
}
