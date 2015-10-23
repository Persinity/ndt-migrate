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

import com.persinity.common.db.Closeable;
import com.persinity.ndt.transform.TransferWindow;

/**
 * Generates a plan for DM ETL transformation.
 *
 * @author Doichin Yordanov
 */
public interface EtlPlanGenerator<S extends Closeable, D extends Closeable> {
    /**
     * Generates Data Motion plan for moving a {@link TransferWindow} of data.
     * <p/>
     * The plan is a directed acyclic graph of data transformation functors.
     * <p/>
     * Each functor creates set of transformations that is responsible for extracting, transforming and loading data
     * into a target entity.
     *
     * @return ETL plan DAG
     */
    EtlPlanDag<S, D> newEtlPlan(TransferWindow<S, D> transferWindow);

    /**
     * Validate that a given functor from the EtlPlanDag generates no operations.
     * The EtlPlanDag can have such no ops vertexes in order to preserve the correct
     * edges between functors that actually do real jobs and should be executed in
     * topology order.
     *
     * @param functor
     *         to validate for
     * @return true if the given functor generates no operations
     */
    boolean isNoOp(TransferFunctor<S, D> functor);
}
