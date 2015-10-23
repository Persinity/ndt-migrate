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
import com.persinity.haka.JobProducer;
import com.persinity.haka.impl.actor.ContextLoggingAdapter;
import com.persinity.haka.impl.actor.JobState;
import com.persinity.haka.impl.actor.Worker;
import com.persinity.haka.impl.actor.WorkerState;
import com.persinity.haka.impl.actor.message.ProcessedMsg;

/**
 * Handle {@link ProcessedMsg}.
 *
 * @author Ivan Dachev
 */
public class ProcessedMsgHandler implements MsgHandler<ProcessedMsg> {

    public ProcessedMsgHandler(Worker worker) {
        this.worker = worker;
        this.log = worker.getLog();
    }

    @SuppressWarnings("unchecked")
    @Override
    public void handleMsg(ProcessedMsg msg, ActorRef sender) {
        WorkerState state = worker.getState();

        JobState jobState = state.getJobState(msg.getJobId().parentId);
        if (jobState == null) {
            log.warning("We do not have parent for such child job ignoring %s", msg);

            worker.sendMsgProcessedAck(msg, sender);

            return;
        }

        JobState childJobState = jobState.getChildren().get(msg.getJobId().id);
        if (childJobState == null) {
            log.warning("We do not have such child job ignoring %s", msg);

            worker.sendMsgProcessedAck(msg, sender);

            return;
        }

        if (childJobState.getStatus() == JobState.Status.DONE) {
            log.warning("We already processed the child job ignoring msg done: %s", msg);

            worker.sendMsgProcessedAck(msg, sender);

            return;
        }

        if (!childJobState.getSessionId().equals(msg.getSessionId())) {
            log.warning("Mismatch sessionId for %s ignore %s", childJobState, msg);

            worker.sendMsgProcessedAck(msg, sender);

            return;
        }

        childJobState.updateJob(msg.getJob());

        JobProducer jobProducer = jobState.getJobProducer();

        log.debug("Calling processed parent: %s child: %s jobProducerInstance: %s", jobState, childJobState,
                Integer.toHexString(jobProducer.hashCode()));

        try {
            jobProducer.processed(jobState.getJob(), childJobState.getJob());
        } catch (Throwable t) {
            // TODO do we need to send ack to the msg sender in this case or
            // will wait for next retry when the sender's watchdog is triggered

            log.error(t, "Call processed failed parent: %s child: %s jobProducerInstance: %s", jobState, childJobState,
                    Integer.toHexString(jobProducer.hashCode()));
            throw t;
        }

        childJobState.setStatus(JobState.Status.DONE);

        log.debug("Done processed parent: %s child: %s jobProducerInstance: %s", jobState, childJobState,
                Integer.toHexString(jobProducer.hashCode()));

        jobState.removeChild(childJobState);

        worker.sendMsgProcessedAck(msg, sender);

        // snapshot here to preserve the child job done state
        worker.doSnapshot();

        worker.sendProcessJobStateMsg(jobState.getJob().getId());
    }

    @Override
    public Class<ProcessedMsg> getMsgClass() {
        return ProcessedMsg.class;
    }

    private final Worker worker;
    private final ContextLoggingAdapter log;
}
