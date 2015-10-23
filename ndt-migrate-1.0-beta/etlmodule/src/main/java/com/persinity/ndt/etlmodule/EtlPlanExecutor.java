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
package com.persinity.ndt.etlmodule;

import com.persinity.common.db.RelDb;

/**
 * @author Ivan Dachev
 */
public interface EtlPlanExecutor {
    /**
     * @param winGen
     *         window generator for the plan
     * @param etlPlanner
     *         plan to execute
     * @param name
     *         of the plan
     */
    void execute(final WindowGenerator<RelDb, RelDb> winGen, final EtlPlanGenerator<RelDb, RelDb> etlPlanner,
            final String name);

    /**
     * Close the plan executor.
     */
    void close();
}
