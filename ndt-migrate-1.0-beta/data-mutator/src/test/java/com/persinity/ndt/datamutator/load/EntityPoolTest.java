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

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import org.junit.Test;

/**
 * @author Ivan Dachev
 */
public class EntityPoolTest {

    @Test
    public void testCalculateMaxAllowedForDelete() throws Exception {
        int res = EntityPool.calculateMaxAllowedForDelete(2, 1, 1);
        assertThat(res, is(0));

        res = EntityPool.calculateMaxAllowedForDelete(100, 30, 70);
        assertThat(res, is(0));

        res = EntityPool.calculateMaxAllowedForDelete(100, 30, 69);
        assertThat(res, is(1));

        res = EntityPool.calculateMaxAllowedForDelete(100, 30, 65);
        assertThat(res, is(5));

        res = EntityPool.calculateMaxAllowedForDelete(256, 78, 15);
        assertThat(res, is(140));

        res = EntityPool.calculateMaxAllowedForDelete(256, 70, 70);
        assertThat(res, is(7));
    }
}