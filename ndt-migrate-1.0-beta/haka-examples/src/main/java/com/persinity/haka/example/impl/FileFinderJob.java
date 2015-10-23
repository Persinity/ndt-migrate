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
package com.persinity.haka.example.impl;

import static com.persinity.common.invariant.Invariant.assertArg;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import com.persinity.haka.Job;
import com.persinity.haka.JobIdentity;
import com.persinity.haka.JobProducer;
import com.persinity.haka.example.impl.utils.ResultAggregator;

/**
 * Holds the state for traversing a root directory and extracting the files ans subdirectories.
 *
 * @author Ivan Dachev
 */
public class FileFinderJob implements Job {

    private static final long serialVersionUID = 2288751680668779442L;

    public FileFinderJob(Set<File> unprocessed, int maxParallelFiles) {
        this(new JobIdentity(), unprocessed, maxParallelFiles);
    }

    public FileFinderJob(JobIdentity id, Set<File> unprocessed, int maxParallelFiles) {
        this(id, unprocessed, maxParallelFiles, new HashMap<String, Integer>());
    }

    public FileFinderJob(FileFinderJob source) {
        this(source.id, source.unprocessed, source.maxParallelFiles, source.result);
    }

    public FileFinderJob(JobIdentity id, Set<File> unprocessed, int maxParallelFiles, HashMap<String, Integer> result) {

        assertArg(id != null, "id");
        assertArg(unprocessed != null, "unprocessed");
        assertArg(maxParallelFiles > 0, "maxParallelFiles");
        assertArg(result != null, "result");

        this.id = id;
        this.unprocessed = unprocessed;
        this.maxParallelFiles = maxParallelFiles;
        this.result = result;
    }

    @Override
    public JobIdentity getId() {
        return id;
    }

    @Override
    public Class<? extends JobProducer> getJobProducerClass() {
        return FileFinderJobProducer.class;
    }

    @Override
    public Job clone() {
        return new FileFinderJob(this);
    }

    public Set<File> getUnprocessed() {
        return unprocessed;
    }

    public Map<String, Integer> getResult() {
        return result;
    }

    public void aggregate(Map<String, Integer> slice) {
        aggregator.aggregate(result, slice);
    }

    private final JobIdentity id;
    private final Set<File> unprocessed;
    private final int maxParallelFiles;
    private final HashMap<String, Integer> result;
    private final ResultAggregator aggregator = new ResultAggregator();

    public int getMaxParallelFiles() {
        return maxParallelFiles;
    }
}
