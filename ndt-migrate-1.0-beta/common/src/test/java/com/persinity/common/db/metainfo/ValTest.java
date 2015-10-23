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

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import org.junit.Test;

/**
 * @author Ivan Dachev
 */
public class ValTest {

    @Test
    public void testToString() throws Exception {
        assertThat(new Val("x").toString(), is("x"));
    }

    @Test(expected = NullPointerException.class)
    public void testToString_NullLeft() throws Exception {
        new Val(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testToString_EmptyLeft() throws Exception {
        new Val("");
    }
}