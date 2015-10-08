/**
 * Copyright (c) 2015 Persinity Inc.
 */
package com.persinity.common.fp;

import static com.persinity.common.invariant.Invariant.notNull;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import com.google.common.base.Function;
import com.google.common.base.Stopwatch;
import com.persinity.common.collection.DirectedEdge;

/**
 * @author Ivan Dachev
 */
public class FunctionUtil {

    /**
     * @param f
     *         function to execute
     * @return Captured system out
     */
    public static String executeAndCaptureSysOut(final Function<Void, Void> f) {
        return executeAndCaptureSysOut(f, null).dst();
    }

    /**
     * @param f
     *         function to execute
     * @param arg
     *         argument to the function
     * @return Function result, Captured system out
     */
    public static <T, E> DirectedEdge<E, String> executeAndCaptureSysOut(final Function<T, E> f, final T arg) {
        notNull(f);

        final ByteArrayOutputStream baos = new ByteArrayOutputStream();
        final PrintStream pout = new PrintStream(baos);
        final PrintStream orgOut = System.out;
        System.setOut(pout);

        String resOut;
        E res;
        try {
            res = f.apply(arg);
        } finally {
            System.setOut(orgOut);
            pout.flush();
            resOut = baos.toString();
        }

        return new DirectedEdge<>(res, resOut);
    }

    /**
     * @param f
     *         function to execute
     * @return the time represent of the execution.
     */
    public static Stopwatch timeOf(final Function<Void, Void> f) {
        return timeOf(f, null).dst();
    }

    /**
     * @param f
     *         funciton to execute
     * @param arg
     *         argument to the function
     * @return Function Result, Stopwatch
     */
    public static <T, E> DirectedEdge<E, Stopwatch> timeOf(final Function<T, E> f, final T arg) {
        notNull(f);

        Stopwatch stopwatch = Stopwatch.createStarted();
        E res;
        try {
            res = f.apply(arg);
        } finally {
            stopwatch.stop();
        }
        return new DirectedEdge<>(res, stopwatch);
    }
}
