/**
 * Copyright (c) 2015 Persinity Inc.
 */
package com.persinity.ndt.etlmodule.relational.common;

import static com.persinity.common.StringUtils.format;
import static com.persinity.common.invariant.Invariant.assertArg;
import static com.persinity.common.invariant.Invariant.notNull;
import static java.util.Arrays.asList;

import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;

import org.apache.log4j.Logger;

import com.persinity.common.Resource;
import com.persinity.common.collection.CollectionUtils;
import com.persinity.common.collection.DirectedEdge;
import com.persinity.common.collection.Pool;
import com.persinity.common.db.RelDb;
import com.persinity.common.db.metainfo.Col;
import com.persinity.common.logging.Log4jLogger;
import com.persinity.ndt.db.TransactionId;
import com.persinity.ndt.dbagent.relational.AgentSqlStrategy;
import com.persinity.ndt.dbagent.relational.SchemaInfo;
import com.persinity.ndt.etlmodule.WindowGenerator;
import com.persinity.ndt.etlmodule.relational.RelTransferWindow;
import com.persinity.ndt.transform.EntitiesDag;
import com.persinity.ndt.transform.ParamQryFunc;
import com.persinity.ndt.transform.TransferWindow;

/**
 * Base {@link WindowGenerator} implementation.
 *
 * @author Doichin Yordanov
 */
public class BaseWindowGenerator implements WindowGenerator<RelDb, RelDb> {

    static final List<Col> TRLOG_COLS = asList(new Col(SchemaInfo.COL_TID), new Col(SchemaInfo.COL_LAST_GID),
            new Col(SchemaInfo.COL_TABLE_NAME));

    protected BaseWindowGenerator(final DirectedEdge<Pool<RelDb>, Pool<RelDb>> dataPoolBridge,
            final TidsLeftCntF unprocessedTidsCntF, final EntityDagFunc entityGraphF,
            final AgentSqlStrategy sqlStrategy, final int windowSize) {
        this(dataPoolBridge, new ParamQryFunc(TRLOG_COLS, sqlStrategy.nextWindow()), unprocessedTidsCntF, entityGraphF,
                sqlStrategy, windowSize);
    }

    /**
     * @param dataPoolBridge
     * @param nextWindowF
     *         Function that returns TIDs information for the next window.
     * @param unprocessedTidsCntF
     *         Function that returns the number of unprocessed trlog records.
     * @param entityGraphF
     *         Function that returns entity DAG.
     * @param sqlStrategy
     * @param windowSize
     */
    BaseWindowGenerator(final DirectedEdge<Pool<RelDb>, Pool<RelDb>> dataPoolBridge, final ParamQryFunc nextWindowF,
            final TidsLeftCntF unprocessedTidsCntF, final EntityDagFunc entityGraphF,
            final AgentSqlStrategy sqlStrategy, final int windowSize) {
        notNull(dataPoolBridge);
        notNull(nextWindowF);
        notNull(unprocessedTidsCntF);
        notNull(entityGraphF);
        notNull(sqlStrategy);
        assertArg(windowSize > 0);

        this.dataPoolBridge = dataPoolBridge;
        this.sqlStrategy = sqlStrategy;
        this.nextWindowF = nextWindowF;
        this.entityGraphF = entityGraphF;
        this.unprocessedTidsCntF = unprocessedTidsCntF;
        this.windowSize = windowSize;

        stopRequested = new AtomicBoolean(false);
        forceStopRequested = new AtomicBoolean(false);

        winIt = newWinIterator(nextWindowF, entityGraphF, sqlStrategy);
    }

    /**
     * @return Non thread safe iterator.
     */
    @Override
    public Iterator<TransferWindow<RelDb, RelDb>> iterator() {
        return winIt;
    }

    @Override
    public void stopWhenFeedExhausted() {
        stopRequested.getAndSet(true);
    }

    @Override
    public void forceStop() {
        forceStopRequested.getAndSet(true);
    }

