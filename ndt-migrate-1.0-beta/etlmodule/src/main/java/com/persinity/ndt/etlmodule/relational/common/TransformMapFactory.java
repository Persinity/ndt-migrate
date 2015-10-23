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
