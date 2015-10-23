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
package com.persinity.ndt.datamutator.load;

import static com.persinity.common.invariant.Invariant.assertState;
import static com.persinity.common.invariant.Invariant.notEmpty;
import static com.persinity.common.invariant.Invariant.notNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.Set;

import org.apache.log4j.Logger;

import com.persinity.common.collection.CombinationIterator;
import com.persinity.common.db.metainfo.Col;
import com.persinity.common.db.metainfo.constraint.FK;
import com.persinity.common.logging.Log4jLogger;
import com.persinity.ndt.datamutator.reldb.RelDbEntityBase;
import com.persinity.ndt.datamutator.reldb.RelDbTypeFactory;

/**
 * @author Ivan Dachev
 */
public class EntityPoolUtil {

    public EntityPoolUtil(final EntityPool entityPool, final RelDbTypeFactory typeFactory) {
        this.entityPool = entityPool;
        this.typeFactory = typeFactory;
    }

    /**
     * @return {@link EntityPool}
     */
    public EntityPool getEntityPool() {
        return entityPool;
    }

    /**
     * @return {@link RelDbTypeFactory}
     */
    public RelDbTypeFactory getTypeFactor() {
        return typeFactory;
    }

    /**
     * @param pkCols
     * @param fks
     * @return true if all PK columns are FKs
     */
    public boolean arePkValuesOnlyFromFks(final Set<Col> pkCols, final Set<FK> fks) {
        notEmpty(pkCols);
        notNull(fks);

        if (fks.size() == 0) {
            return false;
        }

        boolean res = true;
        for (final Col col : pkCols) {
            final FK fk = getFkForCol(fks, col);
            if (fk == null) {
                res = false;
                break;
            }
        }
        return res;
    }

    /**
     * Generate composite PK values by finding the linked FKs that are unique and not exist in the pool.
     *
     * @param table
     * @param pkCols
     * @param fks
     * @return
     */
    public List<Object> generateUniquePkValuesPointingToFks(final String table, final Set<Col> pkCols,
            final Set<FK> fks) {
        notEmpty(table);
        notEmpty(pkCols);
        notEmpty(fks);

        List<List<Object>> colToFkValues = new ArrayList<>();
        for (final Col col : pkCols) {
            final FK fk = getFkForCol(fks, col);
            assertState(fk != null);
            final List<Object> fkValues = getFkValues(fk);
            if (fkValues.size() == 0) {
                // no fk values return empty
                return Collections.emptyList();
            }
            colToFkValues.add(fkValues);
        }

        // usually there is always unique constraint on PK composite columns
        // so we will check if only one column match then thread the newValue as
        // not unique and keep searching
        final List<EntityBase> tableEntities = entityPool.getEntities(table);
        final Set<Object> allIdValues = new HashSet<>();
        for (final EntityBase entity : tableEntities) {
            final List<Object> idValues = entity.getId();
            allIdValues.addAll(idValues);
        }

        List<Object> res = Collections.emptyList();
        final CombinationIterator<Object> iter = new CombinationIterator<>(colToFkValues);
        while (iter.hasNext()) {
            final List<Object> newValues = iter.next();
            boolean found = false;
            for (final Object newValue : newValues) {
                if (allIdValues.contains(newValue)) {
                    found = true;
                    break;
                }
            }
            if (!found) {
                res = newValues;
                break;
            }
        }
        return res;
    }

    /**
     * Generate PK values by using the given ID.
     * If one of the PK composite ID is FK then it will search for value in the pool.
     *
     * @param pkCols
     * @param fks
     * @param id
     * @return
     */
    public List<Object> generatePkValues(final String table, final Set<Col> pkCols, final Set<FK> fks, final long id) {
        notEmpty(table);
        notEmpty(pkCols);
        notNull(fks);

        if (arePkValuesOnlyFromFks(pkCols, fks)) {
            return generateUniquePkValuesPointingToFks(table, pkCols, fks);
        }

        final List<Object> res = new ArrayList<>();
        for (final Col col : pkCols) {
            Object value = null;
            final FK fk = getFkForCol(fks, col);
            if (fk != null) {
                final List<Object> fkValues = getFkValues(fk);
                if (fkValues.size() > 0) {
                    value = fkValues.get(random.nextInt(fkValues.size()));
                    log.debug("Get value: {} for PK col: {} from fk: {}", value, col, fk);
                }
            } else {
                value = typeFactory.formatValue(col.getType(), id, null);
            }
            if (value == null) {
                return Collections.emptyList();
            }
            res.add(value);
        }
        return res;
    }

    /**
     * @param fks
     * @param col
     * @return
     */
    public FK getFkForCol(final Set<FK> fks, final Col col) {
        notNull(fks);
        notNull(col);

        FK res = null;
        for (final FK fk : fks) {
            if (fk.getColumns().contains(col)) {
                res = fk;
                break;
            }
        }
        return res;
    }

    /**
     * @param fks
     * @return
     */
    public boolean haveFksEntities(final Set<FK> fks) {
        notEmpty(fks);

        boolean haveEntities = true;
        for (FK fk : fks) {
            if (entityPool.getEntity(fk.getDstConstraint().getTable()) == null) {
                haveEntities = false;
                break;
            }
        }
        return haveEntities;
    }

    /**
     * Search for all values for given FK destination table from the pool.
     *
     * @param fk
     * @return
     */
    public List<Object> getFkValues(final FK fk) {
        notNull(fk);

        final List<Object> res = new ArrayList<>();
        final List<EntityBase> fkEntities = entityPool.getEntities(fk.getDstConstraint().getTable());
        for (EntityBase fkEntity : fkEntities) {
            final Object value = getFkValue((RelDbEntityBase) fkEntity, fk);
            if (value != null) {
                res.add(value);
            }
        }
        return res;
    }

    /**
     * @param fk
     * @return
     */
    public Object getRandomFkValue(final FK fk) {
        notNull(fk);

        final List<EntityBase> fkEntities = entityPool.getEntities(fk.getDstConstraint().getTable());
        if (fkEntities.size() == 0) {
            return null;
        }

        final EntityBase fkEntity = fkEntities.get(random.nextInt(fkEntities.size()));
        return getFkValue((RelDbEntityBase) fkEntity, fk);
    }

    /**
     * @param dstFkEntity
     * @param fk
     * @return
     */
    private Object getFkValue(final RelDbEntityBase dstFkEntity, final FK fk) {
        notNull(dstFkEntity);
        notNull(fk);

        if (fk.getDstConstraint().getColumns().size() > 1) {
            log.warning("Unsupported foreign key: {} with composite primary key", dstFkEntity);
            return null;
        }

        final Col dstFkCol = fk.getDstConstraint().getColumns().iterator().next();

        Object res = null;
        final Set<Col> pkCols = dstFkEntity.getPk().getColumns();
        final Iterator<Object> iterIdValues = dstFkEntity.getId().iterator();
        for (final Col pkCol : pkCols) {
            final Object idValue = iterIdValues.next();
            if (pkCol.equals(dstFkCol)) {
                res = idValue;
                break;
            }
        }

        if (res == null) {
            log.warning("Destination FK column {} is not from the PK columns of {}", fk, dstFkEntity);
        }

        return res;
    }

    private final EntityPool entityPool;
    private final RelDbTypeFactory typeFactory;

    private final Random random = new Random(System.nanoTime());

    private static final Log4jLogger log = new Log4jLogger(Logger.getLogger(EntityPoolUtil.class));
}
