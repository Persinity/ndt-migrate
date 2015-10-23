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

package com.persinity.common.db.metainfo.constraint;

import java.util.Set;

import com.persinity.common.db.metainfo.Col;

/**
 * @author Ivo Yanakiev
 */
public class PK extends Unique {

    public PK(final String table, final Set<Col> columns) {
        this(PK.class.getSimpleName(), table, columns);
    }

    public PK(final String name, final String table, final Set<Col> columns) {
        super(name, table, columns);
    }

}
