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

package com.persinity.ndt.dbagent.relational;

import java.util.Arrays;

import org.easymock.EasyMock;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.google.common.base.Function;
import com.persinity.common.collection.CollectionUtils;
import com.persinity.common.collection.Tree;
import com.persinity.common.db.RelDb;
import com.persinity.ndt.transform.RelFunc;

/**
 * TODO add more unit tests for rest of the methods
 *
 * @author Ivo Yanakiev
 */
public class RelClogAgentTest {

    @Before
    public void setUp() {
        final SchemaInfo schema = EasyMock.createNiceMock(SchemaInfo.class);
        final AgentSqlStrategy strategy = EasyMock.createNiceMock(AgentSqlStrategy.class);
        final RelDb db = EasyMock.createNiceMock(RelDb.class);
        EasyMock.expect(strategy.deleteAllStatement(EasyMock.anyString())).andReturn(TR_LOG_DEL_STMT).anyTimes();

        EasyMock.replay(strategy);

        testee = new RelClogAgent(db, schema, strategy);
    }

    @Test
    public void testDeleteTrlog() throws Exception {
        final Tree<Function<RelDb, RelDb>> actual = testee.trlogCleanup();

        final Function<RelDb, RelDb> delFunc = new RelFunc(TR_LOG_DEL_STMT);
        final Tree<Function<RelDb, RelDb>> expected = CollectionUtils.newTree(Arrays.asList(delFunc));

        Assert.assertEquals(expected, actual);
    }

    private RelClogAgent testee;
    private String TR_LOG_DEL_STMT = "DELETE FROM trlog";
}