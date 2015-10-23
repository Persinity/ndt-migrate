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
import static com.persinity.common.collection.GraphUtils.leveledTopologicalOrderIteratorOf;

import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import org.apache.log4j.Logger;

import com.persinity.common.db.Closeable;
import com.persinity.common.logging.Log4jLogger;
import com.persinity.haka.JobIdentity;
import com.persinity.haka.JobProducer;
import com.persinity.ndt.etlmodule.EtlPlanDag;
import com.persinity.ndt.etlmodule.EtlPlanGenerator;
import com.persinity.ndt.etlmodule.TransferFunctor;
import com.persinity.ndt.etlmodule.haka.context.ContextFactory;
import com.persinity.ndt.etlmodule.haka.context.ContextFactoryProvider;
import com.persinity.ndt.transform.TransferWindow;

/**
 * Turns target entity tree into an ETL plan.
 * <p/>
 * The ETL plan is a tree that reassembles the entity tree. Each node in the
 * plan tree is responsible for generating ETL instructions for a target entity.
 *
 * @author Ivan Dachev
 */
public class TransferEntityJobProducer<S extends Closeable, D extends Closeable>
        implements JobProducer<TransferWindowJob<S, D>, TransferEntityJob<S, D>> {
    @Override
    public Set<TransferEntityJob<S, D>> process(final TransferWindowJob<S, D> job) {
        log.debug("Processing {}", job);

        final TransferWindow<S, D> transferWindow = job.getTransferWindow();

        if (transferWindow.isEmpty()) {
            log.debug("Found empty window {}", transferWindow);
            return Collections.emptySet();
        }

        Iterator<Set<TransferFunctor<S, D>>> it = job.getCachedIter();

        final EtlPlanGenerator<S, D> etlPlanGenerator = getEtlPlanGenerator(job.getContextName());
        if (it == null) {
            log.debug("DataMotion of {}", transferWindow);
            final EtlPlanDag<S, D> eltEntityPlan = etlPlanGenerator.newEtlPlan(transferWindow);
            log.debug("ETL plan: {}", eltEntityPlan);
            it = leveledTopologicalOrderIteratorOf(eltEntityPlan);
            // it = getSingletonSetIterator(new TopologicalOrderIterator<>(eltEntityPlan)); use this iterator for debug only
            job.setCachedIter(it);
        }

        Set<TransferFunctor<S, D>> transferFunctors = new HashSet<>();
        while (transferFunctors.isEmpty() && it.hasNext()) {
            transferFunctors = filterNoOps(it.next(), etlPlanGenerator);
        }
        final Set<TransferEntityJob<S, D>> result = new HashSet<>();
        for (TransferFunctor<S, D> transferFunctor : transferFunctors) {
            final TransferEntityJob<S, D> transferEntityJob = new TransferEntityJob<>(new JobIdentity(job.getId()),
                    transferFunctor, transferWindow.getDataPoolBridge());
            result.add(transferEntityJob);
            log.debug("Created {}", transferEntityJob);
        }
        return result;

    }

    private static <S extends Closeable, D extends Closeable> Set<TransferFunctor<S, D>> filterNoOps(
            final Set<TransferFunctor<S, D>> functors, final EtlPlanGenerator<S, D> etlPlanGenerator) {

        final Set<TransferFunctor<S, D>> result = new HashSet<>();
        final Iterator<TransferFunctor<S, D>> it = functors.iterator();
        while (it.hasNext()) {
            TransferFunctor<S, D> functor = it.next();
            if (etlPlanGenerator.isNoOp(functor)) {
                log.debug("Skipping {}", functor);
            } else {
                result.add(functor);
            }
        }
        return result;
    }

    @Override
    public void processed(final TransferWindowJob<S, D> parentJob, final TransferEntityJob<S, D> childJob) {
    }

    private EtlPlanGenerator<S, D> getEtlPlanGenerator(final String contextName) {
        final ContextFactory factory = ContextFactoryProvider.getFactory();
        @SuppressWarnings("unchecked")
        EtlContext<S, D> etlContext = (EtlContext<S, D>) factory.getContext(contextName);
        if (etlContext == null) {
            throw new RuntimeException(format("Failed to find EtlContext for name: {}", contextName));
        }
        return etlContext.getEtlPlanGenerator();
    }

    private static final Log4jLogger log = new Log4jLogger(Logger.getLogger(TransferEntityJobProducer.class));
}
