/**
 * Copyright (c) 2015 Persinity Inc.
 */
package com.persinity.haka.example.impl;

import com.persinity.haka.Job;
import com.persinity.haka.JobIdentity;
import com.persinity.haka.JobProducer;
import com.persinity.haka.example.impl.utils.ResultAggregator;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import static com.persinity.common.invariant.Invariant.assertArg;

/**
 * Holds a line from file.
 *
 * @author Ivan Dachev
 */
public class LineCounterJob implements Job {

	private static final long serialVersionUID = -6676285234476262118L;

	public LineCounterJob(JobIdentity id, String line) {
		this(id, line, new HashMap<String, Integer>());
	}

	public LineCounterJob(LineCounterJob source) {
		this(source.id, source.line, source.result);
	}

	public LineCounterJob(JobIdentity id, String line, Map<String, Integer> result) {

		assertArg(id != null, "id");
		assertArg(line != null, "line");
		assertArg(result != null, "result");

		this.id = id;
		this.line = line;
		this.result = result;
	}

	@Override
	public JobIdentity getId() {
		return id;
	}

	@Override
	public Class<? extends JobProducer<? extends Job, ? extends Job>> getJobProducerClass() {
		return LineCounterJobProducer.class;
	}

	@Override
	public Job clone() {
		return new LineCounterJob(this);
	}

	public String getLine() {
		return line;
	}

	public Map<String, Integer> getResult() {
		return Collections.unmodifiableMap(result);
	}

	public void aggregate(String word) {
		aggregator.aggregate(result, word);
	}

	private final JobIdentity id;
	private final String line;
	private Map<String, Integer> result;
	private final ResultAggregator aggregator = new ResultAggregator();
}
