/**
 * Copyright (c) 2015 Persinity Inc.
 */
package com.persinity.ndt.dbagent.relational.oracle;

import static com.persinity.common.Config.loadPropsFrom;
import static com.persinity.common.StringUtils.format;
import static com.persinity.common.collection.CollectionUtils.stringListOf;
import static com.persinity.common.invariant.Invariant.assertArg;
import static com.persinity.common.invariant.Invariant.notEmpty;
import static com.persinity.common.invariant.Invariant.notNull;
import static com.persinity.ndt.dbagent.relational.SchemaInfo.COL_LAST_GID;
import static com.persinity.ndt.dbagent.relational.SchemaInfo.COL_STATUS;
import static com.persinity.ndt.dbagent.relational.SchemaInfo.COL_TID;
import static com.persinity.ndt.dbagent.relational.SchemaInfo.TAB_TRLOG;
import static com.persinity.ndt.dbagent.relational.SchemaInfo.TrlogStatusType.R;
import static com.persinity.ndt.dbagent.relational.oracle.DbAgentConfig.DEFAULT_CONFIG_FILE_NAME;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Set;

import com.persinity.common.collection.DirectedEdge;
import com.persinity.common.db.OracleSqlStrategy;
import com.persinity.common.db.SqlUtil;
import com.persinity.common.db.metainfo.And;
import com.persinity.common.db.metainfo.Between;
import com.persinity.common.db.metainfo.Col;
import com.persinity.common.db.metainfo.In;
import com.persinity.common.db.metainfo.Params;
import com.persinity.common.db.metainfo.Params.ParameterCount;
import com.persinity.common.db.metainfo.SqlFilter;
import com.persinity.common.db.metainfo.SqlPredicate;
import com.persinity.common.db.metainfo.constraint.PK;
import com.persinity.ndt.db.TransactionId;
import com.persinity.ndt.dbagent.relational.AgentSqlStrategy;
import com.persinity.ndt.dbagent.relational.SchemaInfo;
import com.persinity.ndt.dbagent.relational.SchemaInfo.ChangeType;
import com.persinity.ndt.dbagent.relational.StringTid;

/**
 * @author Doichin Yordanov
 */
public class OracleAgentSqlStrategy extends OracleSqlStrategy implements AgentSqlStrategy {
    /**
     * Create with default {@link DbAgentConfig}
     */
    public OracleAgentSqlStrategy() {
        this(DB_AGENT_CONFIG);
    }

    /**
     * @param config
     */
    public OracleAgentSqlStrategy(final DbAgentConfig config) {
        CDC_CLOG_TRIGGER_TEMPLATE = config.getClogTriggerTemplate();
        CDC_TRLOG_TRIGGER_TEMPLATE = config.getTrlogTriggerTemplate();
        notEmpty(CDC_CLOG_TRIGGER_TEMPLATE, "CDC_CLOG_TRIGGER_TEMPLATE");
        notEmpty(CDC_TRLOG_TRIGGER_TEMPLATE, "CDC_TRLOG_TRIGGER_TEMPLATE");
    }

    @Override
    public String clogExtractQuery(final String tableName, final List<? extends Col> cols, final String pidColName,
            final int tidCount, final List<ChangeType> ctypes) {
        assert tableName != null && !tableName.trim().isEmpty() && cols != null && !cols.isEmpty() && pidColName != null
                && !pidColName.trim().isEmpty() && tidCount > 0 && ctypes != null && !ctypes.isEmpty() : "PRE";

        final String colClause = SqlUtil.buildColClause(cols);
        final String modPidCol = mod(pidColName, "?");
        final SqlFilter<?> pidFilter = new Between(new Col(modPidCol), new DirectedEdge<String, String>("?", "?"));
        @SuppressWarnings("rawtypes")
        final SqlFilter<?> tidFilter = new In(new Col(COL_TID),
                new Params(tidCount, ParameterCount.ROUNDED_BY_PWR_OF_TWO));
        @SuppressWarnings({ "rawtypes", "unchecked" })
        final SqlFilter<?> ctypeFilter = new In(new Col(SchemaInfo.COL_CTYPE), stringListOf(ctypes));
        final SqlPredicate filter = new And(Arrays.asList(ctypeFilter, pidFilter, tidFilter));
        final String sql = format("SELECT {} FROM {} WHERE {} ORDER BY {}, {}", colClause, tableName, filter,
                pidColName, SchemaInfo.COL_GID);

        return sql;
    }

