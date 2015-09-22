/**
 * Copyright (c) 2015 Persinity Inc.
 */
package com.persinity.haka.example;

import com.google.common.collect.Sets;
import com.persinity.common.logging.Log4jLogger;
import com.persinity.haka.JobIdentity;
import com.persinity.haka.JobProducer;
import com.persinity.haka.example.impl.FileFinderJob;
import org.apache.log4j.Logger;

import java.io.File;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 * Process {@link FileFinderRootJob} into {@link FileFinderJob}.
 * <p/>
 * When {@link FileFinderJob} is processed logs the results.
 *
 * @author Ivan Dachev
 */
public class FileFinderRootJobProducer implements JobProducer<FileFinderRootJob, FileFinderJob> {

	@Override
	public Set<FileFinderJob> process(FileFinderRootJob rootJob) {

		if (rootJob.isRootDone()) {
			return Collections.emptySet();
		}
		rootJob.setRootDone();

		Set<File> seed = new HashSet<>();
		seed.add(new File("/root/directory/for/seed/"));
		FileFinderJob job = new FileFinderJob(new JobIdentity(rootJob.getId()), seed, 180000);

		log.debug("Created FileFinderJob job: {}", job.getId());

		return Sets.newHashSet(job);
	}

	@Override
	public void processed(FileFinderRootJob parentJob, FileFinderJob childJob) {
		log.debug("FileFinderJob done: {} results: {}", childJob.getId(), childJob.getResult());
	}

	private static final Log4jLogger log = new Log4jLogger(Logger.getLogger(FileFinderRootJobProducer.class));
}
