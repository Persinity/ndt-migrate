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

import static com.persinity.common.StringUtils.format;
import static com.persinity.common.StringUtils.formatObj;
import static com.persinity.common.db.RelDbUtil.newSqlStrategyFor;
import static com.persinity.common.invariant.Invariant.assertArg;
import static com.persinity.common.invariant.Invariant.assertState;
import static com.persinity.common.invariant.Invariant.notEmpty;
import static com.persinity.common.invariant.Invariant.notNull;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.Reader;
import java.sql.Blob;
import java.sql.CallableStatement;
import java.sql.Clob;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.ibatis.jdbc.ScriptRunner;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;

import com.google.common.base.Function;
import com.persinity.common.Log4jOutputStream;
import com.persinity.common.Resource;
import com.persinity.common.Resource.Accessor;
import com.persinity.common.db.metainfo.ProxySchema;
import com.persinity.common.db.metainfo.Schema;
import com.persinity.common.invariant.NotEmpty;
import com.persinity.common.logging.Log4jLogger;

/**
 * Database utility, wrapper around {@link Connection}.<BR>
 * Does not take care of the wrapped connection or if the connection is modified in-flight.
 *
 * @author dyordanov
 */
public class SimpleRelDb implements RelDb {

    /**
     * @param dbConfig
     *         creates from db config
     */
    public SimpleRelDb(final DbConfig dbConfig) {
        this(SimpleRelDb.newConnection(dbConfig), newSqlStrategyFor(dbConfig.getSqlStrategy()), null,
                dbConfig.getDbEnableOutput(), dbConfig.getCacheSql(), dbConfig.getSkipTables());
    }

    /**
     * @param conn
     * @param sqlStrategy
     * @param metaInfo
     *         optional, lazy loaded if null supplied.
     * @param dbEnableOutput
     * @param cacheSql
     * @param skipTables
     */
    public SimpleRelDb(final Connection conn, final SqlStrategy sqlStrategy, final Schema metaInfo,
            final boolean dbEnableOutput, final boolean cacheSql, final Collection<String> skipTables) {
        notNull(conn);
        notNull(sqlStrategy);
        notNull(skipTables);

        this.conn = conn;
        this.sqlStrategy = sqlStrategy;
        this.metaInfo = metaInfo;
        this.dbEnableOutput = dbEnableOutput;
        this.cacheSql = cacheSql;
        this.skipTables = skipTables;

        if (dbEnableOutput) {
            dbmsOutputTask = new DbmsOutputTask();
            dbmsOutputTask.startDbmsOutputTask(conn, 500);
        }

        initSqlCache(cacheSql);

        log.debug(format("Created {}", this));
    }

    @Override
    public Schema metaInfo() {
        synchronized (schemaSingletonLock) {
            if (schema == null) {
                schema = ProxySchema.newSchema(this, getSqlStrategy(), skipTables);
            }
        }
        return schema;
    }

    /**
     * @return The underlying connection
     */
    public synchronized Connection getConnection() {
        return conn;
    }

    @Override
    public int executeDmdl(final String sql) {
        assertArg(sql != null && !sql.isEmpty(), "");
        log(sql);

        final Statement stmt = resource.access(new Accessor<Connection, Statement>(conn, null) {
            @Override
            public Statement access(final Connection resource) throws Exception {
                return resource.createStatement();
            }
        });
        return resource.accessAndAutoClose(new Accessor<Statement, Integer>(stmt, newDescriptionFor(sql)) {
            @Override
            public Integer access(final Statement resource) throws Exception {
                final int rowsUpdated = resource.executeUpdate(sql);
                log(format("{} rows updated", rowsUpdated));
                return rowsUpdated;
            }
        });
    }

    /**
     * Returns the connection's user name
     */
    @Override
    public String toString() {
        if (toString == null) {
            toString = format("{}@{}", getUserName(), Integer.toHexString(conn.hashCode()));
        }
        return toString;
    }

    @Override
    public void executeSp(final String signature, final Object... params) {
        assertArg(signature != null && !signature.isEmpty(), "");

        final String sql = "{call " + signature + "}";
        if (log.getLogger().isDebugEnabled()) {
            final String callTrace = traceCall(sql, params);
            log.debug(callTrace);
        }

        final CallableStatement stmt = resource.access(new Accessor<Connection, CallableStatement>(conn, null) {
            @Override
            public CallableStatement access(final Connection resource) throws Exception {
                return resource.prepareCall(sql);
            }
        });
        resource.accessAndAutoClose(new Accessor<CallableStatement, Void>(stmt, newDescriptionFor(sql)) {
            @Override
            public Void access(final CallableStatement resource) throws Exception {
                if (params != null) {
                    for (int i = 0; i < params.length; i++) {
                        resource.setObject(i + 1, params[i]);
                    }
                }
                resource.executeUpdate();
                return null;
            }
        });
    }

