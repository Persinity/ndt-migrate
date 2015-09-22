/**
 * Copyright (c) 2015 Persinity Inc.
 */
package com.persinity.haka.impl.actor.message;

import com.persinity.common.Id;
import com.persinity.haka.JobIdentity;

import java.io.Serializable;

/**
 * Message to inform that a job is in processing state.
 * <p/>
 * When this message is received worker will update the status to
 * processing only if the status is not done. If it is done this message
 * should be ignored.
 * <p/>
 * The parent worker should replay with {@link ProgressIgnoredMsg} in
 * in case it does not have state for job ID from the message.
 *
 * @author Ivan Dachev
 */
public class ProgressMsg extends SessionMsg implements Serializable {

	private static final long serialVersionUID = 7470062542569657255L;

	public ProgressMsg(JobIdentity jobId, Id sessionId) {
		super(jobId, sessionId);
	}
}
