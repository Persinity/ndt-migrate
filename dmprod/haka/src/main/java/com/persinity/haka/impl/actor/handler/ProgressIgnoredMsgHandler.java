/**
 * Copyright (c) 2015 Persinity Inc.
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
