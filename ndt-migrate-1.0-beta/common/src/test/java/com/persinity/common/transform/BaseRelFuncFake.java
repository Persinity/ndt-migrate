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
package com.persinity.common.transform;

import java.util.List;

import com.persinity.common.collection.DirectedEdge;
import com.persinity.common.db.RelDb;
import com.persinity.ndt.transform.BaseRelFunc;

/**
 * {@link BaseRelFunc} for testing purposes with faked {@link BaseRelFunc#apply(Object)} implementation.
 *
 * @author Doichin Yordanov
 */
public class BaseRelFuncFake extends BaseRelFunc<DirectedEdge<RelDb, List<?>>, Integer> {

    /**
     * @param sql
     */
    public BaseRelFuncFake(final String sql) {
        super(sql);
    }

    @Override
    public Integer apply(final DirectedEdge<RelDb, List<?>> input) {
        return 1;
    }

}
