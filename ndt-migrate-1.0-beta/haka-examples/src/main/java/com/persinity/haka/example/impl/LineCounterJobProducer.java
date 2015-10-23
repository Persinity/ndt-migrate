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
package com.persinity.haka.example.impl;

import java.util.Collections;
import java.util.Set;
import java.util.StringTokenizer;

import org.apache.log4j.Logger;

import com.persinity.common.logging.Log4jLogger;
import com.persinity.haka.Job;
import com.persinity.haka.JobProducer;

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
