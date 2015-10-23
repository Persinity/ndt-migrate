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

import static com.persinity.common.StringUtils.format;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import org.apache.log4j.Logger;

import com.persinity.common.logging.Log4jLogger;
import com.persinity.haka.JobIdentity;
import com.persinity.haka.JobProducer;

/**
 * Processes {@link FileReaderJob} into {@link LineCounterJob}.
 * <p/>
 * When a {@link LineCounterJob} is processed the impl accumulates
 * words count in the parent {@link FileReaderJob}.
 *
 * @author Ivo Yanakiev
 */
public class FileReaderJobProducer implements JobProducer<FileReaderJob, LineCounterJob> {

    @Override
    public Set<LineCounterJob> process(FileReaderJob job) {

        if (job.isProcessed()) {
            log.debug("Skipping file: {} ,it is already processed.", job.getFile());
            return Collections.emptySet();
        }

        File file = job.getFile();
        String absPath = file.getAbsolutePath();

        log.debug("Processing file: {} in {}", absPath, job.getId().toShortString());

        Set<LineCounterJob> result = new HashSet<>();
        int lineNumber = 0;

        try (BufferedReader br = new BufferedReader(new FileReader(file))) {

            for (String line; (line = br.readLine()) != null; ) {

                lineNumber++;
                JobIdentity childId = new JobIdentity(job.getId());
                result.add(new LineCounterJob(childId, line));
            }

        } catch (FileNotFoundException e) {
            throw new RuntimeException(format("Unable to find file: {}", absPath));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        job.setProcessed(true);
        log.debug("Processed file: {}, lines: {}", absPath, lineNumber);
        return result;
    }

    @Override
    public void processed(FileReaderJob parentJob, LineCounterJob childJob) {

        parentJob.aggregate(childJob.getResult());
        log.debug("Aggregated: {} to: {}", childJob.getId().toShortString(), parentJob.getId().toShortString());
    }

    private static final Log4jLogger log = new Log4jLogger(Logger.getLogger(FileReaderJobProducer.class));
}
