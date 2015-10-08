/**
 * Copyright (c) 2015 Persinity Inc.
 */
package com.persinity.ndt.controller;

import org.apache.log4j.Logger;

import com.persinity.common.logging.Log4jLogger;

/**
 * Used to safely cleanup/close NDT controller.
 *
 * @author Ivan Dachev
 */
public class NdtShutdownHook extends Thread {
    public NdtShutdownHook(final NdtController ndtController) {
        this.ndtController = ndtController;
    }

    @Override
    public void run() {
        if (ndtController != null) {
            log.debug("Processing shutdown");
            ndtController.ndtCleanupOnFailure("interrupt");
            ndtController.close();
            ndtController = null;
        }
    }

    private NdtController ndtController;

    private static final Log4jLogger log = new Log4jLogger(Logger.getLogger(NdtShutdownHook.class));
}