    @Override
    public Long executeNumericSf(final String signature, final Object... params) {
        assertArg(signature != null && !signature.isEmpty(), "");

        final String sql = "{? = call " + signature + "}";
        if (log.getLogger().isDebugEnabled()) {
            final String callTrace = traceCall(sql, params);
            log.debug(callTrace);
        }

        final CallableStatement stmt = resource.access(new Accessor<Connection, CallableStatement>(conn, null) {
            @Override
            public CallableStatement access(final Connection resource) throws Exception {
                return resource.prepareCall(sql);
            }
        });
        final long result = resource
                .accessAndAutoClose(new Accessor<CallableStatement, Long>(stmt, newDescriptionFor(sql)) {
                    @Override
                    public Long access(final CallableStatement resource) throws Exception {
                        resource.registerOutParameter(1, Types.NUMERIC);
                        if (params != null) {
                            for (int i = 0; i < params.length; i++) {
                                resource.setObject(i + 2, params[i]);
                            }
                        }

                        resource.executeUpdate();

                        final long result = resource.getLong(1);
                        log(format("Returned: {}", result));
                        return result;
                    }
                });

        return result;

    }

    @Override
    public void close() {
        log.debug("Closing {}", this);
        releaseResources();
        closeConnection(conn);
    }

    /**
     * Releases underlying resources, without the wrapped {@link Connection}
     *
     * @return The wrapped {@link Connection}
     */
    public Connection releaseResources() {
        if (dbEnableOutput) {
            dbmsOutputTask.stopDbmsOutputTask();
        }
        if (scriptRunner != null) {
            scriptRunner.closeConnection();
        }

        for (final String key : prepStmtCache.keySet()) {
            final PreparedStatement stmt = prepStmtCache.remove(key);
            resource.close(stmt);
        }
        assertState(prepStmtCache.isEmpty());

        return conn;
    }

    @Override
    public boolean isDbOutputEnabled() {
        return dbEnableOutput;
    }

    @Override
    public void commit() {
        log("COMMIT");
        resource.access(new Accessor<Connection, Void>(conn, null) {
            @Override
            public Void access(final Connection resource) throws Exception {
                resource.commit();
                return null;
            }
        });
    }

    @Override
    public void rollback() {
        log("ROLLBACK");
        resource.access(new Accessor<Connection, Void>(conn, null) {
            @Override
            public Void access(final Connection resource) throws Exception {
                resource.rollback();
                return null;
            }
        });
    }

    @Override
    public Iterator<Object[]> query(final String sql) {
        assertArg(sql != null && !sql.isEmpty(), "");
        log(sql);

        final Statement stmt = resource.access(new Accessor<Connection, Statement>(conn, null) {
            @Override
            public Statement access(final Connection resource) throws Exception {
                return resource.createStatement();
            }
        });
        return resource.accessAndAutoClose(new Accessor<Statement, Iterator<Object[]>>(stmt, newDescriptionFor(sql)) {
            @Override
            public Iterator<Object[]> access(final Statement resource) throws Exception {
                final List<Object[]> rows = new LinkedList<>();
                final ResultSet rs = resource.executeQuery(sql);
                final int colCount = rs.getMetaData().getColumnCount();
                while (rs.next()) {
                    final Object[] row = new Object[colCount];
                    for (int i = 0; i < colCount; i++) {
                        row[i] = rs.getObject(i + 1);
                    }
                    rows.add(row);
                }
                rs.close();
                return rows.iterator();
            }
        });

    }

    @Override
    public void executeScript(final String scriptName) {
        notEmpty("scriptName");

        if (scriptRunner == null) {
            scriptRunner = new ScriptRunner(getConnection());
            configure(scriptRunner);
        }

        final ScriptRunner _scriptRunner = scriptRunner;
        final InputStream resourceStream = ClassLoader.getSystemResourceAsStream(scriptName);
        assertArg(resourceStream != null, "Failed to find script: {}", scriptName);
        final Reader scriptReader = new InputStreamReader(resourceStream);

        log("Executing DB script ", Collections.singletonList(scriptName));
        resource.accessAndAutoClose(new Accessor<Reader, Void>(scriptReader, scriptName) {
            @Override
            public Void access(final Reader resource) throws Exception {
                _scriptRunner.runScript(new BufferedReader(scriptReader));
                return null;
            }
        });

    }

    /**
     * Tries to close connection and release associated resources.
     *
     * @param conn
     * @throws RuntimeException
     *         upon failure.
     */
    public static void closeConnection(final Connection conn) {
        resource.close(conn);
    }

