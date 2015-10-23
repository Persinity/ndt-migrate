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
package com.persinity.ndt.dbagent.relational;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import com.persinity.common.db.SqlStrategy;
import com.persinity.common.db.metainfo.Col;
import com.persinity.common.db.metainfo.constraint.PK;
import com.persinity.ndt.db.TransactionId;
import com.persinity.ndt.dbagent.relational.SchemaInfo.ChangeType;

/**
 * {@link com.persinity.common.db.SqlStrategy} for agent related SQL that abstracts RDBMS dialect differences.<BR>
 * Implementations are expected to be thread-safe.
 *
 * @author Doichin Yordanov
 */
public interface AgentSqlStrategy extends SqlStrategy {

    List<ChangeType> CTYPES_INS_UPD_DEL = Arrays.asList(ChangeType.I, ChangeType.U, ChangeType.D);
    List<ChangeType> CTYPES_INS_UPD = Arrays.asList(ChangeType.I, ChangeType.U);
    List<ChangeType> CTYPES_DEL = Collections.singletonList(ChangeType.D);

    /**
     * Forms SQL query for extracting CLOG records:<BR>
     * SELECT gid, tid, ctype, empid, ename FROM clog_emp WHERE MOD(empid, ?) BETWEEN (? AND ?) AND tid IN (?, ?...)
     * ORDER BY gid.<BR>
     * The IN clause is with predefined number of "?" parameters falling in the set of i^2 (1, 2, 4, 8 ...), so that i^2
     * >= tidCount. Thus the resulting statement can be cached and prepared for more effective execution.
     *
     * @param tableName
     *         The name of the CLOG table.
     * @param cols
     *         List of all CLOG table columns
     * @param pidColName
     *         The name of the primary ID column. This is the PK column of the table that this CLOG tracks changes
     *         for.
     * @param tidCount
     *         Tids count.
     * @return SQL for extracting CLOG records.
     */
    String clogExtractQuery(String tableName, List<? extends Col> cols, String pidColName, int tidCount,
            List<ChangeType> ctypes);

    /**
     * @param ndtUserName
     * @param triggerName
     * @param tableName
     * @param tableCols
     * @param tablePk
     * @param clogName
     * @return SQL for after I/U/D for each row CDC trigger over {@code tableName} with trigger logic that fills in
     * {@code clogName}
     */
    String createCdcClogTrigger(String ndtUserName, String triggerName, String tableName, Set<Col> tableCols,
            PK tablePk, String clogName);

    /**
     * @param ndtUserName
     * @param triggerName
     * @param tableName
     * @param clogName
     * @param trlogName
     * @return SQL for after I/U/D statement CDC trigger over {@code tableName} with trigger logic that fills in
     * {@code trlogName} by getting the current transaction ID, {@code clogName} and GID.
     */
    String createCdcTrlogTrigger(String ndtUserName, String triggerName, String tableName, String clogName,
            String trlogName);

    /**
     * @param tableName
     *         The name of the CLOG table.
     * @param cols
     *         List of all CLOG table columns
     * @param pids
     *         The primary ID columns of the table that this CLOG tracks changes for.
     * @param tidCount
     *         Tids count.
     * @return SQL for extracting CLOG records.
     */
    String clogExtractQuery(String tableName, List<Col> cols, List<Col> pids, int tidCount, List<ChangeType> ctypes);

    /**
     * @param clogTableName
     *         clog table name
     * @param trlogTableName
     *         trlog table name
     * @return SQL statement to delete clog garbage entries, such entries who's TID is not in TRLOG anymore.
     */
    String clogGcStatement(final String clogTableName, final String trlogTableName);

    /**
     * @param value
     * @return
     * @throws ClassCastException
     *         if the value is of type that is not supported by this {@link AgentSqlStrategy}
     */
    TransactionId newTransactionId(Object value) throws ClassCastException;

    /**
     * Forms SQL statement for cleanup TRLOG records:<BR>
     * DELETE FROM trlog WHERE tid IN (?, ?...)<BR>
     *
     * @param trlogTableName
     * @param tidCount
     *         number of "?" in the IN clause.
     * @return
     */
    String trlogCleanupStatement(final String trlogTableName, final int tidCount);

    /**
     * Forms SQL query for extracting TRLOG records:<BR>
     * SELECT id, tid, last_gid, table_name FROM trlog WHERE tid IN (?, ?...) ORDER BY last_gid<BR>
     *
     * @param trlogTableName
     *         The TRLOG table.
     * @param cols
     *         List of all TRLOG table columns.
     * @param tidCount
     *         Tids count.
     * @return SQL for extracting TRLOG records.
     */
    String trlogExtractQuery(final String trlogTableName, final List<Col> cols, final int tidCount);

    /**
     * Forms SQL update statement for extracting TRLOG records:<BR>
     * SELECT id, tid, last_gid, table_name FROM trlog WHERE tid IN (?, ?...) ORDER BY last_gid<BR>
     *
     * @param trlogTableName
     *         The TRLOG table.
     * @param status
     *         Status to update
     * @param tidCount
     *         number of "?" in the IN clause.
     * @return SQL for extracting CLOG records.
     */
    String trlogUpdateStatus(final String trlogTableName, final SchemaInfo.TrlogStatusType status, final int tidCount);

    /**
     * @return statement to extract the maximal gid value in the transaction log
     */
    String getMaxGidStatement();

    /**
     * @return SQL for extracting the next window of TIDs. The SQL is in the form:<BR>
     * <pre>
     * {@code SELECT tid, last_gid, tab_name
     * FROM trlog
     * WHERE tid IN (SELECT tid FROM trlog WHERE status = 'R' AND last_gid <= ? AND rownum <= ?)
     * ORDER BY last_gid
     * }
     * </pre>
     */
    String nextWindow();

    /**
     * @return SQL for counting the unprocessed TIDs. The SQL is in the form:<BR>
     * <pre>
     * {@code SELECT COUNT(1) AS cnt
     * FROM trlog
     * WHERE tid IN (SELECT tid FROM trlog WHERE status = 'R' AND last_gid <= {})
     * }
     * </pre>
     */
    String countUnprocessedTids();

}
