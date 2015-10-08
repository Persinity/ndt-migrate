/**
 * Copyright (c) 2015 Persinity Inc.
 */

package com.persinity.ndt.etlmodule.haka;

import static com.persinity.common.StringUtils.format;
import static com.persinity.common.invariant.Invariant.notNull;

import com.google.common.base.Function;
import com.persinity.common.collection.Pool;
import com.persinity.common.db.RelDb;
import com.persinity.haka.Job;
import com.persinity.haka.JobIdentity;
import com.persinity.haka.JobProducer;

/**
 * {@link Job} for GC of a given CLOG.
 *
 * @author dyordanov
 */
public class ClogGcJob implements Job {

    public ClogGcJob(final JobIdentity id, final Function<RelDb, RelDb> gcF, final Pool<RelDb> dbPool) {
        notNull(id);
        notNull(gcF);
        notNull(dbPool);

        this.id = id;
        this.gcF = gcF;
        this.dbPool = dbPool;
    }

    @Override
    public JobIdentity getId() {
        return id;
    }

    @Override
    public Class<? extends JobProducer> getJobProducerClass() {
        return ClogGcJobProcessor.class;
    }

    @Override
    public Job clone() {
        return new ClogGcJob(this.getId(), this.getGcF(), this.getDbPool());
    }

    public Function<RelDb, RelDb> getGcF() {
        return gcF;
    }

    public Pool<RelDb> getDbPool() {
        return dbPool;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o)
            return true;
        if (!(o instanceof ClogGcJob))
            return false;

        final ClogGcJob clogGcJob = (ClogGcJob) o;

        return getId().equals(clogGcJob.getId());

    }

    @Override
    public String toString() {
        if (toString == null) {
            toString = format("{}@{}({}, {}, {})", getClass().getSimpleName(), Integer.toHexString(hashCode()),
                    getId().toShortString(), getGcF(), getDbPool());
        }
        return toString;
    }

    private final JobIdentity id;
    private final Function<RelDb, RelDb> gcF;
    private final Pool<RelDb> dbPool;

    private transient String toString;
}
