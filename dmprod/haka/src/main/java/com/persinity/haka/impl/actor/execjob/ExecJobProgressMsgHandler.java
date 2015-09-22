/**
 * Copyright (c) 2015 Persinity Inc.
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
