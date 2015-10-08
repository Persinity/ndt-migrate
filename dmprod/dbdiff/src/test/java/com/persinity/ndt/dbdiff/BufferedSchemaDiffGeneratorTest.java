/**
 * Copyright (c) 2015 Persinity Inc.
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