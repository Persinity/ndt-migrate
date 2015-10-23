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
package com.persinity.haka.impl.actor.pool;

import akka.actor.ActorRef;
import scala.concurrent.duration.FiniteDuration;

/**
 * Abstracting the pool of workers.
 *
 * @author Ivan Dachev
 */
public interface WorkerPool {
    /**
     * Used to send a message to the pool.
     *
     * @param msg
     * @param sender
     */
    void tell(Object msg, ActorRef sender);

    /**
     * Used to send a message to the pool.
     *
     * @param msg
     * @param sender
     * @param localAffinity
     */
    void tell(Object msg, ActorRef sender, boolean localAffinity);

    /**
     * Add new worker to pool.
     *
     * @param worker
     */
    void add(ActorRef worker);

    /**
     * Should destroy itself.
     */
    void destroy();

    /**
     * Schedule tell of message after given delay.
     *
     * @param msg
     * @param sender
     * @param delay
     */
    void schedule(Object msg, ActorRef sender, FiniteDuration delay);

    /**
     * Schedule tell of message after given delay.
     *
     * @param msg
     * @param sender
     * @param localAffinity
     * @param delay
     */
    void schedule(Object msg, ActorRef sender, boolean localAffinity, FiniteDuration delay);
}
