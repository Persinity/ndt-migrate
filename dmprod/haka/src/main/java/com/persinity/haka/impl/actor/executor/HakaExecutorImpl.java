/**
 * Copyright (c) 2015 Persinity Inc.
 */
package com.persinity.haka.impl.actor.executor;

import static com.persinity.common.invariant.Invariant.notNull;

import java.util.HashSet;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import akka.actor.ActorRef;
import akka.actor.ActorSelection;
import akka.actor.ActorSystem;
import akka.pattern.Patterns;
import akka.util.Timeout;
import com.persinity.haka.HakaExecutor;
import com.persinity.haka.Job;
import com.persinity.haka.impl.actor.ContextLoggingAdapter;
import com.persinity.haka.impl.actor.ContextLoggingProvider;
import com.persinity.haka.impl.actor.JobState;
import com.persinity.haka.impl.actor.execjob.ExecJobWorker;
import com.persinity.haka.impl.actor.message.NewMsg;
import com.persinity.haka.impl.actor.message.ProcessedMsg;
import scala.concurrent.Await;
import scala.concurrent.duration.Duration;
import scala.concurrent.duration.FiniteDuration;

/**
 * Implements HakaExecutor by using the ExecJobWorker.
 *
 * @author Ivan Dachev
 */
public abstract class HakaExecutorImpl implements HakaExecutor, ContextLoggingProvider {
    /**
     * @param system
     *         {@link ActorSystem} to init for
     * @param hakaAddress
     *         Address in case of cluster configuration or empty one
     */
    protected void init(final ActorSystem system, final String hakaAddress) {
        notNull(system);
        notNull(hakaAddress);

        if (this.system != null) {
            throw new IllegalStateException("HakaExecutor already initialized");
        }

        this.system = system;
        this.hakaAddress = hakaAddress;

        log = new ContextLoggingAdapter(system, this);

        HakaExecutorSettings settings = HakaExecutorSettings.Provider.SettingsProvider.get(system);

        acquirePool(this, settings.getJobPoolSize());
    }

    @Override
    public <T extends Job> Future<T> executeJob(final T job, final long timeoutMs) {
        if (pool == null) {
            throw new IllegalStateException("HakaExecutor is not initialized");
        }

        log.info("Schedule execute job: %s", JobState.systemInfoString(job));
        return pool.submit(new Callable<T>() {
            @Override
            public T call() throws Exception {
                final FiniteDuration timeout = Duration.create(timeoutMs, TimeUnit.MILLISECONDS);

                final ActorRef ref = resolveExecJobWorker(timeout);

                log.info("Executing job: %s from %s", JobState.systemInfoString(job), ref);

                final NewMsg msg = new NewMsg(job);
                final scala.concurrent.Future future = Patterns.ask(ref, msg, Timeout.durationToTimeout(timeout));

                final long timeStart = System.currentTimeMillis();

                @SuppressWarnings("unchecked")
                final ProcessedMsg result = Await.<ProcessedMsg>result(future, timeout);
                @SuppressWarnings("unchecked")
                final T resultJob = (T) result.getJob();

                final long timeEnd = System.currentTimeMillis();

                log.info("Done (time: %d ms) job: %s from %s", (timeEnd - timeStart),
                        JobState.systemInfoString(resultJob), ref);

                return resultJob;
            }
        });
    }

    @Override
    public void shutdown() {
        releasePool(this);
    }

    @Override
    public void appendContext(StringBuilder sb) {
    }

    private ActorRef resolveExecJobWorker(final FiniteDuration timeout) throws Exception {
        assert system != null;

        final String execJobWorkerName = ExecJobWorker.class.getSimpleName();
        final String path = String.format("%s/user/%s-*", hakaAddress, execJobWorkerName);

        log.debug("Searching for %s", path);

        final ActorSelection execJobWorker = system.actorSelection(path);

        final scala.concurrent.Future<ActorRef> actorRefFuture = execJobWorker.resolveOne(timeout);

        final ActorRef ref = Await.result(actorRefFuture, timeout);

        log.debug("Resolved: %s", ref);
        return ref;
    }

    private static synchronized void acquirePool(final HakaExecutorImpl executor, final int poolSize) {
        if (executors.contains(executor)) {
            throw new IllegalStateException("Pool is already acquired for executor: %s" + executor);
        }

        executors.add(executor);

        if (pool == null) {
            pool = Executors.newFixedThreadPool(poolSize);

            executor.log.info("Created new pool: %s size: %d", pool, poolSize);
        }
    }

    private static synchronized void releasePool(final HakaExecutorImpl executor) {
        executors.remove(executor);

        if (executors.size() == 0) {
            executor.log.info("Shutdown pool: %s", pool);

            if (pool != null) {
                pool.shutdown();
                pool = null;
            }
        }
    }

    private ActorSystem system;
    private String hakaAddress;
    private ContextLoggingAdapter log;

    private static ExecutorService pool;
    private static final HashSet<HakaExecutorImpl> executors = new HashSet<>();
}
