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
