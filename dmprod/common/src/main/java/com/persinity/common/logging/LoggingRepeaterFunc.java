/**
 * Copyright (c) 2015 Persinity Inc.
 */
package com.persinity.common.logging;

import static com.persinity.common.invariant.Invariant.notNull;

import com.google.common.base.Function;

/**
 * RepeaterFunction to log on apply.
 *
 * @author Ivan Dachev
 */
public class LoggingRepeaterFunc<T> implements Function<T, T> {
    public LoggingRepeaterFunc(final Log4jLogger log, final String msg, final String... args) {
        notNull(log);
        notNull(msg);
        notNull(args);

        this.log = log;
        this.msg = msg;
        this.args = args;
    }

    @Override
    public T apply(final T t) {
        log.info(msg, (Object[]) args);
        return t;
    }

    private final Log4jLogger log;
    private final String msg;
    private final String[] args;
}
