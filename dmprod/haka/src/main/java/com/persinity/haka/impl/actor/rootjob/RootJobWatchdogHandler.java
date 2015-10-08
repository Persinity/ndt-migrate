/**
 * Copyright (c) 2015 Persinity Inc.
 */
package com.persinity.haka.impl.actor.rootjob;

import com.persinity.common.Id;
import com.persinity.haka.impl.actor.ContextLoggingAdapter;
import com.persinity.haka.impl.actor.JobState;
import com.persinity.haka.impl.actor.WorkerState;
import com.persinity.haka.impl.actor.handler.WatchdogHandler;

import java.util.HashMap;

/**
 * Handle watchdog
 *
 * @author Ivan Dachev
 */
public class RootJobWatchdogHandler implements WatchdogHandler {
	public RootJobWatchdogHandler(RootJobWorker worker) {
		this.worker = worker;
		this.log = worker.getLog();
	}

	public void handleWatchdog() throws Exception {
		WorkerState state = worker.getState();

		HashMap<Id, JobState> jobStates = state.getJobStates();

		if (jobStates.isEmpty()) {
			return;
		}

		JobState jobState = jobStates.values().iterator().next();

		if (jobState.getStatus() == JobState.Status.NEW) {
			handleWatchdogJobNew(jobState);
		} else if (jobState.getStatus() == JobState.Status.PROCESSING) {
			handleWatchdogJobProcessing(jobState);
		} else if (jobState.getStatus() == JobState.Status.DONE) {
			handleWatchdogJobDone(jobState);
		}
	}

	private void handleWatchdogJobNew(JobState jobState) {
		throw new IllegalStateException(String.format("System job should never be in NEW status: %s", jobState));
	}

	private void handleWatchdogJobProcessing(JobState jobState) {
		if (isRootJobTimeout(jobState)) {
			resendRootJob(jobState);
		}
	}

	private void handleWatchdogJobDone(JobState jobState) {
		log.warning("Timeout: cleanup state detected job done: %s", jobState);

		if (isRootJobTimeout(jobState)) {
			worker.cleanupJobState(jobState);
		}
	}

	protected boolean isRootJobTimeout(JobState jobState) {
		return System.currentTimeMillis() - jobState.getStatusUpdateTime() > worker.getRootJobSettings().getJobTimeout()
				.toMillis();
	}

	protected void resendRootJob(JobState jobState) {
		log.warning("Timeout: resend root job cleanup old one: %s", jobState);

		worker.cleanupJobState(jobState);

		worker.resendRootJob();
	}

	private final RootJobWorker worker;
	private final ContextLoggingAdapter log;
}
