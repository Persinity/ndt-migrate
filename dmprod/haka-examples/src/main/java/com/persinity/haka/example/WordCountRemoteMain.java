/**
 * Copyright (c) 2015 Persinity Inc.
 */
package com.persinity.haka.example;

import com.persinity.common.logging.Log4jLogger;
import com.persinity.haka.HakaExecutor;
import com.persinity.haka.HakaExecutorFactoryProvider;
import com.persinity.haka.JobIdentity;
import com.persinity.haka.example.impl.FileFinderJob;
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
public class WordCountRemoteMain implements Callable<Map<String, Integer>> {

	public WordCountRemoteMain(File rootDir, int maxParallelFiles, int timeoutInSeconds, String hakaHost,
			int hakaPort) {

		assertArg(rootDir != null, "rootDir");
		assertArg(rootDir.exists(), "Unable to find root dir: {}", rootDir.getAbsolutePath());
		assertArg(timeoutInSeconds > 0, "timeoutInSeconds");
		assertArg(hakaHost != null, "hakaHost");
		assertArg(hakaPort >= 0 && hakaPort <= 65535, "hakaPort");

		this.rootDir = rootDir;
		this.maxParallelFiles = maxParallelFiles;
		this.timeoutInSeconds = timeoutInSeconds;
		this.hakaHost = hakaHost;
		this.hakaPort = hakaPort;
	}

	@Override
	public Map<String, Integer> call() throws Exception {

		Set<File> seed = new HashSet<>();
		seed.add(rootDir);
		FileFinderJob job = new FileFinderJob(new JobIdentity(), seed, maxParallelFiles);

		log.debug("Created FileFinderJob job: {}", job.getId());

		HakaExecutor executor = null;

		try {

			executor = HakaExecutorFactoryProvider.getFactory().newRemoteInstance(hakaHost, hakaPort);
			Future<FileFinderJob> future = executor.executeJob(job, timeoutInSeconds * 1000);
			job = future.get();

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (executor != null) {
				executor.shutdown();
			}
		}

		return job.getResult();
	}

	@Override
	public String toString() {
		return "Remote Haka";
	}

	private final File rootDir;
	private final int maxParallelFiles;
	private final int timeoutInSeconds;
	private final String hakaHost;
	private final int hakaPort;

	private static final Log4jLogger log = new Log4jLogger(Logger.getLogger(WordCountRemoteMain.class));
}