    @Override
    public String createCdcClogTrigger(final String ndtUserName, final String triggerName, final String tableName,
            final Set<Col> tableCols, final PK tablePk, final String clogName) {
        notEmpty(ndtUserName);
        notEmpty(triggerName);
        notEmpty(tableName);
        notEmpty(clogName);

        if (tablePk == null) {
            throw new RuntimeException(format("Unable to find primary key for table \"{}\"", tableName));
        }

        if (tableCols == null || tableCols.isEmpty()) {
            throw new RuntimeException(format("Unable to find columns for table \"{}\"", tableName));
        }

        final String newPkValues = clogTemplateValues(tableCols, tablePk, ":new", true);
        final String oldPkValues = clogTemplateValues(tableCols, tablePk, ":old", true);
        final String rowValues = clogTemplateValues(tableCols, tablePk, ":new", false);

        final String result = format(CDC_CLOG_TRIGGER_TEMPLATE, triggerName, tableName, ndtUserName, clogName,
                ndtUserName, newPkValues, newPkValues, oldPkValues, rowValues, ndtUserName, clogName, ndtUserName,
                ndtUserName);
        return result;
    }

    @Override
    public String createCdcTrlogTrigger(final String ndtUserName, final String triggerName, final String tableName,
            final String clogName, final String trlogName) {
        assert triggerName != null && !triggerName.isEmpty();
        assert clogName != null && !clogName.isEmpty();
        assert tableName != null && !tableName.isEmpty();

        final String result = format(CDC_TRLOG_TRIGGER_TEMPLATE, triggerName, tableName, ndtUserName, clogName,
                ndtUserName, ndtUserName);
        return result;
    }

    @Override
    public String clogExtractQuery(final String tableName, final List<Col> cols, final List<Col> ids,
            final int tidCount, final List<ChangeType> ctypes) {
        assert tableName != null && !tableName.trim().isEmpty();
        assert cols != null && !cols.isEmpty();
        assert ids != null && !ids.isEmpty();
        assert tidCount > 0;
        assert ctypes != null && !ctypes.isEmpty() : "PRE";

        final String idsClause = SqlUtil.buildColClause(ids);
        final String colClause = SqlUtil.buildColClause(cols);
        final String modValue = hash(ids);

        final SqlFilter<?> pidFilter = new Between(new Col(mod(modValue, "?")), new DirectedEdge<>("?", "?"));

        @SuppressWarnings("rawtypes")
        final SqlFilter<?> tidFilter = new In(new Col(COL_TID),
                new Params(tidCount, ParameterCount.ROUNDED_BY_PWR_OF_TWO));
        @SuppressWarnings({ "rawtypes", "unchecked" })
        final SqlFilter<?> ctypeFilter = new In(new Col(SchemaInfo.COL_CTYPE), stringListOf(ctypes));
        final SqlPredicate filter = new And(Arrays.asList(ctypeFilter, pidFilter, tidFilter));
        final String sql = format("SELECT {} FROM {} WHERE {} ORDER BY {}, {}", colClause, tableName, filter, idsClause,
                SchemaInfo.COL_GID);

        return sql;
    }

    @Override
    public String clogGcStatement(final String clogTableName, final String trlogTableName) {
        // TODO validate is this OK for big clog entries? Also is it Oracle optimized?
        return format("DELETE FROM {} WHERE gid IN "
                        + "(SELECT gid FROM {} LEFT JOIN {} ON ({}.tid = {}.tid AND {}.tab_name = '{}') WHERE {}.last_gid IS NULL)",
                clogTableName, clogTableName, trlogTableName, clogTableName, trlogTableName, trlogTableName,
                clogTableName, trlogTableName);
    }

    /**
     * The value should be of type {@code Class<String>}, otherwise {@link ClassCastException} is thrown.
     */
    @Override
    public TransactionId newTransactionId(final Object value) throws ClassCastException {
        return new StringTid((String) value);
    }

    @Override
    public String toString() {
        return this.getClass().getSimpleName();
    }

    @Override
    public String trlogCleanupStatement(final String trlogTableName, final int tidCount) {
        notEmpty(trlogTableName, "trlogTableName");
        assertArg(tidCount > 0, "Expected positive tidCount: {}", tidCount);

        final SqlFilter<?> tidFilter = new In(new Col(COL_TID),
                new Params(tidCount, ParameterCount.ROUNDED_BY_PWR_OF_TWO));
        final String sql = format("DELETE FROM {} WHERE {}", trlogTableName, tidFilter);
        return sql;
    }

