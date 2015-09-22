/**
 * Copyright (c) 2015 Persinity Inc.
 */
package com.persinity.haka.impl.actor.message;

import com.persinity.common.Id;
import com.persinity.haka.JobIdentity;

import java.io.Serializable;

/**
 * Message send from parent to child worker to inform that job progress
 * state was ignored as the parent no longer listen for specified job.
 * <p/>
 * This can happen in case the parent worker was restarted without
 * knowledge of the processing jobs because recent snapshot was not done.
 *
 * @author Ivan Dachev
 */
public class ProgressIgnoredMsg extends SessionMsg implements Serializable {

	private static final long serialVersionUID = -7576710364080967973L;

	public ProgressIgnoredMsg(JobIdentity jobId, Id sessionId) {
		super(jobId, sessionId);
	}
}
