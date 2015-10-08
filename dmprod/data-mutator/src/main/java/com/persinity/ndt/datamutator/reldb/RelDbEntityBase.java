/**
 * Copyright (c) 2015 Persinity Inc.
 */
package com.persinity.ndt.datamutator.reldb;

import static com.persinity.common.StringUtils.format;
import static com.persinity.common.invariant.Invariant.notEmpty;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import org.apache.log4j.Logger;

import com.persinity.common.db.metainfo.Col;
import com.persinity.common.db.metainfo.constraint.FK;
import com.persinity.common.db.metainfo.constraint.PK;
import com.persinity.common.logging.Log4jLogger;
import com.persinity.ndt.datamutator.load.EntityBase;
import com.persinity.ndt.datamutator.load.EntityPool;
import com.persinity.ndt.datamutator.load.EntityPoolUtil;

/**
 * Supports single column PK and FK.
 * <p/>
 *
 * @author Ivan Dachev
 */
public class RelDbEntityBase extends EntityBase {

    /**
     * @param id
     * @param table
     * @param cols
     * @param pk
     * @param fks
     * @param entityPoolUtil
     */
    public RelDbEntityBase(final List<Object> id, final String table, final Set<Col> cols, final PK pk,
            final Set<FK> fks, final EntityPoolUtil entityPoolUtil) {
        notEmpty(id);
        notEmpty(table);
        notEmpty(cols);

        this.id = id;
        this.table = table;
        this.cols = cols;
        this.pk = pk;
        this.fks = fks;
        this.entityPoolUtil = entityPoolUtil;

        mutatedData = new HashMap<>();
        colToFk = new HashMap<>();

        if (fks != null) {
            for (FK fk : fks) {
                for (Col fkCol : fk.getColumns()) {
                    colToFk.put(fkCol, fk);
                }
            }
        }
    }

    @Override
    public List<Object> getId() {
        return id;
    }

    /**
     * Mutates the entity by updating all columns with given mutateId if possible.
     * Support mutating an FK column by lookup for FK dest entity in the {@link EntityPool}.
     */
    @Override
    public void mutate(final long mutateId) {
        mutatedData.clear();
        for (Col col : cols) {
            if (pk != null && pk.getColumns().contains(col)) {
                continue;
            }

            final Object value;
            final FK fk = colToFk.get(col);
            if (fk != null) {
                value = entityPoolUtil.getRandomFkValue(fk);
            } else {
                final String colType = col.getType();
                value = entityPoolUtil.getTypeFactor()
                        .formatValue(colType, mutateId, format("{}_{}", mutateId, col.getName()));
            }

            if (value == null) {
                continue;
            }

            mutatedData.put(col, value);
        }
    }

    @Override
    public String getType() {
        return table;
    }

    public Map<Col, Object> getMutatedData() {
        return mutatedData;
    }

    public String getTable() {
        return table;
    }

    public Set<Col> getCols() {
        return cols;
    }

    public PK getPk() {
        return pk;
    }

    public Set<FK> getFks() {
        return fks;
    }

    @Override
    public boolean equals(final Object object) {
        if (this == object) {
            return true;
        }

        if (!(object instanceof RelDbEntityBase)) {
            return false;
        }

        RelDbEntityBase other = (RelDbEntityBase) object;

        return Objects.equals(getType(), other.getType()) && Objects.equals(getId(), other.getId());
    }

    @Override
    public int hashCode() {
        if (hashCode == null) {
            hashCode = Objects.hash(getType(), getId());
        }
        return hashCode;
    }

    @Override
    public String toString() {
        return format("{}({}, {})", getType(), getId(), getMutatedData());
    }

    private final List<Object> id;
    private final String table;
    private final Set<Col> cols;
    private final PK pk;
    private final Set<FK> fks;
    private final EntityPoolUtil entityPoolUtil;
    private final Map<Col, Object> mutatedData;
    private final HashMap<Col, FK> colToFk;

    private Integer hashCode;

    private static final Log4jLogger log = new Log4jLogger(Logger.getLogger(RelDbEntityBase.class));
}
