/**
 * Copyright (c) 2015 Persinity Inc.
 */
package com.persinity.haka.impl.actor.message;

import com.persinity.common.Id;
import com.persinity.haka.JobIdentity;

import java.io.Serializable;

/**
 * Message send from parent to child worker to inform
 * that job processed state was acknowledged.
 *
 * @author Ivan Dachev
 */
public class ProcessedAckMsg extends SessionMsg implements Serializable {

	private static final long serialVersionUID = -5012807270473172805L;

	public ProcessedAckMsg(JobIdentity jobId, Id sessionId) {
		super(jobId, sessionId);
	}
}
