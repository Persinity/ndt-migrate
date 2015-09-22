/**
 * Copyright (c) 2015 Persinity Inc.
 */

package com.persinity.ndt.etlmodule.haka;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import org.apache.log4j.Logger;

import com.google.common.base.Function;
import com.persinity.common.db.RelDb;
import com.persinity.common.logging.Log4jLogger;
import com.persinity.haka.JobIdentity;
import com.persinity.haka.JobProducer;

/**
 * @author dyordanov
 */
public class ClogGcJobProducer implements JobProducer<GcJob, ClogGcJob> {
    @Override
    public Set<ClogGcJob> process(final GcJob job) {
        log.debug("Processing {}", job);
        final Set<ClogGcJob> children = new HashSet<>();
        final Iterator<Function<RelDb, RelDb>> it = job.iterator();
        int i = job.getBulkSize();
        while (it.hasNext() && i-- > 0) {
            final ClogGcJob clogGcJob = new ClogGcJob(new JobIdentity(job.getId()), it.next(), job.getDbPool());
            log.debug("Created {}", clogGcJob);
            children.add(clogGcJob);
        }
        return children;
    }

    @Override
    public void processed(final GcJob parentJob, final ClogGcJob childJob) {
    }

    private static final Log4jLogger log = new Log4jLogger(Logger.getLogger(ClogGcJobProducer.class));
}
