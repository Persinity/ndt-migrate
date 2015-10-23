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
package com.persinity.ndt.etlmodule.relational;

import static com.persinity.common.StringUtils.format;
import static com.persinity.common.StringUtils.formatObj;

import java.util.List;
import java.util.Set;

import org.apache.log4j.Logger;

import com.google.common.base.Objects;
import com.persinity.common.collection.DirectedEdge;
import com.persinity.common.collection.Pool;
import com.persinity.common.db.RelDb;
import com.persinity.common.invariant.Invariant;
import com.persinity.common.logging.Log4jLogger;
import com.persinity.ndt.db.TransactionId;
import com.persinity.ndt.transform.EntitiesDag;
import com.persinity.ndt.transform.TransferWindow;

/**
 * {@link TransferWindow} for relational databases.
 *
 * @author Doichin Yordanov
 */
public class RelTransferWindow implements TransferWindow<RelDb, RelDb> {

    /**
     * Does not guarantee snapshot view of input composite objects.
     *
     * @param dataPoolBridge
     * @param srcTids
     *         optional for empty window
     * @param affectedSrcEntities
     *         optional for empty window
     * @param dstEntitiesDag
     *         optional for empty window
     */
    public RelTransferWindow(final DirectedEdge<Pool<RelDb>, Pool<RelDb>> dataPoolBridge,
            final List<? extends TransactionId> srcTids, final Set<String> affectedSrcEntities,
            final EntitiesDag dstEntitiesDag) {

        Invariant.assertArg(dataPoolBridge != null);

        this.dataPoolBridge = dataPoolBridge;
        this.srcTids = srcTids;
        this.affectedSrcEntities = affectedSrcEntities;
        this.dstEntitiesDag = dstEntitiesDag;

        // used once to store in logs the full toString representation
        // all following debugs will use only the short version
        log.debug("Created {}", toFullString());
    }

    @Override
    public DirectedEdge<Pool<RelDb>, Pool<RelDb>> getDataPoolBridge() {
        return dataPoolBridge;
    }

    @Override
    public List<? extends TransactionId> getSrcTids() {
        return srcTids;
    }

    @Override
    public Set<String> getAffectedSrcEntities() {
        return affectedSrcEntities;
    }

    @Override
    public EntitiesDag getDstEntitiesDag() {
        return dstEntitiesDag;
    }

    @Override
    public boolean isEmpty() {
        return srcTids.isEmpty();
    }

    @Override
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof RelTransferWindow)) {
            return false;
        }
        final RelTransferWindow that = (RelTransferWindow) obj;
        return getClass().equals(that.getClass()) && getDataPoolBridge().equals(that.getDataPoolBridge())
                && getSrcTids().equals(that.getSrcTids()) && getDstEntitiesDag().equals(that.getDstEntitiesDag());
    }

    @Override
    public int hashCode() {
        if (hashCode == null) {
            hashCode = Objects.hashCode(getDataPoolBridge(), getSrcTids(), getDstEntitiesDag());
        }
        return hashCode;
    }

    @Override
    public String toString() {
        if (toString == null) {
            toString = formatObj(this);
        }
        return toString;
    }

    /**
     * @return full string representation
     */
    public String toFullString() {
        if (toFullString == null) {
            toFullString = format("{}({}, {}, {}, {})", toString(), getDataPoolBridge(), getSrcTids(),
                    getAffectedSrcEntities(), getDstEntitiesDag().vertexSet());
        }
        return toFullString;
    }

    private final DirectedEdge<Pool<RelDb>, Pool<RelDb>> dataPoolBridge;
    private final List<? extends TransactionId> srcTids;
    private final Set<String> affectedSrcEntities;
    private final EntitiesDag dstEntitiesDag;

    private Integer hashCode;
    private String toString;
    private String toFullString;

    private static final Log4jLogger log = new Log4jLogger(Logger.getLogger(RelTransferWindow.class));
}
