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
package com.persinity.ndt.etlmodule.haka;

import java.util.Collections;
import java.util.Set;

import org.apache.log4j.Logger;

import com.persinity.common.IoUtils;
import com.persinity.common.collection.DirectedEdge;
import com.persinity.common.collection.Pool;
import com.persinity.common.db.Closeable;
import com.persinity.common.db.RelDbUtil;
import com.persinity.common.logging.Log4jLogger;
import com.persinity.haka.Job;
import com.persinity.haka.JobProducer;
import com.persinity.ndt.transform.TransferFunc;

/**
 * Responsible for executing an ETL instruction, which transfers
 * given partition of window data into a target entity.
 *
 * @author Ivan Dachev
 */
public class LuwJobProcessor<S extends Closeable, D extends Closeable> implements JobProducer<LuwJob<S, D>, Job> {
    @Override
    public Set<Job> process(final LuwJob<S, D> job) {
        log.debug("Processing {}", job);

        final TransferFunc<S, D> transferFunc = job.getTransferFunc();
        final DirectedEdge<Pool<S>, Pool<D>> directedEdge = job.getDataPoolBridge();

        DirectedEdge<S, D> dataBridge = null;
        try {
            dataBridge = RelDbUtil.getBridge(directedEdge);
            transferFunc.apply(dataBridge);
        } finally {
            IoUtils.silentClose(dataBridge);
        }

        return Collections.emptySet();
    }

    @Override
    public void processed(final LuwJob<S, D> parentJob, final Job childJob) {
        throw new UnsupportedOperationException();
    }

    private static final Log4jLogger log = new Log4jLogger(Logger.getLogger(LuwJobProcessor.class));
}
