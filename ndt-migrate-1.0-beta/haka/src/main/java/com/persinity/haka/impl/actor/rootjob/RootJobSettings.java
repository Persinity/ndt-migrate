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
package com.persinity.haka.impl.actor.rootjob;

import static com.persinity.haka.impl.actor.SettingsHelper.ONE_SECOND;
import static com.persinity.haka.impl.actor.SettingsHelper.validateGreaterThen;
import static com.persinity.haka.impl.actor.SettingsHelper.validateIsPositiveOrZero;
import static com.persinity.haka.impl.actor.SettingsHelper.validateNotEmpty;

import java.util.concurrent.TimeUnit;

import akka.actor.AbstractExtensionId;
import akka.actor.ExtendedActorSystem;
import akka.actor.Extension;
import akka.actor.ExtensionIdProvider;
import com.typesafe.config.Config;
import scala.concurrent.duration.Duration;
import scala.concurrent.duration.FiniteDuration;

/**
 * Settings for root job logic.
 *
 * @author Ivan Dachev
 */
public class RootJobSettings implements Extension {
    public String getConfigSection() {
        return CONFIG_SECTION;
    }

    public int getMinClusterUpMembersBeforeFire() {
        return minClusterUpMembersBeforeFire;
    }

    public FiniteDuration getClusterUpMembersCheckPeriod() {
        return clusterUpMembersCheckPeriod;
    }

    public String getJobClass() {
        return jobClass;
    }

    public FiniteDuration getJobTimeout() {
        return jobTimeout;
    }

    protected RootJobSettings(Config config) {
        minClusterUpMembersBeforeFire = config.getInt(getConfigSection() + ".min-cluster-up-members-before-fire");
        validateIsPositiveOrZero(getConfigSection() + ".min-cluster-up-members-before-fire",
                getMinClusterUpMembersBeforeFire());

        clusterUpMembersCheckPeriod = Duration
                .create(config.getDuration(getConfigSection() + ".cluster-up-members-check-period",
                                TimeUnit.MILLISECONDS), TimeUnit.MILLISECONDS);
        validateGreaterThen(getConfigSection() + ".cluster-up-members-check-period", getClusterUpMembersCheckPeriod(),
                ONE_SECOND);

        jobClass = config.getString(getConfigSection() + ".job-class");
        validateNotEmpty(getConfigSection() + ".job-class", getJobClass());

        jobTimeout = Duration.create(config.getDuration(getConfigSection() + ".job-timeout", TimeUnit.MILLISECONDS),
                TimeUnit.MILLISECONDS);
        validateGreaterThen(getConfigSection() + ".job-timeout", getJobTimeout(), ONE_SECOND);
    }

    static public class Provider extends AbstractExtensionId<RootJobSettings> implements ExtensionIdProvider {
        public final static Provider SettingsProvider = new Provider();

        private Provider() {
        }

        public Provider lookup() {
            return Provider.SettingsProvider;
        }

        public RootJobSettings createExtension(ExtendedActorSystem system) {
            return new RootJobSettings(system.settings().config());
        }
    }

    private static final String CONFIG_SECTION = "haka.root-job-worker";

    private final int minClusterUpMembersBeforeFire;
    private final FiniteDuration clusterUpMembersCheckPeriod;
    private final String jobClass;
    private final FiniteDuration jobTimeout;
}
