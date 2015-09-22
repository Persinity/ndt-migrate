/**
 * Copyright (c) 2015 Persinity Inc.
 */
package com.persinity.ndt.etlmodule.relational.common;

import static com.persinity.common.StringUtils.format;
import static com.persinity.common.collection.GraphUtils.addSinkVertex;
import static com.persinity.common.collection.GraphUtils.addSourceVertex;
import static com.persinity.common.collection.GraphUtils.transformDirectedGraph;

import com.google.common.base.Function;
import com.persinity.common.collection.DirectedEdge;
import com.persinity.common.collection.Triple;
import com.persinity.common.db.RelDb;
import com.persinity.common.db.metainfo.FKEdge;
import com.persinity.ndt.etlmodule.EtlPlanDag;
import com.persinity.ndt.etlmodule.EtlPlanEdge;
import com.persinity.ndt.etlmodule.EtlPlanGenerator;
import com.persinity.ndt.etlmodule.TransferFunctor;
import com.persinity.ndt.etlmodule.relational.TransferFunctorFactory;
import com.persinity.ndt.transform.TransferWindow;

/**
 * Base {@link EtlPlanGenerator}
 *
 * @author Ivan Dachev
 */
public abstract class BaseEtlPlanGenerator implements EtlPlanGenerator<RelDb, RelDb> {

    /**
     * @param transferFunctorFactory
     */
    public BaseEtlPlanGenerator(final TransferFunctorFactory transferFunctorFactory) {
        this.transferFunctorFactory = transferFunctorFactory;
    }

    @Override
    public String toString() {
        if (toString == null) {
            toString = format("{}({})", this.getClass().getSimpleName(), transferFunctorFactory);
        }
        return toString;
    }

    /**
     * @param transferWindow
     * @param etlFunctorFactory
     * @param reverse
     * @return
     */
    protected EtlPlanDag<RelDb, RelDb> newEtlPlan(final TransferWindow<RelDb, RelDb> transferWindow,
            final TransferFunctorFactory etlFunctorFactory, boolean reverse) {
        final EtlPlanDag<RelDb, RelDb> res = new EtlPlanDag<>();

        final Function<String, TransferFunctor<RelDb, RelDb>> transformVertexF = new Function<String, TransferFunctor<RelDb, RelDb>>() {
            @Override
            public TransferFunctor<RelDb, RelDb> apply(final String dstEntity) {
                return etlFunctorFactory.newEntityTransferFunctor(dstEntity, transferWindow);
            }
        };

        final TransformEdgeBuilderF transformEdgeF = new TransformEdgeBuilderF() {
            @Override
            public EtlPlanEdge<RelDb, RelDb> apply(
                    final Triple<FKEdge, TransferFunctor<RelDb, RelDb>, TransferFunctor<RelDb, RelDb>> args) {
                return new EtlPlanEdge<>(args.getSecond(), args.getFirst().weight().toString(), args.getThird());
            }
        };

        transformDirectedGraph(transferWindow.getDstEntitiesDag(), res, transformVertexF, transformEdgeF, reverse);

        final TransferFunctor<RelDb, RelDb> preWindowEtlFunctor = etlFunctorFactory
                .newPreWindowTransferFunctor(transferWindow);
        addSourceVertex(res, preWindowEtlFunctor, buildEdgeF);
        res.setRootSourceVertex(preWindowEtlFunctor);

        final TransferFunctor<RelDb, RelDb> postWindowEtlFunctor = etlFunctorFactory
                .newPostWindowTransferFunctor(transferWindow);
        addSinkVertex(res, postWindowEtlFunctor, buildEdgeF);
        res.setBaseSinkVertex(postWindowEtlFunctor);

        return res;
    }

    @Override
    public boolean isNoOp(final TransferFunctor<RelDb, RelDb> functor) {
        return functor instanceof NoOpsRelTransferFunctor;
    }

    protected static final EdgeBuilderF buildEdgeF = new EdgeBuilderF() {
        @Override
        public EtlPlanEdge<RelDb, RelDb> apply(
                final DirectedEdge<TransferFunctor<RelDb, RelDb>, TransferFunctor<RelDb, RelDb>> args) {
            return new EtlPlanEdge<>(args.src(), "", args.dst());
        }
    };

    protected interface TransformEdgeBuilderF extends
            Function<Triple<FKEdge, TransferFunctor<RelDb, RelDb>, TransferFunctor<RelDb, RelDb>>, EtlPlanEdge<RelDb, RelDb>> {
    }

    protected interface EdgeBuilderF extends
            Function<DirectedEdge<TransferFunctor<RelDb, RelDb>, TransferFunctor<RelDb, RelDb>>, EtlPlanEdge<RelDb, RelDb>> {
    }

    /**
     * @return TransferFunctorFactory
     */
    protected TransferFunctorFactory getTransferFunctorFactory() {
        return transferFunctorFactory;
    }

    private final TransferFunctorFactory transferFunctorFactory;
    private String toString;
}
