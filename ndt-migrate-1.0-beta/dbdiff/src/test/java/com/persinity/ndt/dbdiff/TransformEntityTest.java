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
package com.persinity.ndt.dbdiff;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

import java.util.Objects;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;

import com.google.common.collect.Sets;
import com.persinity.common.StringUtils;

/**
 * @author Ivan Dachev
 */
public class TransformEntityTest {

    @Before
    public void setUp() {
        sourceLeadingColumns = Sets.newHashSet("col1", "col2");
        sourceLeadingColumnsDiff = Sets.newHashSet("col1", "col2", "col3");
        testee = new TransformEntity("targetEntity", "transformStatement", "sourceLeadingEntity", sourceLeadingColumns);
    }

    @Test
    public void testGetTargetEntity() throws Exception {
        assertThat(testee.getTargetEntity(), is("targetEntity"));
    }

    @Test
    public void testGetTransformStatement() throws Exception {
        assertThat(testee.getTransformStatement(), is("transformStatement"));
    }

    @Test
    public void testGetSourceLeadingEntity() throws Exception {
        assertThat(testee.getSourceLeadingEntity(), is("sourceLeadingEntity"));
    }

    @Test
    public void testGetSourceLeadingColumns() throws Exception {
        assertThat(testee.getSourceLeadingColumns(), is(sourceLeadingColumns));
    }

    @Test
    public void testEquals() throws Exception {
        assertTrue(testee.equals(new TransformEntity("targetEntity", "transformStatement", "sourceLeadingEntity",
                sourceLeadingColumns)));

        assertFalse(testee.equals(
                new TransformEntity("diff", "transformStatement", "sourceLeadingEntity", sourceLeadingColumns)));
        assertFalse(testee.equals(
                new TransformEntity("targetEntity", "diff", "sourceLeadingEntity", sourceLeadingColumns)));
        assertFalse(
                testee.equals(new TransformEntity("targetEntity", "transformStatement", "diff", sourceLeadingColumns)));
        assertFalse(testee.equals(new TransformEntity("targetEntity", "transformStatement", "sourceLeadingEntity",
                sourceLeadingColumnsDiff)));
    }

    @Test
    public void testHashCode() throws Exception {
        assertThat(testee.hashCode(),
                is(Objects.hash("targetEntity", "transformStatement", "sourceLeadingEntity", sourceLeadingColumns)));
        assertThat(testee.hashCode(),
                is(not(Objects.hash("diff", "transformStatement", "sourceLeadingEntity", sourceLeadingColumns))));
        assertThat(testee.hashCode(), is(not(Objects
                .hash("targetEntity", "transformStatement", "sourceLeadingEntity", sourceLeadingColumnsDiff))));
        assertThat(testee.hashCode(),
                is(not(Objects.hash("targetEntity", "diff", "sourceLeadingEntity", sourceLeadingColumns))));
        assertThat(testee.hashCode(),
                is(not(Objects.hash("targetEntity", "transformStatement", "diff", sourceLeadingColumns))));
    }

    @Test
    public void testToString() throws Exception {
        assertThat(testee.toString(), is(StringUtils
                .format("TransformEntity@{}(targetEntity, transformStatement, sourceLeadingEntity, [col1, col2])",
                        Integer.toHexString(testee.hashCode()))));
    }

    private TransformEntity testee;
    private Set<String> sourceLeadingColumns;
    private Set<String> sourceLeadingColumnsDiff;
}