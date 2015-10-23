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

import java.util.HashSet;
import java.util.concurrent.TimeUnit;

import akka.actor.ActorSystem;
import com.persinity.common.invariant.NotNull;
import com.persinity.haka.impl.actor.HakaNode;
import com.typesafe.config.ConfigFactory;
import scala.concurrent.duration.Duration;

/**
 * Implements Remote HakaExecutor connecting to haka host:port;
 *
 * @author Ivan Dachev
 */
public class RemoteHakaExecutorImpl extends HakaExecutorImpl {
    public RemoteHakaExecutorImpl(final String hakaHost, final int hakaPort) {
        new NotNull("hakaHost").enforce(hakaHost);

        final String hakaAddress = String.format("akka.tcp://%s@%s:%d", HakaNode.ACTOR_SYSTEM_NAME, hakaHost, hakaPort);

        try {
            init(acquireSystem(this), hakaAddress);
        } catch (final RuntimeException e) {
            try {
                shutdown();
            } catch (RuntimeException ignored) {
            }
            throw e;
        }
    }

    @Override
    public void shutdown() {
        super.shutdown();

        releaseSystem(this);
    }

    private static synchronized ActorSystem acquireSystem(final RemoteHakaExecutorImpl executor) {
        if (executors.contains(executor)) {
            throw new IllegalStateException("System is already acquired for executor: %s" + executor);
        }

        executors.add(executor);

        if (system == null) {
            system = ActorSystem.create(CLIENT_ACTOR_SYSTEM_NAME, ConfigFactory.load(config));
        }

        return system;
    }

    private static synchronized void releaseSystem(final RemoteHakaExecutorImpl executor) {
        executors.remove(executor);

        if (executors.size() == 0) {
            system.shutdown();
            system.awaitTermination(SYSTEM_AWAIT_TERMINATION_TIMEOUT_SECONDS);
            system = null;
        }
    }

    public static void setConfig(final String newConfig) {
        new NotNull("newConfig").enforce(newConfig);

        config = newConfig;
    }

    private static final String CLIENT_ACTOR_SYSTEM_NAME = "haka-client";
    private static final String ENV_HAKA_CLIENT_CONFIG_KEY = "haka.client.config";
    private static final String DEFAULT_HAKA_CLIENT_CONFIG = "haka-client.conf";

    private static ActorSystem system;
    private static String config = System.getProperty(ENV_HAKA_CLIENT_CONFIG_KEY, DEFAULT_HAKA_CLIENT_CONFIG);
    private static final Duration SYSTEM_AWAIT_TERMINATION_TIMEOUT_SECONDS = Duration.create(60, TimeUnit.SECONDS);
    private static final HashSet<RemoteHakaExecutorImpl> executors = new HashSet<>();
}
