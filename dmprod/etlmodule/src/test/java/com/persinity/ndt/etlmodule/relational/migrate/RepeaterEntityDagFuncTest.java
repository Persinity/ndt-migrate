/**
 * Copyright (c) 2015 Persinity Inc.
 */

package com.persinity.ndt.etlmodule.relational.migrate;

import static org.junit.Assert.assertEquals;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import org.junit.Assert;
import org.junit.Test;

import com.persinity.ndt.transform.EntitiesDag;

/**
 * @author Doichin Yordanov
 */
public class RepeaterEntityDagFuncTest {

    /**
     * Test method for
     * {@link RepeaterEntityDagFunc#apply(java.util.Set)} with
     * invalid input.
     */
    @Test(expected = NullPointerException.class)
    public void testApplyInvalid() {
        final RepeaterEntityDagFunc f = new RepeaterEntityDagFunc();
        f.apply(null);
    }

    /**
     * Test method for
     * {@link RepeaterEntityDagFunc#apply(java.util.Set)}.
     */
    @Test
    public void testApplyEmpty() {
        final RepeaterEntityDagFunc f = new RepeaterEntityDagFunc();
        final Set<String> entities = Collections.emptySet();
        final EntitiesDag actual = f.apply(entities);
        Assert.assertNotNull(actual);
        final EntitiesDag expected = new EntitiesDag(entities);
        assertEquals(expected, actual);
    }

    /**
     * Test method for
     * {@link RepeaterEntityDagFunc#apply(Set)}.
     */
    @Test
    public void testApply() {
        final RepeaterEntityDagFunc f = new RepeaterEntityDagFunc();
        final Set<String> entities = new HashSet<>(Arrays.asList("a", "b", "c"));
        final EntitiesDag actual = f.apply(entities);
        Assert.assertNotNull(actual);
        final EntitiesDag expected = new EntitiesDag(entities);
        assertEquals(expected, actual);
    }

}
