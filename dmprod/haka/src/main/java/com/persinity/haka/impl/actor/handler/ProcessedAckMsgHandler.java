/**
 * Copyright (c) 2015 Persinity Inc.
 */
package com.persinity.haka.impl.actor.handler;

import akka.actor.ActorRef;
import com.persinity.haka.impl.actor.ContextLoggingAdapter;
import com.persinity.haka.impl.actor.JobState;
import com.persinity.haka.impl.actor.Worker;
import com.persinity.haka.impl.actor.WorkerState;
import com.persinity.haka.impl.actor.message.ProcessedAckMsg;

/**
 * Handle {@link ProcessedAckMsg}.
 *
 * @author Ivan Dachev
 */
public class ProcessedAckMsgHandler implements MsgHandler<ProcessedAckMsg> {

	public ProcessedAckMsgHandler(Worker worker) {
		this.worker = worker;
		this.log = worker.getLog();
	}

	@Override
	public void handleMsg(ProcessedAckMsg msg, ActorRef sender) {
		WorkerState state = worker.getState();

		JobState jobState = state.getJobState(msg.getJobId().id);
		if (jobState == null) {
			log.warning("No such job state ignoring %s", msg);

			return;
		}

		if (jobState.getStatus() != JobState.Status.DONE) {
			log.warning("Job is still in progress ignore %s", msg);

			return;
		}

		if (!jobState.getSessionId().equals(msg.getSessionId())) {
			log.warning("Mismatch sessionId for %s ignore %s", jobState, msg);

			return;
		}

		log.debug("Cleanup on processed ack msg %s", jobState);

		worker.cleanupJobState(jobState);
	}

	@Override
	public Class<ProcessedAckMsg> getMsgClass() {
		return ProcessedAckMsg.class;
	}

	private final Worker worker;
	private final ContextLoggingAdapter log;
}
