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
package com.persinity.ndt.datamutator.reldb;

import static com.persinity.common.StringUtils.format;
import static com.persinity.common.invariant.Invariant.assertArg;
import static com.persinity.common.invariant.Invariant.assertState;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;

import org.apache.log4j.Logger;

import com.persinity.common.db.RelDb;
import com.persinity.common.db.SqlStrategy;
import com.persinity.common.db.metainfo.Col;
import com.persinity.common.logging.Log4jLogger;
import com.persinity.ndt.datamutator.load.EntityBase;
import com.persinity.ndt.datamutator.load.EntitySession;

/**
 * @author Ivan Dachev
 */
public class RelDbEntitySession implements EntitySession {
    public RelDbEntitySession(final RelDb db, final SqlStrategy sqlStrategy,
            final RelDbEntityFactory relDbEntityFactory) {
        this.db = db;
        this.sqlStrategy = sqlStrategy;
        this.relDbEntityFactory = relDbEntityFactory;
    }

    @Override
    public void insert(final EntityBase entity) {
        log.debug("Insert {}", entity);

        assertArg(entity instanceof RelDbEntityBase);
        final RelDbEntityBase relDbEntity = (RelDbEntityBase) entity;

        final Map<Col, Object> mutateData = relDbEntity.getMutatedData();
        if (isMissingRequiredColValue(relDbEntity, mutateData)) {
            relDbEntityFactory.banTable(relDbEntity.getTable());
            throw new RuntimeException(format("Unable to insert into table: \"{}\"", relDbEntity.getTable()));
        }

        removeRandomCols(mutateData, true);

        final Set<Col> pkCols = relDbEntity.getPk().getColumns();
        final List<Object> pkValues = relDbEntity.getId();
        assertState(pkCols.size() == pkValues.size(), "Expected PK columns/values size match {} == {} for {}", pkCols,
                pkValues, relDbEntity.getTable());

        List<Col> mutatedCols = new ArrayList<>(pkCols);

        if (mutateData.size() > 0) {
            mutatedCols.addAll(mutateData.keySet());
        }

        List<Object> values = new ArrayList<>();
        values.addAll(pkValues);

        if (mutateData.size() > 0) {
            for (Col col : mutatedCols) {
                final Object value = mutateData.get(col);
                if (value != null) {
                    values.add(value);
                }
            }
        }

        assertState(mutatedCols.size() == values.size(), "Expected size match {} == {} for {}", mutatedCols, values,
                relDbEntity.getTable());

        final String sql = sqlStrategy.insertStatement(relDbEntity.getTable(), mutatedCols);
        db.executePreparedDml(sql, values);
    }

    @Override
    public void update(final EntityBase entity) {
        log.debug("Update {}", entity);

        assertArg(entity instanceof RelDbEntityBase);
        final RelDbEntityBase relDbEntity = (RelDbEntityBase) entity;

        final Map<Col, Object> mutateData = relDbEntity.getMutatedData();
        if (mutateData.size() == 0) {
            // cannot update tables that have only primary keys or we could not get unique values
            return;
        }

        removeRandomCols(mutateData, false);

        final Set<Col> pkCols = relDbEntity.getPk().getColumns();
        final List<Object> pkValues = relDbEntity.getId();
        assertState(pkCols.size() == pkValues.size(), "Expected PK columns/values size match {} == {} for {}", pkCols,
                pkValues, relDbEntity.getTable());

        List<Col> mutatedCols = new ArrayList<>();
        mutatedCols.addAll(mutateData.keySet());

        List<Col> idCols = new ArrayList<>();
        idCols.addAll(pkCols);

        List<Object> values = new ArrayList<>();
        for (Col col : mutatedCols) {
            final Object value = mutateData.get(col);
            if (value != null) {
                values.add(value);
            }
        }

        values.addAll(pkValues);

        assertState(mutatedCols.size() + idCols.size() == values.size(), "Expected size match {} + {} == {} for {}",
                mutatedCols, idCols, values, relDbEntity.getTable());

        final String sql = sqlStrategy.updateStatement(relDbEntity.getTable(), mutatedCols, idCols);
        db.executePreparedDml(sql, values);
    }

    @Override
    public List<EntityBase> delete(final EntityBase entity) {
        final List<EntityBase> deletePlan = new ArrayList<>();
        relDbEntityFactory.findCascadeDeleteEntities(entity, deletePlan);
        for (final EntityBase entityToDelete : deletePlan) {
            deleteEntity(entityToDelete);
        }
        return deletePlan;
    }

    @Override
    public void openTransaction() {
        // do nothing the RelDb is in auto commit off
    }

    @Override
    public void commitTransaction() {
        db.commit();
    }

    @Override
    public void rollbackTransaction() {
        db.rollback();
    }

    @Override
    public void close() {
        db.rollback();
    }

    private void deleteEntity(final EntityBase entity) {
        log.debug("Delete {}", entity);

        assertArg(entity instanceof RelDbEntityBase);
        final RelDbEntityBase relDbEntity = (RelDbEntityBase) entity;

        final Set<Col> pkCols = relDbEntity.getPk().getColumns();
        final List<Object> pkValues = relDbEntity.getId();
        assertState(pkCols.size() == pkValues.size(), "Expected PK columns/values size match {} == {} for {}", pkCols,
                pkValues, relDbEntity.getTable());

        List<Col> idCols = new ArrayList<>(pkCols);
        List<Object> values = new ArrayList<>(pkValues);
        final String sql = sqlStrategy.deleteStatement(relDbEntity.getTable(), idCols);
        db.executePreparedDml(sql, values);
    }

    private boolean isMissingRequiredColValue(final RelDbEntityBase relDbEntity, final Map<Col, Object> mutateData) {
        for (Col col : relDbEntity.getCols()) {
            if (relDbEntity.getPk().getColumns().contains(col)) {
                continue;
            }
            if (!mutateData.containsKey(col) && !col.isNullAllowed()) {
                log.warning("Not support type {} for not null column {} for table {}", col.getType(), col.getName(),
                        relDbEntity.getTable());
                return true;
            }
        }
        return false;
    }

    private void removeRandomCols(final Map<Col, Object> mutateData, final boolean keepNullCol) {
        if (mutateData.size() > 1) {
            final int toNotInsert = random.nextInt(mutateData.size() - 1);
            final List<Col> cols = new ArrayList<>(mutateData.keySet());
            Collections.shuffle(cols);
            for (int i = 0; i < toNotInsert; i++) {
                final Col col = cols.get(i);
                if (!keepNullCol || col.isNullAllowed()) {
                    mutateData.remove(col);
                }
            }
            assertState(mutateData.size() > 0);
        }
    }

    private final RelDb db;
    private final SqlStrategy sqlStrategy;
    private final RelDbEntityFactory relDbEntityFactory;

    private static final Random random = new Random(System.nanoTime());

    private static final Log4jLogger log = new Log4jLogger(Logger.getLogger(RelDbEntitySession.class));
}
