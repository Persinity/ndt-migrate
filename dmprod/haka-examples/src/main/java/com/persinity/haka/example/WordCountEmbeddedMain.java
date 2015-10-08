/**
 * Copyright (c) 2015 Persinity Inc.
 */
package com.persinity.haka.example;

import com.persinity.common.logging.Log4jLogger;
import com.persinity.haka.HakaExecutor;
import com.persinity.haka.HakaExecutorFactoryProvider;
import com.persinity.haka.example.impl.FileFinderJob;
import com.persinity.haka.impl.actor.HakaNode;
import org.apache.log4j.Logger;

import java.io.File;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.Future;

import static com.persinity.common.invariant.Invariant.assertArg;

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
