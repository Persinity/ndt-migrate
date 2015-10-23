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
package com.persinity.haka.impl.actor.execjob;

import akka.actor.ActorSelection;
import akka.actor.Props;
import akka.japi.Creator;
import com.persinity.haka.impl.actor.WorkerBase;
import com.persinity.haka.impl.actor.message.Msg;

/**
 * This worker will wait for a given job and notifies a future when it is done.
 *
 * @author Ivan Dachev
 */
public class ExecJobWorker extends WorkerBase {
    protected ExecJobWorker(String poolSupervisorName) {
        this.poolSupervisorName = poolSupervisorName;

        addMsgHandler(new ExecJobNewMsgHandler(this));
        addMsgHandler(new ExecJobProgressMsgHandler(this));
        addMsgHandler(new ExecJobProcessedMsgHandler(this));
    }

    public static Props props(final String poolSupervisorName) {
        return Props.create(new Creator<ExecJobWorker>() {
            private static final long serialVersionUID = -5530001218970992748L;

            @Override
            public ExecJobWorker create() throws Exception {
                return new ExecJobWorker(poolSupervisorName);
            }
        });
    }

    public void sendMsgToPool(Msg msg) {
        ActorSelection actorSelection = getContext().actorSelection("/user/" + poolSupervisorName);

        getLog().info("Send exec job %s to %s", msg, actorSelection);

        actorSelection.tell(msg, getSelf());
    }

    private final String poolSupervisorName;
}
