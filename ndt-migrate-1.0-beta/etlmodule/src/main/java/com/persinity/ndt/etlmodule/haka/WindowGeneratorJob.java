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
package com.persinity.ndt.etlmodule.haka;

import static com.persinity.common.StringUtils.format;

import java.util.Iterator;

import com.persinity.common.db.Closeable;
import com.persinity.haka.Job;
import com.persinity.haka.JobIdentity;
import com.persinity.haka.JobProducer;
import com.persinity.ndt.etlmodule.WindowGenerator;
import com.persinity.ndt.transform.TransferWindow;

/**
 * Represents the task of producing a transfer window.
 *
 * @author Ivan Dachev
 */
public class WindowGeneratorJob<S extends Closeable, D extends Closeable> implements Job {

    /**
     * @param windowGenerator
     *         {@link WindowGenerator}
     * @param windowCheckIntervalSeconds
     *         interval to check for a new window presence
     */
    public WindowGeneratorJob(final WindowGenerator<S, D> windowGenerator, final int windowCheckIntervalSeconds) {
        this(new JobIdentity(), windowGenerator, windowCheckIntervalSeconds);
    }

    private WindowGeneratorJob(final JobIdentity id, final WindowGenerator<S, D> windowGenerator,
            final int windowCheckIntervalSeconds) {
        this.id = id;
        this.windowGenerator = windowGenerator;
        this.windowCheckIntervalSeconds = windowCheckIntervalSeconds;
        this.contextName = id.id.toString();
    }

    @Override
    public JobIdentity getId() {
        return id;
    }

    @Override
    public Class<? extends JobProducer> getJobProducerClass() {
        return TransferWindowJobProducer.class;
    }

    @Override
    public Job clone() {
        final WindowGenerator<S, D> windowGeneratorClone = windowGenerator; //TODO clone
        final WindowGeneratorJob<S, D> clone = new WindowGeneratorJob<>(id, windowGeneratorClone,
                windowCheckIntervalSeconds);
        return clone;
    }

    /**
     * @return TransferWindow generator.
     */
    public WindowGenerator<S, D> getWindowGenerator() {
        return windowGenerator;
    }

    /**
     * @return Interval to check for new window in seconds
     */
    public int getWindowCheckIntervalSeconds() {
        return windowCheckIntervalSeconds;
    }

    /**
     * @return EtlContext name
     */
    public String getContextName() {
        return contextName;
    }

    /**
     * @return the cached iterator created out of the WindowGenerator.
     */
    public Iterator<TransferWindow<S, D>> getCachedIter() {
        return cachedIter;
    }

    /**
     * @param iter
     *         the cached iterator created out of the WindowGenerator.
     */
    public void setCachedIter(Iterator<TransferWindow<S, D>> iter) {
        this.cachedIter = iter;
    }

    @Override
    public String toString() {
        if (toString == null) {
            toString = format("{}@{}({}, {}, {}, {})", getClass().getSimpleName(), Integer.toHexString(hashCode()),
                    getId().toShortString(), getWindowGenerator(), getWindowCheckIntervalSeconds(), getContextName());
        }
        return toString;
    }

    private final JobIdentity id;
    private final WindowGenerator<S, D> windowGenerator;
    private final int windowCheckIntervalSeconds;
    private final String contextName;

    private transient Iterator<TransferWindow<S, D>> cachedIter;
    private transient String toString;

    private static final long serialVersionUID = -7547602195103029508L;
}
