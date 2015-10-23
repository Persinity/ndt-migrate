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
