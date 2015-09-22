/**
 * Copyright (c) 2015 Persinity Inc.
 */
package com.persinity.ndt.controller.script;

/**
 * @author Ivan Dachev
 */
public class PreviousStepFailedException extends RuntimeException {
    public PreviousStepFailedException(final RuntimeException cause) {
        super(cause.getMessage(), cause.getCause() != null ? cause.getCause() : cause);
    }
}
