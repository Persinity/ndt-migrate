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

import java.io.File;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import org.apache.log4j.Logger;

import com.google.common.collect.Sets;
import com.persinity.common.logging.Log4jLogger;
import com.persinity.haka.JobIdentity;
import com.persinity.haka.JobProducer;
import com.persinity.haka.example.impl.FileFinderJob;

/**
 * Process {@link FileFinderRootJob} into {@link FileFinderJob}.
 * <p/>
 * When {@link FileFinderJob} is processed logs the results.
 *
 * @author Ivan Dachev
 */
public class FileFinderRootJobProducer implements JobProducer<FileFinderRootJob, FileFinderJob> {

    @Override
    public Set<FileFinderJob> process(FileFinderRootJob rootJob) {

        if (rootJob.isRootDone()) {
            return Collections.emptySet();
        }
        rootJob.setRootDone();

        Set<File> seed = new HashSet<>();
        seed.add(new File("/root/directory/for/seed/"));
        FileFinderJob job = new FileFinderJob(new JobIdentity(rootJob.getId()), seed, 180000);

        log.debug("Created FileFinderJob job: {}", job.getId());

        return Sets.newHashSet(job);
    }

    @Override
    public void processed(FileFinderRootJob parentJob, FileFinderJob childJob) {
        log.debug("FileFinderJob done: {} results: {}", childJob.getId(), childJob.getResult());
    }

    private static final Log4jLogger log = new Log4jLogger(Logger.getLogger(FileFinderRootJobProducer.class));
}
