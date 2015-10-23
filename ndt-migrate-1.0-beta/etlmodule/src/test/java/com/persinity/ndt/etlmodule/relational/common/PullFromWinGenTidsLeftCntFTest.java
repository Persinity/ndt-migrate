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

import static org.easymock.EasyMock.createStrictMock;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.verify;
import static org.junit.Assert.assertEquals;

import org.junit.Test;

/**
 * @author dyordanov
 */
public class PullFromWinGenTidsLeftCntFTest {

    @Test
    public void testApply() throws Exception {
        final PullFromWinGenTidsLeftCntF testee = new PullFromWinGenTidsLeftCntF(GID_SENTINEL);
        final BaseWindowGenerator winGen = createStrictMock(BaseWindowGenerator.class);
        expect(winGen.getGidHead()).andReturn(GID_HEAD);
        replay(winGen);

        final Long actual = testee.apply(winGen);
        assertEquals(GID_SENTINEL - GID_HEAD, actual.longValue());

        verify(winGen);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testPullFromWinGenTidsLeftCntFInvalidInput() throws Exception {
        new PullFromWinGenTidsLeftCntF(-1);
    }

    private static final long GID_SENTINEL = 10L;
    private static final long GID_HEAD = 1L;
}