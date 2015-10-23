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

package com.persinity.ndt.etlmodule.relational.common;

import static org.hamcrest.CoreMatchers.not;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

import java.util.LinkedList;
import java.util.List;

import org.easymock.EasyMock;
import org.hamcrest.CoreMatchers;
import org.junit.Before;
import org.junit.Test;

import com.persinity.common.collection.DirectedEdge;
import com.persinity.ndt.db.TransactionId;
import com.persinity.ndt.dbagent.relational.AgentSqlStrategy;
import com.persinity.ndt.dbagent.relational.SchemaInfo;
import com.persinity.ndt.dbagent.relational.StringTid;

/**
 * @author Ivo Yanakiev
 */
public class PostRelTransferFuncTest {

    @Before
    public void setUp() {
        List<TransactionId> tids1 = new LinkedList<>();
        tids1.add(new StringTid("l"));

        List<TransactionId> tids2 = new LinkedList<>();
        tids2.add(new StringTid("r"));

        @SuppressWarnings("unchecked")
        DirectedEdge<SchemaInfo, SchemaInfo> schemas = EasyMock.createNiceMock(DirectedEdge.class);
        AgentSqlStrategy sqlStrategy = EasyMock.createNiceMock(AgentSqlStrategy.class);

        testee = new PostRelTransferFunc(tids1, schemas, sqlStrategy);
        same = new PostRelTransferFunc(tids1, schemas, sqlStrategy);
        other = new PostRelTransferFunc(tids2, schemas, sqlStrategy);

        diffTids = new PostRelTransferFunc(tids2, schemas, sqlStrategy);
    }

    @Test
    public void testHashCode() throws Exception {
        assertThat(testee.hashCode(), CoreMatchers.is(not(0)));

        assertEquals(testee.hashCode(), same.hashCode());
    }

    @Test
    public void testEquals() throws Exception {
        assertEquals(testee, testee);
        assertEquals(testee, same);

        assertNotEquals(testee, null);
        assertNotEquals(testee, diffTids);
    }

    @Test
    public void testToString() throws Exception {
        assertTrue(testee.toString().matches("PostRelTransferFunc@[0-9a-f]+\\(\\[l\\]\\)"));
        assertTrue(other.toString().matches("PostRelTransferFunc@[0-9a-f]+\\(\\[r\\]\\)"));
    }

    private Object testee;
    private Object same;
    private Object other;
    private Object diffTids;

}