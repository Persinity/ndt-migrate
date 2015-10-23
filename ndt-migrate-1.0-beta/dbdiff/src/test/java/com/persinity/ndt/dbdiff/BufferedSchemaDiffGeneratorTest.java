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

import static org.easymock.EasyMock.createNiceMock;
import static org.easymock.EasyMock.createStrictMock;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.verify;
import static org.junit.Assert.assertEquals;

import java.util.Collection;

import org.junit.Test;

import com.persinity.common.collection.DirectedEdge;
import com.persinity.ndt.dbagent.relational.AgentSqlStrategy;
import com.persinity.ndt.dbagent.relational.SchemaInfo;

/**
 * @author dyordanov
 */
public class BufferedSchemaDiffGeneratorTest {

    @Test(expected = NullPointerException.class)
    public void testBufferedSchemaDiffGenerator_InvalidInput() {
        new BufferedSchemaDiffGenerator(null);
    }

    @Test
    public void testGenerateDiff() {
        final SchemaDiffGenerator sdg = createStrictMock(SchemaDiffGenerator.class);
        final SchemaInfo srcSchemaInfo = createNiceMock(SchemaInfo.class);
        final SchemaInfo dstSchemaInfo = createNiceMock(SchemaInfo.class);
        final DirectedEdge<SchemaInfo, SchemaInfo> dbSources = new DirectedEdge<>(srcSchemaInfo, dstSchemaInfo);
        final AgentSqlStrategy sqlStrategy = createNiceMock(AgentSqlStrategy.class);
        final Collection<TransformEntity> expected = createNiceMock(Collection.class);
        expect(sdg.generateDiff(dbSources, sqlStrategy)).andReturn(expected).once();
        replay(sdg);

        final BufferedSchemaDiffGenerator testee = new BufferedSchemaDiffGenerator(sdg);
        final Collection<TransformEntity> actual = testee.generateDiff(dbSources, sqlStrategy);
        assertEquals(expected, actual);
        verify(sdg);
    }
}