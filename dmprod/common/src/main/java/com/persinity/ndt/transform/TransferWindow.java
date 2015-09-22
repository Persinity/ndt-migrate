/**
 * Copyright (c) 2015 Persinity Inc.
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
