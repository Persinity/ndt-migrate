/**
 * Copyright (c) 2015 Persinity Inc.
 */
package com.persinity.common.db.metainfo.constraint;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertThat;

import java.util.Set;

import org.junit.Before;
import org.junit.Test;

import com.google.common.collect.Sets;
import com.persinity.common.db.metainfo.Col;

/**
 * @author Ivan Dachev
 */
public class ConstraintTest {

    @Before
    public void setUp() {
        table = "table";

        col1 = new Col("col1", "t1", false);
        col2 = new Col("col2", "t2", true);
        cols = Sets.newHashSet(col1, col2);

        testee = new Constraint("c1", table, cols);
        same = new Constraint("c1", table, cols);

        diffTable = new Constraint("c2", table + "diff", cols);

        col3 = new Col("col3", "t3", true);
        colsDiff = Sets.newHashSet(col1, col2, col3);
        diffCols = new Constraint("c2", table, colsDiff);

        sameDiffClass = new RConstraint("c1", table, cols);
    }

    @Test
    public void testEquals() throws Exception {
        assertEquals(testee, testee);
        assertEquals(testee, same);
        assertEquals(same, testee);

        assertNotEquals(testee, null);
        assertNotEquals(testee, diffTable);
        assertNotEquals(testee, diffCols);
        assertNotEquals(testee, sameDiffClass);
    }

    @Test
    public void testGetColumns() throws Exception {
        assertThat(testee.getColumns(), is(cols));
    }

    @Test
    public void testGetTable() throws Exception {
        assertThat(testee.getTable(), is(table));
    }

    @Test
    public void testHashCode() throws Exception {
        assertThat(testee.hashCode(), is(not(0)));

        assertEquals(testee.hashCode(), same.hashCode());
    }

    @Test
    public void testToString() throws Exception {
        assertThat(testee.toString(), is("Constraint(c1, table, [col1, col2])"));
        assertThat(sameDiffClass.toString(), is("RConstraint(c1, table, [col1, col2])"));
    }

    static class RConstraint extends Constraint {
        public RConstraint(final String name, final String table, final Set<Col> columns) {
            super(name, table, columns);
        }
    }

    private String table;
    private Col col1;
    private Col col2;
    private Col col3;
    private Set<Col> cols;
    private Constraint testee;
    private Constraint same;
    private Set<Col> colsDiff;
    private Constraint diffTable;
    private Constraint diffCols;
    private Constraint sameDiffClass;
}