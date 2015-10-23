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
