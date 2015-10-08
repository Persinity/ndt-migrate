/**
 * Copyright (c) 2015 Persinity Inc.
 */
package com.persinity.ndt.etlmodule.relational.common;

import java.util.Map;

import com.persinity.ndt.etlmodule.relational.TransformInfo;

/**
 * Generate mapping of entity to {@link TransformInfo} for source to clog and clog to target.
 *
 * @author Ivan Dachev
 */
public interface TransformMapFactory {
    /**
     * @return Map for applying load statements from source to clog entities.
     */
    Map<String, TransformInfo> getMigrateMap();

    /**
     * @return Map for applying load statements from source to clog entities without coalesce.
     */
    Map<String, TransformInfo> getMigrateNoCoalesceMap();

    /**
     * @return Map for applying transform merge statements from clog to target entities.
     */
    Map<String, TransformInfo> getMergeMap();

    /**
     * @return Map for applying transform delete statements from clog to target entities.
     */
    Map<String, TransformInfo> getDeleteMap();
}
