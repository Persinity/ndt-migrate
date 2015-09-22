/**
 * Copyright (c) 2015 Persinity Inc.
 */
package com.persinity.haka.example.impl;

import com.persinity.common.logging.Log4jLogger;
import com.persinity.haka.JobIdentity;
import com.persinity.haka.JobProducer;
import org.apache.log4j.Logger;

import java.io.File;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

/**
 * Processes {@link FileFinderJob} into {@link FileReaderJob}.
 * <p/>
 * When a {@link FileReaderJob} is processed the impl accumulates
 * words count in the parent {@link FileFinderJob}.
 *
 * @author Ivan Dachev
 */
public class FileFinderJobProducer implements JobProducer<FileFinderJob, FileReaderJob> {

	@Override
	public Set<FileReaderJob> process(FileFinderJob job) {

		Set<File> files = extractUnprocessedNodes(job);
		Set<FileReaderJob> result = buildJobs(files, job.getId());
		log.debug("Sending {} jobs for processing from {}", result.size(), job.getId().toShortString());

		return result;
	}

	private Set<File> extractUnprocessedNodes(FileFinderJob job) {

		Set<File> unprocessed = job.getUnprocessed();
		Set<File> result = new HashSet<>();

		while (result.size() < job.getMaxParallelFiles()) {

			Iterator<File> iter = unprocessed.iterator();

			if (!iter.hasNext()) {
				break;
			}

			File item = iter.next();
			unprocessed.remove(item);

			if (item.isFile()) {

				log.debug("Processing {}", item.getAbsolutePath());
				result.add(item);
				continue;
			}

			for (File path : item.listFiles()) {
				unprocessed.add(path);
			}
		}
		return result;
	}

	private Set<FileReaderJob> buildJobs(Set<File> files, JobIdentity parentId) {

		Set<FileReaderJob> result = new HashSet<>();

		for (File file : files) {

			JobIdentity id = new JobIdentity(parentId);
			FileReaderJob job = new FileReaderJob(id, file);
			result.add(job);
		}

		return result;
	}

	@Override
	public void processed(FileFinderJob parentJob, FileReaderJob childJob) {

		parentJob.aggregate(childJob.getResult());
		log.debug("Aggregated: {} to: {}", childJob.getId().toShortString(), parentJob.getId().toShortString());
	}

	private static final Log4jLogger log = new Log4jLogger(Logger.getLogger(FileFinderJobProducer.class));

}
