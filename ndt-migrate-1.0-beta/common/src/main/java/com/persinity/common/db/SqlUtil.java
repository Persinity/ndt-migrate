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
package com.persinity.common.db;

import static com.persinity.common.collection.CollectionUtils.implode;
import static com.persinity.common.collection.CollectionUtils.transform;
import static com.persinity.common.invariant.Invariant.notEmpty;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import com.google.common.base.Function;
import com.persinity.common.db.metainfo.Col;
import com.persinity.common.db.metainfo.Equal;
import com.persinity.common.db.metainfo.Val;
import com.persinity.common.logging.Log4jLogger;

/**
 * @author Ivan Dachev
 */
public class SqlUtil {
    /**
     * @param cols
     *         list of columns to generate for
     * @return col1 = ?, col2 = ?, ...
     */
    public static List<Equal> toEqualParams(final List<Col> cols) {
        notEmpty(cols);

        final List<Equal> equalParams = new ArrayList<>();
        transform(cols, equalParams, new Function<Col, Equal>() {
            @Override
            public Equal apply(final Col col) {
                return new Equal(new Val(col.getName()), new Val("?"));
            }
        });
        return equalParams;
    }

    /**
     * @param cols
     *         list of columns to look for column
     * @param colName
     *         column name to look for
     * @return found column by name or null if not found
     */
    public static Col findColumn(final List<Col> cols, final String colName) {
        for (int i = 0; i < cols.size(); i++) {
            final Col col = cols.get(i);
            if (col.getName().equals(colName)) {
                return col;
            }
        }
        return null;
    }

    public static String buildColClause(final List<? extends Col> cols) {
        final String colClause = implode(cols, ", ");
        return colClause;
    }

    private static final Log4jLogger log = new Log4jLogger(Logger.getLogger(SqlUtil.class));
}
