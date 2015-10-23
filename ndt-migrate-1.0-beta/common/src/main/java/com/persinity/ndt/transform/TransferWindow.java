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
package com.persinity.ndt.transform;

import java.util.List;
import java.util.Set;

import com.persinity.common.collection.DirectedEdge;
import com.persinity.common.collection.Pool;
import com.persinity.common.db.Closeable;
import com.persinity.ndt.db.TransactionId;

/**
 * Defines slice of changed data to be transferred to a set of target entities.
 *
 * @author Doichin Yordanov
 */
public interface TransferWindow<S extends Closeable, D extends Closeable> {
    /**
     * @return Source and Destination end-points for the transfer.
     */
    DirectedEdge<Pool<S>, Pool<D>> getDataPoolBridge();

    /**
     * @return Source transaction IDs comprising the boundaries of the changed source data.
     */
    List<? extends TransactionId> getSrcTids();

    /**
     * @return Source entities that this window affects.
     */
    Set<String> getAffectedSrcEntities();

    /**
     * @return Schema destination entities in a DirectedAcyclicGraph by
     * their relational dependencies.
     */
    EntitiesDag getDstEntitiesDag();

    /**
     * @return {@code true} if the window does not contain transactions
     */
    boolean isEmpty();
}
