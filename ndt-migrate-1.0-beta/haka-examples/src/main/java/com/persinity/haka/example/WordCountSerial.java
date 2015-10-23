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

import static com.persinity.common.invariant.Invariant.assertArg;

import java.io.File;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Callable;

import org.apache.log4j.Logger;

import com.persinity.common.logging.Log4jLogger;
import com.persinity.haka.JobIdentity;
import com.persinity.haka.example.impl.FileFinderJob;
import com.persinity.haka.example.impl.FileFinderJobProducer;
import com.persinity.haka.example.impl.FileReaderJob;
import com.persinity.haka.example.impl.FileReaderJobProducer;
import com.persinity.haka.example.impl.LineCounterJob;
import com.persinity.haka.example.impl.LineCounterJobProducer;

/**
 * @author Ivo Yanakiev
 */
public class WordCountSerial implements Callable<Map<String, Integer>> {

    public WordCountSerial(File rootDir, int maxParallelFiles) {

        assertArg(rootDir != null, "rootDir");
        assertArg(rootDir.exists(), "Root dir: {} does not exist.", rootDir.getAbsolutePath());
        assertArg(maxParallelFiles > 1, "maxParallelFiles");

        this.rootDir = rootDir;
        this.maxParallelFiles = maxParallelFiles;
    }

    @Override
    public Map<String, Integer> call() {

        FileFinderJob fileFinder = buildFileFinder();

        while (true) {
            // get some files
            Set<FileReaderJob> fileReaderJobs = fileFinderProcessor.process(fileFinder);
            if (fileReaderJobs.isEmpty()) {
                break;
            }
            // read the files
            for (FileReaderJob readerJob : fileReaderJobs) {

                Set<LineCounterJob> lineCounterJobs = fileReaderProcessor.process(readerJob);
                // count the lines
                for (LineCounterJob counterJob : lineCounterJobs) {

                    lineCounterProcessor.process(counterJob);
                    fileReaderProcessor.processed(readerJob, counterJob);
                }
                fileFinderProcessor.processed(fileFinder, readerJob);
            }
        }
        return fileFinder.getResult();
    }

    private FileFinderJob buildFileFinder() {

        Set<File> seed = new HashSet<>();
        seed.add(rootDir);
        JobIdentity id = new JobIdentity();
        return new FileFinderJob(id, seed, maxParallelFiles);
    }

    @Override
    public String toString() {
        return "Serial";
    }

    private final File rootDir;
    private final int maxParallelFiles;

    private final FileFinderJobProducer fileFinderProcessor = new FileFinderJobProducer();
    private final FileReaderJobProducer fileReaderProcessor = new FileReaderJobProducer();
    private final LineCounterJobProducer lineCounterProcessor = new LineCounterJobProducer();

    private static final Log4jLogger log = new Log4jLogger(Logger.getLogger(WordCountSerial.class));
}
