/**
 * Copyright (c) 2015 Persinity Inc.
 */
package com.persinity.ndt.etlmodule.relational;

import com.persinity.common.db.RelDb;
import com.persinity.ndt.etlmodule.TransferFunctor;
import com.persinity.ndt.transform.TransferWindow;

/**
 * Factory of transfer functors. A transfer functor is responsible for building transfer instructions
 * for given destination entity and window.
 *
 * @author Doichin Yordanov
 */
public interface TransferFunctorFactory {
    /**
     * @param win
     *         window to build for
     * @return ETL functor to be executed before the process of all entities in a window
     */
    TransferFunctor<RelDb, RelDb> newPreWindowTransferFunctor(TransferWindow<RelDb, RelDb> win);

    /**
     * @param dstEntity
     *         entity to build for
     * @param win
     *         window to build for
     * @return ETL functor to be executed on process
     */
    TransferFunctor<RelDb, RelDb> newEntityTransferFunctor(String dstEntity, TransferWindow<RelDb, RelDb> win);

    /**
     * @param win
     *         window to build for
     * @return ETL functor to be executed after the process of all entities in a window
     */
    TransferFunctor<RelDb, RelDb> newPostWindowTransferFunctor(TransferWindow<RelDb, RelDb> win);
}
