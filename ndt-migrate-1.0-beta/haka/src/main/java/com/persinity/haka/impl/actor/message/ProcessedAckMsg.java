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
