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

import java.util.Collection;

import com.persinity.common.collection.Dag;

/**
 * ETL plan DirectedAcyclicGraph.
 *
 * @author Ivan Dachev
 */
public class EtlPlanDag<S, D> extends Dag<TransferFunctor<S, D>, EtlPlanEdge<S, D>> {

    private TransferFunctor<S, D> rootSourceVertex;

    public EtlPlanDag() {
        super();
    }

    public EtlPlanDag(final Collection<TransferFunctor<S, D>> functions) {
        super(functions);
    }

    public TransferFunctor<S, D> getRootSourceVertex() {
        return rootSourceVertex;
    }

    public void setRootSourceVertex(final TransferFunctor<S, D> rootSourceVertex) {
        this.rootSourceVertex = rootSourceVertex;
    }

    public TransferFunctor<S, D> getBaseSinkVertex() {
        return baseSinkVertex;
    }

    public void setBaseSinkVertex(final TransferFunctor<S, D> baseSinkVertex) {
        this.baseSinkVertex = baseSinkVertex;
    }

    private TransferFunctor<S, D> baseSinkVertex;

    private static final long serialVersionUID = 4184869384002908464L;
}
