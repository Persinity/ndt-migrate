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
