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
 * @author Ivo Yanakiev
 */
public class FKTest {

    @Before
    public void setUp() {

        Col col1 = new Col("col1", "t1", false);
        Col col2 = new Col("col2", "t2", true);
        Set<Col> cols1 = Sets.newHashSet(col1, col2);

        Col col3 = new Col("col3", "t1", false);
        Col col4 = new Col("col4", "t2", true);
        Set<Col> cols2 = Sets.newHashSet(col3, col4);

        Unique constraint1 = new Unique("constraint1", "table2", cols2);
        Unique constraint2 = new Unique("constraint2", "table3", cols2);

        testee = new FK("c1", "table1", cols1, constraint1);
        same = new FK("c1", "table1", cols1, constraint1);
        diffConstraint = new FK("c1", "table1", cols1, constraint2);
        sameDiffClass = new X("c1", "table1", cols1, constraint1);

        Col col5 = new Col("col3", "t1", true);
        Col col6 = new Col("col4", "t2", true);
        Set<Col> cols3 = Sets.newHashSet(col5, col6);

        diffIsWeak = new FK("c1", "table1", cols3, constraint1);

    }

    @Test
    public void testEquals() throws Exception {

        assertEquals(testee, testee);
        assertEquals(testee, same);

        assertNotEquals(testee, null);
        assertNotEquals(testee, diffConstraint);
        assertNotEquals(testee, diffIsWeak);
        assertNotEquals(testee, sameDiffClass);
    }

    @Test
    public void testHashCode() throws Exception {

        assertThat(testee.hashCode(), is(not(0)));

        assertEquals(testee.hashCode(), same.hashCode());
    }

    @Test
    public void testToString() throws Exception {
        assertThat(testee.toString(),
                is("FK(c1, table1, [col1, col2]) false -> Unique(constraint1, table2, [col4, col3])"));
    }

    private Object testee;
    private Object same;
    private Object diffConstraint;
    private Object diffIsWeak;
    private Object sameDiffClass;

    private class X extends FK {
        public X(final String name, final String table, final Set<Col> columns, final Unique dstConstraint) {
            super(name, table, columns, dstConstraint);
        }
    }
}