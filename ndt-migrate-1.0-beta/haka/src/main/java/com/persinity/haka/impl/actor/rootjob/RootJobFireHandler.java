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

import com.persinity.haka.Job;
import com.persinity.haka.impl.actor.ContextLoggingAdapter;
import com.persinity.haka.impl.actor.JobState;
import com.persinity.haka.impl.actor.WorkerState;
import com.persinity.haka.impl.actor.message.NewMsg;

/**
 * Implements handling of fire event to kick the root job.
 *
 * @author Ivan Dachev
 */
public class RootJobFireHandler {
    public RootJobFireHandler(RootJobWorker worker) {
        this.worker = worker;
        this.log = worker.getLog();
    }

    public void handleFire() {
        final WorkerState state = worker.getState();

        if (state.getJobStates().isEmpty()) {
            sendRootJob();
        } else {
            log.info("We are on fire already");
        }
    }

    public void sendRootJob() {
        final WorkerState state = worker.getState();

        assert state.getJobStates().isEmpty();

        final String rootJobClass = worker.getRootJobSettings().getJobClass();

        Job rootJob;
        try {
            rootJob = (Job) Class.forName(rootJobClass).newInstance();
        } catch (ReflectiveOperationException e) {
            log.error(e, "Failed to instantiate root job class: %s", rootJobClass);
            throw new RuntimeException(e);
        }

        NewMsg msg = new NewMsg(rootJob);

        JobState jobState = new JobState(rootJob, msg.getMsgId(), worker.getSelf().path());
        state.addJobState(jobState);

        String path = "/user/" + worker.getPoolSupervisorName();
        log.info("Send root job %s to %s", msg, path);

        worker.sendMsg(path, msg, worker.getSelf());

        jobState.setStatus(JobState.Status.PROCESSING);

        worker.doSnapshot();
    }

    private final RootJobWorker worker;
    private final ContextLoggingAdapter log;
}
