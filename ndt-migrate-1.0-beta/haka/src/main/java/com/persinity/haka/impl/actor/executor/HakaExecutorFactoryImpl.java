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
package com.persinity.haka.impl.actor.executor;

import com.persinity.haka.HakaExecutor;
import com.persinity.haka.HakaExecutorFactory;
import com.persinity.haka.impl.actor.HakaNode;

/**
 * Factory for creating HakaExecutor.
 *
 * @author Ivan Dachev
 */
public class HakaExecutorFactoryImpl implements HakaExecutorFactory {
    /**
     * @param config
     *         new config to be used by remote HakaExecutor impl
     */
    public void setRemoteHakaConfig(final String config) {
        RemoteHakaExecutorImpl.setConfig(config);
    }

    @Override
    public HakaExecutor newRemoteInstance(final String hakaHost, final int hakaPort) {
        return new RemoteHakaExecutorImpl(hakaHost, hakaPort);
    }

    @Override
    public HakaExecutor newEmbeddedInstance(final HakaNode hakaNode) {
        return new EmbeddedHakaExecutorImpl(hakaNode);
    }
}
