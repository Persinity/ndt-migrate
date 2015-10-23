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
import java.util.concurrent.Future;

import org.apache.log4j.Logger;

import com.persinity.common.logging.Log4jLogger;
import com.persinity.haka.HakaExecutor;
import com.persinity.haka.HakaExecutorFactoryProvider;
import com.persinity.haka.example.impl.FileFinderJob;
import com.persinity.haka.impl.actor.HakaNode;

/**
 * Example of executing a word count root job.
 *
 * @author Ivan Dachev
 */
public class WordCountEmbeddedMain implements Callable<Map<String, Integer>> {

    public WordCountEmbeddedMain(File rootDir, int maxParallelFiles) {
        this(rootDir, maxParallelFiles, JOB_TIMEOUT_DEFAULT_SEC);
    }

    public WordCountEmbeddedMain(File rootDir, int maxParallelFiles, long timeoutInSeconds) {

        assertArg(rootDir != null, "rootDir");
        assertArg(rootDir.exists(), "Unable to find root dir: {}", rootDir.getAbsolutePath());
        assertArg(timeoutInSeconds > 0, "timeoutInSeconds");

        this.rootDir = rootDir;
        this.timeoutInSeconds = timeoutInSeconds;
        this.maxParallelFiles = maxParallelFiles;
    }

    @Override
    public Map<String, Integer> call() throws Exception {

        Set<File> seed = new HashSet<>();
        seed.add(rootDir);

        FileFinderJob job = new FileFinderJob(seed, maxParallelFiles);
        log.debug("Created FileFinderJob job: {}", job.getId());

        HakaNode hakaNode = null;
        HakaExecutor executor = null;

        try {

            hakaNode = new HakaNode();
            executor = HakaExecutorFactoryProvider.getFactory().newEmbeddedInstance(hakaNode);
            Future<FileFinderJob> future = executor.executeJob(job, timeoutInSeconds * 1000);
            job = future.get();

        } catch (Exception e) {
            e.printStackTrace();

        } finally {
            if (executor != null) {
                executor.shutdown();
            }
            if (hakaNode != null) {
                hakaNode.shutdown();
            }
        }
        return job.getResult();
    }

    @Override
    public String toString() {
        return "Local HAKA";
    }

    private final File rootDir;
    private final long timeoutInSeconds;
    private final int maxParallelFiles;

    private static final long JOB_TIMEOUT_DEFAULT_SEC = 600;

    private static final Log4jLogger log = new Log4jLogger(Logger.getLogger(WordCountEmbeddedMain.class));
}
