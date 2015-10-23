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
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import com.persinity.haka.Job;
import com.persinity.haka.JobIdentity;
import com.persinity.haka.JobProducer;
import com.persinity.haka.example.impl.utils.ResultAggregator;

/**
 * Holds the state for file reading job.
 * <p/>
 * Created by Ivo Yanakiev on 5/19/15.
 */
public class FileReaderJob implements Job {

    private static final long serialVersionUID = 4668178960248580480L;

    public FileReaderJob(JobIdentity id, File file) {
        this(id, file, false, new HashMap<String, Integer>());
    }

    public FileReaderJob(FileReaderJob source) {
        this(source.id, source.file, source.isProcessed, source.result);
    }

    public FileReaderJob(JobIdentity id, File file, boolean isProcessed, Map<String, Integer> result) {

        assertArg(id != null, "id");
        assertArg(file != null, "file");
        assertArg(result != null, "result");

        this.id = id;
        this.file = file;
        this.result = result;
        this.isProcessed = isProcessed;
    }

    @Override
    public JobIdentity getId() {
        return id;
    }

    @Override
    public Class<? extends JobProducer> getJobProducerClass() {
        return FileReaderJobProducer.class;
    }

    @Override
    public Job clone() {
        return new FileReaderJob(this);
    }

    public File getFile() {
        return file;
    }

    public boolean isProcessed() {
        return isProcessed;
    }

    public void setProcessed(boolean isProcessed) {
        this.isProcessed = isProcessed;
    }

    public Map<String, Integer> getResult() {
        return Collections.unmodifiableMap(result);
    }

    public void aggregate(Map<String, Integer> slice) {
        aggregator.aggregate(result, slice);
    }

    private final JobIdentity id;
    private final File file;
    private boolean isProcessed;
    private final Map<String, Integer> result;
    private final ResultAggregator aggregator = new ResultAggregator();
}
