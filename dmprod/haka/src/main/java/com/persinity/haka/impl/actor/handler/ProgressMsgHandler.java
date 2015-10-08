/**
 * Copyright (c) 2015 Persinity Inc.
 */
package com.persinity.haka.impl.actor.handler;

import akka.actor.ActorRef;
import com.persinity.haka.impl.actor.ContextLoggingAdapter;
import com.persinity.haka.impl.actor.JobState;
import com.persinity.haka.impl.actor.Worker;
import com.persinity.haka.impl.actor.WorkerState;
import com.persinity.haka.impl.actor.message.ProgressMsg;

/**
 * Handle {@link ProgressMsg}.
 *
 * @author Ivan Dachev
 */
public class ProgressMsgHandler implements MsgHandler<ProgressMsg> {

	public ProgressMsgHandler(Worker worker) {
		this.worker = worker;
		this.log = worker.getLog();
	}

	@Override
	public void handleMsg(ProgressMsg msg, ActorRef sender) {
		WorkerState state = worker.getState();

		JobState jobState = state.getJobState(msg.getJobId().parentId);
		if (jobState == null) {
			log.warning("We do not have parent for such child job ignoring %s", msg);

			worker.sendMsgProgressIgnored(msg, sender);

			return;
		}

		JobState childJobState = jobState.getChildren().get(msg.getJobId().id);
		if (childJobState == null) {
			log.warning("We do not have such child job ignoring %s", msg);

			worker.sendMsgProgressIgnored(msg, sender);

			return;
		}

		if (childJobState.getStatus() == JobState.Status.DONE) {
			log.warning("Ignoring processing msg for already processed child: %s", childJobState);

			worker.sendMsgProgressIgnored(msg, sender);

			return;
		}

		if (!childJobState.getSessionId().equals(msg.getSessionId())) {
			log.warning("Mismatch sessionId for %s ignore %s", childJobState, msg);

			worker.sendMsgProgressIgnored(msg, sender);

			return;
		}

		assert childJobState.getStatus() == JobState.Status.NEW
				|| childJobState.getStatus() == JobState.Status.PROCESSING;

		childJobState.setStatus(JobState.Status.PROCESSING);
	}

	@Override
	public Class<ProgressMsg> getMsgClass() {
		return ProgressMsg.class;
	}

	private final Worker worker;
	private final ContextLoggingAdapter log;
}
