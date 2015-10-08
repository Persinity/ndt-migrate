/**
 * Copyright (c) 2015 Persinity Inc.
 */
package com.persinity.haka.impl.actor.rootjob;

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
public class RootJobProcessedMsgHandler implements MsgHandler<ProcessedMsg> {

	public RootJobProcessedMsgHandler(RootJobWorker worker) {
		this.worker = worker;
		this.log = worker.getLog();
	}

	@Override
	public void handleMsg(ProcessedMsg msg, ActorRef sender) {
		WorkerState state = worker.getState();

		JobState jobState = state.getJobState(msg.getJobId().id);
		if (jobState == null) {
			log.warning("We do not have such root job ignoring %s", msg);

			// always send ack even it is ignored to unblock
			// the child worker to handle new messages
			worker.sendMsgProcessedAck(msg, sender);

			return;
		}

		if (jobState.getStatus() == JobState.Status.DONE) {
			log.warning("We already processed the root job ignoring %s", msg);

			worker.sendMsgProcessedAck(msg, sender);

			return;
		}

		jobState.updateJob(msg.getJob());

		jobState.setStatus(JobState.Status.DONE);

		worker.sendMsgProcessedAck(msg, sender);

		log.info("Cleanup state on done %s", jobState);

		state.removeJobState(jobState.getJob().getId().id);

		worker.doSnapshot();
	}

	@Override
	public Class<ProcessedMsg> getMsgClass() {
		return ProcessedMsg.class;
	}

	private final RootJobWorker worker;
	private final ContextLoggingAdapter log;
}
