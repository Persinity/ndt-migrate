package com.persinity.common.db.metainfo.constraint;

import static com.persinity.common.invariant.Invariant.notEmpty;

import java.util.Objects;
import java.util.Set;

import com.google.common.collect.ImmutableSet;
import com.persinity.common.StringUtils;
import com.persinity.common.db.metainfo.Col;

/**
 * @author Ivo Yanakiev
 */
public class Constraint {

    protected Constraint(final String name, final String table, final Set<Col> columns) {
        notEmpty(name);
        notEmpty(table);
        notEmpty(columns);

        this.name = name;
        this.table = table;
        this.columns = ImmutableSet.copyOf(columns); // will keep the order of insert
    }

    @Override
    public boolean equals(final Object obj) {

        if (!(obj instanceof Constraint)) {
            return false;
        }

        final Constraint that = (Constraint) obj;
        if (this == that) {
            return true;
        }

        // here include the class name to honer the FK/PK/NotNull/Unique constraint
        return getClass().getName().equals(that.getClass().getName()) &&
                name.equals(that.name) &&
                table.equals(that.table) &&
                columns.equals(that.columns);
    }

    /**
     * @return The constraint name.
     */
    public String getName() {
        return name;
    }

    /**
     * @return table name
     */
    public String getTable() {
        return table;
    }

    /**
     * @return columns in constraint
     */
    public Set<Col> getColumns() {
        return columns;
    }

    @Override
    public int hashCode() {
        if (hashCode == null) {
            // here include the class name to honor the FK/PK/NotNull/Unique constraint
            hashCode = Objects.hash(getClass().getName(), getName(), getTable(), getColumns());
        }
        return hashCode;
    }

    @Override
    public String toString() {
        if (toString == null) {
            toString = StringUtils
                    .format("{}({}, {}, {})", getClass().getSimpleName(), getName(), getTable(), getColumns());
        }
        return toString;
    }

    private final String name;
    private final String table;
    private final Set<Col> columns;

    private String toString;
    private Integer hashCode;

}
