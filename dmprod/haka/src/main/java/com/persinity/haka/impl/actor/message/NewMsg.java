/**
 * Copyright (c) 2015 Persinity Inc.
 */
package com.persinity.haka.impl.actor.message;

import com.persinity.haka.Job;
import com.persinity.haka.impl.actor.JobState;

import java.io.Serializable;

/**
 * Message send from worker to start processing new job.
 *
 * @author Ivan Dachev
 */
public class NewMsg extends Msg implements Serializable {

	private static final long serialVersionUID = 7018636329463790863L;

	public NewMsg(Job job) {
		super(job.getId());

		this.job = job.clone();

		validateClone(job, this.job);
	}

	public Job getJob() {
		return job;
	}

	@Override
	public String toString() {
		return String.format("%s[%s]", super.toString(), JobState.systemInfoString(job));
	}

	private final Job job;
}
