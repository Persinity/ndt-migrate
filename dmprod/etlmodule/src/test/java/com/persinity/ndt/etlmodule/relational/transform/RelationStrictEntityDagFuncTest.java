/**
 * Copyright (c) 2015 Persinity Inc.
 */
package com.persinity.ndt.etlmodule.relational.transform;

import com.persinity.ndt.transform.EntitiesDag;
import org.easymock.EasyMock;
import org.junit.Assert;
import org.junit.Test;

/**
 * @author Doichin Yordanov
 */
public class RelationStrictEntityDagFuncTest {

    /**
     * Test method for
     * {@link com.persinity.ndt.etlmodule.relational.transform.RelationStrictEntityDagFunc#RelationStrictEntityDagFunc(com.persinity.ndt.transform.EntitiesDag)}
     * with invalid input.
     */
    @Test(expected = IllegalArgumentException.class)
    public void testRelationStrictEntityDagFuncInvalidInput() {
        new RelationStrictEntityDagFunc(null);
    }

    /**
     * Test method for
     * {@link com.persinity.ndt.etlmodule.relational.transform.RelationStrictEntityDagFunc#apply(java.util.Set)}.
     */
    @Test
    public void testApply() {
        final EntitiesDag dag = EasyMock.createNiceMock(EntitiesDag.class);
        final RelationStrictEntityDagFunc f = new RelationStrictEntityDagFunc(dag);
        final EntitiesDag actual = f.apply(null);
        Assert.assertEquals(dag, actual);
    }

}
