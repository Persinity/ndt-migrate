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

import com.persinity.haka.Job;
import com.persinity.haka.impl.actor.JobState;

/**
 * Message send from worker to start processing new job.
 *
 * @author Ivan Dachev
 */
public class NewMsg extends Msg implements Serializable {

    private static final long serialVersionUID = 7018636329463790863L;

    public NewMsg(Job job) {
        super(job.getId());

        this.job = job.clone();

        validateClone(job, this.job);
    }

    public Job getJob() {
        return job;
    }

    @Override
    public String toString() {
        return String.format("%s[%s]", super.toString(), JobState.systemInfoString(job));
    }

    private final Job job;
}
