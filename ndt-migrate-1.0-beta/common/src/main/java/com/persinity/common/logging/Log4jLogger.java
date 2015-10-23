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
package com.persinity.common.logging;

import org.apache.log4j.Logger;

import com.persinity.common.StringUtils;

/**
 * Log4j wrapper to support {@link StringUtils#format} and context prefix logging.
 *
 * @author Ivan Dachev
 */
public class Log4jLogger {
    public Log4jLogger(Logger log) {
        this(log, null);
    }

    public Log4jLogger(Logger log, ContextProvider context) {
        this.context = context;
        this.log = log;
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
        log.error(buildMsg(format, args));
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
        log.error(buildMsg(format, args), cause);
    }

    /**
     * @param format
     *         for warning msg
     * @param args
     *         for warning msg
     */
    public void warning(String format, Object... args) {
        log.warn(buildMsg(format, args));
    }

    /**
     * @param cause
     *         cause of the warning
     * @param format
     *         for warning msg
     * @param args
     *         for warning msg
     */
    public void warning(Throwable cause, String format, Object... args) {
        log.warn(buildMsg(format, args), cause);
    }

    /**
     * @param format
     *         for warning msg
     * @param args
     *         for warning msg
     */
    public void warn(String format, Object... args) {
        log.warn(buildMsg(format, args));
    }

    /**
     * @param cause
     *         cause of the warning
     * @param format
     *         for warning msg
     * @param args
     *         for warning msg
     */
    public void warn(Throwable cause, String format, Object... args) {
        log.warn(buildMsg(format, args), cause);
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
     * @param cause
     *         cause of the debug
     * @param format
     *         for debug msg
     * @param args
     *         for debug msg
     */
    public void debug(Throwable cause, String format, Object... args) {
        if (log.isDebugEnabled()) {
            log.debug(buildMsg(format, args), cause);
        }
    }

    /**
     * @return {@link Logger}
     */
    public Logger getLogger() {
        return log;
    }

    /**
     * @param format
     *         for the msg
     * @param args
     *         for the msg
     * @return msg with context
     */
    private String buildMsg(String format, Object... args) {
        if (context == null) {
            return StringUtils.format(format, args);
        }

        StringBuilder sb = new StringBuilder();
        context.appendContext(sb);
        sb.append(StringUtils.format(format, args));
        return sb.toString();
    }

    private final ContextProvider context;
    private final Logger log;
}
