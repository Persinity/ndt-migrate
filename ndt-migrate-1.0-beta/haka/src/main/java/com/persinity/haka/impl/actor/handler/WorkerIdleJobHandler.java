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
