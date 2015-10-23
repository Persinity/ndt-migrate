/*
 * Copyright 2015 Persinity Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
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
