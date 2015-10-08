/**
 * Copyright (c) 2015 Persinity Inc.
 */
package com.persinity.haka.impl.actor.execjob;

import akka.actor.ActorPath;
import akka.actor.ActorRef;
import com.persinity.common.Id;
import com.persinity.haka.Job;
import com.persinity.haka.impl.actor.JobState;

/**
 * Holds the ActorRef sender for a Job not only its path.
 *
 * @author Ivan Dachev
 */
public class ExecJobState extends JobState {

	private static final long serialVersionUID = -1809146859786760670L;

	public ExecJobState(Job job, Id sessionId, ActorRef sender) {
		this(job, sessionId, sender.path());

		this.sender = sender;
	}

	// used from super.clone()
	private ExecJobState(Job job, Id sessionId, ActorPath senderPath) {
		super(job, sessionId, senderPath);
	}

	@Override
	public ExecJobState clone() {
		ExecJobState res = (ExecJobState) super.clone();
		res.sender = sender;
		return res;
	}

	public ActorRef getSender() {
		return sender;
	}

	private ActorRef sender;
}
