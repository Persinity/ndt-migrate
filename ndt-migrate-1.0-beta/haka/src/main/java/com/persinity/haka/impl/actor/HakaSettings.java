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
package com.persinity.haka.impl.actor;

import static com.persinity.haka.impl.actor.SettingsHelper.ONE_SECOND;
import static com.persinity.haka.impl.actor.SettingsHelper.validateGreaterThen;
import static com.persinity.haka.impl.actor.SettingsHelper.validateIsPositive;
import static com.persinity.haka.impl.actor.SettingsHelper.validateIsPositiveOrZero;
import static com.persinity.haka.impl.actor.SettingsHelper.validateNotEmpty;

import java.util.List;
import java.util.concurrent.TimeUnit;

import akka.actor.AbstractExtensionId;
import akka.actor.ExtendedActorSystem;
import akka.actor.Extension;
import akka.actor.ExtensionIdProvider;
import com.typesafe.config.Config;
import scala.concurrent.duration.Duration;
import scala.concurrent.duration.FiniteDuration;

/**
 * Settings for haka impl.
 *
 * @author Ivan Dachev
 */
public class HakaSettings implements Extension {

    public String getPoolName() {
        return POOL_NAME;
    }

    public boolean isPoolImplCluster() {
        return POOL_IMPL_CLUSTER;
    }

    public int getWorkers() {
        return WORKERS;
    }

    public int getMaxJobsPerWorker() {
        return MAX_JOBS_PER_WORKER;
    }

    public FiniteDuration getMsgResendDelay() {
        return MSG_RESEND_DELAY;
    }

    public FiniteDuration getWatchdogPeriod() {
        return WATCHDOG_PERIOD;
    }

    public FiniteDuration getIdleJobPeriod() {
        return IDLEJOB_PERIOD;
    }

    public FiniteDuration getStatusUpdateTimeout() {
        return STATUS_UPDATE_TIMEOUT;
    }

    public boolean enablePersistence() {
        return ENABLE_PERSISTENCE;
    }

    public List<String> getMainJobsWorkers() {
        return MAIN_JOB_WORKERS;
    }

    protected HakaSettings(Config config) {
        POOL_NAME = config.getString("haka.pool-name");
        validateNotEmpty("haka.pool-name", getPoolName());

        POOL_IMPL_CLUSTER = config.getBoolean("haka.pool-impl-cluster");

        WORKERS = config.getInt("haka.workers");
        validateIsPositive("haka.workers", getWorkers());

        MAX_JOBS_PER_WORKER = config.getInt("haka.max-jobs-per-worker");
        validateIsPositiveOrZero("haka.max-jobs-per-worker", getMaxJobsPerWorker());

        MSG_RESEND_DELAY = Duration
                .create(config.getDuration("haka.msg-resend-delay", TimeUnit.MILLISECONDS), TimeUnit.MILLISECONDS);
        validateGreaterThen("haka.msg-resend-delays", getMsgResendDelay(), ONE_SECOND);

        WATCHDOG_PERIOD = Duration
                .create(config.getDuration("haka.watchdog-period", TimeUnit.MILLISECONDS), TimeUnit.MILLISECONDS);
        validateGreaterThen("haka.watchdog-period", getWatchdogPeriod(), ONE_SECOND);

        IDLEJOB_PERIOD = Duration
                .create(config.getDuration("haka.idlejob-period", TimeUnit.MILLISECONDS), TimeUnit.MILLISECONDS);
        validateGreaterThen("haka.idlejob-period", getWatchdogPeriod(), ONE_SECOND);

        STATUS_UPDATE_TIMEOUT = Duration
                .create(config.getDuration("haka.status-update-timeout", TimeUnit.MILLISECONDS), TimeUnit.MILLISECONDS);
        validateGreaterThen("haka.status-update-timeout", getStatusUpdateTimeout(), ONE_SECOND);

        ENABLE_PERSISTENCE = config.getBoolean("haka.enable-persistence");

        MAIN_JOB_WORKERS = config.getStringList("haka.main-job-workers");
        validateNotEmpty("haka.main-job-workers", getMainJobsWorkers());
    }

    static public class Provider extends AbstractExtensionId<HakaSettings> implements ExtensionIdProvider {
        public final static Provider SettingsProvider = new Provider();

        private Provider() {
        }

        public Provider lookup() {
            return Provider.SettingsProvider;
        }

        public HakaSettings createExtension(ExtendedActorSystem system) {
            return new HakaSettings(system.settings().config());
        }
    }

    private final String POOL_NAME;
    private final boolean POOL_IMPL_CLUSTER;
    private final int WORKERS;
    private final int MAX_JOBS_PER_WORKER;
    private final FiniteDuration MSG_RESEND_DELAY;
    private final FiniteDuration WATCHDOG_PERIOD;
    private final FiniteDuration IDLEJOB_PERIOD;
    private final FiniteDuration STATUS_UPDATE_TIMEOUT;
    private final boolean ENABLE_PERSISTENCE;
    private final List<String> MAIN_JOB_WORKERS;
}
