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

import akka.actor.ActorPath;
import akka.actor.ActorRef;
import com.persinity.common.Id;
import com.persinity.haka.Job;
import com.persinity.haka.impl.actor.JobState;

/**
 * Holds the ActorRef sender for a Job not only its path.
 *
 * @author Ivan Dachev
 */
public class ExecJobState extends JobState {

    private static final long serialVersionUID = -1809146859786760670L;

    public ExecJobState(Job job, Id sessionId, ActorRef sender) {
        this(job, sessionId, sender.path());

        this.sender = sender;
    }

    // used from super.clone()
    private ExecJobState(Job job, Id sessionId, ActorPath senderPath) {
        super(job, sessionId, senderPath);
    }

    @Override
    public ExecJobState clone() {
        ExecJobState res = (ExecJobState) super.clone();
        res.sender = sender;
        return res;
    }

    public ActorRef getSender() {
        return sender;
    }

    private ActorRef sender;
}
