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
import com.persinity.haka.impl.actor.message.ProcessedMsg;

/**
 * Handle {@link ProcessedMsg}.
 *
 * @author Ivan Dachev
 */
public class ExecJobProcessedMsgHandler implements MsgHandler<ProcessedMsg> {

    public ExecJobProcessedMsgHandler(ExecJobWorker worker) {
        this.worker = worker;
        this.log = worker.getLog();
    }

    @Override
    public void handleMsg(ProcessedMsg msg, ActorRef sender) {
        WorkerState state = worker.getState();

        JobState jobState = state.getJobState(msg.getJobId().id);
        if (jobState == null) {
            log.warning("We do not have such exec job ignoring %s", msg);

            // always send ack even it is ignored to unblock
            // the child worker to handle new messages
            worker.sendMsgProcessedAck(msg, sender);

            return;
        }

        if (jobState.getStatus() == JobState.Status.DONE) {
            log.warning("We already processed the exec job ignoring %s", msg);

            worker.sendMsgProcessedAck(msg, sender);

            return;
        }

        jobState.updateJob(msg.getJob());

        jobState.setStatus(JobState.Status.DONE);

        worker.sendMsgProcessedAck(msg, sender);

        state.removeJobState(jobState.getJob().getId().id);

        worker.sendMsg(((ExecJobState) jobState).getSender(), msg, worker.getSelf());
    }

    @Override
    public Class<ProcessedMsg> getMsgClass() {
        return ProcessedMsg.class;
    }

    private final ExecJobWorker worker;
    private final ContextLoggingAdapter log;
}
