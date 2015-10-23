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
package com.persinity.haka;

import com.persinity.haka.impl.actor.HakaNode;

/**
 * Factory for creating HakaExecutor.
 *
 * @author Ivan Dachev
 */
public interface HakaExecutorFactory {
    /**
     * Creates a new HakaExecutor for remote haka node.
     *
     * @param hakaHost
     *         Haka host to connect to
     * @param hakaPort
     *         Haka port to connect to
     * @return HakaExecutor for desired Job
     */
    HakaExecutor newRemoteInstance(String hakaHost, int hakaPort);

    /**
     * Creates a new HakaExecutor for embedded haka node.
     *
     * @param hakaNode
     *         HakaNode to use as embedded
     * @return HakaExecutor for desired Job
     */
    HakaExecutor newEmbeddedInstance(HakaNode hakaNode);
}
