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
package com.persinity.common.db.metainfo;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;

import com.persinity.common.collection.DirectedEdge;

/**
 * @author Doichin Yordanov
 */
public class AndTest {

    /**
     * Test method for {@link com.persinity.common.db.metainfo.And#And(java.util.List)} with invalid input.
     */
    @Test(expected = NullPointerException.class)
    public void testAndInvalidNull() {
        new And(null);
    }

    /**
     * Test method for {@link com.persinity.common.db.metainfo.And#And(java.util.List)} with invalid input.
     */
    @Test(expected = IllegalArgumentException.class)
    public void testAndIvalidEmpty() {
        final List<? extends SqlPredicate> predicates = Collections.emptyList();
        new And(predicates);
    }

    /**
     * Test method for {@link com.persinity.common.db.metainfo.And#toString()}.
     */
    @Test
    public void testToString() {
        final SqlPredicate predicate1 = new Between(new Col("sal"), new DirectedEdge<>(100, 200));
        final SqlPredicate predicate2 = new In<>(new Col("ename"), Arrays.asList("Doichin", "Ivan"));
        final List<? extends SqlPredicate> predicates = Arrays.asList(predicate1, predicate2);

        And testee = new And(predicates);
        Assert.assertEquals("(sal BETWEEN 100 AND 200) AND (ename IN ('Doichin', 'Ivan'))", testee.toString());

        testee = new And(Collections.singletonList(new Col("col")));
        Assert.assertEquals("col", testee.toString());
    }
}
