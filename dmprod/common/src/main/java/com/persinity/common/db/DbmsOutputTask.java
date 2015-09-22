/**
 * Copyright (c) 2015 Persinity Inc.
 */
package com.persinity.common.db;

import static com.persinity.common.StringUtils.format;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.concurrent.atomic.AtomicBoolean;

import org.apache.log4j.Logger;

import com.persinity.common.invariant.Invariant;

/**
 * Simple task to flush DBMS output to log periodically.
 *
 * @author Ivan Dachev
 */
public class DbmsOutputTask {

    /**
     * Create and start DBMS output task.
     *
     * @param conn
     * @param pullIntervalMs
     *         the poll interval for the task
     */
    public synchronized void startDbmsOutputTask(final Connection conn, final long pullIntervalMs) {
        if (dbmsOutputTaskThread != null && dbmsOutputTaskThread.isAlive()) {
            throw new IllegalStateException("The task must be stopped before make a new start");
        }
        createDbmsOutput(conn);
        enableDbmsOutput();
        dbmsOutputTaskThread = new DbmsOutputTaskThread(pullIntervalMs);
        dbmsOutputTaskThread.start();
    }

    /**
     * Stop DBMS output task.
     */
    public synchronized void stopDbmsOutputTask() {
        synchronized (dbmsOutputTaskThread) {
            if (dbmsOutputTaskThread != null && dbmsOutputTaskThread.isAlive()) {
                dbmsOutputTaskThread.doStop();
            }
            closeDbmsOutput();
        }
        dbmsOutputTaskThread = null;
    }

    class DbmsOutputTaskThread extends Thread {
        public DbmsOutputTaskThread(final long pullIntervalMs) {
            Invariant.assertArg(pullIntervalMs > 0, "Pull interval should be positive");
            this.pullIntervalMs = pullIntervalMs;
        }

        public void doStop() {
            doStop.set(true);
        }

        @Override
        public void run() {
            log.debug(format("{} started with pull interval: {} ms", this, pullIntervalMs));
            while (!doStop.get()) {
                logDbmsOutput();
                try {
                    Thread.sleep(pullIntervalMs);
                } catch (final InterruptedException e) {
                    log.debug(format("{} interrupted.", this));
                    break;
                }
            }
            log.debug(format("{} stopped.", this));
        }

        private final long pullIntervalMs;
        private final AtomicBoolean doStop = new AtomicBoolean(false);
    }

    private void createDbmsOutput(final Connection conn) {
        closeDbmsOutput();
        try {
            dbmsOutput = new DbmsOutput(conn);
        } catch (final SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private void enableDbmsOutput() {
        final DbmsOutput _dbmsOutput = dbmsOutput;
        if (_dbmsOutput != null) {
            try {
                _dbmsOutput.enable(100000);
            } catch (final SQLException e) {
                log.warn("Failed to enable DBMS output", e);
            }
        }
    }

    private void logDbmsOutput() {
        synchronized (dbmsOutputTaskThread) {
            if (dbmsOutput != null) {
                try {
                    dbmsOutput.show();
                } catch (final SQLException e) {
                    log.warn("Failed to log DBMS output", e);
                }
            }
        }
    }

    private void closeDbmsOutput() {
        if (dbmsOutput != null) {
            try {
                dbmsOutput.close();
            } catch (final SQLException e) {
                log.error("Failed to close DBMS", e);
                throw new RuntimeException(e);
            }
        }
    }

    private DbmsOutput dbmsOutput;
    private DbmsOutputTaskThread dbmsOutputTaskThread;
    private static final Logger log = Logger.getLogger(DbmsOutputTask.class);
}
