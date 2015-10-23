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
package com.persinity.haka.impl.actor;

import akka.actor.ActorSystem;
import akka.event.Logging;
import akka.event.LoggingAdapter;

/**
 * Util to add as prefix a context info to each log.
 *
 * @author Ivan Dachev
 */
public class ContextLoggingAdapter {
    public ContextLoggingAdapter(ActorSystem system, ContextLoggingProvider context) {
        this.context = context;
        log = Logging.getLogger(system, context);
    }

    /**
     * @param format
     *         for info msg
     * @param args
     *         for info msg
     */
    public void info(String format, Object... args) {
        if (log.isInfoEnabled()) {
            log.info(buildMsg(format, args));
        }
    }

    /**
     * @param format
     *         for error msg
     * @param args
     *         for error msg
     */
    public void error(String format, Object... args) {
        if (log.isErrorEnabled()) {
            log.error(buildMsg(format, args));
        }
    }

    /**
     * @param cause
     *         cause of the error
     * @param format
     *         for error msg
     * @param args
     *         for error msg
     */
    public void error(Throwable cause, String format, Object... args) {
        if (log.isErrorEnabled()) {
            log.error(cause, buildMsg(format, args));
        }
    }

    /**
     * @param format
     *         for warning msg
     * @param args
     *         for warning msg
     */
    public void warning(String format, Object... args) {
        if (log.isWarningEnabled()) {
            log.warning(buildMsg(format, args));
        }
    }

    /**
     * @param format
     *         for debug msg
     * @param args
     *         for debug msg
     */
    public void debug(String format, Object... args) {
        if (log.isDebugEnabled()) {
            log.debug(buildMsg(format, args));
        }
    }

    /**
     * @param format
     *         for the msg
     * @param args
     *         for the msg
     * @return msg with context
     */
    private String buildMsg(String format, Object... args) {
        StringBuilder sb = new StringBuilder();
        context.appendContext(sb);
        if (args.length > 0) {
            // TODO optimize to not use String format bad performance
            sb.append(String.format(format, args));
        } else {
            sb.append(format);
        }
        return sb.toString();
    }

    private final ContextLoggingProvider context;
    private final LoggingAdapter log;
}
