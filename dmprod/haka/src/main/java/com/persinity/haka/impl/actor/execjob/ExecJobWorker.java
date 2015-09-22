/**
 * Copyright (c) 2015 Persinity Inc.
 */
package com.persinity.haka.impl.actor.execjob;

import akka.actor.ActorSelection;
import akka.actor.Props;
import akka.japi.Creator;
import com.persinity.haka.impl.actor.WorkerBase;
import com.persinity.haka.impl.actor.message.Msg;

/**
 * This worker will wait for a given job and notifies a future when it is done.
 *
 * @author Ivan Dachev
 */
public class ExecJobWorker extends WorkerBase {
	protected ExecJobWorker(String poolSupervisorName) {
		this.poolSupervisorName = poolSupervisorName;

		addMsgHandler(new ExecJobNewMsgHandler(this));
		addMsgHandler(new ExecJobProgressMsgHandler(this));
		addMsgHandler(new ExecJobProcessedMsgHandler(this));
	}

	public static Props props(final String poolSupervisorName) {
		return Props.create(new Creator<ExecJobWorker>() {
			private static final long serialVersionUID = -5530001218970992748L;

			@Override
			public ExecJobWorker create() throws Exception {
				return new ExecJobWorker(poolSupervisorName);
			}
		});
	}

	public void sendMsgToPool(Msg msg) {
		ActorSelection actorSelection = getContext().actorSelection("/user/" + poolSupervisorName);

		getLog().info("Send exec job %s to %s", msg, actorSelection);

		actorSelection.tell(msg, getSelf());
	}

	private final String poolSupervisorName;
}
