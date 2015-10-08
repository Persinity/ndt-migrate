/**
 * Copyright (c) 2015 Persinity Inc.
 */
package com.persinity.ndt.etlmodule.haka.context;

/**
 * TODO move to haka
 *
 * @author Ivan Dachev
 */
public interface ContextFactory {
    /**
     * @param name
     *         of the context
     * @return EtlContext for given name
     */
    Object getContext(String name);

    /**
     * @param name
     *         of the context to set
     * @param context
     *         to set
     */
    void setContext(String name, Object context);
}