    @Override
    public String trlogExtractQuery(final String trlogTableName, final List<Col> cols, final int tidCount) {
        notEmpty(trlogTableName, "trlogTableName");
        notEmpty(cols, "cols");
        assertArg(tidCount > 0, "Expected positive tidCount: {}", tidCount);
        final String colClause = SqlUtil.buildColClause(cols);
        final SqlFilter<?> tidFilter = new In(new Col(COL_TID),
                new Params(tidCount, ParameterCount.ROUNDED_BY_PWR_OF_TWO));
        final String sql = format("SELECT {} FROM {} WHERE {} ORDER BY {}", colClause, trlogTableName, tidFilter,
                COL_LAST_GID);
        return sql;
    }

    @Override
    public String trlogUpdateStatus(final String trlogTableName, final SchemaInfo.TrlogStatusType status,
            final int tidCount) {
        notEmpty(trlogTableName);
        notNull(status);
        assertArg(tidCount > 0, "Expected positive tidCount: {}", tidCount);

        final SqlFilter<?> tidFilter = new In(new Col(COL_TID),
                new Params(tidCount, ParameterCount.ROUNDED_BY_PWR_OF_TWO));
        final String sql = format("UPDATE {} SET {} = '{}' WHERE {}", trlogTableName, COL_STATUS, status,
                tidFilter);
        return sql;
    }

    @Override
    public boolean equals(final Object obj) {
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof OracleAgentSqlStrategy)) {
            return false;
        }
        final OracleAgentSqlStrategy that = (OracleAgentSqlStrategy) obj;
        return CDC_CLOG_TRIGGER_TEMPLATE.equals(that.CDC_CLOG_TRIGGER_TEMPLATE) && CDC_TRLOG_TRIGGER_TEMPLATE
                .equals(that.CDC_TRLOG_TRIGGER_TEMPLATE);
    }

    @Override
    public int hashCode() {
        return Objects.hash(CDC_CLOG_TRIGGER_TEMPLATE, CDC_CLOG_TRIGGER_TEMPLATE);
    }

    private String clogTemplateValues(final Set<Col> tableCols, final PK tablePk, final String cursor,
            final boolean primaryKeyCols) {
        final StringBuilder sb = new StringBuilder();
        boolean firstRowFlg = true;
        for (final Col col : tableCols) {
            final boolean isPkCol = tablePk.getColumns().contains(col);
            if (primaryKeyCols && isPkCol || !primaryKeyCols && !isPkCol) {
                if (!firstRowFlg) {
                    sb.append("\t\t\t");
                }
                sb.append("l_clog_rec.").append(col.getName()).append(" := ").append(cursor).append(".")
                        .append(col.getName()).append("; \n");
                firstRowFlg = false;
            }
        }
        final String newValues = sb.toString();
        return newValues;
    }

    @Override
    public String getMaxGidStatement() {
        return format("SELECT {} FROM {}", max("last_gid"), "trlog");
    }

    @Override
    public String nextWindow() {
        return NEXT_WINDOW_SQL;
    }

    @Override
    public String countUnprocessedTids() {
        return TRLOG_COUNT_REMAINING_SQL;
    }

    private static final String NEXT_WINDOW_SQL =
            "SELECT " + TAB_TRLOG + "." + COL_TID + ", " + TAB_TRLOG + "." + COL_LAST_GID + ", " + TAB_TRLOG
                    + ".tab_name\n" + " FROM \n" + "  (SELECT * FROM (SELECT " + COL_TID + ", MAX(" + COL_LAST_GID
                    + ") AS max_last_gid FROM " + TAB_TRLOG + " WHERE " + COL_STATUS + " = '" + R + "' GROUP BY "
                    + COL_TID + " ORDER BY max_last_gid) WHERE rownum <= ?) torder\n" + "  INNER JOIN " + TAB_TRLOG
                    + " ON (torder." + COL_TID + " = " + TAB_TRLOG + "." + COL_TID + ")\n"
                    + " ORDER BY torder.max_last_gid, " + TAB_TRLOG + "." + COL_LAST_GID;
    private static final String TRLOG_COUNT_REMAINING_SQL =
            "SELECT COUNT(DISTINCT " + COL_TID + ") AS cnt FROM " + TAB_TRLOG + " WHERE " + COL_STATUS + " = '" + R
                    + "'";
    private static final DbAgentConfig DB_AGENT_CONFIG = new DbAgentConfig(loadPropsFrom(DEFAULT_CONFIG_FILE_NAME),
            DEFAULT_CONFIG_FILE_NAME);

    private final String CDC_CLOG_TRIGGER_TEMPLATE;

    private final String CDC_TRLOG_TRIGGER_TEMPLATE;
}
