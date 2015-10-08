/**
 * Copyright (c) 2015 Persinity Inc.
 */

package com.persinity.common.db.metainfo.impl;

import static com.persinity.common.db.metainfo.Col.toColsMap;
import static com.persinity.common.invariant.Invariant.notEmpty;
import static com.persinity.common.invariant.Invariant.notNull;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;

import com.persinity.common.db.RelDb;
import com.persinity.common.db.SqlStrategy;
import com.persinity.common.db.metainfo.BufferedSchema;
import com.persinity.common.db.metainfo.Col;
import com.persinity.common.db.metainfo.Schema;
import com.persinity.common.db.metainfo.constraint.FK;
import com.persinity.common.db.metainfo.constraint.PK;
import com.persinity.common.logging.Log4jLogger;
import com.persinity.ndt.transform.ParamQryFunc;

/**
 * {@link Schema} that knows how to retrieve PK meta-info only. Non PK retrievers throw {@code UnsupportedOperationException}
 */
public class FkSchema implements Schema {

    public FkSchema(final RelDb db, final BufferedSchema tableSchema, final BufferedSchema pkSchema,
            final ParamQryFunc tabConsF) {
        notNull(db);
        notNull(tabConsF);
        notNull(pkSchema);
        notNull(tableSchema);

        this.db = db;
        this.tabConsF = tabConsF;
        this.pkSchema = pkSchema;
        this.tableSchema = tableSchema;
    }

    @Override
    public Set<Col> getTableCols(final String tableName) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Set<FK> getTableFks(final String tableName) {
        notEmpty(tableName);

        final String tableNameLower = tableName.toLowerCase();
        final Iterator<Map<String, Object>> it = tabConsF
                .apply(PkSchema.toArgs(db, tableName.toUpperCase(), CONS_TYPE_FK));
        final Set<FK> result = new HashSet<>();
        if (!it.hasNext()) {
            log.debug("[{}] Retrieved FK for {} : {}", db, tableNameLower, result);
            return result;
        }

        Map<String, Col> srcColsMap = toColsMap(tableSchema.getTableCols(tableName));

        Set<Col> srcColumns = new HashSet<>();
        String prevFkName = null;
        while (it.hasNext()) {
            final Map<String, Object> row = it.next();
            final String fkName = PkSchema.getConstraintName(row);
            final String dstConsName = getDestConsName(row);
            final String srcColName = PkSchema.getConstraintColName(row);
            srcColumns.add(srcColsMap.get(srcColName));
            if (!fkName.equals(prevFkName)) {
                String dstPkTable = pkSchema.getTableName(dstConsName);
                if (dstPkTable != null) {
                    PK dstPk = pkSchema.getTablePk(dstPkTable);
                    final FK fk = new FK(fkName, tableNameLower, srcColumns, dstPk);
                    result.add(fk);
                } else {
                    // TODO support FK pointing to unique constraint
                    log.warn("Failed to find PK for \"{}\" referenced from FK: \"{}\" in \"{}\"", dstConsName, fkName,
                            tableNameLower);
                }
                srcColumns = new HashSet<>();
            }
            prevFkName = fkName;
        }

        log.debug("[{}] Retrieved FK for {} : {}", db, tableNameLower, result);
        return result;
    }

    @Override
    public Set<String> getTableNames() {
        throw new UnsupportedOperationException();
    }

    @Override
    public PK getTablePk(final String tableName) {
        throw new UnsupportedOperationException();
    }

    @Override
    public String getTableName(final String pkName) {
        throw new UnsupportedOperationException();
    }

    @Override
    public String getUserName() {
        throw new UnsupportedOperationException();
    }

    static String getDestConsName(final Map<String, Object> row) {
        return ((String) row.get(SqlStrategy.COL_REF_CONSTRAINT_NAME)).toLowerCase();
    }

    static final String CONS_TYPE_FK = "R";

    private final RelDb db;
    private final ParamQryFunc tabConsF;
    private final BufferedSchema pkSchema;
    private final BufferedSchema tableSchema;

    private static final Log4jLogger log = new Log4jLogger(Logger.getLogger(FkSchema.class));
}
