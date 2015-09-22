/**
 * Copyright (c) 2015 Persinity Inc.
 */
package com.persinity.haka.impl.actor.message;

import com.persinity.common.Id;
import com.persinity.haka.Job;
import com.persinity.haka.impl.actor.JobState;

import java.io.Serializable;

/**
 * Message send from child to parent worker to inform that a job was processed.
 * <p/>
 * The parent worker should replay with {@link ProcessedAckMsg} in order
 * the child worker to cleanup its state and start listen for new jobs.
 * <p/>
 * If the job for which processed msg was sent is atomic without children
 * then the child worker will not wait for the ack and will cleanup
 * its internal state immediately after sending the processed message.
 *
 * @author Ivan Dachev
 */
public class ProcessedMsg extends SessionMsg implements Serializable {

	private static final long serialVersionUID = 5597345608146862939L;

	public ProcessedMsg(Job job, Id sessionId, boolean needAck) {
		super(job.getId(), sessionId);

		this.job = job.clone();
		this.needAck = needAck;

		validateClone(job, this.job);
	}

	public Job getJob() {
		return job;
	}

	public boolean needAck() {
		return needAck;
	}

	@Override
	public String toString() {
		return String.format("%s[%s][needAck: %s]", super.toString(), JobState.systemInfoString(job), needAck);
	}

	private final Job job;
	private final boolean needAck;
}
