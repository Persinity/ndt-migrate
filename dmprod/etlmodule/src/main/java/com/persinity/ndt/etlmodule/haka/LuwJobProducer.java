/**
 * Copyright (c) 2015 Persinity Inc.
 */
package com.persinity.ndt.etlmodule.haka;

import static com.persinity.common.invariant.Invariant.assertState;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import org.apache.log4j.Logger;

import com.persinity.common.collection.DirectedEdge;
import com.persinity.common.collection.Pool;
import com.persinity.common.db.Closeable;
import com.persinity.common.logging.Log4jLogger;
import com.persinity.haka.JobIdentity;
import com.persinity.haka.JobProducer;
import com.persinity.ndt.etlmodule.TransferFunctor;
import com.persinity.ndt.transform.TransferFunc;

/**
 * Calculates the ETL instructions for a target entity
 * that should transfer data that falls into a given window.
 * The calculation effectively is the partitioning of the source
 * data that falls in the transfer window into partitions (LUWs).
 *
 * @author Ivan Dachev
 */
public class LuwJobProducer<S extends Closeable, D extends Closeable>
        implements JobProducer<TransferEntityJob<S, D>, LuwJob<S, D>> {

    @Override
    public Set<LuwJob<S, D>> process(final TransferEntityJob<S, D> job) {
        if (job.isProcessed()) {
            return Collections.emptySet();
        }
        job.setProcessed();

        log.debug("Processing {}", job);

        final TransferFunctor<S, D> functor = job.getFunctor();
        final DirectedEdge<Pool<S>, Pool<D>> dataPoolBridge = job.getDataPoolBridge();

        final Set<TransferFunc<S, D>> transferFuncSet = functor.apply(null);
        assertState(transferFuncSet != null, "Expected not null transferFuncSet for {}", functor);

        final HashSet<LuwJob<S, D>> luws = new HashSet<>();
        for (TransferFunc<S, D> transferFunc : transferFuncSet) {
            final LuwJob<S, D> luwJob = new LuwJob<>(new JobIdentity(job.getId()), transferFunc, dataPoolBridge);
            log.debug("Created {}", luwJob);
            luws.add(luwJob);
        }

        log.debug("Returned jobs: {}", luws.size());
        return luws;
    }

    @Override
    public void processed(final TransferEntityJob<S, D> parentJob, final LuwJob<S, D> childJob) {

    }

    private static final Log4jLogger log = new Log4jLogger(Logger.getLogger(LuwJobProducer.class));
}
