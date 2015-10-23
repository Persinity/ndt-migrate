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
import com.persinity.common.StringUtils;
import com.persinity.common.logging.LogUtil;
import com.persinity.haka.Job;
import com.persinity.haka.JobIdentity;
import com.persinity.haka.impl.actor.JobState;

/**
 * Used as base for all job messages for identity.
 *
 * @author Ivan Dachev
 */
public abstract class Msg implements Serializable {

    private static final long serialVersionUID = 8658414081840038292L;

    public Msg(JobIdentity jobId) {
        this.jobId = jobId;
        msgId = Id.nextValue();
    }

    public JobIdentity getJobId() {
        return jobId;
    }

    public Id getMsgId() {
        return msgId;
    }

    @Override
    public String toString() {
        return StringUtils
                .format("{}[job-{}][msg-{}]", LogUtil.formatPackageName(super.toString()), jobId.toShortString(),
                        msgId.toStringShort());
    }

    static void validateClone(Job job, Job clone) {
        if (!clone.getClass().equals(job.getClass())) {
            throw new IllegalStateException(
                    String.format("Clone class: %s should be: %s for job: %s", clone.getClass(), job.getClass(),
                            JobState.systemInfoString(job)));
        }
    }

    private final JobIdentity jobId;
    private final Id msgId;
}

