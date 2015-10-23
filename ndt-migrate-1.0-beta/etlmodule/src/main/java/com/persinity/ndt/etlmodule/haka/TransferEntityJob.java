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

import com.persinity.common.collection.DirectedEdge;
import com.persinity.common.collection.Pool;
import com.persinity.common.db.Closeable;
import com.persinity.haka.Job;
import com.persinity.haka.JobIdentity;
import com.persinity.haka.JobProducer;
import com.persinity.ndt.etlmodule.TransferFunctor;

/**
 * This Job represents the task for building ETL instructions for a target entity.
 * Composes a functor that returns the set of ETL instructions,
 * each responsible for transferring a partition of the source data into the target entity.
 * <p/>
 * The entire set of returned ETL instructions is responsible
 * for transferring all of the window data to the target entity.
 *
 * @author Ivan Dachev
 */
public class TransferEntityJob<S extends Closeable, D extends Closeable> implements Job {
    public TransferEntityJob(final JobIdentity id, final TransferFunctor<S, D> functor,
            final DirectedEdge<Pool<S>, Pool<D>> dataPoolBridge) {
        this.id = id;
        this.functor = functor;
        this.dataPoolBridge = dataPoolBridge;
    }

    @Override
    public JobIdentity getId() {
        return id;
    }

    @Override
    public Class<? extends JobProducer> getJobProducerClass() {
        return LuwJobProducer.class;
    }

    @Override
    public Job clone() {
        final TransferFunctor<S, D> functorClone = functor; //TODO clone
        final DirectedEdge<Pool<S>, Pool<D>> dataBridgeClone = dataPoolBridge;  //TODO clone
        final TransferEntityJob<S, D> clone = new TransferEntityJob<>(id, functorClone, dataBridgeClone);
        return clone;
    }

    /**
     * @return Function to generate the ETL transfer functions per entity.
     */
    public TransferFunctor<S, D> getFunctor() {
        return functor;
    }

    /**
     * @return DirectedEdge for DB source/destination pool to work on.
     */
    public DirectedEdge<Pool<S>, Pool<D>> getDataPoolBridge() {
        return dataPoolBridge;
    }

    /**
     * @return processed state
     */
    public boolean isProcessed() {
        return processed;
    }

    /**
     * Set processed
     */
    public void setProcessed() {
        this.processed = true;
    }

    @Override
    public String toString() {
        if (toString == null) {
            toString = format("{}@{}({}, {}, {})", getClass().getSimpleName(), Integer.toHexString(hashCode()),
                    getId().toShortString(), getFunctor(), dataPoolBridge);
        }
        return toString;
    }

    private final JobIdentity id;
    private final TransferFunctor<S, D> functor;
    private final DirectedEdge<Pool<S>, Pool<D>> dataPoolBridge;

    private boolean processed;

    private transient String toString;

    private static final long serialVersionUID = -7898707804954282428L;
}
