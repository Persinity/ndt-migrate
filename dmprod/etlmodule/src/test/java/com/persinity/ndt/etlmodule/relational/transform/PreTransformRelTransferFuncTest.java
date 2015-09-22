/**
 * Copyright (c) 2015 Persinity Inc.
 */
package com.persinity.ndt.etlmodule.relational.transform;

import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.expectLastCall;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.easymock.EasyMockSupport;
import org.junit.Test;

import com.persinity.common.collection.DirectedEdge;
import com.persinity.common.db.RelDb;
import com.persinity.common.db.metainfo.Col;
import com.persinity.ndt.dbagent.relational.AgentSqlStrategy;
import com.persinity.ndt.dbagent.relational.SchemaInfo;
import com.persinity.ndt.dbagent.relational.StringTid;

/**
 * @author Ivan Dachev
 */
public class PreTransformRelTransferFuncTest extends EasyMockSupport {

    @SuppressWarnings({ "unchecked", "ConstantConditions" })
    @Test
    public void testApply() throws Exception {
        final DirectedEdge<RelDb, RelDb> dataBridge = createMock(DirectedEdge.class);

        final RelDb srcDb = createMock(RelDb.class);
        expect(dataBridge.src()).andStubReturn(srcDb);

        srcDb.commit();
        expectLastCall();

        final List<StringTid> tids = new ArrayList<>();
        tids.add(new StringTid("3.4.5"));
        tids.add(new StringTid("3.4.2"));

        final DirectedEdge<SchemaInfo, SchemaInfo> schemas = createMock(DirectedEdge.class);

        final SchemaInfo srcSchema = createMock(SchemaInfo.class);
        expect(schemas.src()).andStubReturn(srcSchema);

        expect(SchemaInfo.TAB_TRLOG).andStubReturn("trlog");
        final Set<Col> trlogCols = new HashSet<>();
        trlogCols.add(new Col("status"));
        expect(srcSchema.getTableCols("trlog")).andStubReturn(trlogCols);

        final AgentSqlStrategy sqlStrategy = createMock(AgentSqlStrategy.class);
        expect(sqlStrategy.trlogUpdateStatus("trlog", SchemaInfo.TrlogStatusType.P, 2))
                .andReturn("udpate trlog status to P in 2 tids");

        expect(srcDb.executePreparedDml("udpate trlog status to P in 2 tids", Arrays.asList("3.4.5", "3.4.2")))
                .andReturn(4);

        replayAll();

        final PreTransformRelTransferFunc testee = new PreTransformRelTransferFunc(tids, schemas, sqlStrategy);

        final int res = testee.apply(dataBridge);

        verifyAll();

        assertThat(res, is(4));
    }
}