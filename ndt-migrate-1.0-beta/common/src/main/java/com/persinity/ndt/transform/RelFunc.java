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

import com.persinity.common.db.RelDb;

/**
 * Represents a relational transformation over given schema.<BR>
 * E.g.:<BR>
 * - Transforming data: "INSERT INTO y (collist) SELECT collist FROM x"<BR>
 * - Transforming schema: "ALTER TABLE y ADD COLUMN x..."<BR>
 * - etc.<BR>
 *
 * @author Doichin Yordanov
 */
public class RelFunc extends BaseRelFunc<RelDb, RelDb> {

    public RelFunc(final String sql) {
        super(sql);
    }

    @Override
    public RelDb apply(final RelDb db) {
        db.executeDmdl(getSql());
        return db;
    }

}
