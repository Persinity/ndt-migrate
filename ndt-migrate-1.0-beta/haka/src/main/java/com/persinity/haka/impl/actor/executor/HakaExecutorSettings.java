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

import static com.persinity.haka.impl.actor.SettingsHelper.validateIsPositive;

import akka.actor.AbstractExtensionId;
import akka.actor.ExtendedActorSystem;
import akka.actor.Extension;
import akka.actor.ExtensionIdProvider;
import com.typesafe.config.Config;

/**
 * Settings for exec job logic.
 *
 * @author Ivan Dachev
 */
public class HakaExecutorSettings implements Extension {
    public int getJobPoolSize() {
        return jobPoolSize;
    }

    protected HakaExecutorSettings(final Config config) {
        jobPoolSize = config.getInt(CONFIG_SECTION + ".job-pool-size");
        validateIsPositive(CONFIG_SECTION + ".job-pool-size", jobPoolSize);
    }

    static public class Provider extends AbstractExtensionId<HakaExecutorSettings> implements ExtensionIdProvider {
        public final static Provider SettingsProvider = new Provider();

        private Provider() {
        }

        @Override
        public Provider lookup() {
            return Provider.SettingsProvider;
        }

        @Override
        public HakaExecutorSettings createExtension(final ExtendedActorSystem system) {
            return new HakaExecutorSettings(system.settings().config());
        }
    }

    private static final String CONFIG_SECTION = "haka.executor";

    private final int jobPoolSize;
}
