/**
 * Copyright (c) 2015 Persinity Inc.
 */
package com.persinity.ndt.controller;

import static com.persinity.common.StringUtils.format;
import static com.persinity.common.ThreadUtil.sleep;
import static com.persinity.common.ThreadUtil.sleepSeconds;
import static com.persinity.common.collection.CollectionUtils.implode;
import static com.persinity.common.invariant.Invariant.notEmpty;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;

import com.google.common.base.Function;
import com.persinity.common.collection.DirectedEdge;
import com.persinity.common.db.Closeable;
import com.persinity.common.db.RelDb;
import com.persinity.common.db.RelDbUtil;
import com.persinity.common.db.Trimmer;
import com.persinity.common.db.metainfo.Col;
import com.persinity.common.db.metainfo.constraint.PK;
import com.persinity.common.logging.Log4jLogger;
import com.persinity.ndt.db.RelDbPoolFactory;
import com.persinity.ndt.dbagent.relational.AgentSqlStrategy;
import com.persinity.ndt.dbagent.relational.SchemaInfo;
import com.persinity.test.TestDbUtil;

/**
 * @author Ivan Dachev
 */
public class TestNdtUtil implements Closeable {

    public TestNdtUtil(final RelDbPoolFactory relDbPoolFactory, final AgentSqlStrategy sqlStrategy) {
        final Trimmer trimmer = new Trimmer();
        final int maxNameLength = sqlStrategy.getMaxNameLength();

        this.relDbPoolFactory = relDbPoolFactory;

        this.appBridge = RelDbUtil.getBridge(relDbPoolFactory.appBridge());
        this.ndtBridge = RelDbUtil.getBridge(relDbPoolFactory.ndtBridge());
        this.sqlStrategy = sqlStrategy;
    }

    public void mutateSrcData(final List<List<String>> sqlBatches) {
        mutateSrcData(sqlBatches, 0);
    }

    public void mutateSrcData(final List<List<String>> sqlBatches, final long sleepBetweenBatchMs) {
        notEmpty(sqlBatches);

        final RelDb appSrcDb = appBridge.src();
        for (final List<String> sqlBatch : sqlBatches) {
            notEmpty(sqlBatch);
            for (final String sql : sqlBatch) {
                appSrcDb.executeDmdl(sql);
            }
            appSrcDb.commit();
            if (sleepBetweenBatchMs > 0) {
                sleep(sleepBetweenBatchMs);
            }
        }
    }

    public void doInitialCopy() {
        final RelDb srcAppDb = appBridge.src();
        final RelDb dstAppDb = appBridge.dst();

        final Set<String> tables = srcAppDb.metaInfo().getTableNames();
        assertTrue(tables.size() > 0);
        for (String table : tables) {
            final String qry = sqlStrategy.selectAllStatement(table);
            final Iterator<Map<String, Object>> res = srcAppDb.executeQuery(qry);
            final List<Col> cols = new LinkedList<>(srcAppDb.metaInfo().getTableCols(table));
            final String st = sqlStrategy.insertStatement(table, cols);
            int copiedRows = 0;
            while (res.hasNext()) {
                Map<String, Object> rows = res.next();
                final List<Object> params = new ArrayList<>();
                for (Col col : cols) {
                    params.add(rows.get(col.getName()));
                }
                copiedRows += dstAppDb.executePreparedDml(st, params);
            }
            log.info("Copied rows {} for {}", copiedRows, table);
        }
        dstAppDb.commit();
    }

    public void verifyDestEntityDataAfterDm() {
        final RelDb srcAppDb = appBridge.src();
        final RelDb dstAppDb = appBridge.dst();

        final Set<String> tables = srcAppDb.metaInfo().getTableNames();
        assertTrue(tables.size() > 0);
        for (String table : tables) {
            final Set<Col> cols = srcAppDb.metaInfo().getTableCols(table);
            final PK pk = srcAppDb.metaInfo().getTablePk(table);
            final List<Col> colsList;
            if (pk != null) {
                colsList = new ArrayList<>();
                for (Col pkCol : pk.getColumns()) {
                    colsList.add(pkCol);
                }
                for (Col col : cols) {
                    if (!colsList.contains(col)) {
                        colsList.add(col);
                    }
                }
            } else {
                colsList = new ArrayList<>(cols);
            }

            filterCols(colsList);
            final String colList = implode(colsList, ", ", new Function<Col, String>() {
                @Override
                public String apply(final Col col) {
                    return format("{}", col.getName());
                }
            });
            String qry = format("SELECT {} FROM {} ORDER BY {}", colList, table, colList);
            final int compared = TestDbUtil.compareRs(srcAppDb, dstAppDb, qry, qry);
            log.info("Compared {} results for {}", compared, table);
        }
    }

