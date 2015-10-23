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
