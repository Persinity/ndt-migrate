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
package com.persinity.haka.impl.actor;

import akka.actor.UntypedActor;
import akka.cluster.Cluster;
import akka.cluster.ClusterEvent.CurrentClusterState;
import akka.cluster.ClusterEvent.MemberEvent;
import akka.cluster.ClusterEvent.MemberRemoved;
import akka.cluster.ClusterEvent.MemberUp;
import akka.cluster.ClusterEvent.UnreachableMember;
import akka.event.Logging;
import akka.event.LoggingAdapter;

/**
 * Simple listener to log critical cluster events.
 *
 * @author Ivan Dachev
 */
public class ClusterListener extends UntypedActor {

    @Override
    public void preStart() {
        cluster.subscribe(getSelf(), MemberEvent.class, UnreachableMember.class);
    }

    @Override
    public void postStop() {
        cluster.unsubscribe(getSelf());
    }

    @Override
    public void onReceive(Object message) {
        if (message instanceof CurrentClusterState) {
            CurrentClusterState state = (CurrentClusterState) message;
            log.info(REMOTE_PATH + "Current members: {}", state.members());

        } else if (message instanceof MemberUp) {
            MemberUp mUp = (MemberUp) message;
            log.info(REMOTE_PATH + "Member is Up: {}", mUp.member());

        } else if (message instanceof UnreachableMember) {
            UnreachableMember mUnreachable = (UnreachableMember) message;
            log.info(REMOTE_PATH + "Member detected as unreachable: {}", mUnreachable.member());

        } else if (message instanceof MemberRemoved) {
            MemberRemoved mRemoved = (MemberRemoved) message;
            log.info(REMOTE_PATH + "Member is Removed: {}", mRemoved.member());

        } else if (message instanceof MemberEvent) {
            // ignore

        } else {
            unhandled(message);
        }
    }

    final LoggingAdapter log = Logging.getLogger(getContext().system(), this);
    final Cluster cluster = Cluster.get(getContext().system());
    final String REMOTE_PATH = getSelf().path()
            .toStringWithAddress(getContext().system().provider().getDefaultAddress());
}
