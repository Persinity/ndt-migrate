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
package com.persinity.common;

import static com.persinity.common.invariant.Invariant.assertArg;
import static com.persinity.common.invariant.Invariant.notNull;

import java.io.PrintStream;

/**
 * Implements simple intermediate progress bar.
 *
 * @author Ivan Dachev
 */
public class ConsoleProgressBar implements Runnable {
    public static final int DEFAULT_PROGRESS_TICK_SECONDS = 1;
    public static final int DEFAULT_PROGRESS_LENGTH = 16;

    public ConsoleProgressBar() {
        this(System.out);
    }

    /**
     * @param consoleOut
     *         used to print the progress
     */
    public ConsoleProgressBar(final PrintStream consoleOut) {
        this(consoleOut, DEFAULT_PROGRESS_TICK_SECONDS, DEFAULT_PROGRESS_LENGTH);
    }

    /**
     * @param progressTickSeconds
     *         progress tick in seconds
     * @param progressLength
     *         progress length on the console
     */
    public ConsoleProgressBar(final PrintStream consoleOut, final int progressTickSeconds, final int progressLength) {
        notNull(consoleOut);
        assertArg(progressTickSeconds > 0, "Expected positive progress tick seconds: {}", progressTickSeconds);
        assertArg(progressLength > 0, "Expected positive progress progress length: {}", progressLength);

        this.consoleOut = consoleOut;
        this.progressTickSeconds = progressTickSeconds;
        this.progressLength = progressLength;
    }

    /**
     * Call this to start progress.
     * If the progress is already started this method will do nothing.
     */
    public void start() {
        synchronized (lock) {
            if (running && thread != null && thread.isAlive()) {
                return;
            }

            running = false;
            thread = new Thread(this);
            thread.setDaemon(true);
            thread.start();

            while (!running) {
                try {
                    lock.wait(250);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }

    /**
     * Call this to stop progress.
     */
    public void stop() {
        synchronized (lock) {
            if (running) {
                notifyStop = true;
                lock.notifyAll();
            }
        }

        if (thread != null) {
            try {
                thread.join();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            thread = null;
        }
    }

    @Override
    public void run() {
        synchronized (lock) {
            running = true;
            notifyStop = false;
            lock.notifyAll();
        }

        final StringBuilder msg = new StringBuilder();
        boolean doJob = true;
        while (doJob) {
            for (int i = 0; i < progressLength; i++) {
                msg.setLength(0);
                for (int j = 0; j <= i; j++) {
                    msg.append('.');
                }
                for (int j = i; j < progressLength; j++) {
                    msg.append(' ');
                }
                msg.append('\r');

                consoleOut.print(msg);

                synchronized (lock) {
                    try {
                        lock.wait(progressTickSeconds * 1000L);
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                    if (notifyStop) {
                        doJob = false;
                        break;
                    }
                }
            }
        }

        synchronized (lock) {
            running = false;
            lock.notifyAll();
        }
    }

    private final PrintStream consoleOut;
    private final int progressTickSeconds;
    private final int progressLength;

    private Thread thread;
    private boolean running;
    private boolean notifyStop;
    private final Object lock = new Object();
}
