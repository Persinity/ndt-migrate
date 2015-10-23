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
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.persinity.haka.HakaExecutor;
import com.persinity.haka.HakaExecutorFactoryProvider;
import com.persinity.haka.example.impl.FileFinderJob;
import com.persinity.haka.impl.actor.HakaNode;

/**
 * @author Ivo Yanakiev
 */
public class HakaWordCountIT extends WordCountBase {

    @Before
    public void setUp() {
        hakaNode = new HakaNode("embedded", "test-haka-embedded-node.conf");
        executor = HakaExecutorFactoryProvider.getFactory().newEmbeddedInstance(hakaNode);
    }

    @After
    public void tearDown() {
        safeShutdown(executor);
        safeShutdown(hakaNode);
    }

    @Test
    public void testWordCount() throws ExecutionException, InterruptedException {
        Set<File> seed = new HashSet<>();
        File rootDir = getRootDir();
        seed.add(rootDir);

        FileFinderJob job = new FileFinderJob(seed, MAX_PARALLEL_FILES);

        Future<FileFinderJob> future = executor.executeJob(job, TIMEOUT);
        job = future.get();

        Map<String, Integer> result = job.getResult();

        Assert.assertEquals(EXPECTED, result);
    }

    private void safeShutdown(HakaNode node) {
        if (node != null) {
            node.shutdown();
        }
    }

    private void safeShutdown(HakaExecutor executor) {
        if (executor != null) {
            executor.shutdown();
        }
    }

    private HakaNode hakaNode;
    private HakaExecutor executor;

}
