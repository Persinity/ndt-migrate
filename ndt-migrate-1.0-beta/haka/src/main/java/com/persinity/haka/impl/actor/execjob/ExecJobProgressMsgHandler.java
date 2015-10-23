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
package com.persinity.haka.impl.actor.execjob;

import akka.actor.ActorRef;
import com.persinity.haka.impl.actor.ContextLoggingAdapter;
import com.persinity.haka.impl.actor.JobState;
import com.persinity.haka.impl.actor.WorkerState;
import com.persinity.haka.impl.actor.handler.MsgHandler;
import com.persinity.haka.impl.actor.message.ProgressMsg;

/**
 * Handle {@link ProgressMsg}.
 *
 * @author Ivan Dachev
 */
public class ExecJobProgressMsgHandler implements MsgHandler<ProgressMsg> {

    public ExecJobProgressMsgHandler(ExecJobWorker worker) {
        this.worker = worker;
        this.log = worker.getLog();
    }

    @Override
    public void handleMsg(ProgressMsg msg, ActorRef sender) {
        WorkerState state = worker.getState();

        JobState jobState = state.getJobState(msg.getJobId().id);
        if (jobState == null) {
            log.warning("We do not process any job ignoring %s", msg);

            worker.sendMsgProgressIgnored(msg, sender);

            return;
        }

        if (jobState.getStatus() == JobState.Status.DONE) {
            log.warning("We already processed the exec job ignoring %s", msg);

            worker.sendMsgProgressIgnored(msg, sender);

            return;
        }

        jobState.setStatus(JobState.Status.PROCESSING);

        //TODO notify the sender for progress
    }

    @Override
    public Class<ProgressMsg> getMsgClass() {
        return ProgressMsg.class;
    }

    private final ExecJobWorker worker;
    private final ContextLoggingAdapter log;
}
