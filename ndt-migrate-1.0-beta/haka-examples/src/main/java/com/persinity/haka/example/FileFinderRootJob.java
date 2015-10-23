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
package com.persinity.haka.example;

import com.persinity.haka.Job;
import com.persinity.haka.JobIdentity;
import com.persinity.haka.JobProducer;

/**
 * Word root job.
 * <p/>
 * also move all classes to wordcount package
 *
 * @author Ivan Dachev
 */
public class FileFinderRootJob implements Job {

    private static final long serialVersionUID = 1339115681750938148L;

    public FileFinderRootJob() {

        this(new JobIdentity());
        rootDone = false;
    }

    public FileFinderRootJob(JobIdentity id) {
        this.id = id;
    }

    @Override
    public JobIdentity getId() {
        return id;
    }

    @Override
    public Class<? extends JobProducer<? extends Job, ? extends Job>> getJobProducerClass() {
        return FileFinderRootJobProducer.class;
    }

    @Override
    public Job clone() {

        FileFinderRootJob res = new FileFinderRootJob(id);
        res.rootDone = rootDone;
        return res;
    }

    public boolean isRootDone() {
        return rootDone;
    }

    public void setRootDone() {
        rootDone = true;
    }

    private final JobIdentity id;

    private boolean rootDone;
}
