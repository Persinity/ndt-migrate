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

import akka.actor.ActorRef;
import com.persinity.haka.impl.actor.ContextLoggingAdapter;
import com.persinity.haka.impl.actor.JobState;
import com.persinity.haka.impl.actor.Worker;
import com.persinity.haka.impl.actor.WorkerState;
import com.persinity.haka.impl.actor.message.ProgressIgnoredMsg;

/**
 * Handle {@link ProgressIgnoredMsg}.
 *
 * @author Ivan Dachev
 */
public class ProgressIgnoredMsgHandler implements MsgHandler<ProgressIgnoredMsg> {

    public ProgressIgnoredMsgHandler(Worker worker) {
        this.worker = worker;
        this.log = worker.getLog();
    }

    @Override
    public void handleMsg(ProgressIgnoredMsg msg, ActorRef sender) {
        WorkerState state = worker.getState();

        JobState jobState = state.getJobState(msg.getJobId().id);
        if (jobState == null) {
            log.warning("No such job state ignoring %s", msg);

            return;
        }

        if (!jobState.getSessionId().equals(msg.getSessionId())) {
            log.warning("Mismatch sessionId for %s ignore %s", jobState, msg);

            return;
        }

        log.warning("Cleanup all internal state on ignored progress %s", jobState);

        worker.cleanupJobState(jobState);
    }

    @Override
    public Class<ProgressIgnoredMsg> getMsgClass() {
        return ProgressIgnoredMsg.class;
    }

    private final Worker worker;
    private final ContextLoggingAdapter log;
}
