/**
 * Copyright (c) 2015 Persinity Inc.
 */
package com.persinity.haka.impl.actor.message;

import com.persinity.common.Id;
import com.persinity.haka.JobIdentity;

import java.io.Serializable;

/**
 * Base SessionMsg to hold the session ID that a worker is processing.
 *
 * @author Ivan Dachev
 */
public abstract class SessionMsg extends Msg implements Serializable {

	private static final long serialVersionUID = 899074717939540894L;

	public SessionMsg(JobIdentity jobId, Id sessionId) {
		super(jobId);

		this.sessionId = sessionId;
	}

	public Id getSessionId() {
		return sessionId;
	}

	private Id sessionId;
}
