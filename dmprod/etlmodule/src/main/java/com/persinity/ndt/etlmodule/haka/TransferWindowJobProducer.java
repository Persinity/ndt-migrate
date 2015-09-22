/**
 * Copyright (c) 2015 Persinity Inc.
 */
package com.persinity.ndt.etlmodule.haka;

import java.util.Collections;
import java.util.Iterator;
import java.util.Set;

import org.apache.log4j.Logger;

import com.google.common.base.Function;
import com.persinity.common.collection.DelayedIterator;
import com.persinity.common.db.Closeable;
import com.persinity.common.logging.Log4jLogger;
import com.persinity.haka.JobIdentity;
import com.persinity.haka.JobProducer;
import com.persinity.ndt.etlmodule.WindowGenerator;
import com.persinity.ndt.transform.TransferWindow;

/**
 * Responsible for generating transfer window job.
 *
 * @author Ivan Dachev
 */
public class TransferWindowJobProducer<S extends Closeable, D extends Closeable>
        implements JobProducer<WindowGeneratorJob<S, D>, TransferWindowJob<S, D>> {

    @Override
    public Set<TransferWindowJob<S, D>> process(final WindowGeneratorJob<S, D> job) {
        final WindowGenerator<S, D> windowGenerator = job.getWindowGenerator();
        Iterator<TransferWindow<S, D>> iter = job.getCachedIter();

        log.debug("Getting next window {}", job);

        if (iter == null) {
            iter = windowGenerator.iterator();
            final Function<TransferWindow<S, D>, Boolean> isMarkerValueF = new Function<TransferWindow<S, D>, Boolean>() {
                @Override
                public Boolean apply(final TransferWindow<S, D> sdTransferWindow) {
                    return sdTransferWindow.isEmpty();
                }
            };
            iter = new DelayedIterator<>(iter, isMarkerValueF, job.getWindowCheckIntervalSeconds() * 1000L);
            job.setCachedIter(iter);
        }

        if (iter.hasNext()) {
            final TransferWindow<S, D> transferWindow = iter.next();

            final TransferWindowJob<S, D> transferWindowJob;
            if (transferWindow.isEmpty()) {
                transferWindowJob = new TransferWindowIdleJob<>(new JobIdentity(job.getId()), transferWindow);
            } else {
                transferWindowJob = new TransferWindowJob<>(new JobIdentity(job.getId()), transferWindow);
            }

            transferWindowJob.setContextName(job.getContextName());

            log.debug("Created {}", transferWindowJob);
            return Collections.singleton(transferWindowJob);
        } else {
            log.debug("No more windows");
            return Collections.emptySet();
        }
    }

    @Override
    public void processed(final WindowGeneratorJob<S, D> parentJob, final TransferWindowJob<S, D> childJob) {
    }

    private static final Log4jLogger log = new Log4jLogger(Logger.getLogger(TransferWindowJobProducer.class));
}
