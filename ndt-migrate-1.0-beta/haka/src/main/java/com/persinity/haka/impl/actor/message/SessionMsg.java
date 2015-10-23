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
package com.persinity.haka.impl.actor.message;

import java.io.Serializable;

import com.persinity.common.Id;
import com.persinity.haka.JobIdentity;

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
