/**
 * Copyright (c) 2015 Persinity Inc.
 */

package com.persinity.ndt.etlmodule.haka;

import java.util.Collections;
import java.util.Set;

import org.apache.log4j.Logger;

import com.persinity.common.Resource;
import com.persinity.common.db.RelDb;
import com.persinity.common.logging.Log4jLogger;
import com.persinity.haka.Job;
import com.persinity.haka.JobProducer;

/**
 * @author dyordanov
 */
public class ClogGcJobProcessor implements JobProducer<ClogGcJob, Job> {
    @Override
    public Set<Job> process(final ClogGcJob job) {
        log.debug("Processing {}", job);
        resource.accessAndClose(new Resource.Accessor<RelDb, Void>(job.getDbPool().get(), null) {
            @Override
            public Void access(final RelDb resource) throws Exception {
                job.getGcF().apply(resource);
                resource.commit();
                return null;
            }
        });
        return Collections.emptySet();
    }

    @Override
    public void processed(final ClogGcJob parentJob, final Job childJob) {
        throw new UnsupportedOperationException();
    }

    private static final Log4jLogger log = new Log4jLogger(Logger.getLogger(ClogGcJobProcessor.class));
    private final Resource resource = new Resource();
}
