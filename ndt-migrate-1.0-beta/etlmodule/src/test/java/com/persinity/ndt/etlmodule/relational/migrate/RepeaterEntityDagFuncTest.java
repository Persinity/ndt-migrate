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
