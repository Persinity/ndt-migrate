/**
 * Copyright (c) 2015 Persinity Inc.
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
    public EtlPlanEdge(final TransferFunctor<S, D> source, final String weight, final TransferFunctor<S, D> destination) {
        super(source, weight, destination);
    }
}
