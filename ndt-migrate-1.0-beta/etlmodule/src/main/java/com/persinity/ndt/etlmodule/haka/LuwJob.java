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
import com.persinity.ndt.transform.TransferFunc;

/**
 * Represents the task of calling ETL instructions.
 * <p/>
 * ETL instruction represents partition of source data
 * that falls into the transfer window and needs
 * to be transferred to a target entity.
 *
 * @author Ivan Dachev
 */
public class LuwJob<S extends Closeable, D extends Closeable> implements Job {

    public LuwJob(final JobIdentity id, final TransferFunc<S, D> transferFunc,
            final DirectedEdge<Pool<S>, Pool<D>> dataPoolBridge) {
        this.id = id;
        this.transferFunc = transferFunc;
        this.dataPoolBridge = dataPoolBridge;
    }

    @Override
    public JobIdentity getId() {
        return id;
    }

    @Override
    public Class<? extends JobProducer> getJobProducerClass() {
        return LuwJobProcessor.class;
    }

    @Override
    public Job clone() {
        final TransferFunc<S, D> transferFuncClone = transferFunc; //TODO clone
        final DirectedEdge<Pool<S>, Pool<D>> dataPoolBridgeClone = dataPoolBridge;  //TODO clone
        final LuwJob<S, D> clone = new LuwJob<>(id, transferFuncClone, dataPoolBridgeClone);
        return clone;
    }

    /**
     * @return TransferFunction with ETL instruction per entity.
     */
    public TransferFunc<S, D> getTransferFunc() {
        return transferFunc;
    }

    /**
     * @return DirectedEdge for DB source/destination to work on.
     */
    public DirectedEdge<Pool<S>, Pool<D>> getDataPoolBridge() {
        return dataPoolBridge;
    }

    @Override
    public String toString() {
        if (toString == null) {
            toString = format("{}@{}({}, {}, {})", getClass().getSimpleName(), Integer.toHexString(hashCode()),
                    getId().toShortString(), getTransferFunc(), getDataPoolBridge());
        }
        return toString;
    }

    private final JobIdentity id;
    private final TransferFunc<S, D> transferFunc;
    private final DirectedEdge<Pool<S>, Pool<D>> dataPoolBridge;

    private transient String toString;

    private static final long serialVersionUID = 3410692759874538184L;
}
