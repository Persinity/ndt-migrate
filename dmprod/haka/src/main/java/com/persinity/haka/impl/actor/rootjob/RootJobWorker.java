/**
 * Copyright (c) 2015 Persinity Inc.
 */
package com.persinity.haka.impl.actor.rootjob;

import akka.actor.ActorRef;
import akka.actor.Props;
import akka.japi.Creator;
import com.persinity.haka.impl.actor.WorkerBase;

/**
 * This special worker will instantiate a given root job class from its config and send it to the pool.
 * <p/>
 * The trigger for sending the root job will be by waiting for certain number of cluster nodes to become UP.
 * <p/>
 * Watchdog will resend the root job if no progress received for configurable timeout period.
 *
 * @author Ivan Dachev
 */
public class RootJobWorker extends WorkerBase {
	protected RootJobWorker(String poolSupervisorName) {
		this.poolSupervisorName = poolSupervisorName;

		addMsgHandler(new RootJobProgressMsgHandler(this));
		addMsgHandler(new RootJobProcessedMsgHandler(this));

		setWatchdogHandler(new RootJobWatchdogHandler(this));

		fireHandler = new RootJobFireHandler(this);

		clusterFireRef = getContext().actorOf(Props.create(RootJobClusterFire.class), "RootJobClusterFire");
	}

	public static Props props(final String poolSupervisorName) {
		return Props.create(new Creator<RootJobWorker>() {
			private static final long serialVersionUID = -2666280563540392459L;

			@Override
			public RootJobWorker create() throws Exception {
				return new RootJobWorker(poolSupervisorName);
			}
		});
	}

	@Override
	public void onReceiveCommand(Object msg) throws Exception {
		if (msg == RootJobClusterFire.Message.FIRE) {
			fireHandler.handleFire();
		} else {
			super.onReceiveCommand(msg);
		}
	}

	public void resendRootJob() {
		fireHandler.sendRootJob();
	}

	public RootJobSettings getRootJobSettings() {
		return rootJobSettings;
	}

	public String getPoolSupervisorName() {
		return poolSupervisorName;
	}

	private final RootJobSettings rootJobSettings = RootJobSettings.Provider.SettingsProvider
			.get(getContext().system());

	private final String poolSupervisorName;

	private final ActorRef clusterFireRef;

	private final RootJobFireHandler fireHandler;
}
