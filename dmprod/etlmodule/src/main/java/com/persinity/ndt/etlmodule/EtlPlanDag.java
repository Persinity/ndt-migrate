/**
 * Copyright (c) 2015 Persinity Inc.
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
