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

import java.util.HashMap;

import com.persinity.common.Id;
import com.persinity.haka.Job;
import com.persinity.haka.JobIdentity;
import com.persinity.haka.JobProducer;

/**
 * @author Ivan Dachev
 */
public class TestJob implements Job {

    private static final long serialVersionUID = -1651720957926948700L;

    public static final String STATE_INIT = "INIT";
    public static final String STATE_DONE = "DONE";

    public TestJob(Class<? extends JobProducer> jobProducer) {
        this(new JobIdentity(), jobProducer, false);
    }

    public TestJob(TestJob parent, Class<? extends JobProducer> jobProducer) {
        this(new JobIdentity(parent.getId()), jobProducer, false);
    }

    public TestJob(JobIdentity parent, Class<? extends JobProducer> jobProducer) {
        this(new JobIdentity(parent), jobProducer, false);
    }

    public TestJob(JobIdentity jobId, Class<? extends JobProducer> jobProducer, boolean clone) {
        this.jobId = jobId;
        this.jobProducer = jobProducer;
        children = new HashMap<>();
    }

    @Override
    public JobIdentity getId() {
        return jobId;
    }

    @Override
    public Class<? extends JobProducer> getJobProducerClass() {
        return jobProducer;
    }

    @Override
    public Job clone() {
        TestJob clone = new TestJob(this.jobId, this.jobProducer, true);
        clone.state = this.state;
        clone.children = new HashMap<>();
        for (TestChildJob childJob : children.values()) {
            clone.children.put(childJob.getId().id, (TestChildJob) childJob.clone());
        }
        return clone;
    }

    @Override
    public String toString() {
        return String
                .format("%s[%s][state: %s][children: %s]", super.toString(), jobId.toShortString(), state, children);
    }

    protected final JobIdentity jobId;

    protected final Class<? extends JobProducer> jobProducer;

    public String state = STATE_INIT;

    protected HashMap<Id, TestChildJob> children;
}
