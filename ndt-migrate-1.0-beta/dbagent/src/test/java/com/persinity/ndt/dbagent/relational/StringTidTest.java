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

import org.junit.Assert;
import org.junit.Test;

/**
 * @author Doichin Yordanov
 */
public class StringTidTest {

    /**
     * Test method for {@link com.persinity.ndt.dbagent.relational.StringTid#hashCode()}.
     */
    @Test
    public void testHashCode() {
        final StringTid tid = new StringTid("T1");
        final StringTid tidSame = new StringTid("T1");
        Assert.assertEquals(tid.hashCode(), tidSame.hashCode());
    }

    /**
     * Test method for {@link com.persinity.ndt.dbagent.relational.StringTid#equals(java.lang.Object)}.
     */
    @Test
    public void testEqualsObject() {
        final StringTid tid = new StringTid("T1");
        final StringTid tidSame = new StringTid("T1");
        final StringTid tidDiff = new StringTid("T2");

        Assert.assertEquals(tid, tid);
        Assert.assertEquals(tid, tidSame);
        Assert.assertEquals(tidSame, tid);
        Assert.assertNotEquals(tid, null);
        Assert.assertNotEquals(tid, tidDiff);
    }

}
