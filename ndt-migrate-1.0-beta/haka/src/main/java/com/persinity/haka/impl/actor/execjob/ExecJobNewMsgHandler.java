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
import com.persinity.haka.impl.actor.JobState;
import com.persinity.haka.impl.actor.WorkerState;
import com.persinity.haka.impl.actor.handler.MsgHandler;
import com.persinity.haka.impl.actor.message.NewMsg;

/**
 * Handle {@link NewMsg}.
 *
 * @author Ivan Dachev
 */
public class ExecJobNewMsgHandler implements MsgHandler<NewMsg> {

    public ExecJobNewMsgHandler(ExecJobWorker worker) {
        this.worker = worker;
    }

    @Override
    public void handleMsg(NewMsg msg, ActorRef sender) {
        WorkerState state = worker.getState();

        ExecJobState jobState = new ExecJobState(msg.getJob(), msg.getMsgId(), sender);
        state.addJobState(jobState);

        worker.sendMsgToPool(msg);

        jobState.setStatus(JobState.Status.PROCESSING);
    }

    @Override
    public Class<NewMsg> getMsgClass() {
        return NewMsg.class;
    }

    private final ExecJobWorker worker;
}
