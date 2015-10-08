/**
 * Copyright (c) 2015 Persinity Inc.
 */
package com.persinity.ndt.controller.script;

import static com.persinity.common.StringUtils.format;
import static com.persinity.common.invariant.Invariant.notNull;

import java.util.Map;

import org.apache.log4j.Logger;

import com.persinity.common.logging.Log4jLogger;
import com.persinity.ndt.controller.NdtController;

/**
 * Implements step scheduling capabilities leaving the actual work to successors
 *
 * @author Doichin Yordanov
 */
public abstract class BaseStep implements Step {

    /**
     * @param prev
     *         Step or {@code null} if the first step
     * @param delaySecs
     *         from previous step
     * @param ctx
     *         context
     */
    public BaseStep(final Step prev, final int delaySecs, final Map<Object, Object> ctx) {
        notNull(ctx);

        this.prev = prev;
        this.delaySecs = delaySecs;
        this.ctx = ctx;
    }

    @Override
    public void sigStop() {
        log.debug("Sig stop {}", this);
        stopRequested = true;
    }

    @Override
    public boolean isStopRequested() {
        return stopRequested;
    }

    @Override
    public void run() {
        Thread.currentThread().setName(getClass().getSimpleName());

        if (!waitPreviousStep()) {
            return;
        }

        executeStep();
    }

    @Override
    public boolean isFinished() {
        return isFinished;
    }

    @Override
    public boolean isFailed() {
        return failedException != null;
    }

    @Override
    public RuntimeException getFailedException() {
        return failedException;
    }

    @Override
    public String toString() {
        if (toString == null) {
            toString = format("{} - {}", getClass().getSimpleName(), Thread.currentThread().getId());
        }
        return toString;
    }

    /**
     * @return Context
     */
    public Map<Object, Object> getCtx() {
        return ctx;
    }

    private boolean waitPreviousStep() {
        log.debug("Scheduling {}", this);
        if (prev != null) {
            while (!prev.isFinished() && !stopRequested) {
                try {
                    prev.waitToFinish(500);
                    if (Thread.currentThread().isInterrupted()) {
                        throw new RuntimeException(new InterruptedException());
                    }
                } catch (final RuntimeException e) {
                    log.error(e, "Wait on prev step: {} aborted, aborting {}", prev, this);
                    failedException = e;
                    markFinishAndNotifyAll();
                    throw failedException;
                }
            }

            if (prev.isFailed()) {
                final RuntimeException e = prev.getFailedException();
                // do not log exception here it must be already logged by the step
                log.error("Prev step: {} failed, aborting {}", prev, this);
                failedException = e instanceof PreviousStepFailedException ? e : new PreviousStepFailedException(e);
                markFinishAndNotifyAll();
                throw failedException;
            }
        }

        if (stopRequested) {
            log.debug("Stop requested aborting {}", this);
            markFinishAndNotifyAll();
            return false;
        } else {
            try {
                Thread.sleep(delaySecs * 1000);
            } catch (final InterruptedException e) {
                log.warn(e, "Interrupted on delay aborting {}", this);
                failedException = new RuntimeException(e);
                markFinishAndNotifyAll();
                throw failedException;
            }
        }

        return true;
    }

    private void executeStep() {
        log.debug("Running {}", this);
        isFinished = false;
        try {
            work();
            log.debug("Done {}", this);
        } catch (final RuntimeException e) {
            log.error(e, "Failed running {}", this);
            failedException = e;
            markFinishAndNotifyAll();
            throw e;
        }
        markFinishAndNotifyAll();
        log.debug("Exiting {}", this);
    }

    @Override
    public void waitToFinish(long timeoutMs) {
        synchronized (lock) {
            if (!isFinished()) {
                log.debug("Wait to finish: {}", this);
                try {
                    lock.wait(timeoutMs);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }

    /**
     * Define actual work here
     */
    protected abstract void work();

    /**
     * TODO move this out of BaseStep into strongly typed context.
     *
     * @return NdtController
     */
    protected final NdtController getController() {
        final NdtController ndtController = (NdtController) ctx.get(NdtController.class);
        assert ndtController != null;
        return ndtController;
    }

    private void markFinishAndNotifyAll() {
        synchronized (lock) {
            isFinished = true;
            log.debug("Notify all finish: {}", this);
            lock.notifyAll();
        }
    }

    private final Step prev;
    private final int delaySecs;
    private final Map<Object, Object> ctx;
    private final Object lock = new Object();

    private boolean stopRequested;
    private boolean isFinished;
    private RuntimeException failedException;
    private String toString;

    private static final Log4jLogger log = new Log4jLogger(Logger.getLogger(BaseStep.class));
}
