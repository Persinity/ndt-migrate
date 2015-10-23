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

import java.util.StringTokenizer;

import com.persinity.common.invariant.Invariant;

/**
 * SQL formatting utils
 *
 * @author Doichin Yordanov
 */
public class SqlFormatter {
    private static final String TOKEN_FROM = " FROM ";
    private static final String TOKEN_INSERT = "INSERT INTO ";
    private static final String TOKEN_UPDATE = "UPDATE ";
    private static final String TOKEN_DELETE = "DELETE FROM ";
    private static final String TOKEN_WHERE = " WHERE ";
    private static final String TOKEN_ORDER = " ORDER BY ";
    private static final String TOKEN_GROUPBY = " GROUP BY ";

    /**
     * @param sql
     *         query or DML statement with keywords in upper case
     * @return The table(s) involved in the SQL clause if such is found, else empty string
     * @throws NullPointerException
     *         if sql is null
     */
    public static String getTableClause(String sql) {
        sql = sql.trim();
        if (sql.startsWith("(") && sql.endsWith(")")) {
            sql = sql.substring(1, sql.length() - 1); // inline view
        } else if (sql.startsWith(TOKEN_INSERT)) {
            sql = new StringTokenizer(sql.substring(TOKEN_INSERT.length())).nextToken();
        } else if (sql.startsWith(TOKEN_UPDATE)) {
            sql = substring(sql, TOKEN_UPDATE, " SET ");
        } else if (sql.startsWith(TOKEN_DELETE)) {
            sql = new StringTokenizer(sql.substring(TOKEN_DELETE.length())).nextToken();
        } else if (sql.startsWith("SELECT ")) {
            if (sql.contains(TOKEN_WHERE)) {
                sql = substring(sql, TOKEN_FROM, TOKEN_WHERE);
            } else if (sql.contains(TOKEN_ORDER)) {
                sql = substring(sql, TOKEN_FROM, TOKEN_ORDER);
            } else if (sql.contains(TOKEN_GROUPBY)) {
                sql = substring(sql, TOKEN_FROM, TOKEN_GROUPBY);
            } else {
                sql = substring(sql, TOKEN_FROM, null);
            }
        } else {
            return sql;
        }
        return getTableClause(sql);
    }

    /**
     * TODO move in StringUtils when present
     *
     * @param string
     *         To search in
     * @param startToken
     *         null to start from the beginning of the string
     * @param endToken
     *         null to stop at the end of the string
     * @return substring that is found between startToken and endToken, or empty string if not found.
     * @throws IllegalArgumentException
     *         if string is null
     */
    public static String substring(final String string, final String startToken, final String endToken) {
        Invariant.assertArg(string != null);

        int startTokenPos = 0;
        int startTokenLength = 0;
        if (startToken != null) {
            startTokenPos = string.indexOf(startToken);
            if (startTokenPos < 0) {
                return "";
            }
            startTokenLength = startToken.length();
        }
        final int startPos = startTokenPos + startTokenLength;

        final int endTokenPos = endToken == null ? string.length() : string.lastIndexOf(endToken);
        if (endTokenPos <= startPos) {
            return "";
        }

        final String result = string.substring(startPos, endTokenPos);
        return result;
    }
}