    @Override
    public String toString() {
        if (toString == null) {
            toString = format("{}({}, {}, {}, {})", this.getClass().getSimpleName(), dataPoolBridge, entityGraphF,
                    unprocessedTidsCntF, nextWindowF);
        }
        return toString;
    }

    /**
     * @return The value of the max GID of the last processed transaction.
     */
    public long getGidHead() {
        return gidHead;
    }

    public DirectedEdge<Pool<RelDb>, Pool<RelDb>> getDataPoolBridge() {
        return dataPoolBridge;
    }

    private Iterator<TransferWindow<RelDb, RelDb>> newWinIterator(final ParamQryFunc nextWindowF,
            final EntityDagFunc entityGraphF, final AgentSqlStrategy sqlStrategy) {
        final Iterator<TransferWindow<RelDb, RelDb>> result = new Iterator<TransferWindow<RelDb, RelDb>>() {

            @Override
            public boolean hasNext() {
                return !forceStopRequested.get() && (!stopRequested.get() || hasMoreData());
            }

            @Override
            public TransferWindow<RelDb, RelDb> next() {
                if (!hasNext()) {
                    throw new NoSuchElementException();
                }

                List<TransactionId> srcTids = new LinkedList<>();
                final Set<String> srcEntities = new HashSet<>();

                final Iterator<Map<String, Object>> windowIt = resource.accessAndClose(
                        new Resource.Accessor<RelDb, Iterator<Map<String, Object>>>(dataPoolBridge.src().get(), null) {
                            @Override
                            public Iterator<Map<String, Object>> access(final RelDb resource) throws Exception {
                                final DirectedEdge<RelDb, List<?>> nextWindowQryParams = new DirectedEdge<RelDb, List<?>>(
                                        resource, asList(windowSize));
                                final Iterator<Map<String, Object>> windowIt = nextWindowF.apply(nextWindowQryParams);
                                return windowIt;
                            }
                        });

                long curGidHead = gidHead;
                while (windowIt.hasNext()) {
                    final Map<String, Object> windowInfo = windowIt.next();

                    final TransactionId tid = sqlStrategy.newTransactionId(windowInfo.get(SchemaInfo.COL_TID));
                    srcTids.add(tid);
                    curGidHead = ((Number) windowInfo.get(SchemaInfo.COL_LAST_GID)).longValue();

                    final String entity = (String) windowInfo.get(SchemaInfo.COL_TABLE_NAME);
                    srcEntities.add(entity);
                }

                srcTids = CollectionUtils.deDuplicate(srcTids);
                log.debug("TIDs loaded from {}: {} for {}", dataPoolBridge.src(), srcTids, srcEntities);
                final EntitiesDag dstEntitiesDag = entityGraphF.apply(srcEntities);

                gidHead = curGidHead;
                log.debug("GID head progressed to: {}", gidHead);
                return new RelTransferWindow(dataPoolBridge, srcTids, srcEntities, dstEntitiesDag);
            }

            @Override
            public void remove() {
                throw new UnsupportedOperationException();
            }

        };

        return result;
    }

    @SuppressWarnings("ConstantConditions")
    private boolean hasMoreData() {
        final Long cnt = unprocessedTidsCntF.apply(this);
        log.debug("TIDs left in {}: {}", dataPoolBridge.src(), cnt);
        return cnt > 0;
    }

    private final Iterator<TransferWindow<RelDb, RelDb>> winIt;
    private final int windowSize;
    private long gidHead;

    private final DirectedEdge<Pool<RelDb>, Pool<RelDb>> dataPoolBridge;
    private final AgentSqlStrategy sqlStrategy;
    private final AtomicBoolean stopRequested;
    private final AtomicBoolean forceStopRequested;
    private final ParamQryFunc nextWindowF;
    private final TidsLeftCntF unprocessedTidsCntF;
    private String toString;
    private final EntityDagFunc entityGraphF;
    private final Resource resource = new Resource();

    private final Log4jLogger log = new Log4jLogger(Logger.getLogger(this.getClass()));
}
