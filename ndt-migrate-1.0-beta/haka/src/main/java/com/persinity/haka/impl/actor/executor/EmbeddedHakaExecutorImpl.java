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

import akka.actor.ActorSystem;
import com.persinity.haka.impl.actor.HakaNode;

/**
 * Implements embedded HakaExecutor..
 *
 * @author Ivan Dachev
 */
public class EmbeddedHakaExecutorImpl extends HakaExecutorImpl {
    public EmbeddedHakaExecutorImpl(final HakaNode hakaNode) {
        final String hakaAddress = "";

        final ActorSystem system = hakaNode.getActorSystem();

        try {
            init(system, hakaAddress);
        } catch (final RuntimeException e) {
            try {
                shutdown();
            } catch (RuntimeException ignored) {
            }
            throw e;
        }
    }
}
