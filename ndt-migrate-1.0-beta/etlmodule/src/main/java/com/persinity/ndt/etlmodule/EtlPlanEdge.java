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

import com.persinity.common.collection.ComparableDirectedEdge;

/**
 * ETL plan DirectedAcyclicGraph.
 * <p/>
 * TODO replace here the String weight with the constraints relations for each source/destination.
 *
 * @author Ivan Dachev
 */
public class EtlPlanEdge<S, D> extends ComparableDirectedEdge<TransferFunctor<S, D>, String, TransferFunctor<S, D>> {
    public EtlPlanEdge(final TransferFunctor<S, D> source, final String weight,
            final TransferFunctor<S, D> destination) {
        super(source, weight, destination);
    }
}
