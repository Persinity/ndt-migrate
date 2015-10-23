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

import static com.persinity.common.StringUtils.format;
import static com.persinity.common.invariant.Invariant.assertArg;
import static com.persinity.common.invariant.Invariant.notNull;

import java.util.Iterator;

import com.google.common.base.Function;
import com.persinity.common.collection.Pool;
import com.persinity.common.collection.Tree;
import com.persinity.common.db.RelDb;
import com.persinity.haka.Job;
import com.persinity.haka.JobIdentity;
import com.persinity.haka.JobProducer;

/**
 * {@link Job} for GC of all CLOGs
 *
 * @author dyordanov
 */
public class GcJob implements Job, Iterable<Function<RelDb, RelDb>> {

    /**
     * @param id
     * @param plan
     * @param dbPool
     * @param bulkSize
     *         How many children to produce at once.
     */
    public GcJob(final JobIdentity id, final Tree<Function<RelDb, RelDb>> plan, final Pool<RelDb> dbPool,
            int bulkSize) {
        notNull(id);
        notNull(plan);
        notNull(dbPool);
        assertArg(bulkSize > 0);

        this.id = id;
        this.plan = plan;
        this.it = plan.breadthFirstTraversal(plan.getRoot()).iterator();
        this.dbPool = dbPool;
        this.bulkSize = bulkSize;
    }

    @Override
    public JobIdentity getId() {
        return id;
    }

    @Override
    public Class<? extends JobProducer> getJobProducerClass() {
        return ClogGcJobProducer.class;
    }

    @Override
    public Job clone() {
        final Job clone = new GcJob(this.getId(), this.getPlan(), this.getDbPool(), this.getBulkSize());
        return clone;
    }

    @Override
    public Iterator<Function<RelDb, RelDb>> iterator() {
        return it;
    }

    /**
     * @return Pool<RelDb>
     */
    public Pool<RelDb> getDbPool() {
        return dbPool;
    }

    /**
     * @return The number of children to be produced on single call to
     * the process method of the corresponding {@link JobProducer}
     */
    public int getBulkSize() {
        return bulkSize;
    }

    /**
     * @return Plan
     */
    protected Tree<Function<RelDb, RelDb>> getPlan() {
        return plan;
    }

    @Override
    public String toString() {
        if (toString == null) {
            toString = format("{}@{}({}, {}, {}, {})", this.getClass().getSimpleName(), Integer.toHexString(hashCode()),
                    getId().toShortString(), getPlan(), getDbPool(), getBulkSize());
        }
        return toString;
    }

    private final int bulkSize;
    private final Pool<RelDb> dbPool;
    private JobIdentity id;

    private Tree<Function<RelDb, RelDb>> plan;
    private Iterator<Function<RelDb, RelDb>> it;

    private transient String toString;
}
