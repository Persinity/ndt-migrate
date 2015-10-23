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

import akka.actor.ActorRef;
import com.persinity.common.Id;
import com.persinity.haka.impl.actor.ContextLoggingAdapter;
import com.persinity.haka.impl.actor.JobState;
import com.persinity.haka.impl.actor.Worker;
import com.persinity.haka.impl.actor.WorkerState;
import com.persinity.haka.impl.actor.message.NewMsg;

/**
 * Handle {@link NewMsg}.
 *
 * @author Ivan Dachev
 */
public class NewMsgHandler implements MsgHandler<NewMsg> {

    public NewMsgHandler(Worker worker) {
        this.worker = worker;
        this.log = worker.getLog();
    }

    @Override
    public void handleMsg(NewMsg msg, ActorRef sender) {
        final WorkerState state = worker.getState();

        final HashMap<Id, JobState> jobStates = state.getJobStates();

        final int maxJobsPerWorker = worker.getSettings().getMaxJobsPerWorker();
        if (maxJobsPerWorker > 0 && jobStates.size() >= maxJobsPerWorker) {
            log.warning("Hit max jobs per worker: %d resend %s", maxJobsPerWorker, msg);

            worker.resendMsgNew(msg, sender);

            return;
        }

        if (state.getJobState(msg.getJobId().id) != null) {
            log.error("We already processing Job from %s", msg);

            return;
        }

        final JobState jobState = new JobState(msg.getJob(), msg.getMsgId(), sender.path());
        state.addJobState(jobState);

        worker.sendProcessJobStateMsg(jobState.getJob().getId());
    }

    @Override
    public Class<NewMsg> getMsgClass() {
        return NewMsg.class;
    }

    private final Worker worker;
    private final ContextLoggingAdapter log;
}
