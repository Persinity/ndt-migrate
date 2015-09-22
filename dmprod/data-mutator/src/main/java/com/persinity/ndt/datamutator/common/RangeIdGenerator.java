/*
 * Copyright (c) 2015 Persinity Inc.
 */

package com.persinity.ndt.datamutator.common;

import static com.persinity.common.invariant.Invariant.assertArg;
import static com.persinity.common.invariant.Invariant.notNull;

import org.apache.log4j.Logger;

import com.persinity.common.StringUtils;
import com.persinity.common.collection.DirectedEdge;
import com.persinity.common.db.RelDb;
import com.persinity.common.db.metainfo.Col;
import com.persinity.common.db.metainfo.Schema;
import com.persinity.common.db.metainfo.constraint.PK;
import com.persinity.common.logging.Log4jLogger;

/**
 * @author Ivo Yanakiev
 */
public class RangeIdGenerator implements IdGenerator {

    public RangeIdGenerator(final DirectedEdge<Long, Long> range, final long increment, final RelDb db) {
        notNull(range);
        notNull(db);
        assertArg(increment > 0);

        this.start = range.src();
        this.stop = range.dst();
        this.increment = increment;
        this.sequence = currentId(start, stop, db);
    }

    @Override
    public synchronized long getNext() {
        if (this.sequence >= stop) {
            throw new RuntimeException(StringUtils.format("Sequence {} exhausted", this));
        }
        this.sequence += this.increment;
        return this.sequence;
    }

    @Override
    public String toString() {
        return StringUtils.format("{}[{}, {}]:{}", this.getClass().getSimpleName(), start, stop, sequence);
    }

    /**
     * Recreate initial transaction ID generator by choosing the maximum ID from across all tables.
     */
    private static long currentId(final long start, final long stop, final RelDb db) {

        final Schema schema = db.metaInfo();
        long result = start;

        for (String table : schema.getTableNames()) {

            PK pk = schema.getTablePk(table);
            if (pk != null) {
                for (Col column : pk.getColumns()) {

                    final String query = StringUtils.format("SELECT NVL(MAX({}), 0) FROM {} WHERE {} BETWEEN {} AND {}",
                            // exclude idRangeMax value from the select
                            column.getName(), table, column.getName(), start, stop - 1);
                    log.debug("Max current id query: {}", query);

                    try {
                        final Object currentRaw = db.executeQuery(query).next().values().iterator().next();
                        final long current = Long.parseLong(currentRaw.toString());
                        result = Math.max(result, current);
                    } catch (Exception e) {
                        throw new RuntimeException("Exception during max current id query.", e.getCause());
                    }

                }
            }
        }
        log.info("Current max id for range [{}, {}]: {}", start, stop, result);
        return result;
    }

    private long sequence;
    private final long start;
    private final long stop;
    private final long increment;

    private static final Log4jLogger log = new Log4jLogger(Logger.getLogger(RangeIdGenerator.class));
}
