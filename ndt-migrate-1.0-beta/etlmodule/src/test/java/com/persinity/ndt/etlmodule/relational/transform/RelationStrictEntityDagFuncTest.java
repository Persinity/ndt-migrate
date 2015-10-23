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
package com.persinity.ndt.etlmodule.relational.transform;

import org.easymock.EasyMock;
import org.junit.Assert;
import org.junit.Test;

import com.persinity.ndt.transform.EntitiesDag;

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