    public void verifyEmptyDestEntityData() {
        final RelDb dstAppDb = appBridge.dst();
        final Set<String> tables = dstAppDb.metaInfo().getTableNames();
        assertTrue(tables.size() > 0);
        for (String table : tables) {
            final String qry = sqlStrategy.selectAllStatement(table);
            final Iterator<Map<String, Object>> res = dstAppDb.executeQuery(qry);
            assertFalse(format("Expect empty table: {}", table), res.hasNext());
        }
    }

    public int getTrlogRowsCount(final RelDb ndtDb) {
        final String qry = format("SELECT COUNT(1) FROM {}", SchemaInfo.TAB_TRLOG);
        return ndtDb.getInt(qry);
    }

    /**
     * Wait for no change on the src/dst TRLOG rows count for timeoutSeconds.
     * When the event is detected the method will fail if the src/dst TRLOG are not empty.
     *
     * @param timeoutSeconds
     */
    public void waitForEmptyWindows(final int timeoutSeconds) {
        int lastSrcCount = getTrlogRowsCount(ndtBridge.src());
        int lastDstCount = getTrlogRowsCount(ndtBridge.dst());
        long lastTime = System.currentTimeMillis();
        while (true) {
            sleepSeconds(timeoutSeconds / 10);

            final int currentSrcCount = getTrlogRowsCount(ndtBridge.src());
            final int currentDstCount = getTrlogRowsCount(ndtBridge.dst());

            if (currentSrcCount != lastSrcCount || currentDstCount != lastDstCount) {
                lastSrcCount = currentSrcCount;
                lastDstCount = currentDstCount;
                lastTime = System.currentTimeMillis();
                continue;
            }

            final long currentTime = System.currentTimeMillis();
            if (lastTime > currentTime) {
                // handle clock shift just reset time
                lastTime = currentTime;
                continue;
            }

            if ((currentTime - lastTime) > timeoutSeconds * 1000L) {
                break;
            }
        }

        if (getTrlogRowsCount(ndtBridge.src()) != 0) {
            fail("Expected src TRLOG to be empty");
        }

        if (getTrlogRowsCount(ndtBridge.dst()) != 0) {
            fail("Expected dst TRLOG to be empty");
        }
    }

    public DirectedEdge<RelDb, RelDb> getAppBridge() {
        return appBridge;
    }

    public DirectedEdge<RelDb, RelDb> getNdtBridge() {
        return ndtBridge;
    }

    /**
     * BLOBs/CLOBs are causing comparison difficulties.
     *
     * @param colsList
     */
    private void filterCols(final List<Col> colsList) {
        final Iterator<Col> it = colsList.iterator();
        while (it.hasNext()) {
            final Col col = it.next();
            final String type = col.getType();
            if (type.equalsIgnoreCase("blob") || type.equalsIgnoreCase("clob")) {
                it.remove();
            }
        }
    }

    @Override
    public void close() throws RuntimeException {
        RelDbUtil.closeBridge(relDbPoolFactory.appBridge(), appBridge);
        RelDbUtil.closeBridge(relDbPoolFactory.ndtBridge(), ndtBridge);
    }

    private final DirectedEdge<RelDb, RelDb> appBridge;
    private final DirectedEdge<RelDb, RelDb> ndtBridge;
    private final AgentSqlStrategy sqlStrategy;

    private static final Log4jLogger log = new Log4jLogger(Logger.getLogger(TestNdtUtil.class));
    private final RelDbPoolFactory relDbPoolFactory;
}