    /**
     * Establishes and returns new {@link Connection}
     *
     * @param conf
     *         NDT and connection properties
     */
    public static Connection newConnection(final DbConfig conf) {
        return newConnection(conf.getDbUrl(), conf.getDbUser(), conf.getDbPass());
    }

    /**
     * Establishes and returns new {@link Connection}
     *
     * @param url
     *         JDBC URL, e.g. jdbc:oracle:thin:@myserver.com:1521:mysid
     * @param dbUser
     * @param dbPass
     * @return
     */
    public static Connection newConnection(final String url, final String dbUser, final String dbPass) {
        new NotEmpty("url", "dbUser", "dbPass").enforce(url, dbUser, dbPass);

        final Properties connectionProps = new Properties();
        connectionProps.put("user", dbUser);
        connectionProps.put("password", dbPass);

        final Connection conn = resource.access(new Accessor<String, Connection>(format("{}@{}", dbUser, url), null) {
            @Override
            public Connection access(final String resource) throws Exception {
                final Connection res = DriverManager.getConnection(url, connectionProps);
                res.setAutoCommit(false);
                return res;
            }
        });
        log.debug("Connected to {}@{} conn: {}", dbUser, url, formatObj(conn));
        return conn;
    }

    @Override
    public Iterator<Map<String, Object>> executePreparedQuery(final String sql, final List<?> params) {
        assertArg(sql != null && !sql.isEmpty(), "");
        log(sql, params);

        final PreparedStatement stmt = getPrepStmtF.apply(sql);
        final Iterator<Map<String, Object>> result = resource
                .access(new Accessor<PreparedStatement, Iterator<Map<String, Object>>>(stmt, newDescriptionFor(sql)) {
                    @Override
                    public Iterator<Map<String, Object>> access(final PreparedStatement resource) throws Exception {
                        resource.clearParameters();
                        int paramIndex = 1;
                        for (final Object param : params) {
                            resource.setObject(paramIndex++, param);
                        }

                        final ResultSet rs = resource.executeQuery();
                        final List<Map<String, Object>> result = processResultSet(rs);
                        rs.close();

                        return result.iterator();
                    }
                });
        closePrepStmtF.apply(stmt);
        return result;
    }

    @Override
    public Iterator<Map<String, Object>> executeQuery(final String sql) {
        assertArg(sql != null && !sql.isEmpty(), "");
        log(sql);

        final Statement stmt = resource.access(new Accessor<Connection, Statement>(getConnection(), null) {
            @Override
            public Statement access(final Connection resource) throws Exception {
                return resource.createStatement();
            }
        });

        return resource.accessAndAutoClose(
                new Accessor<Statement, Iterator<Map<String, Object>>>(stmt, newDescriptionFor(sql)) {
                    @Override
                    public Iterator<Map<String, Object>> access(final Statement resource) throws Exception {
                        final ResultSet rs = resource.executeQuery(sql);
                        final List<Map<String, Object>> result = processResultSet(rs);
                        rs.close();

                        return result.iterator();
                    }
                });
    }

    private String newDescriptionFor(final String sql) {
        return format("[{}] {}", getUserName(), sql);
    }

    @Override
    public int executePreparedDml(final String sql, final List<?> params) {
        assertArg(sql != null && !sql.isEmpty(), "");
        log(sql, params);

        final PreparedStatement stmt = getPrepStmtF.apply(sql);
        final int result = resource.access(new Accessor<PreparedStatement, Integer>(stmt, newDescriptionFor(sql)) {
            @Override
            public Integer access(final PreparedStatement resource) throws Exception {
                resource.clearParameters();
                int i = 1;
                for (final Object param : params) {
                    if (param instanceof Clob) {
                        resource.setClob(i++, ((Clob) param).getCharacterStream());
                    } else if (param instanceof Blob) {
                        resource.setBlob(i++, ((Blob) param).getBinaryStream());
                    } else {
                        resource.setObject(i++, param);
                    }
                }
                final int rowsUpdated = resource.executeUpdate();
                log(format("{} rows updated", rowsUpdated));
                return rowsUpdated;
            }
        });
        closePrepStmtF.apply(stmt);
        return result;
    }

    @Override
    public int getInt(final String sql) {
        final Integer result = getNumber(sql).intValue();
        log(format("Return getInt: {}", result));
        return result;
    }

    @Override
    public long getLong(final String sql) {
        final Long result = getNumber(sql).longValue();
        log(format("Return getLong: {}", result));
        return result;
    }

