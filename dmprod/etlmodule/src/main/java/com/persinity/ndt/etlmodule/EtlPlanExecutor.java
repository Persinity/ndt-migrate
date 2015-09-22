/**
 * Copyright (c) 2015 Persinity Inc.
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
