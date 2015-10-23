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
package com.persinity.common;

import static com.persinity.common.IoUtils.silentClose;
import static com.persinity.common.invariant.Invariant.isNotEmpty;

import com.persinity.common.db.Closeable;

/**
 * Governs access to a resource which has underlying session/connection hence can throw checked exceptions and/or cause
 * resource leakage.
 *
 * @author Doichin Yordanov
 */
public class Resource {

    /**
     * Resource accessor functor.
     *
     * @author Doichin Yordanov
     */
    public abstract static class Accessor<X, Y> {
        protected Accessor() {
        }

        protected Accessor(final X resource, final String resourceDescr) {
            this.resource = resource;
            this.resourceDescr = resourceDescr;
        }

        protected X getResource() {
            return resource;
        }

        protected String getDescription() {
            return resourceDescr;
        }

        /**
         * Access the resource and do the job.
         *
         * @throws Exception
         */
        public abstract Y access(X resource) throws Exception;

        private String resourceDescr;
        private X resource;
    }

    /**
     * Accesses resource and on failure logs error and wraps the exception in {@link RuntimeException}.
     *
     * @param accessor
     * @throws RuntimeException
     *         If there was an issue during resource access.
     */
    public <X, Y> Y access(final Accessor<X, Y> accessor) throws RuntimeException {
        try {
            final X resource = accessor.getResource();
            return accessor.access(resource);
        } catch (final Throwable e) {
            final String msg = getExceptionDescriptionFor(accessor.getResource(), accessor.getDescription());
            throw new RuntimeException(msg, e);
        }
    }

    /**
     * Accesses {@link AutoCloseable} resource and tries to close it regardless of the access success or failure.
     *
     * @param accessor
     * @throws RuntimeException
     *         If there was an issue during resource access.
     */
    public <X extends AutoCloseable, Y> Y accessAndAutoClose(final Accessor<X, Y> accessor) throws RuntimeException {
        try (final X resource = accessor.getResource()) {
            return accessor.access(resource);
        } catch (final Throwable e) {
            final String msg = getExceptionDescriptionFor(accessor.getResource(), accessor.getDescription());
            throw new RuntimeException(msg, e);
        }
    }

    /**
     * Accesses {@link Closeable} resource and tries to close it regardless of the access success or failure.
     *
     * @param accessor
     * @throws RuntimeException
     *         If there was an issue during resource access.
     */
    public <X extends Closeable, Y> Y accessAndClose(final Accessor<X, Y> accessor) throws RuntimeException {
        X resource = null;
        try {
            resource = accessor.getResource();
            return accessor.access(resource);
        } catch (final Throwable e) {
            final String msg = getExceptionDescriptionFor(accessor.getResource(), accessor.getDescription());
            throw new RuntimeException(msg, e);
        } finally {
            silentClose(resource);
        }
    }

    public <X extends AutoCloseable> X close(final X resource) throws RuntimeException {
        try {
            resource.close();
        } catch (final Throwable e) {
            final String msg = getExceptionDescriptionFor(resource, null);
            throw new RuntimeException(msg, e);
        }
        return resource;
    }

    static String getExceptionDescriptionFor(final Object resource, final String description) {
        final String resourceDescription;
        if (isNotEmpty(description)) {
            resourceDescription = description;
        } else {
            resourceDescription = resource != null ? resource.toString() : "resource";
        }
        return StringUtils.format("Access failed to {}", resourceDescription);
    }
}
