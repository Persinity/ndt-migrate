/**
 * Copyright (c) 2015 Persinity Inc.
 */
package com.persinity.ndt.etlmodule.haka;

import static com.persinity.common.StringUtils.format;

import java.util.Iterator;
import java.util.Set;

import com.persinity.common.db.Closeable;
import com.persinity.haka.Job;
import com.persinity.haka.JobIdentity;
import com.persinity.haka.JobProducer;
import com.persinity.ndt.etlmodule.TransferFunctor;
import com.persinity.ndt.transform.TransferWindow;

/**
 * Represents the task for producing an ETL plan out of Transfer window.
 *
 * @author Ivan Dachev
 */
public class TransferWindowJob<S extends Closeable, D extends Closeable> implements Job {

    public TransferWindowJob(final JobIdentity id, final TransferWindow<S, D> transferWindow) {
        this.id = id;
        this.transferWindow = transferWindow;
    }

    @Override
    public JobIdentity getId() {
        return id;
    }

    @Override
    public Class<? extends JobProducer> getJobProducerClass() {
        return TransferEntityJobProducer.class;
    }

    @Override
    public Job clone() {
        final TransferWindow<S, D> transferWindowClone = transferWindow; //TODO clone
        final TransferWindowJob<S, D> clone = new TransferWindowJob<>(id, transferWindowClone);
        clone.setContextName(contextName);
        return clone;
    }

    /**
     * @return TransferWindow
     */
    public TransferWindow<S, D> getTransferWindow() {
        return transferWindow;
    }

    /**
     * @return EtlContext name
     */
    public String getContextName() {
        return contextName;
    }

    /**
     * @param contextName
     *         for EtlContext
     */
    public void setContextName(String contextName) {
        this.contextName = contextName;
    }

    /**
     * @return the cached iterator created out of the TransferWindow.
     */
    public Iterator<Set<TransferFunctor<S, D>>> getCachedIter() {
        return cachedIter;
    }

    /**
     * @param iter
     *         the cached iterator created out of the TransferWindow.
     */
    public void setCachedIter(Iterator<Set<TransferFunctor<S, D>>> iter) {
        this.cachedIter = iter;
    }

    @Override
    public String toString() {
        if (toString == null) {
            toString = format("{}@{}({}, {}, {})", getClass().getSimpleName(), Integer.toHexString(hashCode()),
                    getId().toShortString(), getTransferWindow(), getContextName());
        }
        return toString;
    }

    private final JobIdentity id;
    private final TransferWindow<S, D> transferWindow;
    private String contextName;

    private transient Iterator<Set<TransferFunctor<S, D>>> cachedIter;
    private transient String toString;

    private static final long serialVersionUID = -6940502693464635114L;
}
