/**
 * Copyright (c) 2015 Persinity Inc.
 */
package com.persinity.ndt.controller.script;

import static com.persinity.common.invariant.Invariant.notNull;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import com.persinity.common.logging.Log4jLogger;

/**
 * Template for scripting composite workload comprised of multiple scheduled steps each running in own thread.
 *
 * @author Doichin Yordanov
 */
public class Script extends BaseStep {

    public Script(final Step prev, final int delaySecs, final Map<Object, Object> ctx) {
        super(prev, delaySecs, ctx);
        script = new LinkedList<>();
    }

    /**
     * @param step
     *         to add to the script
     */
    public void addStep(final Step step) {
        notNull(step);
        script.add(step);
    }

    @Override
    protected void work() {
        startAllSteps();
        waitAllStepsToComplete();
    }

    @Override
    public void sigStop() {
        super.sigStop();
        stopAllSteps();
    }

    private void startAllSteps() {
        log.debug("Start all steps {}", this);
        for (final Step step : script) {
            new ExceptionSilentThread(step).start();
        }
    }

    private void stopAllSteps() {
        log.debug("Stop all steps {}", this);
        for (final Step step : script) {
            try {
                step.sigStop();
            } catch (RuntimeException e) {
                log.error(e, "Failed to stop step: {}", step);
            }
        }
    }

    private void waitAllStepsToComplete() {
        log.debug("Wait all steps to complete {}", this);
        boolean completed = false;
        while (!completed) {
            completed = true;
            for (final Step step : script) {
                if (!step.isFinished()) {
                    completed = false;
                    break;
                }
            }

            for (final Step step : script) {
                if (step.isFailed()) {
                    final RuntimeException failedException = step.getFailedException();
                    // do not log exception here it must be already logged by the step
                    log.error("Found failed step {} in script {}", step, this);
                    stopAllSteps();
                    throw failedException;
                }
            }

            try {
                Thread.sleep(STEPS_COMPLETE_PULL_INTERVAL_MS);
            } catch (final InterruptedException e) {
                log.warn("Interrupted on sleep between steps {}", this);
                stopAllSteps();
                completed = true;
            }
        }
        log.debug("All steps completed {}", this);
    }

    private static class ExceptionSilentThread extends Thread {
        public ExceptionSilentThread(final Step step) {
            this.step = step;
        }

        @Override
        public void run() {
            try {
                step.run();
            } catch (RuntimeException e) {
                if (!(e instanceof PreviousStepFailedException)) {
                    log.error(e, "Script step run failed {}", step);
                    System.err.println(e.getMessage());
                }
            }
        }

        private final Step step;
    }

    private final List<Step> script;
    private static final long STEPS_COMPLETE_PULL_INTERVAL_MS = 200;

    private static final Log4jLogger log = new Log4jLogger(Logger.getLogger(Script.class));
}
