/**
 * Copyright (c) 2015 Persinity Inc.
 */
package com.persinity.ndt.dbagent.relational.oracle;

import static com.persinity.common.StringUtils.format;
import static com.persinity.common.invariant.Invariant.notEmpty;

import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

import com.persinity.common.db.Trimmer;
import com.persinity.common.db.metainfo.Col;
import com.persinity.common.db.metainfo.Schema;
import com.persinity.common.db.metainfo.constraint.FK;
import com.persinity.common.db.metainfo.constraint.PK;
import com.persinity.common.invariant.NotEmpty;
import com.persinity.common.invariant.NotNull;
import com.persinity.ndt.dbagent.relational.SchemaInfo;

/**
 * Provides Oracle schema meta information. Thread-safe.<BR>
 * Lazy-loads the needed information from the DB and caches it. Create new instance in order to get in-flight schema
 * changes.
 *
 * @author Doichin Yordanov
 */
public class OracleSchemaInfo implements SchemaInfo {

    static final String TYPE_GID = "NUMBER(20,0)";
    static final String TYPE_TID = "VARCHAR2(40)";
    static final String TRG_CLOG_PREFIX = "trg_clog_";
    static final String TRG_TRLOG_PREFIX = "trg_trlog_";
    static final String CLOG_PREFIX = "clog_";

    /**
     * @param schema
     * @param trimmer
     * @param maxNameLength
     *         Maximum length of DB object name.
     */
    public OracleSchemaInfo(final Schema schema, final Trimmer trimmer, final int maxNameLength) {
        new NotNull("schema", "trimmer").enforce(schema, trimmer);
        this.schema = schema;
        this.maxNameLength = maxNameLength;
        this.trimmer = trimmer;

        // The following collections are mutated only through idempotent ops.
        // Hence data integrity is guaranteed, no need for thread sync.
        // Just sync at structure level for data consistency.
        table2RowTriggerNameMap = Collections.synchronizedMap(new HashMap<String, String>());
        table2StmtTriggerNameMap = Collections.synchronizedMap(new HashMap<String, String>());
        table2ClogNameMap = Collections.synchronizedMap(new HashMap<String, String>());
    }

    @Override
    public Set<String> getTableNames() {
        synchronized (this) { // non-idempotent op, guarantee data integrity through thread sync.
            if (tableNames == null || tableNames.size() == 0) {
                tableNames = schema.getTableNames();
            }
        }
        return tableNames;
    }

    /**
     * Generates DB object name by joining prefix and suffix.<BR>
     * If the resulting string is lengthier than the maximum DB name length, then the suffix is trimmed and unique
     * counter is added to guarantee that the trimmed name is unique.
     *
     * @param prefix
     * @param suffix
     * @return
     */
    @Override
    public String newName(final String prefix, final String suffix) {
        new NotNull("prefix", "suffix").enforce(prefix, suffix);
        final String result = trimmer.trim(prefix + suffix, maxNameLength);
        return result;
    }

    /**
     * Returns a change log name by given tracked table name.
     *
     * @param tableName
     * @return
     */
    @Override
    public String getClogTableName(final String tableName) {
        new NotEmpty("tableName").enforce(tableName);

        String clogName = null;
        synchronized (table2ClogNameMap) {
            clogName = table2ClogNameMap.get(tableName);
            if (clogName == null) {
                clogName = newName(CLOG_PREFIX, tableName);
                table2ClogNameMap.put(tableName, clogName);
            }
        }
        return clogName;
    }

    @Override
    public String getClogTriggerName(final String tableName) {
        new NotEmpty("tableName").enforce(tableName);

        String triggerName = null;
        synchronized (table2RowTriggerNameMap) {
            triggerName = table2RowTriggerNameMap.get(tableName);
            if (triggerName == null) {
                triggerName = newName(TRG_CLOG_PREFIX, tableName);
                table2RowTriggerNameMap.put(tableName, triggerName);
            }
        }
        return triggerName;
    }

    @Override
    public Set<Col> getTableCols(final String tableName) {
        notEmpty(tableName);
        return schema.getTableCols(tableName);
    }

    @Override
    public PK getTablePk(String tableName) {
        notEmpty(tableName);
        return schema.getTablePk(tableName);
    }

    @Override
    public Set<FK> getTableFks(final String tableName) {
        notEmpty(tableName);
        return schema.getTableFks(tableName);
    }

    @Override
    public Set<Col> getClogTableCols(final String tableName) {
        new NotEmpty("tableName").enforce(tableName);
        final Set<Col> cols = new LinkedHashSet<Col>();
        cols.add(new Col(COL_GID, TYPE_GID, false));
        cols.add(new Col(COL_TID, TYPE_TID, false));
        cols.add(new Col(COL_CTYPE, "CHAR(1)", false));
        cols.addAll(getTableCols(tableName));
        return Collections.unmodifiableSet(cols);
    }

    @Override
    public String getTrlogTriggerName(final String tableName) {
        new NotEmpty("tableName").enforce(tableName);

        String triggerName;
        synchronized (table2StmtTriggerNameMap) {
            triggerName = table2StmtTriggerNameMap.get(tableName);
            if (triggerName == null) {
                triggerName = newName(TRG_TRLOG_PREFIX, tableName);
                table2StmtTriggerNameMap.put(tableName, triggerName);
            }
        }
        return triggerName;
    }

    @Override
    public String toString() {
        if (toString == null) {
            toString = format("{}@{}({})", this.getClass().getSimpleName(), Integer.toHexString(hashCode()), schema);
        }
        return toString;
    }

    private final Schema schema;
    private final Trimmer trimmer;
    private final Map<String, String> table2RowTriggerNameMap;
    private final Map<String, String> table2StmtTriggerNameMap;
    private final Map<String, String> table2ClogNameMap;
    private final int maxNameLength;

    private Set<String> tableNames;
    private String toString;
}
