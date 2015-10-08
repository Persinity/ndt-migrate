/**
 * Copyright (c) 2015 Persinity Inc.
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
