/**
 * Copyright (c) 2015 Persinity Inc.
 */
package com.persinity.haka.impl.actor.rootjob;

import akka.actor.ActorSystem;
import akka.actor.Cancellable;
import akka.actor.UntypedActor;
import akka.cluster.Cluster;
import akka.cluster.Member;
import akka.cluster.MemberStatus;
import akka.event.Logging;
import akka.event.LoggingAdapter;

/**
 * Listen for certain number of cluster members and sends Message.FIRE to its parent.
 *
 * @author Ivan Dachev
 */
public class RootJobClusterFire extends UntypedActor {
	public enum Message {
		FIRE
	}

	@Override
	public void preStart() {
		ActorSystem system = getContext().system();
		watchdogCancellable = system.scheduler()
				.schedule(settings.getClusterUpMembersCheckPeriod(), settings.getClusterUpMembersCheckPeriod(),
						getSelf(), InternalMessage.TICK, system.dispatcher(), null);
	}

	@Override
	public void postStop() {
		if (watchdogCancellable != null) {
			watchdogCancellable.cancel();
		}
	}

	@Override
	public void onReceive(Object msg) {
		if (msg == InternalMessage.TICK) {
			checkAndFire();
		} else {
			unhandled(msg);
		}
	}

	private void checkAndFire() {
		if (fired) {
			return;
		}

		int upMembers = 0;
		for (Member member : cluster.state().getMembers()) {
			if (member.status().equals(MemberStatus.up())) {
				upMembers += 1;
			}
		}

		if (upMembers >= settings.getMinClusterUpMembersBeforeFire()) {
			fired = true;
			watchdogCancellable.cancel();

			log.info(String.format("[%s] Send fire event to parent: %s", getSelf().path().toStringWithoutAddress(),
					getSelf().path().parent().toStringWithoutAddress()));
			getContext().parent().tell(Message.FIRE, getSelf());
		} else {
			log.debug(String.format("[%s] Wait for min cluster members: %d current: %d",
					getSelf().path().toStringWithoutAddress(), settings.getMinClusterUpMembersBeforeFire(), upMembers));
		}
	}

	private enum InternalMessage {
		TICK
	}

	private boolean fired = false;

	private final RootJobSettings settings = RootJobSettings.Provider.SettingsProvider.get(getContext().system());

	private Cancellable watchdogCancellable;

	private final Cluster cluster = Cluster.get(getContext().system());

	private final LoggingAdapter log = Logging.getLogger(getContext().system(), this);
}
