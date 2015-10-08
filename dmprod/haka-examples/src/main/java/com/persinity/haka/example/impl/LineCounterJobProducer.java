/**
 * Copyright (c) 2015 Persinity Inc.
 */
package com.persinity.haka.example.impl;

import com.persinity.common.logging.Log4jLogger;
import com.persinity.haka.Job;
import com.persinity.haka.JobProducer;
import org.apache.log4j.Logger;

import java.util.Collections;
import java.util.Set;
import java.util.StringTokenizer;

/**
 * Processes {@link LineCounterJob}.
 *
 * @author Ivan Dachev
 */
public class LineCounterJobProducer implements JobProducer<LineCounterJob, Job> {

	@Override
	public Set<Job> process(LineCounterJob job) {

		String line = job.getLine();
		StringTokenizer itr = new StringTokenizer(line, " \t\n\r\f.,!?");

		while (itr.hasMoreTokens()) {

			String word = itr.nextToken();
			if (!word.isEmpty()) {
				job.aggregate(word);
			}
		}

		log.debug("Processed line: {}", line);

		return Collections.emptySet();
	}

	@Override
	public void processed(LineCounterJob parentJob, Job childJob) {
		throw new UnsupportedOperationException();
	}

	private static final Log4jLogger log = new Log4jLogger(Logger.getLogger(LineCounterJobProducer.class));
}
