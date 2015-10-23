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

import com.persinity.haka.Job;
import com.persinity.haka.JobIdentity;
import com.persinity.haka.JobProducer;

/**
 * @author Ivan Dachev
 */
public class TestChildJob implements Job {

    private static final long serialVersionUID = 5048488960075842878L;

    public TestChildJob(TestJob parentJob, Class<? extends JobProducer> jobProducer) {
        this(new JobIdentity(parentJob.getId()), jobProducer);
    }

    private TestChildJob(JobIdentity jobId, Class<? extends JobProducer> jobProducer) {
        this.jobId = jobId;
        this.jobProducer = jobProducer;
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
        TestChildJob clone = new TestChildJob(this.jobId, this.jobProducer);
        clone.state = this.state;
        return clone;
    }

    @Override
    public String toString() {
        return String.format("%s[%s][state: %s]", super.toString(), jobId.toShortString(), state);
    }

    public final JobIdentity jobId;

    public final Class<? extends JobProducer> jobProducer;

    public String state;
}
