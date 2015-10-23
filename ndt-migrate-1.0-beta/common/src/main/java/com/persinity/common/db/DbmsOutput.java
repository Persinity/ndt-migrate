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

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.SQLException;

import org.apache.log4j.Logger;

/**
 * Implements Oracle DBMS output retriever.
 *
 * @author Ivan Dachev
 */
public class DbmsOutput {

    /**
     * Initialize the DBMS output statements.
     *
     * @param conn
     * @throws SQLException
     */
    public DbmsOutput(Connection conn) throws SQLException {
        enable_stmt = conn.prepareCall("BEGIN DBMS_OUTPUT.ENABLE(:1); END;");
        disable_stmt = conn.prepareCall("BEGIN DBMS_OUTPUT.DISABLE; END;");

        show_stmt = conn.prepareCall(
                "DECLARE " + "    l_line VARCHAR2(255); " + "    l_done NUMBER; " + "    l_buffer LONG; " + "BEGIN "
                        + "  LOOP " + "    EXIT WHEN LENGTH(l_buffer)+255 > :maxbytes OR l_done = 1; "
                        + "    DBMS_OUTPUT.GET_LINE(l_line, l_done); "
                        + "    l_buffer := l_buffer || l_line || chr(10); " + "  END LOOP; " + " :done := l_done; "
                        + " :buffer := l_buffer; " + "end;");
    }

    /**
     * Set size of the DBMS buffer and enable it.
     *
     * @param size
     *         - the DBMS size
     * @throws SQLException
     */
    public void enable(int size) throws SQLException {
        enable_stmt.setInt(1, size);
        enable_stmt.executeUpdate();
    }

    /**
     * Disable DBMS output buffer
     *
     * @throws SQLException
     */
    public void disable() throws SQLException {
        disable_stmt.executeUpdate();
    }

    /**
     * Show the output to the log.
     *
     * @throws SQLException
     */
    public void show() throws SQLException {
        show_stmt.registerOutParameter(2, java.sql.Types.INTEGER);
        show_stmt.registerOutParameter(3, java.sql.Types.VARCHAR);

        while (true) {
            show_stmt.setInt(1, 32000);
            show_stmt.executeUpdate();

            String msg = show_stmt.getString(3);
            msg = msg.trim();
            if (msg.length() > 0) {
                log.debug(msg);
            }

            final int done = show_stmt.getInt(2);
            if (done == 1) {
                break;
            }
        }
    }

    /**
     * Close when no longer used.
     *
     * @throws SQLException
     */
    public void close() throws SQLException {
        enable_stmt.close();
        disable_stmt.close();
        show_stmt.close();
    }

    private final CallableStatement enable_stmt;
    private final CallableStatement disable_stmt;
    private final CallableStatement show_stmt;
    private static final Logger log = Logger.getLogger(DbmsOutput.class);
}