/**
 * Copyright (c) 2015 Persinity Inc.
 */
package com.persinity.common.invariant;

import static com.persinity.common.StringUtils.format;

import java.util.Arrays;
import java.util.Collection;
import java.util.Map;

/**
 * Enforces algorithm invariant over some argument.<BR>
 * Subclasses should overload the enforce method according the type of the checked arguments.
 * <p/>
 * TODO check the {@link com.google.common.base.Preconditions} there are similar methods
 *
 * @author Doichin Yordanov
 */
public abstract class Invariant {
    private final String[] argName;

    public Invariant(final String... argName) {
        assert argName != null && argName.length > 0;
        this.argName = argName;
    }

    /**
     * @param object
     *         evaluate is not null and raise IllegalArgumentException if fail
     * @param parameterName
     *         optional to use it in the msg of IllegalArgumentException
     */
    public static void notNull(final Object object, final String parameterName) {
        if (object == null) {
            throwNotNull(parameterName);
        }
    }

    /**
     * @see #notNull(Object, String)
     */
    public static void notNull(final Object object) {
        notNull(object, null);
    }

    /**
     * @param seq
     *         evaluate is not empty and raise IllegalArgumentException if fail
     * @param parameterName
     *         optional to use it in the msg of IllegalArgumentException
     */
    public static void notEmpty(final CharSequence seq, final String parameterName) {
        notNull(seq, parameterName);

        if (seq.length() <= 0) {
            throwNotEmpty(parameterName);
        }
    }

    /**
     * @param seq
     * @return true if the string is not empty
     */
    public static boolean isNotEmpty(final String seq) {
        return !(seq == null || seq.trim().isEmpty());
    }

    /**
     * @see #notEmpty(CharSequence, String)
     */
    public static void notEmpty(final CharSequence seq) {
        notEmpty(seq, null);
    }

    /**
     * @param collection
     *         evaluate is not empty and raise IllegalArgumentException if fail
     * @param parameterName
     *         optional to use it in the msg of IllegalArgumentException
     */
    public static void notEmpty(final Collection collection, final String parameterName) {
        notNull(collection, parameterName);

        if (collection.isEmpty()) {
            throwNotEmpty(parameterName);
        }
    }

    /**
     * @see #notEmpty(Collection, String)
     */
    public static void notEmpty(final Collection collection) {
        notEmpty(collection, null);
    }

    /**
     * @param map
     *         evaluate is not empty and raise IllegalArgumentException if fail
     * @param parameterName
     *         optional to use it in the msg of IllegalArgumentException
     */
    public static void notEmpty(final Map map, final String parameterName) {
        notNull(map, parameterName);

        if (map.isEmpty()) {
            throwNotEmpty(parameterName);
        }
    }

    /**
     * @see #notEmpty(Map, String)
     */
    public static void notEmpty(final Map map) {
        notEmpty(map, null);
    }

    /**
     * Evaluates argument expression and on {@code false} raises
     * {@link IllegalArgumentException} with the supplied message
     *
     * @param expression
     *         to evaluate
     * @param msgFormat
     *         optional format msg for the {@link IllegalArgumentException}
     * @param args
     *         used for msgFormat
     * @throws IllegalArgumentException
     */
    public static void assertArg(final boolean expression, final String msgFormat, final Object... args)
            throws IllegalArgumentException {
        if (!expression) {
            if (msgFormat != null) {
                throw new IllegalArgumentException(format(msgFormat, args));
            } else {
                throw new IllegalArgumentException();
            }
        }
    }

    /**
     * Evaluates argument expression and on {@code false} raises
     * {@link IllegalArgumentException} with the supplied message
     *
     * @param expression
     *         to evaluate
     * @param msg
     * @throws IllegalArgumentException
     */
    public static void assertArg(final boolean expression, final String msg) throws IllegalArgumentException {
        if (!expression) {
            if (msg != null) {
                throw new IllegalArgumentException(msg);
            } else {
                throw new IllegalArgumentException();
            }
        }
    }

    /**
     * @see #assertArg(boolean, String)
     */
    public static void assertArg(final boolean expression) throws IllegalArgumentException {
        assertArg(expression, null);
    }

    /**
     * Evaluates expression and on {@code false} raises
     * {@link IllegalStateException} with the supplied message
     *
     * @param expression
     *         to evaluate
     * @param msgFormat
     *         optional format msg for the {@link IllegalStateException}
     * @param args
     *         used for msgFormat
     * @throws IllegalStateException
     */
    public static void assertState(final boolean expression, final String msgFormat, final Object... args)
            throws IllegalStateException {
        if (!expression) {
            if (msgFormat != null) {
                throw new IllegalStateException(format(msgFormat, args));
            } else {
                throw new IllegalStateException();
            }
        }
    }

    /**
     * Evaluates argument expression and on {@code false} raises
     * {@link IllegalStateException} with the supplied message
     *
     * @param expression
     *         to evaluate
     * @param msg
     * @throws IllegalStateException
     */
    public static void assertState(final boolean expression, final String msg) throws IllegalStateException {
        if (!expression) {
            if (msg != null) {
                throw new IllegalStateException(msg);
            } else {
                throw new IllegalStateException();
            }
        }
    }

    /**
     * @see #assertState(boolean, String)
     */
    public static void assertState(final boolean expression) throws IllegalStateException {
        assertState(expression, null);
    }

    /**
     * @param arg
     *         Not null args
     * @throws RuntimeException
     *         if the invariant does not hold
     */
    public void enforce(final Object... arg) throws RuntimeException {
        throw new IllegalArgumentException(arg + " is not supported!");
    }

    protected final String[] getArgName() {
        return Arrays.copyOf(argName, argName.length);
    }

    @Override
    public final String toString() {
        return format("{} {}", this.getClass().getSimpleName(), argName);
    }

    private static void throwNotNull(final String parameterName) {
        final String msg = parameterName == null || parameterName.length() == 0 ?
                "Expected not null" :
                format("Expected not null: {}", parameterName);

        throw new NullPointerException(msg);
    }

    private static void throwNotEmpty(final String parameterName) {
        final String msg = parameterName == null || parameterName.length() == 0 ?
                "Expected not empty" :
                format("Expected not empty: {}", parameterName);

        throw new IllegalArgumentException(msg);
    }
}
