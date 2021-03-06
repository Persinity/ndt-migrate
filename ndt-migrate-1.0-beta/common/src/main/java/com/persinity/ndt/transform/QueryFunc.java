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
package com.persinity.ndt.transform;

import java.util.Iterator;
import java.util.Map;

import com.persinity.common.db.RelDb;

/**
 * Represents query function.
 *
 * @author Doichin Yordanov
 */
public class QueryFunc extends BaseRelFunc<RelDb, Iterator<Map<String, Object>>> {

    /**
     * @param sql
     */
    public QueryFunc(final String sql) {
        super(sql);
    }

    @Override
    public Iterator<Map<String, Object>> apply(final RelDb input) {
        return input.executeQuery(getSql());
    }

}
