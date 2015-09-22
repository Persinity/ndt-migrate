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
