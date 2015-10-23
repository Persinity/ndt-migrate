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

import java.util.Arrays;
import java.util.List;

import org.easymock.EasyMock;
import org.junit.Assert;
import org.junit.Test;

import com.persinity.common.collection.DirectedEdge;
import com.persinity.common.collection.Pool;
import com.persinity.common.db.RelDb;
import com.persinity.ndt.db.TransactionId;
import com.persinity.ndt.dbagent.relational.StringTid;
import com.persinity.ndt.etlmodule.relational.RelTransferWindow;
import com.persinity.ndt.transform.EntitiesDag;

/**
 * @author Doichin Yordanov
 */
public class RelTransferWindowTest {

    private static final EntitiesDag ENTITIES = new EntitiesDag(Arrays.asList("emp", "dept"));
    private static final EntitiesDag DIFF_ENTITIES = new EntitiesDag(Arrays.asList("employee", "department"));
    private static final DirectedEdge<Pool<RelDb>, Pool<RelDb>> DATA_POOL_BRIDGE = new DirectedEdge<Pool<RelDb>, Pool<RelDb>>(
            EasyMock.createNiceMock(Pool.class), EasyMock.createNiceMock(Pool.class));
    private static final List<? extends TransactionId> TIDS = Arrays
            .asList(new StringTid("TransactionA"), new StringTid("TransactionB"));

    /**
     * Test method for {@link com.persinity.ndt.etlmodule.relational.RelTransferWindow#hashCode()}.
     */
    @Test
    public void testHashCode() {
        final RelTransferWindow win = stubWindow(ENTITIES);
        final RelTransferWindow winSame = stubWindow(ENTITIES);
        Assert.assertEquals(win.hashCode(), winSame.hashCode());
    }

    /**
     * Test method for {@link com.persinity.ndt.etlmodule.relational.RelTransferWindow#getDataPoolBridge()}.
     */
    @Test
    public void testDataBridge() {
        final RelTransferWindow win = stubWindow(ENTITIES);
        Assert.assertEquals(DATA_POOL_BRIDGE, win.getDataPoolBridge());
    }

    /**
     * Test method for {@link com.persinity.ndt.etlmodule.relational.RelTransferWindow#getSrcTids()}.
     */
    @Test
    public void testSrcTids() {
        final RelTransferWindow win = stubWindow(ENTITIES);
        Assert.assertEquals(TIDS, win.getSrcTids());
    }

    /**
     * Test method for {@link com.persinity.ndt.etlmodule.relational.RelTransferWindow#getDstEntitiesDag()}.
     */
    @Test
    public void testDstEntities() {
        final RelTransferWindow win = stubWindow(ENTITIES);
        Assert.assertEquals(ENTITIES, win.getDstEntitiesDag());
    }

    /**
     * Test method for {@link com.persinity.ndt.etlmodule.relational.RelTransferWindow#equals(java.lang.Object)}.
     */
    @Test
    public void testEqualsObject() {
        final RelTransferWindow win = stubWindow(ENTITIES);
        final RelTransferWindow winSame = stubWindow(ENTITIES);
        final RelTransferWindow winDiff = stubWindow(DIFF_ENTITIES);

        Assert.assertEquals(win, win);
        Assert.assertEquals(win, winSame);
        Assert.assertEquals(winSame, win);
        Assert.assertNotEquals(win, null);
        Assert.assertNotEquals(win, winDiff);
    }

    private RelTransferWindow stubWindow(final EntitiesDag entities) {
        return new RelTransferWindow(DATA_POOL_BRIDGE, TIDS, entities.vertexSet(), entities);
    }

}