    private void configure(final ScriptRunner scriptRunner) {
        // All statements are separated by "/" (put on new line as is the Oracle script style)
        scriptRunner.setDelimiter("/");
        scriptRunner.setFullLineDelimiter(true);

        // Abort on error, as once deviated, subsequent stmts may lead the DB to unpredictable state
        scriptRunner.setStopOnError(true);

        // Do not auto commit, leave that to the RelDb user
        scriptRunner.setAutoCommit(false);

        // Log script feedback
        // Aways set log writer otherwise it will dump on System.out
        scriptRunner.setLogWriter(new PrintWriter(new Log4jOutputStream(log.getLogger(), Level.DEBUG)));
        scriptRunner.setErrorLogWriter(new PrintWriter(new Log4jOutputStream(log.getLogger(), Level.ERROR)));
    }

    private String traceCall(final String sql, final Object... params) {
        String callTrace = "Executing " + sql;
        if (params != null) {
            callTrace += " with (";
            for (final Object param : params) {
                callTrace += param + ", ";
            }
            callTrace += ")";
        }
        return callTrace;
    }

    @Override
    public String getUserName() {
        final String connName = resource.access(new Accessor<Connection, String>(getConnection(), null) {
            @Override
            public String access(final Connection resource) throws Exception {
                return resource.getMetaData().getUserName();
            }
        });
        return connName.toLowerCase();
    }

    @Override
    public SqlStrategy getSqlStrategy() {
        return sqlStrategy;
    }

    /**
     * @param rs
     * @return List of rows, each row represented by an associative map (by column names)
     * @throws SQLException
     */
    private List<Map<String, Object>> processResultSet(final ResultSet rs) throws SQLException {
        final List<Map<String, Object>> result = new LinkedList<Map<String, Object>>();
        if (rs == null) {
            return result;
        }
        final int colCount = rs.getMetaData().getColumnCount();
        while (rs.next()) {
            final Map<String, Object> row = new HashMap<String, Object>();
            for (int colIndex = 1; colIndex < colCount + 1; colIndex++) {
                final String colName = rs.getMetaData().getColumnName(colIndex).toLowerCase();
                final Object value = rs.getObject(colIndex);
                row.put(colName, value);
            }
            result.add(row);
        }
        return result;
    }

    private void log(final String sql) {
        log.debug("[{}]: {}", this, sql);
    }

    private void log(final String sql, final List<?> params) {
        log.debug("[{}]: {} - {}", this, sql, params);
    }

    private Number getNumber(final String sql) {
        return ((Number) executeQuery(sql).next().values().iterator().next());
    }

    private void initSqlCache(final boolean cacheSql) {
        prepStmtCache = new ConcurrentHashMap<>();
        if (cacheSql) {
            getPrepStmtF = new Function<String, PreparedStatement>() {
                @Override
                public PreparedStatement apply(final String sql) {
                    PreparedStatement result = prepStmtCache.get(sql);
                    if (result == null) {
                        result = resource.access(new Accessor<Connection, PreparedStatement>(getConnection(), null) {
                            @Override
                            public PreparedStatement access(final Connection resource) throws Exception {
                                return resource.prepareStatement(sql);
                            }
                        });
                        prepStmtCache.put(sql, result);
                    }
                    return result;
                }
            };
            closePrepStmtF = new Function<Statement, Statement>() {
                @Override
                public Statement apply(final Statement stmt) {
                    return stmt;
                }
            };
        } else {
            getPrepStmtF = new Function<String, PreparedStatement>() {
                @Override
                public PreparedStatement apply(final String sql) {
                    return resource.access(new Accessor<Connection, PreparedStatement>(getConnection(), null) {
                        @Override
                        public PreparedStatement access(final Connection resource) throws Exception {
                            return resource.prepareStatement(sql);
                        }
                    });
                }
            };
            closePrepStmtF = new Function<Statement, Statement>() {
                @Override
                public Statement apply(final Statement stmt) {
                    return resource.close(stmt);
                }
            };
        }
    }

    private final Connection conn;
    private final Schema metaInfo;
    private final boolean dbEnableOutput;
    private final boolean cacheSql;
    private DbmsOutputTask dbmsOutputTask;
    private Map<String, PreparedStatement> prepStmtCache;
    private Function<String, PreparedStatement> getPrepStmtF;
    private Function<Statement, Statement> closePrepStmtF;
    private Schema schema;

    private ScriptRunner scriptRunner;
    private String toString;

    private static final Resource resource = new Resource();
    private static final Log4jLogger log = new Log4jLogger(Logger.getLogger(SimpleRelDb.class));
    private Object schemaSingletonLock = 1;
    private final SqlStrategy sqlStrategy;
    private final Collection<String> skipTables;
}
