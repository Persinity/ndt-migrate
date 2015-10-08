/**
 * Copyright (c) 2015 Persinity Inc.
 */
package com.persinity.ndt.etlmodule.haka.context;

import java.util.HashMap;
import java.util.Map;

import com.persinity.common.invariant.NotEmpty;
import com.persinity.common.invariant.NotNull;

/**
 * Local EtlContextFactory implemented with HashMap.
 * <p/>
 * TODO not suitable for cross jvm use
 *
 * @author Ivan Dachev
 */
public class LocalContextFactory implements ContextFactory {

    public LocalContextFactory() {
        contextMap = new HashMap<>();
    }

    @Override
    public synchronized Object getContext(String name) {
        new NotEmpty("name").enforce(name);
        return contextMap.get(name);
    }

    @Override
    public synchronized void setContext(String name, Object context) {
        new NotEmpty("name").enforce(name);
        new NotNull("context").enforce(context);
        contextMap.put(name, context);
    }

    private Map<String, Object> contextMap;
}
