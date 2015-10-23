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
import static com.persinity.common.invariant.Invariant.notNull;

import java.util.Objects;
import java.util.Set;

import org.apache.log4j.Logger;

import com.persinity.common.collection.DirectedEdge;
import com.persinity.common.db.metainfo.Col;
import com.persinity.common.logging.Log4jLogger;
import com.persinity.ndt.transform.RelExtractFunc;
import com.persinity.ndt.transform.RelLoadFunc;
import com.persinity.ndt.transform.TupleFunc;

/**
 * Contains mapping information about transformation to a destination entity, namely:<BR>
 * - The destination entity for the transformation.<BR>
 * - The extraction and load SQLs.<BR>
 * - The leading source entity used for mapping records from the extraction query to destination entity.<BR>
 * - The columns from the leading source entity used for mapping records from the extraction query to destination entity.<BR>
 *
 * @author Doichin Yordanov
 */
public class TransformInfo {

    public TransformInfo(final DirectedEdge<String, String> entityMapping,
            final DirectedEdge<Set<Col>, Set<Col>> colsMapping, final RelExtractFunc extractFunc,
            final TupleFunc transformFunc, final RelLoadFunc loadFunc, int maxTidsCount) {
        notNull(entityMapping);
        notNull(colsMapping);
        notNull(extractFunc);
        notNull(loadFunc);
        // TODO use repeater f for transformFunc to guard against null values.

        this.entityMapping = entityMapping;
        this.colsMapping = colsMapping; // can be null
        this.extractFunc = extractFunc;
        this.transformFunc = transformFunc; // can be null
        this.loadFunc = loadFunc;
        this.maxTidsCount = maxTidsCount;

        // used once to store in logs the full toString representation
        // all following debugs will use only the short version
        log.debug("Created {}", toFullString());
    }

    /**
     * @return Leading source and destination entities.
     */
    public DirectedEdge<String, String> getEntityMapping() {
        return entityMapping;
    }

    /**
     * @return The source and destination leading columns used for mapping records from the extraction query to
     * destination entity. Can be null when used for load clog to cclog stage only.
     */
    public DirectedEdge<Set<Col>, Set<Col>> getColumnsMapping() {
        return colsMapping;
    }

    /**
     * @return Extract function
     */
    public RelExtractFunc getExtractFunc() {
        return extractFunc;
    }

    /**
     * @return Transform function, can be null
     */
    public TupleFunc getTransformFunc() {
        return transformFunc;
    }

    /**
     * @return Load function
     */
    public RelLoadFunc getLoadFunc() {
        return loadFunc;
    }

    /**
     * @return max transaction IDs supported in the extract/transform/load functions.
     */
    public int getMaxTidsCount() {
        return maxTidsCount;
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
            toFullString = format("{}({}, {}, {}, {}, {}, {})", toString(), getEntityMapping(), getColumnsMapping(),
                    getExtractFunc(), getTransformFunc(), getLoadFunc(), getMaxTidsCount());
        }
        return toFullString;
    }

    @Override
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof TransformInfo)) {
            return false;
        }
        final TransformInfo that = (TransformInfo) obj;

        return getEntityMapping().equals(that.getEntityMapping()) && getColumnsMapping()
                .equals(that.getColumnsMapping()) && getExtractFunc().equals(that.getExtractFunc())
                && getTransformFunc().equals(that.getTransformFunc()) && getLoadFunc().equals(that.getLoadFunc());
    }

    @Override
    public int hashCode() {
        if (hashCode == null) {
            hashCode = Objects.hash(entityMapping, colsMapping, extractFunc, transformFunc, loadFunc);
        }
        return hashCode;
    }

    public static final boolean MIGRATE_COALESCE = true;
    public static final boolean MIGRATE_DONT_COALESCE = false;

    private final DirectedEdge<String, String> entityMapping;
    private final DirectedEdge<Set<Col>, Set<Col>> colsMapping;
    private final RelExtractFunc extractFunc;
    private final TupleFunc transformFunc;
    private final RelLoadFunc loadFunc;
    private final int maxTidsCount;

    private Integer hashCode;
    private String toString;
    private String toFullString;

    private static final Log4jLogger log = new Log4jLogger(Logger.getLogger(TransformInfo.class));
}
