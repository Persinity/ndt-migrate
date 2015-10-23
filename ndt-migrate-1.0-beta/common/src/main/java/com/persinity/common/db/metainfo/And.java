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
package com.persinity.common.db.metainfo;

import static com.persinity.common.StringUtils.format;
import static com.persinity.common.invariant.Invariant.notEmpty;

import java.util.List;

import com.persinity.common.collection.CollectionUtils;

/**
 * @author Doichin Yordanov
 */
public class And implements SqlPredicate {

    private final String sql;

    public And(final List<? extends SqlPredicate> predicates) {
        notEmpty(predicates);
        if (predicates.size() == 1) {
            sql = predicates.get(0).toString();
        } else {
            sql = format("({})", CollectionUtils.implode(predicates, ") AND ("));
        }
    }

    @Override
    public String toString() {
        return sql;
    }
}
